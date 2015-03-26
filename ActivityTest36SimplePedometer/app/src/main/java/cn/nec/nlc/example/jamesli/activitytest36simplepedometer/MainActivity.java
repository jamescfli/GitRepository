package cn.nec.nlc.example.jamesli.activitytest36simplepedometer;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private TextView textViewStepCounter;
    private TextView textViewDisplayAndroidPedo;
    private SensorManager sensorManager;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch(sensorEvent.sensor.getType()) {
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    System.arraycopy(sensorEvent.values, 0, accResultVector, 0, 3);
                    // add new value to the raw data list and derive sma list
                    accValuesOnZaxis.addNewDataToRawList(accResultVector[2]);   // consider Z-axis only
                    // check whether peak has appeared
                    if (accValuesOnZaxis.findPeak()) {
                        stepCounterPeakDetection++;
                        // renew textView
                        textViewStepCounter.setText("Step Conducted: " + stepCounterPeakDetection);
                    } // otherwise, wait for the next sensor updates
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    if (sensorEvent.values[0] == 1.0f) {
                        stepCounterAndroidNative++;
                    }
                    textViewDisplayAndroidPedo.setText("Step Conducted: " + stepCounterAndroidNative);
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Unknown sensor type event", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            // n.a.
        }
    };

    private float[] accResultVector;
    private int stepCounterPeakDetection;
    private int stepCounterAndroidNative;
    private AccValuesOnZaxis accValuesOnZaxis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewStepCounter = (TextView) findViewById(R.id.textViewStepCounter);
        textViewDisplayAndroidPedo = (TextView) findViewById(R.id.textViewDisplayAndroidPedo);

        //initiate sensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accResultVector = new float[3];
    }

    @Override
    protected void onResume() {
        super.onResume();
        stepCounterPeakDetection = 0;
        stepCounterAndroidNative = 0;
        int smaListLength = 15;     // smoothing factor, empirical value
        int peakListLength = 7;     // peak detecting samples, empirical value
        float peakDetectThreshold = 3;  // acc has to be significant enough, empirical value
        accValuesOnZaxis = new AccValuesOnZaxis(smaListLength, peakListLength,
                peakDetectThreshold);
        textViewStepCounter.setText(getText(R.string.textview_stepcounter_initial));
        textViewDisplayAndroidPedo.setText(getText(R.string.textview_stepcounter_initial));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // leave the app by unregister sensor listener
        sensorManager.unregisterListener(sensorEventListener);
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



    public void onClickStartButton(View view) {
        // Android API 19 add Sensor of "TYPE_STEP_COUNTER"
        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (sensorManager.registerListener(sensorEventListener, stepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST)) {
            // the sensor is supported
            // notify the user the counting has started
            Toast.makeText(this, "Step counter started", Toast.LENGTH_LONG).show();
        } else {
            textViewDisplayAndroidPedo.setText("native Step Detector is not supported.");
        }

        // traditional Pedometer with linear accelerometer sensor
        Sensor linearAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(sensorEventListener, linearAccSensor, SensorManager.SENSOR_DELAY_FASTEST);


    }

    public void onClickResetButton(View view) {
        // unregister sensor
        sensorManager.unregisterListener(sensorEventListener);
        // reset textView to original one with 0 step
        textViewStepCounter.setText(getText(R.string.textview_stepcounter_initial));
        textViewDisplayAndroidPedo.setText(getText(R.string.textview_stepcounter_initial));
        stepCounterPeakDetection = 0;
        stepCounterAndroidNative = 0;
        accValuesOnZaxis.clearSavedAccData();   // clear saved list
    }
}
