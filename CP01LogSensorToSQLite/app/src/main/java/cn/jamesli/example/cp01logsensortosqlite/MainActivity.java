package cn.jamesli.example.cp01logsensortosqlite;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import cn.jamesli.example.cp01logsensortosqlite.database.SensorMeasureSavor;
import cn.jamesli.example.cp01logsensortosqlite.sensor.AccSensorDataItem;
import cn.jamesli.example.cp01logsensortosqlite.sensor.GyroSensorDataItem;


public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGyro;

    private List<AccSensorDataItem> mListAccMeasure;
    private List<GyroSensorDataItem> mListGyroMeasure;
    private AccSensorDataItem mAccDataItem;
    private GyroSensorDataItem mGyroDataItem;
    private boolean isAccMeasureUpdated;
    private boolean isGyroMeasureUpdated;

    // SQLite savor
    private SensorMeasureSavor mSensorMeasureSavor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mListAccMeasure = new ArrayList<>();
        mListGyroMeasure = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAccMeasureUpdated = false;
        isGyroMeasureUpdated = false;
        mSensorManager.registerListener(this, mSensorAcc, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorGyro, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

        mSensorMeasureSavor = new SensorMeasureSavor(this);
        mSensorMeasureSavor.open();
        if (mListAccMeasure.size() == mListGyroMeasure.size()) {
            for (int i = 0, SIZE = mListAccMeasure.size(); i < SIZE; i++) {
                mSensorMeasureSavor.createMeasure(mListAccMeasure.get(i), mListGyroMeasure.get(i));
            }
        } else {
            throw new IllegalArgumentException("Acc list and Gyro list do not match in item numbers.");
        }
        mSensorMeasureSavor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                mAccDataItem = new AccSensorDataItem(event);
                isAccMeasureUpdated = true;
                // to sync
                if (isGyroMeasureUpdated) {
                    // add both to the list
                    mListAccMeasure.add(mAccDataItem);
                    mListGyroMeasure.add(mGyroDataItem);
                    isAccMeasureUpdated = false;
                    isGyroMeasureUpdated = false;
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                mGyroDataItem = new GyroSensorDataItem(event);
                isGyroMeasureUpdated = true;
                if (isAccMeasureUpdated) {
                    mListAccMeasure.add(mAccDataItem);
                    mListGyroMeasure.add(mGyroDataItem);
                    isAccMeasureUpdated = false;
                    isGyroMeasureUpdated = false;
                }
                break;
            default:
                throw new IllegalArgumentException("This sensor type was not registered!");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
