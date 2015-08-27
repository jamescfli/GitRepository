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
    private EditText mEditTextParas;
//    private EditText edA,edB,edC;
    private Button buttonStart;
    private Button buttonStop;
    private static final String[] arrayFilters ={
            "RunningAverageRssiFilter",
            "ArmaRssiFilter",
            "NoProcessFilter"
    };
    private ArrayAdapter<String> adapterFilters;
    private double iFilterParas;
//    private double a,b,c;
    private Beacon beaconFirst;
    private Region regionBeacon = new Region("myRegionUniqueId", null, null, null);
    private LogToFile mLogToFile;
//    Time time = new Time();   // deprecated
    private Date mDate = new Date();


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
        mEditTextParas = (EditText)findViewById(R.id.edPar);
//        edA = (EditText)findViewById(R.id.edA);
//        edB = (EditText)findViewById(R.id.edB);
//        edC = (EditText)findViewById(R.id.edC);

        buttonStart = (Button) findViewById(R.id.button_start);
        buttonStop = (Button) findViewById(R.id.button_stop);
        buttonStop.setEnabled(false);


        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.bind(this);   // unbind in onDestroy()
//        mBeaconManager.setDebug(true);  // deprecated
        LogManager.setLogger(Loggers.verboseLogger());
        LogManager.setVerboseLoggingEnabled(true);
        if (mBeaconManager.isBound(this))
            mBeaconManager.setBackgroundMode(false);
        mBeaconManager.setRssiFilterImplClass(NoProcessFilter.class);
        ArmaRssiFilter.setDEFAULT_ARMA_SPEED(0.25);

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
                    mEditTextParas.setText(String.valueOf(iFilterParas));
                    break;
                case 1:
                    mBeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
                    mTextViewParas.setText("ARMA:");
                    iFilterParas = 0.25;
                    mEditTextParas.setText(String.valueOf(iFilterParas));
                    break;
                case 2:
                    mBeaconManager.setRssiFilterImplClass(NoProcessFilter.class);
                    mTextViewParas.setText("NoFilter:");
                    iFilterParas = 0;
                    mEditTextParas.setText(String.valueOf(iFilterParas));
                    break;
                default:
                    mBeaconManager.setRssiFilterImplClass(ArmaRssiFilter.class);
                    mTextViewParas.setText("ARMA:");
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
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
                Log.i("MainActivity", "didRangeBeaconsInRegion() " + String.valueOf(beacons.size()));
                logToDisplay("beacon size: " + beacons.size(), mTextViewParas);
                // TODO figure out why beacons.size() == 0
                if (beacons.size() > 0) {
                    beaconFirst = beacons.iterator().next();
                    logToDisplay(beaconFirst.getId1() + "\nRSS: " + beaconFirst.getRssi()
                            + "\nTXPower: " + beaconFirst.getTxPower()
                            + "\nDistance: " + beaconFirst.getDistance(), mTextViewStatus);
                }
            }
        });
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

        ArrayList<Integer> tmp = NoProcessFilter.getRssiResultArray();
        mTextViewStatus.setText("length: " + tmp.size());
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);
        String filename = "CurrentStatus"+dateFormat.format(mDate.getTime())+".csv";
        mLogToFile = new LogToFile(this, filename);
        for(int i = 0; i < tmp.size(); i++) {
            mLogToFile.write(tmp.get(i).toString());
        }
        mLogToFile.close();
    }
}
