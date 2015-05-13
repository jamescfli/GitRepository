package cn.nec.nlc.jamesli.tools.at74schedulerepeatalarm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SampleSchedulingService extends IntentService {
    private static final String TAG = "Scheduling Demo";
    private static final int NOTIFICATION_ID = 1;
    // The string the app searches for in the Google home page content. If the app finds
    // the string, it indicates the presence of a doodle.
    private static final String SEARCH_STRING = "新闻";
    // The Google home page URL from which the app fetches content.
    // You can find a list of other Google domains with possible doodles here:
    // http://en.wikipedia.org/wiki/List_of_Google_domains
    private static final String URL = "http://www.baidu.com";
    private NotificationManager mNotificationManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SampleSchedulingService() {
        super("SchedulingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // The URL from which to fetch content.
        String urlString = URL;
        String result = "";

        try {
            result = loadFromNetwork(urlString);
        } catch (IOException e) {
            Log.i(TAG, getString(R.string.connection_error));
        }

        // If the app finds the string "doodle" in the Google home page content, it
        // indicates the presence of a doodle. Post a "Doodle Alert" notification.
        if (result.indexOf(SEARCH_STRING) != -1) {
            sendNotification(getString(R.string.doodle_found));
            Log.i(TAG, "Found news in Baidu!!");
        } else {
            sendNotification(getString(R.string.no_doodle));
            Log.i(TAG, "No news found @ Baidu :-(");
        }

        // Release the wake lock provided by the BroadcastReceiver
        SampleAlarmReceiver.completeWakefulIntent(intent);
    }

    // Post a notification indicating whether a doodle was found
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contextIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.doodle_alert))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);
        mBuilder.setContentIntent(contextIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    // methods to fetch content from the specified URL and return the content as a string
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str = "";
        try {
            stream = downloadUrl(urlString);
            str = readIt(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000); // in milliseconds
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);  // Sets the flag indicating whether this URLConnection allows input.
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }

    private String readIt(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        for (String line = reader.readLine();  line != null; line = reader.readLine()) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }
}
