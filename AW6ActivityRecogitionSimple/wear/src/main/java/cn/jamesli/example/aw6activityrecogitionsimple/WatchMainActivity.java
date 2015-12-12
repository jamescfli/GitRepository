package cn.jamesli.example.aw6activityrecogitionsimple;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jamesli.example.aw6activityrecogitionsimple.database.LogToSqlFileAsync;
import cn.jamesli.example.aw6activityrecogitionsimple.measure.AccDataItem;

public class WatchMainActivity extends Activity implements SensorEventListener {
    private static final String TAG = "WatchMainActivity";

    private TextView mTextViewStatus;
    private Button mButtonDownStairs;
    private Button mButtonUpStairs;
    private Button mButtonBiking;
    private Button mButtonDriving;
    private Button mButtonWalking;
    private Button mButtonRunning;
    private Button mButtonStill;

    private Button mButtonLogToFile;
    private boolean isSensorListenerRegistered;
    private boolean isActivityButtonPressed;

    private SensorManager mSensorManager;
    private Sensor mSensorAcc;

    // temp data storage
    private List<AccDataItem> mListAccData;

    private enum ActivityInstances {
        DOWNSTAIRS,
        UPSTAIRS,
        BIKING,
        DRIVING,
        WALKING,
        RUNNING,    // add more activities, 2015/12/11
//        WARMUP,     // add more FFS
//        SWIMMING,   // add more FFS
        STILL       // add more
    }
    private ActivityInstances mActivityLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);

        Log.i(TAG, "onCreate()");

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextViewStatus = (TextView) stub.findViewById(R.id.text_status);
                mButtonDownStairs = (Button) stub.findViewById(R.id.button_downstair);
                mButtonUpStairs = (Button) stub.findViewById(R.id.button_upstair);
                mButtonBiking = (Button) stub.findViewById(R.id.button_biking);
                mButtonDriving = (Button) stub.findViewById(R.id.button_driving);
                mButtonWalking = (Button) stub.findViewById(R.id.button_walking);
                mButtonRunning = (Button) stub.findViewById(R.id.button_running);
                mButtonStill = (Button) stub.findViewById(R.id.button_still);

                mButtonLogToFile = (Button) stub.findViewById(R.id.button_save);

                // storage button
                mButtonLogToFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // prevent from further pressing
                        mButtonLogToFile.setEnabled(false);
                        // turn off Sensor Listener first
                        // resume to initial state without saving the measurements to file
                        isSensorListenerRegistered = false;
                        // unregister sensor listener
                        mSensorManager.unregisterListener(WatchMainActivity.this);
                        Toast.makeText(WatchMainActivity.this, "Sensor Unregistered ..", Toast.LENGTH_LONG).show();
                        if (mListAccData == null || mListAccData.isEmpty()) {
                            mTextViewStatus.setText("No data in list ..");
                        } else {
                            // prepare log file
                            String nameOfActivity = mActivityLabel.toString();
                            LogToSqlFileAsync logToSqlFileAsync = new LogToSqlFileAsync(
                                    WatchMainActivity.this,
                                    nameOfActivity
                            );
                            mTextViewStatus.setText("Saving " + nameOfActivity + " data. Pls wait ..");
                            logToSqlFileAsync.saveToExternalCacheDir(mListAccData);
                        }
                        // resume the button
                        mButtonLogToFile.setEnabled(true);
                    }
                });
            }
        });

        isSensorListenerRegistered = false;
        isActivityButtonPressed = false;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // no gravity
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // with gravity
    }

    // when one of activity buttons is pressed
    public void onClickActivityButton(View v) {
        if (!isActivityButtonPressed) {
            switch (v.getId()) {
                case R.id.button_downstair:
                    mActivityLabel = ActivityInstances.DOWNSTAIRS;
                    break;
                case R.id.button_upstair:
                    mActivityLabel = ActivityInstances.UPSTAIRS;
                    break;
                case R.id.button_biking:
                    mActivityLabel = ActivityInstances.BIKING;
                    break;
                case R.id.button_driving:
                    mActivityLabel = ActivityInstances.DRIVING;
                    break;
                case R.id.button_walking:
                    mActivityLabel = ActivityInstances.WALKING;
                    break;
                case R.id.button_running:
                    mActivityLabel = ActivityInstances.RUNNING;
                    break;
                case R.id.button_still:
                    mActivityLabel = ActivityInstances.STILL;
                    break;
                default:
                    throw new IllegalArgumentException("Button pressed can not be recognized!");
            }
            startRecordingAccMeasure(mActivityLabel);
            isActivityButtonPressed = true;
        } else {
            cancelRecordingAccMeasure();
            isActivityButtonPressed = false;
        }
    }

    private void startRecordingAccMeasure(ActivityInstances activityLabel) {
        isSensorListenerRegistered = true;
        mListAccData = new ArrayList<>();   // temp data savor
        mSensorManager.registerListener(this, mSensorAcc, SensorManager.SENSOR_DELAY_UI);
        disableRestButtons(activityLabel);
        mTextViewStatus.setText("Start recording acc data ..");
    }

    private void cancelRecordingAccMeasure() {
        enableAllButtons(true);
        // clear the measurements
        if (mListAccData != null) { // does not care whether it is empty or not
            mListAccData.clear();   // for further usage
        }
        // show text message
        mTextViewStatus.setText("Data cleared ..");
    }

    private void disableRestButtons(ActivityInstances activityLabel) {
        enableAllButtons(false);    // disable all activity buttons

        // leave the initiated activity button pressable
        switch (activityLabel) {
            case DOWNSTAIRS:
                mButtonDownStairs.setEnabled(true);
                break;
            case UPSTAIRS:
                mButtonUpStairs.setEnabled(true);
                break;
            case BIKING:
                mButtonBiking.setEnabled(true);
                break;
            case DRIVING:
                mButtonDriving.setEnabled(true);
                break;
            case WALKING:
                mButtonWalking.setEnabled(true);
                break;
            case RUNNING:
                mButtonRunning.setEnabled(true);
                break;
            case STILL:
                mButtonStill.setEnabled(true);
                break;
            default:
                throw new IllegalArgumentException("activityLabel = " + activityLabel.toString());
        }
    }

    private void enableAllButtons(boolean enable) {
        mButtonDownStairs.setEnabled(enable);
        mButtonUpStairs.setEnabled(enable);
        mButtonBiking.setEnabled(enable);
        mButtonDriving.setEnabled(enable);
        mButtonWalking.setEnabled(enable);
        mButtonRunning.setEnabled(enable);
        mButtonStill.setEnabled(enable);
    }

    public TextView getTextViewStatus() {
        return mTextViewStatus;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
//        setScreenBrightness(+1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
//        setScreenBrightness(-1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isSensorListenerRegistered) {
//        if (event.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION) {
//            return;
//        }
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                return;
            }
            // record the value to mListAccData
            mListAccData.add(new AccDataItem(event));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // n.a.
    }
}
