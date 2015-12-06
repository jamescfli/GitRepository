package cn.jamesli.example.cp01logsensortosqlite.sensor;

import android.hardware.SensorEvent;

/**
 * Created by jamesli on 15/12/5.
 */
public class GyroSensorDataItem extends SensorDataItem {
    public GyroSensorDataItem(long timestamp, float x, float y, float z) {
        super(timestamp, x, y, z);
    }

    public GyroSensorDataItem(SensorEvent sensorEvent) {
        super(sensorEvent);
    }

    public long getGyroTimeStamp() {
        return getTimeStamp();
    }

    public float getGyroXAxisMeasure() {
        return getXAxisMeasure();
    }

    public float getGyroYAxisMeasure() {
        return getYAxisMeasure();
    }

    public float getGyroZAxisMeasure() {
        return getZAxisMeasure();
    }
}