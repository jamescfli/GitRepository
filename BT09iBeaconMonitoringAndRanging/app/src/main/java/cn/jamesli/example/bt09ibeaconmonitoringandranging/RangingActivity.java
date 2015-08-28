package cn.jamesli.example.bt09ibeaconmonitoringandranging;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class RangingActivity extends AppCompatActivity implements BeaconConsumer {
    private static final String TAG = "RangingActivity";
    private TextView mTextViewRangingResults;
    private BeaconManager mBeaconManager;
    private Region regionBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);

        mTextViewRangingResults = (TextView) findViewById(R.id.text_view_ranging_results);

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
//        // Since mBeaconManager is shared by the two Activities, i.e. Monitoring and Ranging
//        // we don't need to add the parsers again
//        // Add AltBeacon Parser
//        mBeaconManager.getBeaconParsers().add(new BeaconParser()
//                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//        // Add iBeacon Parser
//        mBeaconManager.getBeaconParsers().add(new BeaconParser()
//                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//        mBeaconManager.bind(this);

        regionBeacon = new Region("myRangingRegion", null, null, null);

        // Note: this is important
        mBeaconManager.bind(this);
        mTextViewRangingResults.setText("BeaconManager has been bind.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ranging, menu);
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

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
//                // for test
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTextViewRangingResults.setText("beacons.size() = " + beacons.size() + "\n"
//                                + mTextViewRangingResults.getText());
//                    }
//                });
                if (beacons.size() > 0) {
                    Log.i(TAG, "I see some beacon(s).");
                    StringBuilder builder = new StringBuilder();
                    for (Beacon beacon : beacons) {
                        builder.append(beacon.getBluetoothName() + " "
                                + beacon.getBluetoothAddress() + " "
                                + beacon.getRssi() + " "
                                + beacon.getDistance() + "(m)" + "\n");
                    }
                    final String output = builder.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextViewRangingResults.setText("New batch:\n" + output
                                    + mTextViewRangingResults.getText());
                        }
                    });
                }
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(regionBeacon);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
