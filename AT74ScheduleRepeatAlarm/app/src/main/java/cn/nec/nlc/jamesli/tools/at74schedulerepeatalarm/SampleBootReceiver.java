package cn.nec.nlc.jamesli.tools.at74schedulerepeatalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SampleBootReceiver extends BroadcastReceiver {
    private final static String TAG = "SampleBootReceiver";
    private SampleAlarmReceiver alarm = new SampleAlarmReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i(TAG, "Device has been rebooted.");
            alarm.setAlarm(context);
        }
    }
}
