package cn.jamesli.example.bt10ibeacontxrx.wifitutil;

import android.net.wifi.ScanResult;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by jamesli on 15/9/23.
 */
public class WifiFingerprint {
    private static final String FINGERPRINT_DEFAULT_NAME = "Fingerprint";
    private String nameOfFingerprint;
    // Timestamp - Long, MAC address - String, one RSSI measurement - Integer
    private Table<Long, String, Integer> tableOfRssiSamples;

    public WifiFingerprint(String name) {
        if (name != null && name.length() > 0) {
            nameOfFingerprint = name;
        } else {
            nameOfFingerprint = FINGERPRINT_DEFAULT_NAME;
        }
        tableOfRssiSamples = HashBasedTable.create();
    }

    public void putOneScanList(List<ScanResult> listOfScanResults) {
        // get current time as the row index
        Date date = new Date();
        long currentTimestamp = date.getTime();
        for (ScanResult scanResult : listOfScanResults) {
            tableOfRssiSamples.put(currentTimestamp, scanResult.BSSID, scanResult.level);
        }
        // if not initiated, return null
    }

    public String getNameOfFingerprint() {
        return nameOfFingerprint;
    }

    public List<Integer> getSamplesOfOneAp(String macAddress) {
        List<Integer> samplesOfOneAp = new ArrayList<>();
        for (Integer rssi : tableOfRssiSamples.column(macAddress).values()) {
            if (rssi !=  null) {
                samplesOfOneAp.add(rssi);
            }
        }
        return samplesOfOneAp;
    }

    // get row set
    public Set<Long> getTimestampSet() {
        return tableOfRssiSamples.rowKeySet();
    }

    // get column set
    public Set<String> getMacAddressSet() {
        return tableOfRssiSamples.columnKeySet();
    }

    public boolean isRssiTableEmpty() {
        return tableOfRssiSamples.isEmpty();
    }

    public Integer getRssi(Long row, String column) {
        return tableOfRssiSamples.get(row, column);
    }

    public void clear() {
        nameOfFingerprint = FINGERPRINT_DEFAULT_NAME;
        tableOfRssiSamples.clear();
    }
}
