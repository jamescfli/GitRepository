package cn.jamesli.example.aw6activityrecogitionsimple.measure;

import android.hardware.SensorEvent;

/**
 * Created by jamesli on 15/11/30.
 */
public class SensorValueItem {
    private long timeStamp;
    private float xAxisValue;
    private float yAxisValue;
    private float zAxisValue;

    public SensorValueItem(long timestamp, float x, float y, float z) {
        timeStamp = timestamp;
        xAxisValue = x;
        yAxisValue = y;
        zAxisValue = z;
    }

    public SensorValueItem(SensorEvent event) {
        timeStamp = event.timestamp;
        xAxisValue = event.values[0];
        yAxisValue = event.values[1];
        zAxisValue = event.values[2];
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public float getXAxisValue() {
        return xAxisValue;
    }

    public float getYAxisValue() {
        return yAxisValue;
    }

    public float getZAxisValue() {
        return zAxisValue;
    }

    public String toString() {
        // for csv storage or debug content display
        String string = timeStamp + "," + xAxisValue + "," + yAxisValue + "," + zAxisValue + "\n";
        return string;
    }
}
