package cn.jamesli.example.at86alarmcreator;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class AlarmCreateActivity extends Activity {

    private static final String TAG = "AlarmCreateActivity";

    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent;

    private static final long INITIAL_ALARM_DELAY = 2 * 1000L;  // 2 secs


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_create);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(AlarmCreateActivity.this,
                AlarmNotificationReceiver.class);

        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(AlarmCreateActivity.this,
                0, mNotificationReceiverIntent, 0);

        final Button singleAlarmButton = (Button) findViewById(R.id.buttonSingleAlarm);
        singleAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // For API <= 19 5000 has to be defined as a constant
//                mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
//                        SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
//                        5000, mNotificationReceiverPendingIntent);
                mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + INITIAL_ALARM_DELAY,
                        mNotificationReceiverPendingIntent);
                Toast.makeText(getApplicationContext(), "Single Alarm has been set.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
//        Log.i(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
