package com.hitojaguar.transroute;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.hitojaguar.transroute.dataaccess.dao.TransRouteMainDao;
import com.hitojaguar.transroute.dataaccess.importing.FileLoader;
import com.hitojaguar.transroute.entities.Result;
import com.hitojaguar.transroute.route.IRouteCalculator;
import com.hitojaguar.transroute.route.RouteCalculator;
import com.hitojaguar.transroute.widgets.extend.ResultExpandableListAdapter;

public class TransRoute extends Activity {
    private static final String SOURCE = "source";
    private static final String DEST = "destination";
    private static final String RES = "r";
	private ExpandableListView mExpandList;
	private ResultExpandableListAdapter mExpandListAdapter;
	private TransRouteMainDao mDao;
	
	private Result mResult;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mResult = (savedInstanceState == null) ? new Result() : (Result) savedInstanceState.getSerializable(TransRoute.RES);
        String source = (savedInstanceState == null) ? "" : savedInstanceState.getString(TransRoute.SOURCE);
        String destin = (savedInstanceState == null) ? "" : savedInstanceState.getString(TransRoute.DEST);
        
        mExpandList = (ExpandableListView) findViewById(R.id.results);
        mExpandListAdapter = new ResultExpandableListAdapter(mResult,this);
		mExpandList.setAdapter(mExpandListAdapter);

		EditText editSource = (EditText) findViewById(R.id.source);
		EditText editDestination = (EditText) findViewById(R.id.destination );
		
		editSource.setText(source);
		editDestination.setText(destin);

		Button findButton = (Button) findViewById(R.id.confirm);
		findButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                //setResult(RESULT_OK);
                //finish();

        		EditText editSource = (EditText) findViewById(R.id.source);
        		EditText editDestination = (EditText) findViewById(R.id.destination);
        		
            	findRoute(editSource.getText().toString(), editDestination.getText().toString());
            	mExpandListAdapter.notifyDataSetChanged();
            }
        });

		Button importButton = (Button) findViewById(R.id.import1);
		importButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	importFile();
            }
		});
		
		mDao = TransRouteMainDao.getInstance(this);
		try {
			mDao.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		EditText source = (EditText) findViewById(R.id.source);
		String value = source.getText().toString();
		outState.putString(TransRoute.SOURCE, value);

		EditText dest = (EditText) findViewById(R.id.destination);
		value = dest.getText().toString();
		outState.putString(TransRoute.DEST, value);
		
		outState.putSerializable(TransRoute.RES, mResult);
	}
    
    private void findRoute(String source, String destination) {
    	IRouteCalculator rc = new RouteCalculator(this);
    	mResult = rc.FindRoutes(source, destination);
    	mExpandListAdapter.changeResult(mResult);
    }
    
    private void importFile() {
    	
    	
    	//Intent fci = Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT), "text/csv");
    	//this.sendBroadcast(fci);
    	
    	
    	FileLoader fl = new FileLoader(this);
    	fl.load(new File("/mnt/sdcard/rutas.csv"));
	}
}