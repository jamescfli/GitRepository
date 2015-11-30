package cn.jamesli.example.aw6activityrecogitionsimple;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WatchMainActivity extends Activity implements SensorEventListener{

    private TextView mTextViewStatus;
    private Button mButtonDownStairs;
    private Button mButtonUpStairs;
    private Button mButtonBiking;
    private Button mButtonDriving;
    private Button mButtonWalking;
    private Button mButtonLogToFile;
    private boolean isActivityButtonPressed;

    private SensorManager mSensorManager;
    private Sensor mSensorAcc;

    // temp data storage
    private List<AccDataItem> mListAccData;

    private enum ActivityInstances {
        DOWN_STAIRS,
        UP_STAIRS,
        BIKING,
        DRIVING,
        WALKING
    }
    private ActivityInstances mActivityLabel;

    // file storage
    private LogToFile mLogToFile;   // initiated when 'save' button is pressed

    // for turn screen on
    private WindowManager.LayoutParams mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextViewStatus = (TextView) stub.findViewById(R.id.text_status);
                mButtonDownStairs = (Button) stub.findViewById(R.id.button_down_stair);
                mButtonUpStairs = (Button) stub.findViewById(R.id.button_up_stair);
                mButtonBiking = (Button) stub.findViewById(R.id.button_biking);
                mButtonDriving = (Button) stub.findViewById(R.id.button_driving);
                mButtonWalking = (Button) stub.findViewById(R.id.button_walking);
                mButtonLogToFile = (Button) stub.findViewById(R.id.button_save);

                // task buttons
                mButtonDownStairs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isActivityButtonPressed) {
                            mActivityLabel = ActivityInstances.DOWN_STAIRS;
                            startRecordingAccMeasure(mActivityLabel);
                        } else {
                            cancelRecordingAccMeasure();

                        }
                    }
                });
                mButtonUpStairs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isActivityButtonPressed) {
                            mActivityLabel = ActivityInstances.UP_STAIRS;
                            startRecordingAccMeasure(mActivityLabel);
                        } else {
                            cancelRecordingAccMeasure();

                        }
                    }
                });
                mButtonBiking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isActivityButtonPressed) {
                            mActivityLabel = ActivityInstances.BIKING;
                            startRecordingAccMeasure(mActivityLabel);
                        } else {
                            cancelRecordingAccMeasure();

                        }
                    }
                });
                mButtonDriving.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isActivityButtonPressed) {
                            mActivityLabel = ActivityInstances.DRIVING;
                            startRecordingAccMeasure(mActivityLabel);
                        } else {
                            cancelRecordingAccMeasure();

                        }
                    }
                });
                mButtonWalking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isActivityButtonPressed) {
                            mActivityLabel = ActivityInstances.WALKING;
                            startRecordingAccMeasure(mActivityLabel);
                        } else {
                            cancelRecordingAccMeasure();

                        }
                    }
                });

                // storage button
                mButtonLogToFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListAccData == null || mListAccData.isEmpty()) {
                            mTextViewStatus.setText("No data in list ..");
                        } else {
                            // prepare log file
                            String SAVE_FILE_PREFIX = mActivityLabel.toString();
                            String SAVE_FILE_APPENDIX = ".csv";
                            mLogToFile = new LogToFile(WatchMainActivity.this, SAVE_FILE_PREFIX, SAVE_FILE_APPENDIX);
                            // prepare saved data
                            StringBuilder stringToFile = new StringBuilder();
                            stringToFile.append("Timestamp,X,Y,Z\n");   // title line
                            for (AccDataItem item : mListAccData) {
                                stringToFile.append(item.toString());
                            }
                            mLogToFile.saveToExternalCacheDir(stringToFile.toString());
                            mTextViewStatus.setText("Data saved ..");
                        }
                    }
                });
            }
        });

        isActivityButtonPressed = false;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // no gravity
    }

    private void startRecordingAccMeasure(ActivityInstances activityLabel) {
        isActivityButtonPressed = true;
        mListAccData = new ArrayList<>();   // temp data savor
        mSensorManager.registerListener(this, mSensorAcc,
                SensorManager.SENSOR_DELAY_GAME);
        disableRestButtons(activityLabel);
        mTextViewStatus.setText("Start recording acc data ..");
    }

    private void cancelRecordingAccMeasure() {
        // unregister sensor listener
        mSensorManager.unregisterListener(this);
        // resume to initial state without saving the measurements to file
        isActivityButtonPressed = false;
        enableAllButtons();
        // clear the measurements
        if (mListAccData != null) { // does not care whether it is empty or not
            mListAccData.clear();   // for further usage
        }
        // show text message
        mTextViewStatus.setText("Data cleared ..");
    }

    private void disableRestButtons(ActivityInstances activityLabel) {
        mButtonDownStairs.setEnabled(false);
        mButtonUpStairs.setEnabled(false);
        mButtonBiking.setEnabled(false);
        mButtonDriving.setEnabled(false);
        mButtonWalking.setEnabled(false);

        // switch on the pressed button
        switch (activityLabel) {
            case DOWN_STAIRS:
                mButtonDownStairs.setEnabled(true);
                break;
            case UP_STAIRS:
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
            default:
                throw new IllegalArgumentException("activityLabel = " + activityLabel.toString());
        }
    }

    private void enableAllButtons() {
        mButtonDownStairs.setEnabled(true);
        mButtonUpStairs.setEnabled(true);
        mButtonBiking.setEnabled(true);
        mButtonDriving.setEnabled(true);
        mButtonWalking.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setScreenBrightness(+1);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        setScreenBrightness(-1);
    }

    private void setScreenBrightness(int brightness) {
        // Note: brightness
        //  -1, normal with power save sleeping mode
        //   0, dim light screen
        //  +1, full brightness
        mParams.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mParams.screenBrightness = brightness;   // 0 dim light, +1 full bright
        getWindow().setAttributes(mParams);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION) {
            return;
        }
        // record the value to mListAccData
        mListAccData.add(new AccDataItem(event));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // n.a.
    }


}
