package com.hitojaguar.transroute.widgets.extend;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RouteResultViewCreator implements IRouteResultViewCreator {

	@Override
	public View getResultView(Context context, String img, String text){
		LinearLayout linLa = new LinearLayout(context);
		linLa.addView(new ImageView(context));
		linLa.addView(new TextView(context));
		return linLa;
	}
}
