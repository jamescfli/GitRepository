package cn.jamesli.example.bt10ibeacontxrx.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.jamesli.example.bt10ibeacontxrx.R;
import cn.jamesli.example.bt10ibeacontxrx.filestorage.LogToFile;
import cn.jamesli.example.bt10ibeacontxrx.findtarget.HighPeakDetection;
import cn.jamesli.example.bt10ibeacontxrx.findtarget.LowPeakDetection;
import cn.jamesli.example.bt10ibeacontxrx.findtarget.SimpleMovingAverage;
import cn.jamesli.example.bt10ibeacontxrx.util.Constants;
import cn.jamesli.example.bt10ibeacontxrx.wifitutil.WifiFingerprint;
import cn.jamesli.example.bt10ibeacontxrx.wifitutil.WifiSimpleSaver;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WifiFindFingerprintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WifiFindFingerprintFragment extends Fragment implements SensorEventListener {
    private static final String TAG = "WifiFindFingerprintFragment";

    // for UI
    private Button mButtonSetTarget;
    private Switch mSwitchRssScan;
    private TextView mTextViewSeekBarEffApNumber;
    private SeekBar mSeekBarEffApNumber;
    private TextView mTextViewSeekBarRssSampleTimes;
    private SeekBar mSeekBarRssSampleTimes;
    private TextView mTextViewSeekBarLowPeakWindowSize;
    private SeekBar mSeekBarLowPeakWindowSize;
    private TextView mTextViewSeekBarEmaFactor;
    private SeekBar mSeekBarEmaFactor;
    private TextView mTextViewScannedApNumber;
    private TextView mTextViewInstantSimilarDistance;
    private TextView mTextViewSmoothedSimilarDistance;
    private TextView mTextViewHistoricalMinDistance;
    private TextView mTextViewLowPeakTrend; // display low peak value when peak formed, and display increase/decrease trend for SD
    private TextView mTextViewStepCounter;
    private TextView mTextViewOutcome;

    // log file storage
    private StringBuilder mContentSavedToLogFile;   // temp storage for log file content

    // mobile sensors / components
    private SensorManager mSensorManagerPedometer;
    private Vibrator mVibratorApproachAlarm;
    private float[] mAcceleration;
    private float[] mGravity;    // use mGravity value to adjust the effective acc value
    private int mStepCounter;
    private SimpleMovingAverage mSimpleMovingAverage;
    private HighPeakDetection mHighPeakDetection;

    // wifi related
    private boolean isRssSamplingOn = false;    // differentiate initial scan
    private WifiManager mWifiManager;
    private WifiScanReceiver mWifiScanReceiver;
    private int mNumberOfFingerprintSampling;   // to provide more robust fingerprint
    private int mCounterOfFingerprintSampling;  // counter for fingerprint sampling
    private WifiFingerprint mWifiFingerprintBuffered; // to calculate mWifiFingerprintSimpleSavor
    private Map<String, Float> mWifiFingerprintSimpleSavor;
    private int mNumberOfEffApsForTarget;   // record the total number of effective APs for fingerprint
    private boolean isWifiScanRequested;    // lazy solution for extra wifi scan result feed
    private boolean isWifiScanResultReadyForArrivalDetection;   // detection applied only with new scan result
    private boolean isWifiBufferedScanResultSelectedApsInitiated;
    private Map<String, List<Float>> mWifiBufferedScanResultSelectedAps; // MAC -> List of RSSIs
    private int mRssSampleTimes;    // upperbound of total item number of List<Float>

    // calculation related
    private float mSimilarDistance;
    private boolean isSmoothedSimilarDistanceInitiated;
    private float mSmoothedSimilarDistance;
    private float mSmoothedSimilarDistanceLast;
    private boolean isMinSimilarDistanceOnTrackInitiated;
    private float mMinSimilarDistanceOnTrack; // record min RSSI similar distance value on target trial
    private float mSmoothingAverageFactorForSimilarDistance;      // is the weight for historical similar distance
    private LowPeakDetection mLowPeakDetection;

    private static String mFragmentTitle;

    public static WifiFindFingerprintFragment newInstance(String fragmentTitle) {
        mFragmentTitle = fragmentTitle;
        return new WifiFindFingerprintFragment();
    }

    public WifiFindFingerprintFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        mWifiScanReceiver = new WifiScanReceiver();
        mSensorManagerPedometer = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mVibratorApproachAlarm = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewFindFingerprintFragment = (View) inflater.inflate
                (R.layout.fragment_wifi_find_fingerprint, container, false);
        getActivity().setTitle(mFragmentTitle);

        initiateUi(viewFindFingerprintFragment);

        return viewFindFingerprintFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // initiate log file
        isWifiScanRequested = false;    // request on demand
        if (Constants.IS_DEBUG_WITH_LOG_FILE) {
            mContentSavedToLogFile = new StringBuilder();
        }
        getActivity().registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        // log to file if DEBUG requires and temp savor has content
        if (Constants.IS_DEBUG_WITH_LOG_FILE && mContentSavedToLogFile.length() > 0) {
            String SAVE_FILE_PREFIX = "FindTargetLog";
            String SAVE_FILE_APPENDIX = ".csv";
            LogToFile logToFile = new LogToFile(getActivity(), SAVE_FILE_PREFIX, SAVE_FILE_APPENDIX);
            logToFile.saveToExternalCacheDir(mContentSavedToLogFile.toString());
        }
        getActivity().unregisterReceiver(mWifiScanReceiver);
    }


    private void initiateUi(View view) {
        mButtonSetTarget = (Button) view.findViewById(R.id.button_mark_target_fingerprint);
        mButtonSetTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // wifi scan type control
                isWifiScanRequested = true;
                isRssSamplingOn = false;

                // initiate
                mNumberOfFingerprintSampling =
                        translateSbValueToSamplingTimes(mSeekBarRssSampleTimes.getProgress());
                if (mNumberOfFingerprintSampling > 1) {
                    // initiate mWifiFingerprintBuffered if we need multiple scans
                    mWifiFingerprintBuffered = new WifiFingerprint(Constants.DEFAULT_WIFI_FINGERPRINT_NAME);
                }
                mCounterOfFingerprintSampling = 0;

                // start
                mWifiManager.startScan();

                // UI
                mTextViewOutcome.setText("setting target ..");
            }
        });

        mSwitchRssScan = (Switch) view.findViewById(R.id.switch_rss_sampling_starter);
        mSwitchRssScan.setEnabled(false);   // enabled after getting wifi fingerprint
        mSwitchRssScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRssSamplingOn = mSwitchRssScan.isChecked();
                if (isRssSamplingOn) {
                    // temporarily shut down target setting
                    mButtonSetTarget.setEnabled(false); // no more target setting

                    clearStatusTextView();
                    initiateRssRelatedValues();

                    startStepCounter();
                    startRssSampling();

                    mTextViewOutcome.setText("Both step counter and RSS sampling were started.");

                    // write log
                    if (Constants.IS_DEBUG_WITH_LOG_FILE) {
                        // Note: ScanResult in wifiScanResultList has different timestamp and may vary
                        // a lot from one AP to another. The reason is still unknown, e.g.
                        // SSID: OCCUPY, level: -60, frequency: 2412, timestamp: 801211141093
                        // SSID: adsl,   level: -65, frequency: 2432, timestamp: 801274971316
                        // so we use Date() now time instead
                        logTargetFingerprint(new Date().getTime());     // milliseconds since 1970/01/01
                    }
                } else {
                    mButtonSetTarget.setEnabled(true);  // could set new target

                    stopStepCounter();
                    stopRssSampling();

                    mTextViewOutcome.setText("Both step counter and RSS sampling were stopped.");
                }
            }
        });

        mTextViewSeekBarEffApNumber = (TextView) view.findViewById(R.id.text_view_eff_ap_number);
        mSeekBarEffApNumber = (SeekBar) view.findViewById(R.id.seek_bar_eff_ap_number);
        // set default value for effective AP number, according to xml
        mTextViewSeekBarEffApNumber.setText(getString(R.string.seek_bar_text_eff_ap_number)
                + " " + translateSbValueToEffApNumber(mSeekBarEffApNumber.getProgress()));
        mSeekBarEffApNumber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewSeekBarEffApNumber.setText(getString(R.string.seek_bar_text_eff_ap_number)
                        + " " + translateSbValueToEffApNumber(mSeekBarEffApNumber.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTextViewSeekBarRssSampleTimes = (TextView) view.findViewById(R.id.text_view_rss_sampling_times);
        mSeekBarRssSampleTimes = (SeekBar) view.findViewById(R.id.seek_bar_sampling_number);
        // set default value for RSS sampling times, according to xml
        mTextViewSeekBarRssSampleTimes.setText(getString(R.string.seek_bar_text_sampling_number)
                + " " + translateSbValueToSamplingTimes(mSeekBarRssSampleTimes.getProgress()));
        mSeekBarRssSampleTimes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewSeekBarRssSampleTimes.setText(getString(R.string.seek_bar_text_sampling_number)
                        + " " + translateSbValueToSamplingTimes(mSeekBarRssSampleTimes.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTextViewSeekBarLowPeakWindowSize = (TextView) view.findViewById(R.id.text_view_low_peak_window);
        mSeekBarLowPeakWindowSize = (SeekBar) view.findViewById(R.id.seek_bar_low_peak_window);
        mTextViewSeekBarLowPeakWindowSize.setText(getString(R.string.seek_bar_text_low_peak_window)
                + " " + translateSbValueToLowPeakWindowSize(mSeekBarLowPeakWindowSize.getProgress()));
        mSeekBarLowPeakWindowSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewSeekBarLowPeakWindowSize.setText(getString(R.string.seek_bar_text_low_peak_window)
                        + " " + translateSbValueToLowPeakWindowSize(mSeekBarLowPeakWindowSize.getProgress()));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTextViewSeekBarEmaFactor = (TextView) view.findViewById(R.id.text_view_ema_factor);
        mSeekBarEmaFactor = (SeekBar) view.findViewById(R.id.seek_bar_ema_factor);
        mTextViewSeekBarEmaFactor.setText(getString(R.string.seek_bar_text_ema_factor)
                + " " + mSeekBarEmaFactor.getProgress()/10.0f);
        mSeekBarEmaFactor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewSeekBarEmaFactor.setText(getString(R.string.seek_bar_text_ema_factor)
                        + " " + mSeekBarEmaFactor.getProgress()/10.0f);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTextViewScannedApNumber = (TextView)
                view.findViewById(R.id.text_view_ap_num_content);
        mTextViewInstantSimilarDistance = (TextView)
                view.findViewById(R.id.text_view_similar_distance_content);
        mTextViewSmoothedSimilarDistance = (TextView)
                view.findViewById(R.id.text_view_smoothed_similar_distance_content);
        mTextViewHistoricalMinDistance = (TextView)
                view.findViewById(R.id.text_view_historical_minimum_content);
        mTextViewLowPeakTrend = (TextView)
                view.findViewById(R.id.text_view_low_peak_content);
        mTextViewStepCounter = (TextView)
                view.findViewById(R.id.text_view_step_counter_content);
        mTextViewOutcome = (TextView)
                view.findViewById(R.id.text_view_text_status_outcome_content);

        clearStatusTextView();
    }

    private void logTargetFingerprint(long timestamp) {
        // Title line
        StringBuilder titleLine = new StringBuilder();
        titleLine.append("Timestamp");
        StringBuilder targetFingerprintLine = new StringBuilder();
        targetFingerprintLine.append(timestamp);
        for (String keyMacAddress : mWifiFingerprintSimpleSavor.keySet()) {
            titleLine.append("," + keyMacAddress);
            targetFingerprintLine.append("," + (float) mWifiFingerprintSimpleSavor.get(keyMacAddress));
        }
        // last column smoothed SD
        titleLine.append("," + "Smoothed SD" + "\n");
        targetFingerprintLine.append("," + "N/A" + "\n");
        // write those two lines into log file
        mContentSavedToLogFile.append(titleLine);
        mContentSavedToLogFile.append(targetFingerprintLine);
    }

    private void logRssSampleScanResult(long timestamp, Map<String, Float> selectedApRssiList) {
        StringBuilder rssiLine = new StringBuilder();
        rssiLine.append(timestamp);
        for (String keyMacAddress : mWifiFingerprintSimpleSavor.keySet()) {
            rssiLine.append("," + selectedApRssiList.get(keyMacAddress).toString());
        }
        // last column smoothed SD
        rssiLine.append("," + mSmoothedSimilarDistance + "\n");
        mContentSavedToLogFile.append(rssiLine);
    }

    private void startStepCounter() {
        mStepCounter = 0;
        mSimpleMovingAverage = new SimpleMovingAverage(10); // fix smoothing length to 10
        mHighPeakDetection = new HighPeakDetection(7, Constants.STEP_COUNTER_ACC_THRESHOLD);
        mSensorManagerPedometer.registerListener(this, mSensorManagerPedometer
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
        mSensorManagerPedometer.registerListener(this, mSensorManagerPedometer
                .getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);
//        mTextViewOutcome.setText("Step counter was started.");
    }

    private void stopStepCounter() {
        mStepCounter = 0;
        mSensorManagerPedometer.unregisterListener(this);
//        mTextViewOutcome.setText("Step counter was stopped.");
    }

    private void clearStatusTextView() {
        mTextViewScannedApNumber.setText("N/A");
        mTextViewInstantSimilarDistance.setText("N/A");
        mTextViewSmoothedSimilarDistance.setText("N/A");
        mTextViewHistoricalMinDistance.setText("N/A");
        mTextViewLowPeakTrend.setText("N/A");
        mTextViewStepCounter.setText("N/A");
    }

    private void initiateRssRelatedValues() {
        isSmoothedSimilarDistanceInitiated = false;
        mSmoothedSimilarDistance = 0;
        mSmoothingAverageFactorForSimilarDistance =  mSeekBarEmaFactor.getProgress()/10.0f;
        isMinSimilarDistanceOnTrackInitiated = false;
        mMinSimilarDistanceOnTrack = Float.NaN;
        mLowPeakDetection = new LowPeakDetection(translateSbValueToLowPeakWindowSize
                (mSeekBarLowPeakWindowSize.getProgress()));
        isWifiBufferedScanResultSelectedApsInitiated = false;
        mWifiBufferedScanResultSelectedAps = new HashMap<>();
        mRssSampleTimes = translateSbValueToSamplingTimes(mSeekBarRssSampleTimes.getProgress());
    }

    private void startRssSampling() {
        isWifiScanRequested = true;
        isRssSamplingOn = true;
        isWifiScanResultReadyForArrivalDetection = false; // no new Wifi yet
        mTextViewOutcome.setText("RSS sampling ON. Start to find target.");
        mWifiManager.startScan();
    }

    private void stopRssSampling() {
        isWifiScanRequested = false;    // assure not extra data update after stop RSS sampling
        isRssSamplingOn= false;
//        mTextViewOutcome.setText("RSS sampling was stopped.");
    }

    /**
     * 3 functions : translate seekbar values to real values for the corresponding items
     */
    private int translateSbValueToEffApNumber(int progress) {
        // translate seekbar progress to effective number of APs, 4, 8, 12, 16, 20
        return (progress+1) * 4;
    }

    private int translateSbValueToSamplingTimes(int progress) {
        // translate seekbar progress to sampling times 1, 2, 3, 4, 5
        return (progress+1);
    }

    private int translateSbValueToLowPeakWindowSize(int progress) {
        // translate seekbar progress to low peak window size 3, 4, 5, 6
        // e.g. decrease, decrease, increase, increase for a peak if window size = 4
        return (progress+3);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float smaAcc;
        switch(event.sensor.getType())
        {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                mAcceleration = event.values.clone();
                //smooth
                if(mGravity[2]>mGravity[1])     // mobile lays down to be paralleled with floor ground
                    smaAcc = mSimpleMovingAverage.add(mAcceleration[2]);
                else    // if mobile stands up straight
                    smaAcc = mSimpleMovingAverage.add(mAcceleration[1]);

                // detected step
                if(mHighPeakDetection.findPeak(smaAcc)) {
                    mTextViewStepCounter.setText((++mStepCounter) + " steps");
                    StringBuilder stepCounterForLog = new StringBuilder();
                    stepCounterForLog.append(new Date().getTime() + ", Step " + mStepCounter);
                    if (isWifiBufferedScanResultSelectedApsInitiated && isWifiScanResultReadyForArrivalDetection) {
                        // make sure mWifiBufferedScanResultSelectedAps has values and
                        // wifi scan has fed back new scan results and the results have not been used
                        detectArrivalAtTarget();
                        // scan result used, wait for further updates on wifi scan
                        isWifiScanResultReadyForArrivalDetection = false;
                        stepCounterForLog.append(" detect arrival");
                        int scannedApCounter = calculateScannedApNumber();
                        mTextViewScannedApNumber.setText(scannedApCounter + " / "
                                + mNumberOfEffApsForTarget);
                    }
                    stepCounterForLog.append("\n");
                    if (Constants.IS_DEBUG_WITH_LOG_FILE) {
                        mContentSavedToLogFile.append(stepCounterForLog);
                    }
                }
                break;
            case Sensor.TYPE_GRAVITY:
                mGravity = event.values.clone();
                break;
            default:
                Log.e("StepCounter", "Wrong Type onSensorChanged");
        }
    }

    private int calculateScannedApNumber() {
        int counterEffAp = 0;
        for (String keyMacAddress : mWifiFingerprintSimpleSavor.keySet()) {
            for (Float rssiValue : mWifiBufferedScanResultSelectedAps.get(keyMacAddress)) {
                if (rssiValue > Constants.WIFI_RX_SENSITIVITY) {
                    // This AP (keyMacAddress) has effective RSSI value
                    counterEffAp++;
                    break;  // go to next AP
                }
            }
        }
        return counterEffAp;
    }

    private void detectArrivalAtTarget() {
        // conduct required initialization
        mSimilarDistance = deriveInstantSimilarDistance();
        if (!isSmoothedSimilarDistanceInitiated) {
            mSmoothedSimilarDistance = mSimilarDistance;
            isSmoothedSimilarDistanceInitiated = true;
        } else {
            mSmoothedSimilarDistanceLast = mSmoothedSimilarDistance;
            mSmoothedSimilarDistance = mSmoothedSimilarDistance * mSmoothingAverageFactorForSimilarDistance
                    + mSimilarDistance * (1- mSmoothingAverageFactorForSimilarDistance);
        }
        if (!isMinSimilarDistanceOnTrackInitiated) {
            mMinSimilarDistanceOnTrack = mSmoothedSimilarDistance;
            isMinSimilarDistanceOnTrackInitiated = true;
        } else {
            if (mSmoothedSimilarDistance < mMinSimilarDistanceOnTrack) {
                mMinSimilarDistanceOnTrack = mSmoothedSimilarDistance;
            }
        }

        displaySimilarDistanceRelatedInfoInTextView();

        detectSdLowPeakAndTargetArrival();
    }

    private void detectSdLowPeakAndTargetArrival() {
        if (mLowPeakDetection.findPeak(mSmoothedSimilarDistance)) {
            // smoothed SD forms a low peak already
            float lowPeakSimilarDistance = mLowPeakDetection.getCurrentPeakValue();
            mTextViewLowPeakTrend.setText(String.valueOf(lowPeakSimilarDistance));
            if (lowPeakSimilarDistance ==  mMinSimilarDistanceOnTrack &&
                    lowPeakSimilarDistance <
                            (Constants.TARGET_ONE_AP_SD_THRESHOLD_FOR_ARRIVAL
                            * Math.sqrt(mNumberOfEffApsForTarget)
                            * Constants.BALLPARK_DISTANCE_THRESHOLD_APPROACH_TO_NEAR)) {
                // .. Note: SD = sqrt(sum(SD_i^2) / N), therefore low peak SD needs to compare
                // with threshold * sqrt(N)

                stopRssSampling();
                stopStepCounter();
                // vibrate for half a second to signify arrival
                mVibratorApproachAlarm.vibrate(500);
                mSwitchRssScan.setChecked(false);   // stop wifi scanning
                mTextViewOutcome.setText("Arrived at Target");
                // record the arrival event and corresponding low peak SD value in the log file
                if (Constants.IS_DEBUG_WITH_LOG_FILE) {
                    mContentSavedToLogFile.append("Arrived at Target, " +
                            "final Smoothed SD = " + mSmoothedSimilarDistance + ", " +
                            "Threshold = " + (Constants.TARGET_ONE_AP_SD_THRESHOLD_FOR_ARRIVAL
                            * Math.sqrt(mNumberOfEffApsForTarget)
                            * Constants.BALLPARK_DISTANCE_THRESHOLD_APPROACH_TO_NEAR) + "\n");
                }
                // resume Target button to allow setting new targets
                mButtonSetTarget.setEnabled(true);
            }
        }
    }

    private float deriveInstantSimilarDistance() {
        // get the averaged RSS sampling over mRssSampleTimes by mWifiBufferedScanResultSelectedAps
        float similarDistance = 0;
        float effAverageRssi;
        float effRssiCounter;
        float targetRssi;
        for (String keyMacAddress : mWifiFingerprintSimpleSavor.keySet()) {
            effAverageRssi = 0;
            effRssiCounter = 0;
            for (Float rssiSample : mWifiBufferedScanResultSelectedAps.get(keyMacAddress)) {
                if (rssiSample > Constants.WIFI_RX_SENSITIVITY) {
                    effAverageRssi += (float) rssiSample;
                    effRssiCounter++;
                }
            }
            if (effRssiCounter == 0) {
                // no valid value detected by scan
                effAverageRssi = Constants.WIFI_RX_SENSITIVITY; // use sensitivity to fill valid value
            } else {
                effAverageRssi = effAverageRssi / effRssiCounter;
            }
            targetRssi = (float) mWifiFingerprintSimpleSavor.get(keyMacAddress);
            similarDistance += (effAverageRssi - targetRssi) * (effAverageRssi - targetRssi);
        }
        return (float) Math.sqrt(similarDistance);
    }

    private void displaySimilarDistanceRelatedInfoInTextView() {
        mTextViewInstantSimilarDistance.setText(String.valueOf(mSmoothedSimilarDistance));
        mTextViewSmoothedSimilarDistance.setText(String.valueOf(mSmoothedSimilarDistance));
        mTextViewHistoricalMinDistance.setText(String.valueOf(mMinSimilarDistanceOnTrack));
        mTextViewLowPeakTrend.setText(mSmoothedSimilarDistanceLast < mSmoothedSimilarDistance ?
                "increased" :
                (mSmoothedSimilarDistanceLast > mSmoothedSimilarDistance ? "even" : "decreased"));
        float approachingThreshold =  Constants.TARGET_ONE_AP_SD_THRESHOLD_FOR_ARRIVAL
                * ((float) Math.sqrt(mNumberOfEffApsForTarget));
        mTextViewOutcome.setText(mSmoothedSimilarDistance >
                (approachingThreshold * Constants.BALLPARK_DISTANCE_THRESHOLD_NEAR_TO_FAR) ?
                "Far .. keep going" : (mSmoothedSimilarDistance <
                (approachingThreshold * Constants.BALLPARK_DISTANCE_THRESHOLD_APPROACH_TO_NEAR) ?
                "Approaching .. slower" : "Near .. slow"));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class WifiScanReceiver extends BroadcastReceiver {
        private List<ScanResult> wifiScanResultList;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isWifiScanRequested) {      // make sure it is the requested one
                if (isRssSamplingOn) {
                    wifiScanResultList = mWifiManager.getScanResults();     // get raw data

                    // TODO in the future we could improve stability of scanResult by buffered the
                    // last round scan result, if certain selected AP was missed in this round, we
                    // can use last round RSSI value instead. But this can not last for more than
                    // one round, i.e. two consecutive -100dBm's means null in this round.
                    // This can deal with the case when approaching fingerprint and missing one RSSI
                    // value from one of the selected APs of the fingerprint, due to no access to AP
                    // temporarily, i.e. -68dBm, null, -70dBm for MAC xx:xx:xx:xx:xx.
                    // And don't forget to log the temp value for debug.

                    // collect RSSI value of the interested / selected APs
                    // and save the results to wifiSelectedApRssiList
                    Map<String, Float> wifiSelectedApRssiList = new HashMap<>();
                    for (String keyMacAddress : mWifiFingerprintSimpleSavor.keySet()) {
                        // default RSSI = sensitivity, if keyMacAddress AP is not detected by mWifiManager
                        wifiSelectedApRssiList.put(keyMacAddress, new Float(Constants.WIFI_RX_SENSITIVITY));
                        // .. remember to check value of sensitivity before using it
                        for (ScanResult scanResult : wifiScanResultList) {
                            if (keyMacAddress.equals(scanResult.BSSID)) {
                                wifiSelectedApRssiList.put(keyMacAddress, new Float(scanResult.level));
                                break;
                            }
                        }
                    }

                    // save scan results in mWifiBufferedScanResultSelectedAps for arrival detection
                    // in onSensorChanged() of SensorEventListener
                    if (!isWifiBufferedScanResultSelectedApsInitiated) {
                        for (String keyMacAddress : mWifiFingerprintSimpleSavor.keySet()) {
                            LinkedList<Float> rssiList = new LinkedList<>();
                            rssiList.add(new Float(wifiSelectedApRssiList.get(keyMacAddress)));
                            mWifiBufferedScanResultSelectedAps.put(keyMacAddress, rssiList);
                        }
                        isWifiBufferedScanResultSelectedApsInitiated = true;
                    } else {
                        for (String keyMacAddress : mWifiFingerprintSimpleSavor.keySet()) {
                            mWifiBufferedScanResultSelectedAps.get(keyMacAddress)
                                    .add(Float.valueOf(wifiSelectedApRssiList.get(keyMacAddress)));
                            if (mWifiBufferedScanResultSelectedAps.get(keyMacAddress).size() > mRssSampleTimes) {
                                // length of RSSI list > mRssSampleTimes, remove the first one
                                // keep list length = mRssSampleTimes
                                mWifiBufferedScanResultSelectedAps.get(keyMacAddress).remove(0);
                            }
                        }
                    }

                    // new scan value available in mWifiBufferedScanResultSelectedAps
                    // values are ready for arrival detection, good to go
                    isWifiScanResultReadyForArrivalDetection = true;
                    if (Constants.IS_DEBUG_WITH_LOG_FILE) {
                        logRssSampleScanResult(new Date().getTime(), wifiSelectedApRssiList);
                    }

                    // keep scan and retrieve new RSSI values for further buffering
                    mWifiManager.startScan();

                } else {

                    // task: initial scan to set target fingerprint
                    wifiScanResultList = mWifiManager.getScanResults();

                    if (mNumberOfFingerprintSampling == 1) {
                        // apply traditional single fingerprint scan

                        deriveWifiFingerprintSimpleSavor(wifiScanResultList);

                        updateUiWithNewFingerprint();

                        // no further scan required
                        isWifiScanRequested = false;    // no further scan until mSwitchRssScan is On

                    } else {    // implement multiple scans and average
                        mWifiFingerprintBuffered.putOneScanList(wifiScanResultList);
                        mCounterOfFingerprintSampling++;
                        if (mCounterOfFingerprintSampling == mNumberOfFingerprintSampling) {
                            // get enough sample already
                            deriveWifiFingerprintSimpleSavor(mWifiFingerprintBuffered);
                            updateUiWithNewFingerprint();
                            // no further scan required
                            isWifiScanRequested = false;
                        } else {
                            mWifiManager.startScan();   // require more scans
                        }
                    }
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
            mNumberOfEffApsForTarget =
                    translateSbValueToEffApNumber(mSeekBarEffApNumber.getProgress());
            // the number is also capped by wifiSimpleSaverFullList
            mNumberOfEffApsForTarget = Math.min(mNumberOfEffApsForTarget,
                    wifiSimpleSaverFullList.size());

            // initiate simple savor
            mWifiFingerprintSimpleSavor = new HashMap<>();
            for (int i = 0; i < mNumberOfEffApsForTarget; i++) {
                mWifiFingerprintSimpleSavor.put(wifiSimpleSaverFullList.get(i).getMacAddress(),
                        wifiSimpleSaverFullList.get(i).getRssi());
            }
        }

        private void deriveWifiFingerprintSimpleSavor(List<ScanResult> scanResultList) {
            Collections.sort(scanResultList, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    // requires sort in RSSI descending order, so > => -1 and < => +1
                    return lhs.level > rhs.level ? -1 : (lhs.level < rhs.level ? +1 : 0);
                }
            });

            // take the first x APs with most significant RSSI values, large RSSI values
            mNumberOfEffApsForTarget =
                    translateSbValueToEffApNumber(mSeekBarEffApNumber.getProgress());

            int sizeOfScanList = scanResultList.size();
            mNumberOfEffApsForTarget = Math.min(mNumberOfEffApsForTarget, sizeOfScanList);

            // initiate and put values to mWifiFingerprintSimpleSavor
            mWifiFingerprintSimpleSavor = new HashMap<>();
            for (int i = 0; i < mNumberOfEffApsForTarget; i++) {
                mWifiFingerprintSimpleSavor.put(scanResultList.get(i).BSSID,
                        (float) (scanResultList.get(i).level));
            }
        }

        private void updateUiWithNewFingerprint() {
            StringBuilder targetFingerprintDisplay = new StringBuilder();
            String prefix = "";
            for (String keyMacAddress : mWifiFingerprintSimpleSavor.keySet()) {
                targetFingerprintDisplay.append(prefix + keyMacAddress + "  "
                        + mWifiFingerprintSimpleSavor.get(keyMacAddress));
                prefix = "\n";
            }
            // mWifiFingerprintSimpleSavor has been updated with scan results
            mTextViewOutcome.setText(targetFingerprintDisplay);
            // mSwitchRssScan can be enabled for arrival detection, set to be false first
            mSwitchRssScan.setEnabled(true);
            mSwitchRssScan.setChecked(false);
        }
    }
}
