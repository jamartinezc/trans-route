package com.hitojaguar.transroute;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.hitojaguar.transroute.entities.Result;
import com.hitojaguar.transroute.widgets.extend.ResultExpandableListAdapter;

public class TransRoute extends Activity {
    private static final String SOURCE = "source";
    private static final String DEST = "destination";
    private static final String RES = "r";
	private ExpandableListView mExpandList;
	
	private Result mResult;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Result result = (savedInstanceState == null) ? new Result() : (Result) savedInstanceState.getSerializable(TransRoute.RES);
        String source = (savedInstanceState == null) ? "" : savedInstanceState.getString(TransRoute.SOURCE);
        String destin = (savedInstanceState == null) ? "" : savedInstanceState.getString(TransRoute.DEST);
        
        mExpandList = (ExpandableListView) findViewById(R.id.results);
        ExpandableListAdapter adapter = new ResultExpandableListAdapter(result,this);
		mExpandList.setAdapter(adapter);

		EditText editSource = (EditText) findViewById(R.id.source);
		EditText editDestination = (EditText) findViewById(R.id.destination);
		
		editSource.setText(source);
		editDestination.setText(destin);
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
    
    private void saveState() {
    	
    }
}