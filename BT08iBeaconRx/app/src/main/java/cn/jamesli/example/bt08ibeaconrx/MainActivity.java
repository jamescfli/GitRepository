package cn.jamesli.example.bt08ibeaconrx;

import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.logging.Loggers;
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private BeaconManager mBeaconManager;
    private TextView mTextViewStatus;
    private Spinner mSpinnerFilter;
    private TextView mTextViewParas;
//    private EditText mEditTextParas;
    private Button buttonStart;
    private Button buttonStop;
    private static final String[] arrayFilters ={
            "RunningAverageRssiFilter",
            "ArmaRssiFilter",
            "NoProcessFilter"
    };
    private ArrayAdapter<String> adapterFilters;
    private double iFilterParas;
    private Beacon beaconFirst;
    private Region regionBeacon;
    private Date mDate = new Date();
//    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpinnerFilter = (Spinner)findViewById(R.id.spFilter);
        // simple_spinner_item: the resource ID for a layout file containing a TextView
        adapterFilters = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                arrayFilters);
        // set the layout resource to create the drop down views
        adapterFilters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // set the Adapter used to provide the data which backs this Spinner
        mSpinnerFilter.setAdapter(adapterFilters);
        // set the currently selected item.
        mSpinnerFilter.setSelection(2);     // position = 2, i.e. NoProcessFilter
        mSpinnerFilter.setOnItemSelectedListener(new SpinnerSelectedListenerFilter());

        mTextViewParas = (TextView)findViewById(R.id.txPar);
//        mEditTextParas = (EditText)findViewById(R.id.edPar);
//        edA = (EditText)findViewById(R.id.edA);
//        edB = (EditText)findViewById(R.id.edB);
//        edC = (EditText)findViewById(R.id.edC);

        buttonStart = (Button) findViewById(R.id.button_start);
        buttonStop = (Button) findViewById(R.id.button_stop);
        buttonStop.setEnabled(false);


        mBeaconManager = BeaconManager.getInstanceForApplication(this);
//        // default AltBeacom with beac header has already been added to parser by default
//        BeaconParser altBeacon = new BeaconParser()
//                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
//        mBeaconManager.getBeaconParsers().add(altBeacon);
        mBeaconManager.setRssiFilterImplClass(NoProcessFilter.class);
        // DEFAULT_FOREGROUND_SCAN_PERIOD = 1100(ms), in order to catch 10Hz beacons, we set
        mBeaconManager.setForegroundScanPeriod(110);
        mBeaconManager.bind(this);   // unbind in onDestroy()
        LogManager.setLogger(Loggers.verboseLogger());
        LogManager.setVerboseLoggingEnabled(true);
//        ArmaRssiFilter.setDEFAULT_ARMA_SPEED(0.25);
        String uuidString = "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6";
        Identifier uuid = Identifier.parse(uuidString);
        Identifier majorId = Identifier.parse("1");
        Identifier minorId = Identifier.parse("2");
        regionBeacon = new Region("myRangingRegion", uuid, majorId, minorId);
        // or
        regionBeacon = new Region("myRangingRegion", null, null, null);

        mTextViewStatus = (TextView)findViewById(R.id.tvStatus);
//        a = 0.42093;
//        edA.setText(String.valueOf(a));
//        b = 6.9476;
//        edB.setText(String.valueOf(b));
//        c = 0.54992;
//        edC.setText(String.valueOf(c));
    }

    class SpinnerSelectedListenerFilter implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
            mTextViewStatus.setText(arrayFilters[arg2]);
            switch (arg2){
                case 0:
                    mBeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
                    mTextViewParas.setText("Average:");
                    iFilterParas = 5000;
//                    mEditTextParas.setText(String.valueOf(iFilterParas));
                    break;
                case 1:
                    mBeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
                    mTextViewParas.setText("ARMA:");
                    iFilterParas = 0.25;
//                    mEditTextParas.setText(String.valueOf(iFilterParas));
                    break;
                case 2:
                    mBeaconManager.setRssiFilterImplClass(NoProcessFilter.class);
                    mTextViewParas.setText("NoProcessFilter:");
                    iFilterParas = 0;
//                    mEditTextParas.setText("Para N/A");
                    break;
                default:
                    mBeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
                    mTextViewParas.setText("Default filter: ARMA");
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

//    public void btUpdatecoefficient(View view)
//    {
//        a = Double.parseDouble(edA.getText().toString());
//        b = Double.parseDouble(edB.getText().toString());
//        c = Double.parseDouble(edC.getText().toString());
//        Beacon.setDistanceCalculator(new ModelCustomizedDistanceCalculator(a, b, c));
//    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i("MainActivity", "onBeaconServiceConnect callback is initiated");

        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            Log.i("MainActivity", "didRangeBeaconsInRegion() beacons.size() = "
                    + String.valueOf(beacons.size()));
//                logToDisplay("beacon size: " + beacons.size(), mTextViewStatus);
            if (beacons.size() > 0) {
                beaconFirst = beacons.iterator().next();
                logToDisplay(beaconFirst.getId1() + "\nRSS: " + beaconFirst.getRssi()
                        + "\nTXPower: " + beaconFirst.getTxPower()
                        + "\nDistance: " + beaconFirst.getDistance(), mTextViewStatus);
            }
            }
        });
//        // then start the ranging action
//        try {
//            mBeaconManager.startRangingBeaconsInRegion(regionBeacon);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    private void logToDisplay(final String line, final TextView tv) {
        runOnUiThread(new Runnable() {
            public void run() {
                tv.setText(line + "\n" + tv.getText().toString());    // refresh with new measurement
            }
        });
    }
    public void buttonStart(View view) {
        NoProcessFilter.clear();    // clear historical measurements
        try {
            mBeaconManager.startRangingBeaconsInRegion(regionBeacon);
            buttonStart.setEnabled(false);
            buttonStop.setEnabled(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void buttonStop(View view) {
        try {
            mBeaconManager.stopRangingBeaconsInRegion(regionBeacon);
            buttonStart.setEnabled(true);
            buttonStop.setEnabled(false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Bad practice: show save it in onBeaconServiceConnect() not NoProcessFilter
        ArrayList<Integer> rssiResultArray = NoProcessFilter.getRssiResultArray();
        mTextViewStatus.setText("length: " + rssiResultArray.size());
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);
        String filename = "CurrentStatus"+dateFormat.format(mDate.getTime())+".csv";
        LogToFile mLogToFile = new LogToFile(this, filename);
        for(int i = 0; i < rssiResultArray.size(); i++) {
            mLogToFile.write(rssiResultArray.get(i).toString());
        }
        mLogToFile.close();
    }
}
