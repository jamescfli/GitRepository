package cn.jamesli.example.cp01logsensortosqlite.sensor;

import android.hardware.SensorEvent;

/**
 * Created by jamesli on 15/12/5.
 */
public class AccSensorDataItem extends SensorDataItem {
    public AccSensorDataItem(long timestamp, float x, float y, float z) {
        super(timestamp, x, y, z);
    }

    public AccSensorDataItem(SensorEvent sensorEvent) {
        super(sensorEvent);
    }

    public long getAccTimeStamp() {
        return getTimeStamp();
    }

    public float getAccXAxisMeasure() {
        return getXAxisMeasure();
    }

    public float getAccYAxisMeasure() {
        return getYAxisMeasure();
    }

    public float getAccZAxisMeasure() {
        return getZAxisMeasure();
    }
}
