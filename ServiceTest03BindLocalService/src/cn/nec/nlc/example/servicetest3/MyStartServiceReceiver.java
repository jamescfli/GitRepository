package cn.nec.nlc.example.servicetest3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//registered in Manifest.xml, started from MyScheduleReceiver
public class MyStartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, LocalWordService.class);
		context.startService(service);
	}
} 