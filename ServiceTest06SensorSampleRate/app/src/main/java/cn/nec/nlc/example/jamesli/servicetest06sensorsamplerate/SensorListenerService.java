package cn.nec.nlc.example.jamesli.servicetest06sensorsamplerate;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SensorListenerService extends Service implements SensorEventListener {
    public static final String NOTIFICATION = "cn.nec.nlc.example.jamesli.servicetest06";
    public static final String SAMPLE_RATE = "sample_rate";  // sample rate result key of intent
    public static final String SENSOR_TYPE = "monitored_sensor_type";   // acc, gyro or mag
    public static final String SENSOR_SAMPLE_RATE_LEVEL = "sample_rate_type"; // Normal, UI, Game ..

    // define IBinder for Activity and Service connection
    private final IBinder mBinder = new LocalBinder();

    // public signature, for ServiceConnection access
    public class LocalBinder extends Binder {
        SensorListenerService getService() {
            return SensorListenerService.this;
        }
    };

//    // Local Broadcast Manager to send the result back to UI
//    private LocalBroadcastManager mLocalBroadcastManager;

    // variable for sampling rate calculation
    private long timestampCurrent;
    private long timestampCalSampleRate;
    private int counterSampleRate;
    private float CALCULATION_INTERVAL = 5.0f;  // calculate every 5 seconds, subject to change

    // sensor registration
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int sensorType;     // 1 ~ Acc, 4 ~ Gyro, 2 ~ Mag
    private int sensorSampleRateLevel;  // 3, 2, 1, 0 ~ Normal, UI, Game, Fastest

    // unit trans
    private static final float NS2S = 1.0f / 1000000000.0f;

    @Override
    public IBinder onBind(Intent intent) {
        timestampCurrent = 0;
        timestampCalSampleRate = 0;
        counterSampleRate = 0;
        Bundle bundle = intent.getExtras();
        sensorType = bundle.getInt(SENSOR_TYPE);
        sensorSampleRateLevel = bundle.getInt(SENSOR_SAMPLE_RATE_LEVEL);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(sensorType);
        mSensorManager.registerListener(this, mSensor, sensorSampleRateLevel);
        Log.i("Service", "onBind()");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSensorManager.unregisterListener(this);
        Log.i("Service", "onUnbind()");
        return super.onUnbind(intent);
    }

    // implements SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i("Service#SensorEventListener", "onSensorChanged()");
        if (sensorEvent.sensor.getType() == sensorType) {
            if (timestampCurrent != 0) {
                // retrieve current time
                timestampCurrent = sensorEvent.timestamp;
                // check whether passing required time interval
                float timestampDiff = (timestampCurrent - timestampCalSampleRate) * NS2S; // in sec
                if (timestampDiff >= CALCULATION_INTERVAL) {
                    counterSampleRate++;    // received a new sample
                    float derviedSampleRate = counterSampleRate / timestampDiff;
                    // send the result out through local broadcast
                    publishResultToUiThroughLocalBroadcast(derviedSampleRate);
                    // reset counter and timestampCalSampleRate
                    timestampCalSampleRate = timestampCurrent;
                    counterSampleRate = 0;
                } else {    // have not exceeded CALCULATION_INTERVAL time interval
                    counterSampleRate++;    // keep counting sample
                }
            } else {    // timestampCurrent = 0
                // initiate timestamps
                timestampCurrent = sensorEvent.timestamp;
                timestampCalSampleRate = timestampCurrent;
                counterSampleRate = 0;
            }
        } else {
            throw new IllegalArgumentException("Somehow, type of the sensor event does not " +
                    "coincide with preset sensor type!");
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // n.a.
    }

    private void publishResultToUiThroughLocalBroadcast(float sampleRate) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(SAMPLE_RATE, sampleRate);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
