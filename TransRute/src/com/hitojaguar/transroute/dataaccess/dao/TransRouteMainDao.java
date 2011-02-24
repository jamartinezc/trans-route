/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.hitojaguar.transroute.dataaccess.dao;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TransRouteMainDao {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";

    protected static final String TAG = "NotesDbAdapter";
    protected DatabaseHelper mDbHelper;
    protected SQLiteDatabase mDb;
    
    protected static final String DB_NAME = "TransRouteDB";
    protected static final String DB_PATH = "/data/data/com.hitojaguar.transroute/databases/";
    protected static final String DATABASE_TABLE = "notes";
    protected static final int DATABASE_VERSION = 1;

    protected final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
    	
        private SQLiteDatabase myDataBase;
        private final Context myContext;

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
        	myContext = context;
        }
        
        public void createDataBase() throws IOException{
        	 
        	boolean dbExist = checkDataBase();
     
        	if(dbExist){
        		//do nothing - database already exist
        	}else{
     
        		//By calling this method and empty database will be created into the default system path
                   //of your application so we are gonna be able to overwrite that database with our database.
            	this.getReadableDatabase();
     
            	try {
     
        			copyDataBase();
     
        		} catch (IOException e) {
     
            		throw new Error("Error copying database");
     
            	}
        	}
     
        }
     
        /**
         * Check if the database already exist to avoid re-copying the file each time you open the application.
         * @return true if it exists, false if it doesn't
         */
        private boolean checkDataBase(){
     
        	SQLiteDatabase checkDB = null;
     
        	try{
        		String myPath = DB_PATH + DB_NAME;
        		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
     
        	}catch(SQLiteException e){
     
        		//database does't exist yet.
     
        	}
     
        	if(checkDB != null){
     
        		checkDB.close();
     
        	}
     
        	return checkDB != null ? true : false;
        }
     
        /**
         * Copies your database from your local assets-folder to the just created empty database in the
         * system folder, from where it can be accessed and handled.
         * This is done by transfering bytestream.
         * */
        private void copyDataBase() throws IOException{
     
        	//Open your local db as the input stream
        	InputStream myInput = myContext.getAssets().open(DB_NAME);
     
        	// Path to the just created empty db
        	String outFileName = DB_PATH + DB_NAME;
     
        	//Open the empty db as the output stream
        	OutputStream myOutput = new FileOutputStream(outFileName);
     
        	//transfer bytes from the inputfile to the outputfile
        	byte[] buffer = new byte[1024];
        	int length;
        	while ((length = myInput.read(buffer))>0){
        		myOutput.write(buffer, 0, length);
        	}
     
        	//Close the streams
        	myOutput.flush();
        	myOutput.close();
        	myInput.close();
     
        }
     
        public void openDataBase() throws SQLException{
     
        	//Open the database
            String myPath = DB_PATH + DB_NAME;
        	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
     
        }
     
        @Override
    	public synchronized void close() {
     
        	    if(myDataBase != null)
        		    myDataBase.close();
     
        	    super.close();
     
    	}
     
    	@Override
    	public void onCreate(SQLiteDatabase db) {
    		try{
    			createDataBase();
    		}catch(Exception e){
    			Log.e("Error creating DB", e.getMessage(), e);
    		}
    		
    	}
     
    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     
    	}
     
            // Add your public helper methods to access and get content from the database.
           // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
           // to you to create adapters for your views.
     
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public TransRouteMainDao(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TransRouteMainDao open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDbHelper.openDataBase();
        mDb = mDbHelper.myDataBase;
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String body) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_BODY}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_BODY}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
