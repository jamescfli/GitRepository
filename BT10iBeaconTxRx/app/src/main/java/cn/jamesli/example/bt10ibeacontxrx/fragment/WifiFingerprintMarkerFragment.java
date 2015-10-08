package cn.jamesli.example.bt10ibeacontxrx.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.jamesli.example.bt10ibeacontxrx.R;
import cn.jamesli.example.bt10ibeacontxrx.adapter.WifiFingerprintDisplayItem;
import cn.jamesli.example.bt10ibeacontxrx.adapter.WifiFingerprintListAdapter;
import cn.jamesli.example.bt10ibeacontxrx.filestorage.LogToFile;
import cn.jamesli.example.bt10ibeacontxrx.nicespinner.NiceSpinner;
import cn.jamesli.example.bt10ibeacontxrx.wifitutil.WifiFingerprint;

public class WifiFingerprintMarkerFragment extends Fragment {
    private static final String TAG = "WifiFpMarkerFragment";
    private static final String SAVE_FILE_PREFIX = "Fingerprint";
    private static final String SAVE_FILE_APPENDIX = ".csv";

    // for WiFi
    private WifiManager mWifiManager;
    private WifiScanReceiver mWifiScanReceiver; // customized broadcast receiver for FP marking
    private int mWifiScanSampleTotal;   // select from 5, 10, and 20 samples per FP
    private int mWifiScanSampleCounter;
    private boolean isWifiScanRequested;   // sometime, startScan() gives more results than required, lazy solution

    // for UI
    private NiceSpinner mSpinnerNumberOfSamples;
//    private NiceSpinner mSpinnerSmoothMethod;
    private EditText mEditTextFingerprintName;
    private Button mButtonMark;
    private Button mButtonSave;
    private static final Integer[] WIFI_FINGERPRINT_NO_OF_SAMPLES = new Integer[] { 5, 10, 20 };
    private NumberProgressBar mNumberProgressBarWifiScan;
    private TextView mTextViewWifiStatus;
    private static String mFragmentTitle;
    private RecyclerView mRecyclerViewWifiFingerprintDisplay;

    // save Scan result to
    private WifiFingerprint mWifiFingerprint;

    // for file storage
    private static final DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);

    // for displaying fingerprint in RecyclerView
    private RecyclerView.Adapter mFingerprintListAdapter;
    private List<WifiFingerprintDisplayItem> mFingerprintDisplayList;

    public static WifiFingerprintMarkerFragment newInstance(String fragmentTitle) {
        mFragmentTitle = fragmentTitle;
        return new WifiFingerprintMarkerFragment();
    }

    public WifiFingerprintMarkerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        mWifiScanReceiver = new WifiScanReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewWifiFingerprintMarker = inflater.inflate(R.layout.fragment_wifi_fingerprint_marker,
                container, false);
        getActivity().setTitle(mFragmentTitle);
        initiateUi(viewWifiFingerprintMarker);
        return viewWifiFingerprintMarker;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        mButtonMark.setEnabled(true);
        mButtonSave.setEnabled(false);
//        mEditTextFingerprintName.setText("");
        getActivity().registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        isWifiScanRequested = false;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        getActivity().unregisterReceiver(mWifiScanReceiver);
        isWifiScanRequested = false;
    }

    private void initiateUi(View view) {
        mSpinnerNumberOfSamples = (NiceSpinner) view.findViewById(R.id.spinner_wifi_fingerprint_no_of_samples);
        ArrayAdapter<Integer> adapterNoOfSamples = new ArrayAdapter<Integer>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, WIFI_FINGERPRINT_NO_OF_SAMPLES);
        mSpinnerNumberOfSamples.setAdapter(adapterNoOfSamples);
        mEditTextFingerprintName = (EditText) view.findViewById(R.id.edit_text_wifi_fingerprint_name);
        mButtonMark = (Button) view.findViewById(R.id.button_wifi_fingerprint_mark);
        mButtonMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);
                }
                mNumberProgressBarWifiScan.setVisibility(View.VISIBLE);
                mNumberProgressBarWifiScan.setProgress(0);
                mWifiScanSampleTotal = Integer.valueOf(mSpinnerNumberOfSamples.getText().toString());
                mWifiScanSampleCounter = mWifiScanSampleTotal;
                String fingerprintName = mEditTextFingerprintName.getText().toString();
                if (fingerprintName == null || fingerprintName.length() == 0) {
                    // use default name
                    fingerprintName = getString(R.string.wifi_fingerprint_default_name);
                }
                mWifiFingerprint = new WifiFingerprint(fingerprintName);
                if (mWifiManager.startScan()) {
                    isWifiScanRequested = true;
                    mButtonMark.setEnabled(false);
                    mButtonSave.setEnabled(false);
                    mSpinnerNumberOfSamples.setEnabled(false);
                    mEditTextFingerprintName.setEnabled(false);
                    mTextViewWifiStatus.setText("Status: batched scan stared.");
                }
            }
        });
        mButtonSave = (Button) view.findViewById(R.id.button_wifi_fingerprint_save);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumberProgressBarWifiScan.setVisibility(View.INVISIBLE);
                mNumberProgressBarWifiScan.setProgress(0);
                if (mWifiFingerprint.isRssiTableEmpty()) {
                    mTextViewWifiStatus.setText("Error: RSSI table has no record!");
                } else {
                    saveFingerprintWithAllRssiValues();
                }
            }
        });
        mNumberProgressBarWifiScan = (NumberProgressBar) view.findViewById
                (R.id.progress_bar_wifi_fingerprint_scan);
        mTextViewWifiStatus = (TextView) view.findViewById
                (R.id.text_view_wifi_fingerprint_mark_status);
        mRecyclerViewWifiFingerprintDisplay = (RecyclerView) view.findViewById
                (R.id.recycler_view_wifi_fingerprint_display);
        // use a linear layout manager
        mRecyclerViewWifiFingerprintDisplay.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void saveFingerprintWithAllRssiValues() {
        Date dateTimeForNow = new Date();
        String filename = SAVE_FILE_PREFIX + dateFormat.format(dateTimeForNow.getTime())
                + SAVE_FILE_APPENDIX;
        LogToFile mLogToFile = new LogToFile(getActivity(), filename);
        // achieve the row and column set of the RSSI table
        final Set<Long> rowSetRssiTable = mWifiFingerprint.getTimestampSet();
        final Set<String> columnSetRssiTable = mWifiFingerprint.getMacAddressSet();
        // input fingerprint name
        mLogToFile.write(mWifiFingerprint.getNameOfFingerprint());
        // input MAC address of all scanned AP
        StringBuilder lineOfMacAddresses = new StringBuilder();
        String prefix = "";
        for (String macAddress : columnSetRssiTable) {
            lineOfMacAddresses.append(prefix);
            prefix = ",";
            lineOfMacAddresses.append(macAddress);
        }
//        // or use Guava method - Joiner
//        String lineOfMacAddresses = Joiner.on(',').useForNull("N/A")
//                .join(Arrays.asList(columnSetRssiTable));
        mLogToFile.write(lineOfMacAddresses.toString());
        // input RSSI content
        String rssiValueAsString;
        for (Long timestamp : rowSetRssiTable) {
            StringBuilder lineOfRssi = new StringBuilder();
            String lineOfRssiPrefix = "";
            for (String macAddress : columnSetRssiTable) {
                lineOfRssi.append(lineOfRssiPrefix);
                lineOfRssiPrefix = ",";
                rssiValueAsString = mWifiFingerprint.getRssi(timestamp, macAddress) == null ?
                        "" : String.valueOf(mWifiFingerprint.getRssi(timestamp, macAddress));
//                            rssiValueAsString = mWifiFingerprint.getRssi(timestamp, macAddress) == null ?
//                                    Constants.WIFI_RX_SENSITIVITY :     // use -100dBm default value
//                                    mWifiFingerprint.getRssi(timestamp, macAddress);
                lineOfRssi.append(rssiValueAsString);
            }
            mLogToFile.write(lineOfRssi.toString());
        }
        if (mLogToFile.close()) {
            mTextViewWifiStatus.setText("Message: whole RSSI table saved.");
            mWifiFingerprint.clear();
            mButtonSave.setEnabled(false);
        } else {
            mTextViewWifiStatus.setText("Error: file saving failed");
        }
    }

    private void saveFingerprintWithAverageRssiValues() {
        // TODO
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        List<ScanResult> wifiScanResultList;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isWifiScanRequested) {      // make sure it is the requested one
                wifiScanResultList = mWifiManager.getScanResults();
                mWifiFingerprint.putOneScanList(wifiScanResultList);
                if (--mWifiScanSampleCounter > 0) {     // if batch scan has not been finished
                    mNumberProgressBarWifiScan.setProgress((int) (mWifiScanSampleTotal
                            - mWifiScanSampleCounter) * 100 / mWifiScanSampleTotal);
                    mWifiManager.startScan();
                } else {    // batch scan finished
                    mNumberProgressBarWifiScan.setProgress(100);
                    mButtonMark.setEnabled(true);
                    mButtonSave.setEnabled(true);
                    mSpinnerNumberOfSamples.setEnabled(true);
                    mEditTextFingerprintName.setEnabled(true);
                    mTextViewWifiStatus.setText("Status: batch scan finished.");
                    isWifiScanRequested = false;
                    deriveWifiFingerprintDisplayDataList();
                    displayWifiFingerprintInRecyclerView();
                }
            }
        }
    }

    private void displayWifiFingerprintInRecyclerView() {
        mFingerprintListAdapter = new WifiFingerprintListAdapter(mFingerprintDisplayList);
        mRecyclerViewWifiFingerprintDisplay.setAdapter(mFingerprintListAdapter);
    }

    private void deriveWifiFingerprintDisplayDataList() {
        mFingerprintDisplayList = new ArrayList<>();
        final Set<Long> rowSetRssiTable = mWifiFingerprint.getTimestampSet();
        final Set<String> columnSetRssiTable = mWifiFingerprint.getMacAddressSet();
        // scan for all MAC address
        for (String macAddress : columnSetRssiTable) {
            int effScanCounter = 0;
            float avgRssiValue = 0;
            for (Long timestamp : rowSetRssiTable) {
                if (mWifiFingerprint.getRssi(timestamp, macAddress) != null) {
                    effScanCounter++;
                    avgRssiValue = avgRssiValue + mWifiFingerprint
                            .getRssi(timestamp, macAddress);
                }
            }
            avgRssiValue = Math.round(avgRssiValue /  effScanCounter * 10) / 10f;
            mFingerprintDisplayList.add(new WifiFingerprintDisplayItem(macAddress,
                    String.valueOf(effScanCounter) + "/" + mWifiScanSampleTotal,
                    String.valueOf(avgRssiValue) + "dBm"));
        }
    }
}
