package cn.jamesli.example.bt08ibeaconrx;

import android.util.Log;

import org.altbeacon.beacon.service.RssiFilter;

import java.util.ArrayList;

/**
 * Created by jamesli on 15/8/26.
 */
public class NoProcessFilter implements RssiFilter {
    private int rssi;
    private static ArrayList<Integer> arraylistRSSI = new ArrayList<Integer>();


    @Override
    public void addMeasurement(Integer integer) {
        this.rssi = integer;
        Log.i("NoFilter add RSSI", String.valueOf(rssi));
        arraylistRSSI.add(integer);
    }

    @Override
    public boolean noMeasurementsAvailable() {
        return false;
    }

    @Override
    public double calculateRssi() {
        Log.i("NoFilter calcalate RSSI", String.valueOf(rssi));
        return rssi;
    }
    public static void clear()
    {
        arraylistRSSI.clear();
    }
    public static ArrayList<Integer> getRssiResultArray()
    {
        return arraylistRSSI;
    }
}
