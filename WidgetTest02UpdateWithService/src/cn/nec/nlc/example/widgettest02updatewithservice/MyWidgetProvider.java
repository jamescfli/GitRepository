package cn.nec.nlc.example.widgettest02updatewithservice;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyWidgetProvider extends AppWidgetProvider {
	private static final String LOG = "cn.nec.nlc.example.widgettest02updatewithservice";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.w(LOG, "onUpdate method called");
		// get all ids
		ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		
		// build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(), UpdateWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
		
		// update widgets via service
		context.startService(intent);
	}

}
