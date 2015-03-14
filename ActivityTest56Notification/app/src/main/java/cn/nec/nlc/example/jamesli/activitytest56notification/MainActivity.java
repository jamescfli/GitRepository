package cn.nec.nlc.example.jamesli.activitytest56notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
    private Button buttonStartNoti;
    private static final int mNotificationId = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStartNoti = (Button) findViewById(R.id.buttonStartNoti);
        buttonStartNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // build notificatin
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MainActivity.this)
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle("My notification")
                                .setContentText("Hello World!");
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                // Adds the back stack for the Intent (but not the Intent itself)
                // addParentStack (Class<?> sourceActivityClass)
                stackBuilder.addParentStack(ResultActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                // Add a new Intent to the task stack. The most recently added Intent will invoke
                // the Activity at the top of the final task stack.
                stackBuilder.addNextIntent(resultIntent);
                // Obtain a PendingIntent for launching the task constructed by this builder so far
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,      // requestCode
                                PendingIntent.FLAG_UPDATE_CURRENT);   // flag: Flag indicating
                                // that if the described PendingIntent already exists, then keep
                                // it but replace its extra data with what is in this new Intent.
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(mNotificationId, mBuilder.build());
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
