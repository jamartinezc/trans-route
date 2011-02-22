package com.hitojaguar.transroute.widgets.extend;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

public interface IRouteResultViewCreator {

	public abstract View getResultView(Context context, Bitmap img, String text, boolean forChild);

}