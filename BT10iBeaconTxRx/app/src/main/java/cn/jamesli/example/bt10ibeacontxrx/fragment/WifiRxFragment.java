package cn.jamesli.example.bt10ibeacontxrx.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jamesli.example.bt10ibeacontxrx.R;
import cn.jamesli.example.bt10ibeacontxrx.filestorage.LogToFile;
import cn.jamesli.example.bt10ibeacontxrx.nicespinner.NiceSpinner;
import cn.jamesli.example.bt10ibeacontxrx.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WifiRxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WifiRxFragment extends Fragment {
    private static final String TAG = "WifiRxFragment";

    // for WiFi
    private WifiManager mWifiManager;
    // Note: it is kind of risky to put SSID as the key due to duplicating names, better use BSSID
    private String mTargetApBssid = null; // work as a target AP (to be measured) unique ID
    private WifiScanReceiver mWifiScanReceiver;
    private Map<String, ScanResult> mWifiKeyScanResultMap;  // BSSID -> ScanResult
    private int mWifiScanSampleTotal;       // select from 5, 10, 20 samples per point
    private int mWifiScanSampleCounter;     // count from mWifiScanSampleTotal down to 0
    private boolean isWifiScanRequested;   // sometime, startScan() gives more results than required, lazy solution by toggle


    // for UI
    private NiceSpinner mSpinnerNumberOfSamples;    // per measure point, 5, 10, 20
    private NiceSpinner mSpinnerListOfAps;          // candidate AP list
    private TextView mTextViewMacAddress;
    private Button mButtonWifiScan;
    private Button mButtonWifiStart;
    private Button mButtonWifiSave;
    private static final Integer[] WIFI_RX_NO_OF_SAMPLES_PER_POINT = new Integer[] { 5, 10, 20 };
    private NumberProgressBar mNumberProgressBar;
    private TextView mTextViewWifiRxStatus;
    private static String mFragmentTitle;

    // for Scan Results and File storage
    private List<Integer> mWifiScanBatchedResult;

    public static WifiRxFragment newInstance(String fragmentTitle) {
        mFragmentTitle = fragmentTitle;
        return new WifiRxFragment();
    }

    public WifiRxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        mWifiScanReceiver = new WifiScanReceiver();  // register in onResume, unregister in onPause
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewWifiRxFragment = inflater.inflate(R.layout.fragment_wifi_rx, container, false);
        getActivity().setTitle(mFragmentTitle);
        initiateUi(viewWifiRxFragment);
        return viewWifiRxFragment;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        // need to scan first, in order to measure multiple samples
        mButtonWifiStart.setEnabled(false);
//        mButtonWifiStop.setEnabled(false);
        mButtonWifiSave.setEnabled(false);
        // MAC address is not available
        mTextViewMacAddress.setText("N/A");
        // register WifiScanReceiver for scan results ready action
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
        mSpinnerNumberOfSamples = (NiceSpinner) view.findViewById(R.id.spinner_wifi_no_of_samples);
        ArrayAdapter<Integer> adapterNoOfSamples = new ArrayAdapter<Integer>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, WIFI_RX_NO_OF_SAMPLES_PER_POINT);
        mSpinnerNumberOfSamples.setAdapter(adapterNoOfSamples);
        mSpinnerListOfAps = (NiceSpinner) view.findViewById(R.id.spinner_wifi_interested_aps);
        mSpinnerListOfAps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMacAddressOfShownAp(view);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mTextViewMacAddress = (TextView) view.findViewById(R.id.text_view_wifi_rx_mac_address_of_ap);
        mButtonWifiScan = (Button) view.findViewById(R.id.button_wifi_rx_scan);
        mButtonWifiStart = (Button) view.findViewById(R.id.button_wifi_rx_start);
//        mButtonWifiStop = (Button) view.findViewById(R.id.button_wifi_rx_stop);
        mButtonWifiSave = (Button) view.findViewById(R.id.button_wifi_rx_save);
        mButtonWifiScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumberProgressBar.setVisibility(View.INVISIBLE);
                mNumberProgressBar.setProgress(0);
                mButtonWifiSave.setEnabled(false);
                // turn on wifi receiving if not enabled
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);
                }
                // nullify target AP to signify it is Scan button being pressed
                mTargetApBssid = null;
                if (mWifiManager.startScan()) {
                    isWifiScanRequested = true;
                    // disable Scan and Start button to avoid multiple scan
                    mButtonWifiScan.setEnabled(false);
                    mButtonWifiStart.setEnabled(false);
                    mTextViewWifiRxStatus.setText("Status: single scan has been started.");
                } else {
                    Toast.makeText(getActivity(), "Failed to start WiFi scan",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mButtonWifiStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTargetApBssid = null;
                String targetApSsid = mSpinnerListOfAps.getText().toString();
                for (ScanResult scanResult : mWifiKeyScanResultMap.values()) {
                    if (targetApSsid.equals(scanResult.SSID)) {
                        mTargetApBssid = scanResult.BSSID;
                        break;
                    }
                }
                if (mTargetApBssid != null) {
                    // there has been a list of APs, we are only interested in mTargetApSsid AP
                    // turn on wifi receiving if not enabled
                    if (!mWifiManager.isWifiEnabled()) {
                        mWifiManager.setWifiEnabled(true);
                    }
                    // prepare the number progress bar
                    mNumberProgressBar.setVisibility(View.VISIBLE);
                    mNumberProgressBar.setProgress(0);
                    // retrieve how many values per batch scan are required
                    mWifiScanSampleTotal = Integer.valueOf(mSpinnerNumberOfSamples
                            .getText().toString());
                    mWifiScanSampleCounter = mWifiScanSampleTotal;  // start from the total number
                    // prepare the result holder
                    mWifiScanBatchedResult = new ArrayList<Integer>();
                    if (mWifiManager.startScan()) {
                        isWifiScanRequested = true;
                        // disable Scan and Start button to avoid multiple scan
                        mButtonWifiScan.setEnabled(false);
                        mButtonWifiStart.setEnabled(false);
                        // disable spinners as well
                        mSpinnerNumberOfSamples.setEnabled(false);
                        mSpinnerListOfAps.setEnabled(false);
                        // update status text view
                        mTextViewWifiRxStatus.setText("Status: batched scan has been stared.");
                    }
                } else {
                    mTextViewWifiRxStatus.setText("Status: nothing in the list of APs, " +
                            "or corresponding SSID was not in AP list. Press SCAN button first!");
                }
            }
        });
        mButtonWifiSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumberProgressBar.setVisibility(View.INVISIBLE);
                mNumberProgressBar.setProgress(0);
                if (mWifiScanBatchedResult == null || mWifiScanBatchedResult.size() <= 0) {
                    mTextViewWifiRxStatus.setText("Error: No scan results available!");
                } else {
                    String SAVE_FILE_PREFIX = "WifiRssiResult";
                    String SAVE_FILE_APPENDIX = ".csv";
                    LogToFile mLogToFile = new LogToFile(getActivity(), SAVE_FILE_PREFIX, SAVE_FILE_APPENDIX);
                    StringBuilder tempWifiBatchScanResult = new StringBuilder();
                    for(int i = 0, SIZE = mWifiScanBatchedResult.size(); i < SIZE; i++) {
                        tempWifiBatchScanResult.append(mWifiScanBatchedResult.get(i).toString() + "\n");
                    }
                    mLogToFile.saveToExternalCacheDir(tempWifiBatchScanResult.toString());
                    mTextViewWifiRxStatus.setText("Message: RSSI data "
                            + mWifiScanBatchedResult.size()
                            + " (samples) was successfully saved to file.");
                    // delete old data as long as it is successfully saved
                    mWifiScanBatchedResult.clear();    // to prevent duplicate savings
                    mButtonWifiSave.setEnabled(false);  // avoid further save
                }
            }
        });
        mNumberProgressBar = (NumberProgressBar) view.findViewById(R.id.progress_bar_wifi_scan);
        mNumberProgressBar.setVisibility(View.INVISIBLE);   // hide the progress bar first
//        mNumberProgressBar.setProgress(0);
        mTextViewWifiRxStatus = (TextView) view.findViewById(R.id.text_view_wifi_rx_status);
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        List<ScanResult> wifiScanResultList;
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isWifiScanRequested) {
                if (mTargetApBssid == null) {  // Scan button was pressed
                    wifiScanResultList = mWifiManager.getScanResults();
                    mWifiKeyScanResultMap = new HashMap<>();
                    for (ScanResult scanResult : wifiScanResultList) {
                        mWifiKeyScanResultMap.put(scanResult.BSSID, scanResult);
                    }
                    List<String> wifiKeyList = new ArrayList<>();   // for display in Spinner
                    // display the SSID only
                    for (ScanResult scanResult : mWifiKeyScanResultMap.values()) {
                        wifiKeyList.add(scanResult.SSID);
                    }
                    ArrayAdapter<String> adapterListOfAps = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line, wifiKeyList);
                    mSpinnerListOfAps.setAdapter(adapterListOfAps);
                    updateMacAddressOfShownAp(mSpinnerListOfAps.getText().toString());

                    // resume buttons
                    mButtonWifiScan.setEnabled(true);
                    mButtonWifiStart.setEnabled(true);
                    // update status
                    mTextViewWifiRxStatus.setText("Status: single scan has been finished.");
                    isWifiScanRequested = false;
                } else {                            // Start button was pressed
                    // mTargetApSsid != null, the AP we are interested
                    wifiScanResultList = mWifiManager.getScanResults();
                    mWifiKeyScanResultMap = new HashMap<>();
                    for (ScanResult scanResult : wifiScanResultList) {
                        mWifiKeyScanResultMap.put(scanResult.BSSID, scanResult);
                    }
                    // check mTargetApSsid has new scan value?
                    if (mWifiKeyScanResultMap.keySet().contains(mTargetApBssid)) {
                        mWifiScanBatchedResult.add(mWifiKeyScanResultMap.get(mTargetApBssid).level);
                    } else {
                        mWifiScanBatchedResult.add(Constants.WIFI_RX_SENSITIVITY);  // -100 dBm means fail to scan "mTargetApBssid"
                    }
                    if ((--mWifiScanSampleCounter) > 0) {
                        mNumberProgressBar.setProgress((int) (mWifiScanSampleTotal
                                - mWifiScanSampleCounter) * 100 / mWifiScanSampleTotal);
                        mWifiManager.startScan();
                    } else {
                        // finished with whole batch of results in the ArrayList
                        mNumberProgressBar.setProgress(100);
                        mButtonWifiScan.setEnabled(true);
                        mButtonWifiStart.setEnabled(true);
                        mButtonWifiSave.setEnabled(true);
                        mSpinnerNumberOfSamples.setEnabled(true);
                        mSpinnerListOfAps.setEnabled(true);
                        int counterEffectiveValue = 0;
                        float averageRssiForThisBatch = 0;
                        for (int rssi : mWifiScanBatchedResult) {
                            if (rssi > Constants.WIFI_RX_SENSITIVITY) {
                                counterEffectiveValue++;
                                averageRssiForThisBatch += rssi;
                            }
                        }
                        averageRssiForThisBatch = averageRssiForThisBatch / counterEffectiveValue;
                        mTextViewWifiRxStatus.setText("Status: batched scan has been finished. " +
                                "Totally, " + counterEffectiveValue + " effective values and " +
                                "the avearge RSSI = " + averageRssiForThisBatch + ".");
                        isWifiScanRequested = false;
                    }
                }
            }
        }
    }

    private void updateMacAddressOfShownAp(String apNameSsid) {
        if (apNameSsid != null && apNameSsid.length() > 0) {
            mTextViewMacAddress.setText("N/A");
            for (ScanResult scanResult : mWifiKeyScanResultMap.values()) {
                if (apNameSsid.equals(scanResult.SSID)) {
                    mTextViewMacAddress.setText(scanResult.BSSID);
                    break;
                }
            }
        } else {
            mTextViewMacAddress.setText("N/A");
        }
    }

    private void updateMacAddressOfShownAp(View view) {
        String apNameSsid = ((TextView) view).getText().toString();
        updateMacAddressOfShownAp(apNameSsid);
    }

}
