package cn.jamesli.example.aw2wififingerprintfinder.wifiutil;

/**
 * Created by jamesli on 15/11/5.
 */
public class Constants {
    // WiFi related
    public static final String DEFAULT_WIFI_FINGERPRINT_NAME = "Fingerprint";
    public static final int WIFI_FINGERPRINT_SCAN_TIMES = 5;  // scan for 5 times to include stable APs
    public static final int WIFI_RX_SENSITIVITY = -100;   // RSSI -100dBm as the sensitivity
    public static final int WIFI_FINGERPRINT_EFFECTIVE_AP_NUMBER = 8;   // take 8 (or less) effective APs

    // for debug info in Log file mLogToFile
    public static final boolean IS_DEBUG_WITH_LOG_FILE = true; // true when test on mobile alone

}
