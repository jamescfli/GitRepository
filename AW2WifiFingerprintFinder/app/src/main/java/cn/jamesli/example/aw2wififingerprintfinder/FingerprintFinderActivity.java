package cn.jamesli.example.aw2wififingerprintfinder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;
import cn.jamesli.example.aw2wififingerprintfinder.filestorage.LogToFile;
import cn.jamesli.example.aw2wififingerprintfinder.wifiutil.Constants;
import cn.jamesli.example.aw2wififingerprintfinder.wifiutil.WifiFingerprint;
import cn.jamesli.example.aw2wififingerprintfinder.wifiutil.WifiSimpleSaver;

public class FingerprintFinderActivity extends Activity {

    // UI
    private CircleButton mButtonSetTarget;
    private Switch mSwitchRss;
    private TextView mTextViewStatus;

    // file storage
    private StringBuilder mStringBuilderContentToLog;   // temp savor before log file

    // WiFi related
    private WifiManager mWifiManager;
    private int mNumberOfFingerprintSampling;
    private int mCounterOfFingerprintSampling;
    private WifiFingerprint mWifiFingerprintBuffered;
    private boolean isWifiScanRequested;    // for WifiScanReceiver
    private boolean isRssSamplingOn;        // for WifiScanReceiver
    private WifiScanReceiver mWifiScanReceiver;
    private int mNumberOfEffApsForTarget;
    private Map<String, Float> mWifiFingerprintSimpleSaver;

    // step counter related
    private SensorManager mSensorManagerPedometer;

    // arrival alarm
    private Vibrator mVibratorApproachAlarm;

    // for turn screen on
    private WindowManager.LayoutParams mParams;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_finder);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                initiateRoundUi(stub);
            }
        });

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiScanReceiver = new WifiScanReceiver();
        mSensorManagerPedometer = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mVibratorApproachAlarm = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mParams = getWindow().getAttributes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isWifiScanRequested = false;
        if (Constants.IS_DEBUG_WITH_LOG_FILE) {
            mStringBuilderContentToLog = new StringBuilder();
        }
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // keep screen on with full brightness
        setScreenBrightness(+1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // log to file if DEBUG requires and temp savor has content
        if (Constants.IS_DEBUG_WITH_LOG_FILE && mStringBuilderContentToLog.length() > 0) {
            String SAVE_FILE_PREFIX = "FindTargetLog";
            String SAVE_FILE_APPENDIX = ".csv";
            LogToFile logToFile = new LogToFile(this, SAVE_FILE_PREFIX, SAVE_FILE_APPENDIX);
            logToFile.saveToExternalCacheDir(mStringBuilderContentToLog.toString());
        }
        unregisterReceiver(mWifiScanReceiver);

        // back to normal
        setScreenBrightness(-1);
    }

    private void setScreenBrightness(int brightness) {
        // Note: brightness
        //  -1, normal with power save sleeping mode
        //   0, dim light screen
        //  +1, full brightness
        mParams.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mParams.screenBrightness = brightness;   // 0 dim light, +1 full bright
        getWindow().setAttributes(mParams);
    }

    private void initiateRoundUi(WatchViewStub stub) {
        mButtonSetTarget = (CircleButton) stub.findViewById(R.id.button_set_target);
        mButtonSetTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // test
//                Toast.makeText(getApplicationContext(), "Button pressed", Toast.LENGTH_LONG).show();
                initiateWifiRelatedValues();

                isWifiScanRequested = true;
//                isRssSamplingOn = false;

                // start WiFi scan
                mWifiManager.startScan();

                // UI
                mTextViewStatus.setText("Setting target ..");
                // disable target set until current is available
                mButtonSetTarget.setEnabled(false);     // get resumed in onReceive()
                // disable RSS switch temporarily until target has been set
                mSwitchRss.setEnabled(false);
            }
        });
        mSwitchRss = (Switch) stub.findViewById(R.id.switch_rss);
        mSwitchRss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRssSamplingOn = mSwitchRss.isChecked();
                if (isRssSamplingOn) {
                    mButtonSetTarget.setEnabled(false);
                    // test
                    mTextViewStatus.setText("Save fingerprint to string builder .. ");

                    if (Constants.IS_DEBUG_WITH_LOG_FILE) {
                        logTargetFingerprint(new Date().getTime());
                    }
                } else {
                    mButtonSetTarget.setEnabled(true);
                    // test
                    mTextViewStatus.setText("Fingerprint saved to string builder .. ");
                }
            }
        });
        mTextViewStatus = (TextView) stub.findViewById(R.id.text_status);

        mSwitchRss.setEnabled(false);   // set to disabled before setting fingerprint
        mTextViewStatus.setText("Please set target first ..");
    }

    private void initiateWifiRelatedValues() {
        // only AP with all valid scans can be included into fingerprint
        mNumberOfFingerprintSampling = Constants.WIFI_FINGERPRINT_SCAN_TIMES;
        mCounterOfFingerprintSampling = 0;

        // fingerprint savor, with default name provided in Constants.java
        mWifiFingerprintBuffered = new WifiFingerprint();

        mNumberOfEffApsForTarget = Constants.WIFI_FINGERPRINT_EFFECTIVE_AP_NUMBER;
    }

    private void logTargetFingerprint(long timestamp) {
        // Title line
        StringBuilder titleLine = new StringBuilder();
        titleLine.append("Timestamp");
        StringBuilder targetFingerprintLine = new StringBuilder();
        targetFingerprintLine.append(timestamp);
        for (String keyMacAddress : mWifiFingerprintSimpleSaver.keySet()) {
            titleLine.append("," + keyMacAddress);
            targetFingerprintLine.append("," + (float) mWifiFingerprintSimpleSaver.get(keyMacAddress));
        }
        // last column smoothed SD
        titleLine.append("," + "Smoothed SD" + "\n");
        targetFingerprintLine.append("," + "N/A" + "\n");
        // write those two lines into log file
        mStringBuilderContentToLog.append(titleLine);
        mStringBuilderContentToLog.append(targetFingerprintLine);
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        private List<ScanResult> wifiScanResultList;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isWifiScanRequested) {
                wifiScanResultList = mWifiManager.getScanResults();
                mWifiFingerprintBuffered.putOneScanList(wifiScanResultList);
                mCounterOfFingerprintSampling++;
                // UI update
                mTextViewStatus.setText("Scan " + mCounterOfFingerprintSampling);
                if (mCounterOfFingerprintSampling == mNumberOfFingerprintSampling) {
                    // get enough sample already
                    deriveWifiFingerprintSimpleSavor(mWifiFingerprintBuffered);
                    mTextViewStatus.setText("All " + Constants.WIFI_FINGERPRINT_SCAN_TIMES
                            + " scans finished ..");
                    // no further scan needed
                    isWifiScanRequested = false;
                    // turn on RSS switch
                    mSwitchRss.setEnabled(true);
                    // enable target button for other target set
                    mButtonSetTarget.setEnabled(true);
                } else {
                    mWifiManager.startScan();   // keep scan
                }
            }
        }

        private void deriveWifiFingerprintSimpleSavor(WifiFingerprint wifiFingerprintBuffered) {
            List<WifiSimpleSaver> wifiSimpleSaverFullList = new LinkedList<>();

            for (String keyMacAddress : wifiFingerprintBuffered.getMacAddressSet()) {
                // get a column of a list of RSSI values with the same MAC address
                List<Integer> listOfRssiValues = wifiFingerprintBuffered.getSamplesOfOneAp(keyMacAddress);
                if (listOfRssiValues.size() == mNumberOfFingerprintSampling) {
                    // consider APs with successful scan of all (mNumberOfFingerprintSampling) times
                    float average = 0;
                    for (Integer rssiValue : listOfRssiValues) {
                        average += (float) rssiValue;
                    }
                    average = average / mNumberOfFingerprintSampling;
                    // save Mac address and RSSI into the full list for sorting
                    wifiSimpleSaverFullList.add(new WifiSimpleSaver(keyMacAddress, average));
                } // drop the AP if it does not have full list of RSSI values
            }

            // sort wifi full list in decending order
            Collections.sort(wifiSimpleSaverFullList, new Comparator<WifiSimpleSaver>() {
                @Override
                public int compare(WifiSimpleSaver lhs, WifiSimpleSaver rhs) {
                    return lhs.getRssi() > rhs.getRssi() ? -1 : (lhs.getRssi() < rhs.getRssi() ? +1 : 0);
                }
            });

            // take the first x APs with most significant RSSI values, large RSSI values
            // the number is also capped by wifiSimpleSaverFullList
            mNumberOfEffApsForTarget = Math.min(mNumberOfEffApsForTarget,
                    wifiSimpleSaverFullList.size());

            // initiate simple savor
            mWifiFingerprintSimpleSaver = new HashMap<>();
            for (int i = 0; i < mNumberOfEffApsForTarget; i++) {
                mWifiFingerprintSimpleSaver.put(wifiSimpleSaverFullList.get(i).getMacAddress(),
                        wifiSimpleSaverFullList.get(i).getRssi());
            }
        }
    }
}
