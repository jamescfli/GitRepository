package cn.jamesli.example.bt10ibeacontxrx.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.jamesli.example.bt10ibeacontxrx.R;
import cn.jamesli.example.bt10ibeacontxrx.filestorage.LogToFile;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WifiFindFingerprintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WifiFindFingerprintFragment extends Fragment {
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
    private TextView mTextViewLowPeakTrend;
    private TextView mTextViewStepCounter;
    private TextView mTextViewOutcome;

    // log file storage
    private static final DateFormat dateFormat = new SimpleDateFormat("yyMMdd'T'HHmmss", Locale.CHINA);
    private static final String SAVE_FILE_PREFIX = "FindTargetLog";
    private static final String SAVE_FILE_APPENDIX = ".csv";
    private Date mDateTimeForNow;
    private LogToFile mLogToFile;

    // mobile sensors / components
    private SensorManager mSensorManagerPedometer;
    private Vibrator mVibratorApproachAlarm;
    private float[] acceleration;
    private float[] gravity;

    // wifi related
    private boolean isRssSamplingOn = false;    // differentiate initial scan
    private WifiManager mWifiManager;
    private WifiScanReceiver mWifiScanReceiver;
    private Map<String, Float> mWifiFingerprintSimpleSavor;
    private boolean isWifiScanRequested;    // lazy solution for extra wifi scan result feed

    // caculation related
    private float similarDistance;
    private float smoothedSimilarDistance;
    private float minSimilarDistanceOnTrack;
    private float lowPeakSimilarDistance;

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
        mDateTimeForNow = new Date();
        String filename = SAVE_FILE_PREFIX + dateFormat.format(mDateTimeForNow.getTime())
                + SAVE_FILE_APPENDIX;
        mLogToFile = new LogToFile(getActivity(), filename);
        getActivity().registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        isWifiScanRequested = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        // close log file
        mLogToFile.close();
        getActivity().unregisterReceiver(mWifiScanReceiver);
    }


    private void initiateUi(View view) {
        mButtonSetTarget = (Button) view.findViewById(R.id.button_mark_target_fingerprint);
        mButtonSetTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWifiScanRequested = true;
                isRssSamplingOn = false;
                // Note: the logic with SwitchRssScan needs to be carefully designed
                mSwitchRssScan.setEnabled(true);
                mSwitchRssScan.setChecked(false);
                mWifiManager.startScan();
            }
        });
        mSwitchRssScan = (Switch) view.findViewById(R.id.switch_rss_sampling_starter);
        mSwitchRssScan.setEnabled(false);   // enabled after getting wifi fingerprint
        mSwitchRssScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRssSamplingOn = mSwitchRssScan.isChecked();
                if (isRssSamplingOn) {
                    mButtonSetTarget.setEnabled(false); // no more target setting
                    startStepCounter();
                    startRssSampling();
                    mTextViewOutcome.setText("Both step counter and RSS sampling were started.");
                    clearStatusTextView();
                    initiateRssRelatedValues();
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
        mTextViewScannedApNumber = (TextView) view.findViewById(R.id.text_view_ap_num_content);
        mTextViewInstantSimilarDistance = (TextView) view.findViewById(R.id.text_view_similar_distance_content);
        mTextViewSmoothedSimilarDistance = (TextView) view.findViewById(R.id.text_view_smoothed_similar_distance_content);
        mTextViewHistoricalMinDistance = (TextView) view.findViewById(R.id.text_view_historical_minimum_content);
        mTextViewLowPeakTrend = (TextView) view.findViewById(R.id.text_view_low_peak_content);
        mTextViewStepCounter = (TextView) view.findViewById(R.id.text_view_step_counter_content);
        mTextViewOutcome = (TextView) view.findViewById(R.id.text_view_text_status_outcome_content);
        clearStatusTextView();
    }

    private void startStepCounter() {
        // TODO
//        mTextViewOutcome.setText("Step counter was started.");
    }

    private void startRssSampling() {
        // TODO
//        mTextViewOutcome.setText("RSS sampling was started.");
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
        similarDistance = Float.POSITIVE_INFINITY;
        smoothedSimilarDistance = Float.POSITIVE_INFINITY;
        minSimilarDistanceOnTrack = Float.POSITIVE_INFINITY;
        lowPeakSimilarDistance = Float.POSITIVE_INFINITY;

        // TODO counter, eMA, getMin, lowPeakDetection
    }

    private void stopStepCounter() {
        // TODO
//        mTextViewOutcome.setText("Step counter was stopped.");
    }

    private void stopRssSampling() {
        // TODO
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
        // translate seekbar progress to low peak window size 2, 4, 6, 8
        // same number of downs and ups on both sides
        return (progress+1)*2;
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        List<ScanResult> wifiScanResultList;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isWifiScanRequested) {      // make sure it is the requested one
                if (isRssSamplingOn) {
                    // TODO for real RSS sampling to find target
                } else {
                    // initial scan to set target fingerprint
                    wifiScanResultList = mWifiManager.getScanResults();
                    Collections.sort(wifiScanResultList, new Comparator<ScanResult>() {
                        @Override
                        public int compare(ScanResult lhs, ScanResult rhs) {
                            // requires sort in RSSI descending order, so > => -1 and < => +1
                            return lhs.level > rhs.level ? -1 : (lhs.level < rhs.level ? +1 : 0);
                        }
                    });
                    // take the first x APs with most significant RSSI values
                    int numberOfStrongAps = translateSbValueToEffApNumber(mSeekBarEffApNumber.getProgress());
                    int sizeOfScanList = wifiScanResultList.size();
                    mWifiFingerprintSimpleSavor = new HashMap<>();
                    StringBuilder targetFingerprintDisplay = new StringBuilder();
                    String prefix = "";
                    for (int i = 0, SIZE = Math.min(numberOfStrongAps, sizeOfScanList); i < SIZE; i++) {
                        targetFingerprintDisplay.append(prefix + wifiScanResultList.get(i).BSSID + "  "
                                + wifiScanResultList.get(i).level);
                        prefix = "\n";
                        mWifiFingerprintSimpleSavor.put(wifiScanResultList.get(i).BSSID,
                                (float) wifiScanResultList.get(i).level);
                    }
                    // mWifiFingerprintSimpleSavor has been updated with scan results
                    mTextViewOutcome.setText(targetFingerprintDisplay);
                    isWifiScanRequested = false;    // scan finished
                }
            }
        }
    }
}
