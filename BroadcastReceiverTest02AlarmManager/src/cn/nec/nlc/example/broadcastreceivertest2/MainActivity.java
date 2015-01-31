package cn.nec.nlc.example.broadcastreceivertest2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	// button onClick
	public void startAlert(View view) {
		EditText text = (EditText) this.findViewById(R.id.time);
		// get the delay time (in secs) from edittext
		int i = Integer.parseInt(text.getText().toString());
		Intent intent = new Intent(this, MyBroadcastReceiver.class);
		// Retrieve a PendingIntent that will perform a broadcast
		PendingIntent pendingIntent = PendingIntent.getBroadcast
				(this.getApplicationContext(), 234324243, intent, 0);
		// .. 
		// context	The Context in which this PendingIntent should perform the broadcast.
		// requestCode	Private request code for the sender (currently not used).
		// intent	The Intent to be broadcast.
		// flags	May be FLAG_ONE_SHOT, FLAG_NO_CREATE, FLAG_CANCEL_CURRENT, 
		//	FLAG_UPDATE_CURRENT, or any of the flags as supported by Intent.fillIn() 
		//	to control which unspecified parts of the intent that can be supplied 
		// 	when the actual send happens.
		
		// AlarmManager: allow you to schedule your application to be run at 
		// some point in the future
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// RTC_WAKEUP wakes up the device when it goes off
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (i*1000), pendingIntent);
		Toast.makeText(this, "Alarm set in " + i + " seconds. Please wait!", 
				Toast.LENGTH_LONG).show();
	}

}
