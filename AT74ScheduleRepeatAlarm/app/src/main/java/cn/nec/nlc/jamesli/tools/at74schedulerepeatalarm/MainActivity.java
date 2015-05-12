package cn.nec.nlc.jamesli.tools.at74schedulerepeatalarm;

import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;


public class MainActivity extends ActionBarActivity {
    PowerManager.WakeLock wakeLock;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        wakeLock.release();
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
