package com.hitojaguar.transroute.widgets.extend;

import android.content.Context;
import android.view.View;

public interface IRouteResultViewCreator {

	public abstract View getResultView(Context context, String img, String text);

}