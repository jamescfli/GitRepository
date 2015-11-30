package cn.jamesli.example.aw6activityrecogitionsimple;

import android.hardware.SensorEvent;

/**
 * Created by jamesli on 15/11/30.
 */
public class AccDataItem {
    private long timeStamp;
    private float xAxisAcc;
    private float yAxisAcc;
    private float zAxisAcc;

    public AccDataItem(long timestamp, float x, float y, float z) {
        timeStamp = timestamp;
        xAxisAcc = x;
        yAxisAcc = y;
        zAxisAcc = z;
    }

    public AccDataItem(SensorEvent event) {
        timeStamp = event.timestamp;
        xAxisAcc = event.values[0];
        yAxisAcc = event.values[1];
        zAxisAcc = event.values[2];
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public float getXAxisAcc() {
        return xAxisAcc;
    }

    public float getYAxisAcc() {
        return yAxisAcc;
    }

    public float getZAxisAcc() {
        return zAxisAcc;
    }

    public String toString() {
        // for csv file storage
        String string = timeStamp + "," + xAxisAcc + "," + yAxisAcc + "," + zAxisAcc + "\n";
        return string;
    }
}
