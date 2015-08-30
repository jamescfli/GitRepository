package cn.jamesli.example.bt09ibeaconmonitoringandranging;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;


public class MonitoringActivity extends AppCompatActivity implements BeaconConsumer {
    private static final String TAG = "MonitoringActivity";
    private TextView mTextViewStatus;
    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        mTextViewStatus = (TextView) findViewById(R.id.text_view_status);

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        // Add AltBeacon Parser, which has been by default added to BeaconManager
        mBeaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//        // Add iBeacon Parser
//        mBeaconManager.getBeaconParsers().add(new BeaconParser()
//                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBeaconManager.bind(this);
        mTextViewStatus.setText("BeaconManager has been bind.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBeaconManager.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_monitoring, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ranging) {
            Intent intent = new Intent(this, RangingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconServiceConnect() {
        // once mBeaconManager bind the BeaconConsumer
        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewStatus.setText("I just saw an beacon for the first time!\n"
                                + mTextViewStatus.getText().toString());
                    }
                });
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewStatus.setText("I no longer see an beacon\n"
                                + mTextViewStatus.getText().toString());
                    }
                });
            }

            @Override
            public void didDetermineStateForRegion(final int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewStatus.setText("I have just switched from seeing/not seeing beacons: "
                                + state + "\n" + mTextViewStatus.getText().toString());
                    }
                });
            }
        });

        try {
            mBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringRegion",
                    null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
