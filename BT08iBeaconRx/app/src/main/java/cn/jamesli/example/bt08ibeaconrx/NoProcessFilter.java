package cn.jamesli.example.bt08ibeaconrx;

import android.util.Log;

import org.altbeacon.beacon.service.RssiFilter;

import java.util.ArrayList;

/**
 * Created by jamesli on 15/8/26.
 */
public class NoProcessFilter implements RssiFilter {
    private int rssi;
    private ArrayList<Integer> arrayListRssi = new ArrayList<Integer>();


    @Override
    public void addMeasurement(Integer integer) {
        this.rssi = integer;
        Log.i("NoFilter add RSSI", String.valueOf(rssi));
        arrayListRssi.add(integer);
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

    public ArrayList<Integer> getRssiResultArray()
    {
        return arrayListRssi;
    }
}
