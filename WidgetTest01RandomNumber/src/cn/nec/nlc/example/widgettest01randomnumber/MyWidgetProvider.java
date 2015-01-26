package cn.nec.nlc.example.widgettest01randomnumber;

import java.util.Random;

import cn.nec.nlc.example.widgettest01randomnumber.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {

//	private static final String ACTION_CLICK = "ACTION_CLICK";

	// Called in response to the ACTION_APPWIDGET_UPDATE and ACTION_APPWIDGET_RESTORED broadcast 
	// when AppWidget provider is being asked to provide RemoteViews for a set of AppWidgets.
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	      int[] appWidgetIds) {

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
	    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
	    for (int widgetId : allWidgetIds) {
	    	// create some random data
	    	int number = (new Random().nextInt(100));

	    	// RemoteViews to set layout resources; getPackageName(): get the application package
	    	RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
	    			R.layout.widget_layout);
	    	// whenever a new random number is generated
	    	Log.w("WidgetExample", String.valueOf(number));
	    	// Set the text
	    	remoteViews.setTextViewText(R.id.update, String.valueOf(number));

	    	// Register an onClickListener
	    	Intent intent = new Intent(context, MyWidgetProvider.class);

	    	intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	    	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

	    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
	    			0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	    	// Generate new random number when being clicked
	    	remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
	    	appWidgetManager.updateAppWidget(widgetId, remoteViews);
	    	
	    	// Otherwise, self update the random number in 300 secs
	    	// see res/xml/widget_info.xml <appwidget-provider android:updatePeriodMillis="300000"/>
	    	// for further detail
	    }
	}
	
} 
