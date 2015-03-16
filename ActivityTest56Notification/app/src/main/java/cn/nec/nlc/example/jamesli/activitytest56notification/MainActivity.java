package cn.nec.nlc.example.jamesli.activitytest56notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
    private Button buttonStartNoti;
    private static final int mNotificationId = 1001;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private int counterMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStartNoti = (Button) findViewById(R.id.buttonStartNoti);
        buttonStartNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // test 1
//                buildBasicNotification();

//                // test 2
//                buildExpendedNotification();

//                // test 3
//                buildIncreasedCounterNotification();

                // test 4
                // display progress in notification
                buildProgressBarNotification();

            }
        });
//        // build notification, come with test 3
//        mBuilder = new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.notification_icon)
//                        .setContentTitle("New Message")
//                        .setContentText("You have received new messages");
//        counterMessage = 0;
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        // mId allows you to update the notification later on.
//        mNotificationManager.notify(mNotificationId, mBuilder.build());

    }

    private void buildBasicNotification() {
        // build notificatin
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Notifications remain visible until one of the following happens:
        //  1) The user dismisses the notification either individually or by using "Clear All"
        //      (if the notification can be cleared).
        //  2) The user clicks the notification, and you called setAutoCancel() when you created
        //      the notification.
        //  3) You call cancel() for a specific notification ID. This method also deletes ongoing
        //      notifications.
        //  4) You call cancelAll(), which removes all of the notifications you previously issued.
        mBuilder.setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ResultActivity.class);

//                // Option 1) use pending intent to go back to System Home when entering
//                //   ResultActivity from Notification
//                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0,
//                        resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                mBuilder.setContentIntent(pendingIntent);

        // Option 2) use TaskStackBuilder to go back to MainActivity from ResultActivity
        //   However, this does not work for API 21 Android 5.0
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        // addParentStack (Class<?> sourceActivityClass)
        stackBuilder.addParentStack(ResultActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        // Add a new Intent to the task stack. The most recently added Intent will invoke
        // the Activity at the top of the final task stack.
        stackBuilder.addNextIntent(resultIntent); // for getPendingIntent()

        // If you need to, add arguments to Intent objects on the stack by calling
        // TaskStackBuilder.editIntentAt().

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

    private void buildExpendedNotification() {
        // expended layout to notification
        // Remember that expanded notifications are not available on platforms prior to
        // Android 4.1.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Event tracker")
                .setContentText("Events received");
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        for (int i=0; i < events.length; i++) {
            events[i] = "event " + (i+1);
        }
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
        // Moves events into the expanded layout
        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);
        // Issue the notification here.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

    private void buildIncreasedCounterNotification() {
        counterMessage++;
        mBuilder.setContentText("You've received new messages. (renewed)")
            .setNumber(counterMessage);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }


    private void buildProgressBarNotification() {
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.notification_icon);
        // Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr+=5) {
//                            // Sets the progress indicator to a max value, the
//                            // current completion percentage, and "determinate"
//                            // state
//                            mBuilder.setProgress(100, incr, false);
                            // To display an indeterminate activity indicator, add it to your
                            // notification with setProgress(0, 0, true), e.g.
                            mBuilder.setProgress(0, 0, true);
                            // Displays the progress bar for the first time.
                            mNotificationManager.notify(0, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try {
                                // Sleep for 2 seconds
                                Thread.sleep(2*1000);
                            } catch (InterruptedException e) {
                                Log.d("MainActivity", "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        // however, in practice, it will start off another notification when completed
                        mNotificationManager.notify(mNotificationId, mBuilder.build());
                    }
                }
        // Starts the thread by calling the run() method in its Runnable
        ).start();
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
