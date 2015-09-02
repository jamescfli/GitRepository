package cn.jamesli.example.bt10ibeacontxrx.fragment;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RxFragment extends Fragment implements BeaconConsumer {
    private static final String TAG = "RxFragment";

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RxFragment.
     */
    public static RxFragment newInstance() {
        RxFragment fragment = new RxFragment();
        return fragment;
    }

    public RxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rssiScanResultArray = new ArrayList<>();
        // Beacon scan related settings
        mBeaconManager = BeaconManager.getInstanceForApplication(getActivity()); // initiate BeaconManager
        initiateBeaconRegion();
        mBeaconManager.bind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRxFragment = inflater.inflate(R.layout.fragment_rx, container, false);
        getActivity().setTitle("Receiver");
        initiateUi(viewRxFragment);
        return viewRxFragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mBeaconManager.stopRangingBeaconsInRegion(mBeaconRegion);
            resumeButtonsToStartMode();
            mTextViewStatus.setText("Status: scanning has been stopped, with totally, "
                    + rssiScanResultArray.size() + " samples.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
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

    private void initiateUi(View view) {
        mSpinnerSampleRate = (NiceSpinner) view.findViewById(R.id.spinner_rx_sample_rate);
        mSpinnerSampleRate.attachDataSource(sampleRateItems);
        mSpinnerSampleRate.setSelectedIndex(0); // default 10Hz

        mSpinnerAveraging = (NiceSpinner) view.findViewById(R.id.spinner_rx_averaging);
        mSpinnerAveraging.attachDataSource(averagingMethods);
        mSpinnerAveraging.setSelectedIndex(2); // default No Process

        mSpinnerNumberOfSamples = (NiceSpinner) view.findViewById(R.id.spinner_rx_number_of_samples);
        mSpinnerNumberOfSamples.attachDataSource(numberOfSamplesArray);
        mSpinnerNumberOfSamples.setSelectedIndex(0); // default 20 samples

        mButtonStart = (Button) view.findViewById(R.id.button_rx_start);
        mButtonStop = (Button) view.findViewById(R.id.button_rx_stop);
        mButtonSave = (Button) view.findViewById(R.id.button_rx_save);
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
                    Toast.makeText(getActivity(), "Please turn Bluetooth on!", Toast.LENGTH_LONG).show();
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
                    LogToFile mLogToFile = new LogToFile(getActivity(), filename);
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
        mTextViewStatus = (TextView) view.findViewById(R.id.text_view_rx_status);
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

    private boolean isBluetoothEnabled() {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
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
                    getActivity().runOnUiThread(new Runnable() {
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

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int mode) {
        return getActivity().bindService(intent, serviceConnection, mode);
    }
}
