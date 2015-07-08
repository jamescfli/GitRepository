package cn.jamesli.example.at85notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class NotificationStatusBarActivity extends Activity {

    // Notification ID to allow for future updates
    private static final int MY_NOTIFICATION_ID = 1;

    // Notification Count
    private int mNotificationCount;

    // Notification Text Elements
    private final CharSequence tickerText = "This is a Really, Really, Super Long Notification Message!";
    private final CharSequence contentTitle = "Notification";
    private final CharSequence contentText = "You've Been Notified!";

    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    // Notification Sound and Vibration on Arrival
    private Uri soundURI = Uri
            .parse("android.resource://cn.jamesli.example.at85notification/"
                    + R.raw.alarm_rooster);
    private long[] mVibratePattern = { 0, 200, 200, 300 };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_status_bar);

        mNotificationIntent = new Intent(getApplicationContext(), NotificationSubActivity.class);
        mContentIntent = PendingIntent.getActivity(getApplicationContext(), 0, mNotificationIntent,
                PendingIntent.FLAG_ONE_SHOT);
        // FLAG_CANCEL_CURRENT:	Flag indicating that if the described PendingIntent already exists,
        //  the current one should be canceled before generating a new one.
        // FLAG_NO_CREATE: Flag indicating that if the described PendingIntent does not already
        //  exist, then simply return null instead of creating it.
        // FLAG_ONE_SHOT: Flag indicating that this PendingIntent can be used only once.
        // FLAG_UPDATE_CURRENT: Flag indicating that if the described PendingIntent already exists,
        //  then keep it but replace its extra data with what is in this new Intent.
        // All of these are referring to the Intent to be initiated, i.e. NotificationSubActivity

        final Button button = (Button) findViewById(R.id.buttonStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext())
                        .setTicker(tickerText)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setAutoCancel(true)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText + " (" + ++mNotificationCount + ")")
                        .setContentIntent(mContentIntent)
                        .setSound(soundURI)
                        .setVibrate(mVibratePattern);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService
                        (Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_status_bar, menu);
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
