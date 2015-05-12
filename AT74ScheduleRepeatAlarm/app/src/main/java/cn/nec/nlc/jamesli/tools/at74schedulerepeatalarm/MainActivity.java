package cn.nec.nlc.jamesli.tools.at74schedulerepeatalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {
//    private PowerManager.WakeLock wakeLock;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
          // unlike wake locks (discussed in Keep the CPU On)
          //    a) it doesn't require special permission
          //    b) the platform correctly manages the user moving between applications,
          //    without your app needing to worry about releasing unused resources
        // Another way is to set android:keepScreenOn="true" in main layout, which is
        // equivalent to using FLAG_KEEP_SCREEN_ON.

        // one can also programmatically turn off the keep-screen-on flag
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        // wakelock is to keep your CPU on, to control the power state of the host device
//        // you should never need to use a wake lock in an activity
//        // Prefer to use WakefulBroadcastReceiver
//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                "MyWakelockTag");
//        wakeLock.acquire();

        // 1) ELAPSED_REALTIME_WAKEUP
        // Wake up the device to fire the alarm in 30 minutes, and every 30 minutes after that
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
        // alarm in one minute
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60*1000,
                alarmIntent);

        // 2) RTC
        // difference between setInexactRepeating (approximately) and setRepeating (precise)
        // setInexactRepeating was recommended to save handset power: When you use setInexactRepeating(),
        // Android synchronizes repeating alarms from multiple apps and fires them at the same time.
        // This reduces the total number of times the system must wake the device, thus reducing drain
        // on the battery.
        // a) Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants -- in this case, AlarmManager.INTERVAL_DAY
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        // b) Wake up the device to fire the alarm at precisely 8:30 a.m., and every 20 minutes thereafter
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // Set the alarm to start at 8:30 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 20, alarmIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        wakeLock.release();
        // cancel alarm
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.start_action) {
            return true;
        } else if (id == R.id.cancel_action) {
            return true;
        } else if (id == R.id.start_wakeful_service) {
            sendBroadcast(new Intent(this, MyWakefulReceiver.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
