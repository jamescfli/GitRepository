package cn.jamesli.example.aw3sensortester;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.WindowManager;
import android.widget.TextView;

public class SensorTestActivity extends Activity {

    // UI
    private TextView mTextViewMeasureLAcc;
    private TextView mTextViewMeasureAcc;
    private TextView mTextViewMeasureGyro;
    private TextView mTextViewMeasureMag;
    private TextView mTextViewMeasureGrav;

    // sensor
    private SensorManager mSensorManager;
    private SensorEventReceiver mSensorEventReceiver;

    // calculation
    private static final int COUNTER_CAP_FOR_ALL_SENSORS = 50;
    private long lastTimeMarkerLAcc;
    private long lastTimeMarkerAcc;
    private long lastTimeMarkerGyro;
    private long lastTimeMarkerMag;
    private long lastTimeMarkerGrav;
    private int counterLAcc;
    private int counterAcc;
    private int counterGyro;
    private int counterMag;
    private int counterGrav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextViewMeasureLAcc = (TextView) stub.findViewById(R.id.text_measure_lacc);
                mTextViewMeasureAcc = (TextView) stub.findViewById(R.id.text_measure_acc);
                mTextViewMeasureGyro = (TextView) stub.findViewById(R.id.text_measure_gyro);
                mTextViewMeasureMag = (TextView) stub.findViewById(R.id.text_measure_mag);
                mTextViewMeasureGrav = (TextView) stub.findViewById(R.id.text_measure_grav);
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorEventReceiver = new SensorEventReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
                .getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);

//        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
//                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
//        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
//                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
//        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
//                .getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
//        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
//                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
//        mSensorManager.registerListener(mSensorEventReceiver, mSensorManager
//                .getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_GAME);

        counterLAcc = 0;
        counterAcc = 0;
        counterGyro = 0;
        counterMag = 0;
        counterGrav = 0;
        long currentTime = System.currentTimeMillis();
        lastTimeMarkerLAcc = currentTime;
        lastTimeMarkerAcc = currentTime;
        lastTimeMarkerGyro = currentTime;
        lastTimeMarkerMag = currentTime;
        lastTimeMarkerGrav = currentTime;

        setScreenBrightness(+1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventReceiver);
        setScreenBrightness(-1);
    }

    private void setScreenBrightness(int brightness) {
        // brightness -1 sleep mode, 0 dim screen, +1 full bright screen
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = brightness;
        getWindow().setAttributes(params);
    }

    private class SensorEventReceiver implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch(event.sensor.getType()) {
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    counterLAcc++;
                    if (counterLAcc == COUNTER_CAP_FOR_ALL_SENSORS) {
                        long currentTime = System.currentTimeMillis();
                        mTextViewMeasureLAcc.setText(String.valueOf(Math.round
                                (COUNTER_CAP_FOR_ALL_SENSORS*1000f
                                        /(currentTime - lastTimeMarkerLAcc))));
                        // renew
                        lastTimeMarkerLAcc = currentTime;
                        counterLAcc = 0;
                    }
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    counterAcc++;
                    if (counterAcc == COUNTER_CAP_FOR_ALL_SENSORS) {
                        long currentTime = System.currentTimeMillis();
                        mTextViewMeasureAcc.setText(String.valueOf(Math.round
                                (COUNTER_CAP_FOR_ALL_SENSORS*1000f
                                /(currentTime - lastTimeMarkerAcc))));
                        // renew
                        lastTimeMarkerAcc = currentTime;
                        counterAcc = 0;
                    }
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    counterGyro++;
                    if (counterGyro == COUNTER_CAP_FOR_ALL_SENSORS) {
                        long currentTime = System.currentTimeMillis();
                        mTextViewMeasureGyro.setText(String.valueOf(Math.round
                                (COUNTER_CAP_FOR_ALL_SENSORS*1000f
                                /(currentTime - lastTimeMarkerGyro))));
                        // renew
                        lastTimeMarkerGyro = currentTime;
                        counterGyro = 0;
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    counterMag++;
                    if (counterMag == COUNTER_CAP_FOR_ALL_SENSORS) {
                        long currentTime = System.currentTimeMillis();
                        mTextViewMeasureMag.setText(String.valueOf(Math.round
                                (COUNTER_CAP_FOR_ALL_SENSORS*1000f
                                /(currentTime - lastTimeMarkerMag))));
                        // renew
                        lastTimeMarkerMag = currentTime;
                        counterMag = 0;
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    counterGrav++;
                    if (counterGrav == COUNTER_CAP_FOR_ALL_SENSORS) {
                        long currentTime = System.currentTimeMillis();
                        mTextViewMeasureGrav.setText(String.valueOf(Math.round
                                (COUNTER_CAP_FOR_ALL_SENSORS*1000f
                                        /(currentTime - lastTimeMarkerGrav))));
                        // renew
                        lastTimeMarkerGrav = currentTime;
                        counterGrav = 0;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Sensor Event with uninterested event");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // n.a.
        }
    }
}
