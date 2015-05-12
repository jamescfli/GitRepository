package cn.nec.nlc.jamesli.tools.at74schedulerepeatalarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyIntentService extends IntentService {
//    public static final int NOTIFICATION_ID = 1;
//    private NotificationManager mNotificationManager;
//    NotificationCompat.Builder builder;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        Bundle extras = intent.getExtras();
        // Do the work that requires your app to keep the CPU running.
        for (int i = 0; i < 5; i++) {
            Log.i("MyIntentService", "Running service " + (i+1) + "/5 @ "
                    + SystemClock.elapsedRealtime());
            SystemClock.sleep(3000);
        }
        Log.i("MyIntentService", "Completed service @ " + SystemClock.elapsedRealtime());
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        // It is guaranteed that CPU will stay awake until you fire completeWakefulIntent
        MyWakefulReceiver.completeWakefulIntent(intent);
    }
}
