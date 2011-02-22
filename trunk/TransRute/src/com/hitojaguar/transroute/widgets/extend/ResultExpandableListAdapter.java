package com.hitojaguar.transroute.widgets.extend;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.hitojaguar.transroute.entities.Result;

public class ResultExpandableListAdapter extends BaseExpandableListAdapter {
	private Result mResult;
	private Activity mParent;

	public ResultExpandableListAdapter(Result mResult, Activity mParent) {
		super();
		this.mResult = mResult;
		this.mParent = mParent;
	}

	public Object getChild(int groupPosition, int childPosition) {
        return mResult.getPath()[groupPosition][childPosition];
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return mResult.getPath()[groupPosition].length;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        IRouteResultViewCreator rc = new RouteResultViewCreator();
        View childView = rc.getResultView(mParent, null, getChild(groupPosition, childPosition).toString(), true);
        childView.setLayoutParams(lp);
        
        return childView;
    }

    public Object getGroup(int groupPosition) {
        return mResult.getPathType()[groupPosition];
    }

    public int getGroupCount() {
        return mResult.getPathType().length;
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        IRouteResultViewCreator rc = new RouteResultViewCreator();
        View groupView = rc.getResultView(mParent, null, getGroup(groupPosition).toString(), false);
        groupView.setLayoutParams(lp);
        
        return groupView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void changeResult(Result result){
    	mResult = result;
    }
}
