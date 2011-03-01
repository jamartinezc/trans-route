package com.hitojaguar.transroute.dataaccess.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class StationDao {
	
	private TransRouteMainDao mTRdao;

	public static final String DATABASE_TABLE = "station";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_LAT = "gps_latitude";
	public static final String KEY_LON = "gps_longitude";

	public StationDao(TransRouteMainDao mTRdao) {
		super();
		this.mTRdao = mTRdao;
	}

	public long createStation(String name, String lat, String lon) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_LAT, lat);
        initialValues.put(KEY_LON, lon);

        return mTRdao.mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the Station with the given rowId
     * 
     * @param rowId id of Station to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteStation(long rowId) {

        return mTRdao.mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all Stations in the database
     * 
     * @return Cursor over all Stations
     */
    public Cursor fetchAllStations() {

        return mTRdao.mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_LAT,KEY_LAT},
        		null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the Station that matches the given rowId
     * 
     * @param rowId id of Station to retrieve
     * @return Cursor positioned to matching Station, if found
     * @throws SQLException if Station could not be found/retrieved
     */
    public Cursor fetchStation(long rowId) throws SQLException {

        Cursor mCursor =

            mTRdao.mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_LAT, KEY_LON}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    

    /**
     * Return a Cursor over the list of all Stations in the database with the given name.
     * @param name the station's name
     * 
     * @return Cursor over all Stations with the given name
     */
    public Cursor fetchStations(String name) throws SQLException {

        Cursor mCursor =

            mTRdao.mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_LAT, KEY_LON}, KEY_NAME + "=" + name, null,
                    null, null, null, null);
        return mCursor;

    }

    /**
     * Update the Station using the details provided. The Station to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of Station to update
     * @param name value to set Station title to
     * @param body value to set Station body to
     * @return true if the Station was successfully updated, false otherwise
     */
    public boolean updateStation(long rowId, String name, String lat, String lon) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_LAT, lat);
        args.put(KEY_LON, lon);

        return mTRdao.mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
