package cn.jamesli.example.bt10ibeacontxrx.util;

/**
 * Created by jamesli on 15/9/23.
 */
public class Constants {

    public static final int WIFI_RX_SENSITIVITY = -100;   // RSSI -100dBm as the sensitivity

    public static final String DEFAULT_WIFI_FINGERPRINT_NAME = "Fingerprint";

    // for find fingerprint target
    public static final float STEP_COUNTER_ACC_THRESHOLD = 1.2f;    // customized for people
    public static final float TARGET_ONE_AP_SD_THRESHOLD_FOR_ARRIVAL = 10.0f;

    // for debug info in Log file mLogToFile
    public static final boolean IS_DEBUG_WITH_LOG_FILE = true; // true when test on mobile alone
}