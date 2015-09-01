package cn.jamesli.example.bt10ibeacontxrx.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.jamesli.example.bt10ibeacontxrx.R;
import cn.jamesli.example.bt10ibeacontxrx.filestorage.LogToFile;
import cn.jamesli.example.bt10ibeacontxrx.filter.NoProcessFilter;
import cn.jamesli.example.bt10ibeacontxrx.nicespinner.NiceSpinner;


public class RxActivity extends AppCompatActivity implements BeaconConsumer {
    private static final String TAG = "RxActivity";

    // for UI
    private NiceSpinner mSpinnerSampleRate;
    private static final List<String> sampleRateItems
            = new ArrayList<>(Arrays.asList("10Hz", "3Hz", "1Hz"));
    private NiceSpinner mSpinnerAveraging;
    private static final List<String> averagingMethods
            = new ArrayList<>(Arrays.asList("Buffered Avg", "ARMA", "No Process"));
    private NiceSpinner mSpinnerNumberOfSamples;
    private static final List<Integer> numberOfSamplesArray
            = new ArrayList<>(Arrays.asList(20, 50, 100));

    private Button mButtonStart;
    private Button mButtonStop;
    private Button mButtonSave;
    private TextView mTextViewStatus;

    // for AltBeacon Scanner
    private static BeaconManager mBeaconManager;
    private static Region mBeaconRegion;
    private static final long FOREGROUND_SCAN_PERIOD_10HZ = 110;    // in ms
    private static final long FOREGROUND_SCAN_PERIOD_3HZ = 340;
    private static final long FOREGROUND_SCAN_PERIOD_1HZ = 1100;
//    private static long foregroundScanPeriod = FOREGROUND_SCAN_PERIOD_10HZ; // default 10Hz beacon
    private static final long foregroundScanBetweenPeriod = 0;
//    private static enum AverageFilter { BUFFERED_AVG, ARMA, NO_PROCESS };
//    private static AverageFilter averageFilterType;
    private static int numberOfSamplesForOneScan;

    // for Scan Results and File storage
    private ArrayList<Integer> rssiScanResultArray;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx);

        setTitle("Receiver");

        initiateUi();

        // Beacon scan related settings
        mBeaconManager = BeaconManager.getInstanceForApplication(this); // initiate BeaconManager
        initiateBeaconRegion();
        mBeaconManager.bind(this);

        rssiScanResultArray = new ArrayList<>();
    }

    private void initiateUi() {
        mSpinnerSampleRate = (NiceSpinner) findViewById(R.id.spinnerSampleRate);
        mSpinnerSampleRate.attachDataSource(sampleRateItems);
        mSpinnerSampleRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
////                        // for Debug
////                        Toast.makeText(RxActivity.this, "10Hz", Toast.LENGTH_SHORT).show();
//                        foregroundScanPeriod = FOREGROUND_SCAN_PERIOD_10HZ;
//                        break;
//                    case 1:
////                        Toast.makeText(RxActivity.this, "3Hz", Toast.LENGTH_SHORT).show();
//                        foregroundScanPeriod = FOREGROUND_SCAN_PERIOD_3HZ;
//                        break;
//                    case 2:
////                        Toast.makeText(RxActivity.this, "1Hz", Toast.LENGTH_SHORT).show();
//                        foregroundScanPeriod = FOREGROUND_SCAN_PERIOD_1HZ;
//                        break;
//                    default:
//                        // n.a.
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // n.a.
            }
        });
        mSpinnerSampleRate.setSelectedIndex(0); // default 10Hz

        mSpinnerAveraging = (NiceSpinner) findViewById(R.id.spinnerAveraging);
        mSpinnerAveraging.attachDataSource(averagingMethods);
        mSpinnerAveraging.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // did the following in initiateBeaconScanner() instead
//                switch (position) {
//                    case 0:
//                        averageFilterType = AverageFilter.BUFFERED_AVG;
//                        break;
//                    case 1:
//                        averageFilterType = AverageFilter.ARMA;
//                        break;
//                    case 2:
//                        averageFilterType = AverageFilter.NO_PROCESS;
//                        break;
//                    default:
//                        // n.a.
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // n.a.
            }
        });
        mSpinnerAveraging.setSelectedIndex(2); // default No Process

        mSpinnerNumberOfSamples = (NiceSpinner) findViewById(R.id.spinnerNumberOfSamples);
        mSpinnerNumberOfSamples.attachDataSource(numberOfSamplesArray);
        mSpinnerNumberOfSamples.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // did the following in initiateBeaconScanner() instead
//                numberOfSamplesForOneScan = numberOfSamplesArray.get(position);
//                Toast.makeText(RxActivity.this, "Number of Samples = " + numberOfSamplesForOneScan,
//                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // n.a.
            }
        });
        mSpinnerNumberOfSamples.setSelectedIndex(0); // default 20 samples

        mButtonStart = (Button) findViewById(R.id.button_start);
        mButtonStop = (Button) findViewById(R.id.button_stop);
        mButtonSave = (Button) findViewById(R.id.button_save);
        resumeButtonsToStartMode();     // Button enabled or disabled
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBluetoothEnabled()) {
                    initiateBeaconScanner();    // update parameters according to Spinner selections
                    // clear data for new scan results
                    rssiScanResultArray.clear();
                    try {
                        mBeaconManager.startRangingBeaconsInRegion(mBeaconRegion);
                        resumeButtonsToStopMode();
                        mTextViewStatus.setText("Status: ranging has been started.");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(RxActivity.this, "Please turn on Bluetooth service!", Toast.LENGTH_LONG).show();
                }
            }
        });
        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mBeaconManager.stopRangingBeaconsInRegion(mBeaconRegion);
                    resumeButtonsToStartMode();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rssiScanResultArray == null || rssiScanResultArray.size() <= 0) {
                    mTextViewStatus.setText("Error: No scan results (or array.size() == 0) so far!");
                } else {
                    // Note Date will update to current time in this way, Unique Date will generate one storage file
                    Date dateTimeForNow = new Date();
                    String filename = "RssiResult" + dateFormat.format(dateTimeForNow.getTime())+".csv";
                    LogToFile mLogToFile = new LogToFile(RxActivity.this, filename);
                    for(int i = 0; i < rssiScanResultArray.size(); i++) {
                        mLogToFile.write(rssiScanResultArray.get(i).toString());
                    }
                    if (mLogToFile.close()) {
                        mTextViewStatus.setText("Message: RSSI data " + rssiScanResultArray.size()
                                + "(samples) was successfully saved to file.");
                        // delete old data as long as it is successfully saved
                        rssiScanResultArray.clear();    // to prevent duplicate savings
                    } else {
                        mTextViewStatus.setText("Error: File saving failed!");
                    }
                }
            }
        });
        mTextViewStatus = (TextView) findViewById(R.id.text_view_status);
    }

    private boolean isBluetoothEnabled() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            final BluetoothManager bluetoothManager = (BluetoothManager) this
                    .getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private void initiateBeaconRegion() {
        String uuidString = "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6";
        Identifier uuid = Identifier.parse(uuidString);
        Identifier majorId = Identifier.parse("1");
        Identifier minorId = Identifier.parse("2");
        mBeaconRegion = new Region("myRangingRegion", uuid, majorId, minorId);
    }

    private void initiateBeaconScanner() {
        switch (mSpinnerSampleRate.getSelectedIndex()) {
            case 0:
                mBeaconManager.setForegroundScanPeriod(FOREGROUND_SCAN_PERIOD_10HZ);
                break;
            case 1:
                mBeaconManager.setForegroundScanPeriod(FOREGROUND_SCAN_PERIOD_3HZ);
                break;
            case 2:
                mBeaconManager.setForegroundScanPeriod(FOREGROUND_SCAN_PERIOD_1HZ);
                break;
            default:
                throw new IllegalArgumentException("Wrong sampling rate selection");
        }
        mBeaconManager.setForegroundBetweenScanPeriod(foregroundScanBetweenPeriod);
        switch (mSpinnerAveraging.getSelectedIndex()) {
            case 0:
                mBeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
                break;
            case 1:
                mBeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
                break;
            case 2:
                mBeaconManager.setRssiFilterImplClass(NoProcessFilter.class);
                break;
            default:
                throw new IllegalArgumentException("Wrong RSSI averaging scheme selection");
        }
        // numberOfSamplesForOneScan was already set by Spinner
        numberOfSamplesForOneScan = numberOfSamplesArray.get(mSpinnerNumberOfSamples.getSelectedIndex());
    }

    @Override
    protected void onPause() {
        super.onPause();
        resumeButtonsToStartMode();

    }

    private void resumeButtonsToStartMode() {
        // resume to starting mode
        mButtonStart.setEnabled(true);
        mButtonStop.setEnabled(false);
        mButtonSave.setEnabled(true);
    }

    private void resumeButtonsToStopMode() {
        // resume to starting mode
        mButtonStart.setEnabled(false);
        mButtonStop.setEnabled(true);
        mButtonSave.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rx, menu);
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
        Log.i(TAG, "onBeaconServiceConnect callback is initiated");
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i(TAG, "didRangeBeaconsInRegion() beacons.size() = "
                        + String.valueOf(beacons.size()));
                if (beacons.size() > 0) {
                    // assume there is only one interested beacon
                    final Beacon beaconFirst = beacons.iterator().next();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rssiScanResultArray.add(beaconFirst.getRssi());
                            int totalNumberOfSamples = rssiScanResultArray.size();
                            mTextViewStatus.setText("Counter: " + totalNumberOfSamples);
                            if (totalNumberOfSamples >= numberOfSamplesForOneScan) {
                                // get enough samples, stop scanning and resume buttons
                                try {
                                    mBeaconManager.stopRangingBeaconsInRegion(mBeaconRegion);
                                    resumeButtonsToStartMode();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
