package cn.nec.nlc.example.broadcastreceivertest2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// the context could be the phone page when app is in background, 
		// i.e. after onPause()
		Toast.makeText(context, "Don't panik but your time is up!!", 
				Toast.LENGTH_LONG).show();
		// vibrate the mobile phone
		Vibrator vibrator = (Vibrator) context.getSystemService
				(Context.VIBRATOR_SERVICE);
		// pattern + repeat in milliseconds
		vibrator.vibrate(new long[] { 1000, 50, 1000, 50, 1000 }, -1);
		// pattern: pause, alarm, pause, alarm, pause
		// repeat: -1 don't repeat, or repeat times
		vibrator.cancel();
	}
}
