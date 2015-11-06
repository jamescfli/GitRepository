package cn.jamesli.example.aw2wififingerprintfinder.wifiutil;

/**
 * Created by jamesli on 15/11/5.
 */
public class WifiSimpleSaver {
    private String macAddress;
    private float rssi;

    public WifiSimpleSaver(String macAddress, float rssi) {
        this.macAddress = macAddress;
        this.rssi = rssi;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public float getRssi() {
        return rssi;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "MAC" + this.macAddress
                + " RSSI " + ((float) Math.round(rssi*100))/100f;
    }
}
