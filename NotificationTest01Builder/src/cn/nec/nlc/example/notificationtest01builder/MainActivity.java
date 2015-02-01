package cn.nec.nlc.example.notificationtest01builder;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void showNotification(View v) {
		// prepare intent which is triggered if the notification is selected
		Intent intent = new Intent(this, NotificationReceiver.class);
		// pIntent will be triggered later by notification, wrapper for intents which will not be executed immediately
		// Retrieve a PendingIntent that will start a new activity, like calling Context.startActivity(Intent)
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		// .. context: the current context will be saved and the new intent will be started from this context
		// 0: requestCode
		// intent: the intent will be started later
		// flags: May be 
		//		FLAG_ONE_SHOT, - PendingIntent can be used only once
		//		FLAG_NO_CREATE, - if not exist, then simply return null instead of creating it
		//		FLAG_CANCEL_CURRENT, - the current one, if exists, should be canceled before generating a new one
		//		FLAG_UPDATE_CURRENT, - if exists, keep it but replace its extra data with what is in this new Intent
		// 	or any of the flags as supported by Intent.fillIn() to control 
		// which unspecified parts of the intent that can be supplied when the actual send happens.

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n  = new Notification.Builder(this)
		        .setContentTitle("New mail from " + "test@gmail.com")
		        .setContentText("Subject")
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentIntent(pIntent)
		        .setAutoCancel(true)
		        .addAction(R.drawable.ic_launcher, "Call", pIntent)
		        .addAction(R.drawable.ic_launcher, "More", pIntent)
		        .addAction(R.drawable.ic_launcher, "And more", pIntent).build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// 0 ~ ID for notification, n ~ notification itself
		notificationManager.notify(0, n); 
	}

}
