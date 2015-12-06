package cn.jamesli.example.cp01logsensortosqlite.sensor;

import android.hardware.SensorEvent;

/**
 * Created by jamesli on 15/12/5.
 */
public class SensorDataItem {
    private long timeStamp;
    private float xAxisMeasure;
    private float yAxisMeasure;
    private float zAxisMeasure;

    public SensorDataItem(long timestamp, float x, float y, float z) {
        timeStamp = timestamp;
        xAxisMeasure = x;
        yAxisMeasure = y;
        zAxisMeasure = z;
    }

    public SensorDataItem(SensorEvent event) {
        timeStamp = event.timestamp;
        xAxisMeasure = event.values[0];
        yAxisMeasure = event.values[1];
        zAxisMeasure = event.values[2];
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public float getXAxisMeasure() {
        return xAxisMeasure;
    }

    public float getYAxisMeasure() {
        return yAxisMeasure;
    }

    public float getZAxisMeasure() {
        return zAxisMeasure;
    }

    public String toString() {
        // for csv file storage
        String string = timeStamp + "," + xAxisMeasure + "," + yAxisMeasure + "," + zAxisMeasure + "\n";
        return string;
    }
}
