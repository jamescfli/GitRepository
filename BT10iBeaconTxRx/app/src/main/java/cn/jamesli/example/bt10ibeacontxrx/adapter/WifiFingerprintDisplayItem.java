package cn.jamesli.example.bt10ibeacontxrx.adapter;

/**
 * Created by jamesli on 15/10/6.
 */
public class WifiFingerprintDisplayItem {
    private String mBssid;
    private String mEffScanCounter;
    private String mAvgRssi;

    public WifiFingerprintDisplayItem(String bssid, String effScanCounter, String avgRssi) {
        mBssid = bssid;
        mEffScanCounter = effScanCounter;
        mAvgRssi = avgRssi;
    }

    public String getBssid() {
        return mBssid;
    }

    public String getEffScanCounter() {
        return mEffScanCounter;
    }

    public String getAvgRssi() {
        return mAvgRssi;
    }
}
