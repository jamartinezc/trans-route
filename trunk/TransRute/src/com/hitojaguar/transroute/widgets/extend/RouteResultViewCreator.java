package com.hitojaguar.transroute.widgets.extend;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hitojaguar.transroute.R;

public class RouteResultViewCreator implements IRouteResultViewCreator {

	@Override
	public View getResultView(Context context, Bitmap img, String text,boolean forChild){
		

		LinearLayout linLa = new LinearLayout(context);
		ImageView iv = new ImageView(context);
		iv.setImageResource(R.drawable.default_bus);
		iv.setAdjustViewBounds(true);
		iv.setMaxWidth(25); 
		iv.setMaxHeight(25); 
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		iv.setLayoutParams(lp);
		linLa.addView(iv);
		TextView tv =  new TextView(context);
		tv.setText(text);
		tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		linLa.addView(tv);
		

		int padding = 0;
		if(forChild){
			padding = 20;
		}else{
			padding = 10;
		}
		linLa.setPadding(padding, linLa.getPaddingTop(), linLa.getPaddingRight(), linLa.getPaddingBottom());
		
		
		return linLa;
	}
}
