package cn.nec.nlc.jamesli.activitytest34senssamplerate;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    // UI
    private RadioGroup radioGroupSensor;
    private RadioButton radioBtnAcc;
    private RadioButton radioBtnGyro;
    private RadioButton radioBtnMag;
    private TextView textViewOutputSensorType;
    private TextView textViewOutputSamRate;
    // Sensor management
    private SensorManager sensorManager;
    private Sensor accSensor;
    private Sensor magSensor;
    private Sensor gyroSensor;
    // Sample rate calculation
    private static final int TOTAL_NUM_MEASURED = 100;
    private int counterSamMeasured;
    private long lastMarkedTimeInMilliSec;
    private boolean flagSensorSamRateMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroupSensor = (RadioGroup) findViewById(R.id.radio_button_group_sensors);
        radioBtnAcc = (RadioButton) findViewById(R.id.radio_button_acc);
        radioBtnGyro = (RadioButton) findViewById(R.id.radio_button_gyro);
        radioBtnMag = (RadioButton) findViewById(R.id.radio_button_mag);
        textViewOutputSensorType = (TextView) findViewById(R.id.textView_output_sensor_type);
        textViewOutputSamRate = (TextView) findViewById(R.id.textView_output_sensor_rate);

//        // set default checked sensor
//        radioBtnAcc.setChecked(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sensor initialization
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // calculation initialization
        initiateSamRateMeasure();
        // delete sample rate result textView if there is any
        textViewOutputSamRate.setText("");
        flagSensorSamRateMeasure = false;   // stop sampling rate measurement

        // clear all checked radio button when resumed
        radioGroupSensor.clearCheck();
        textViewOutputSensorType.setText(R.string.text_output_sensor_type);
    }

    private void initiateSamRateMeasure() {
        counterSamMeasured = 0;
        lastMarkedTimeInMilliSec = 0;
        flagSensorSamRateMeasure = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(myListener);
    }

    // view = individual radio button not the group
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // check which one was checked
        switch(view.getId()) {
            case R.id.radio_button_acc:
                if (checked) {
                    textViewOutputSensorType.setText("Selected Sensor: Accelerometer");
                    // clear previous measured sampling rate
                    textViewOutputSamRate.setText("");
                    flagSensorSamRateMeasure = false;   // stop sampling rate measurement
                    sensorManager.unregisterListener(myListener);
                }
                break;
            case R.id.radio_button_gyro:
                if (checked) {
                    textViewOutputSensorType.setText("Selected Sensor: Gyroscope");
                    // clear previous measured sampling rate
                    textViewOutputSamRate.setText("");
                    flagSensorSamRateMeasure = false;   // stop sampling rate measurement
                    sensorManager.unregisterListener(myListener);
                }
                break;
            case R.id.radio_button_mag:
                if (checked) {
                    textViewOutputSensorType.setText("Selected Sensor: Magnetic Field");
                    // clear previous measured sampling rate
                    textViewOutputSamRate.setText("");
                    flagSensorSamRateMeasure = false;   // stop sampling rate measurement
                    sensorManager.unregisterListener(myListener);
                }
                break;
            default:
                sensorManager.unregisterListener(myListener);
                throw new IllegalArgumentException("Sensor type was not recognized: "+view.getId());
        }
    }

    public void onCalculateButtonClicked(View view) {
        counterSamMeasured = 0;
        // record the starting time
        lastMarkedTimeInMilliSec = System.currentTimeMillis();
        // topple measurement switch on
        flagSensorSamRateMeasure = true;
        // display the starting of measurement
        textViewOutputSamRate.setText("measurement started");

        // check which radio button is checked
        switch (radioGroupSensor.getCheckedRadioButtonId()) {
            case R.id.radio_button_acc:
                // register to the new sensor listener
                sensorManager.registerListener(myListener, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
                break;
            case R.id.radio_button_gyro:
                sensorManager.registerListener(myListener, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
                break;
            case R.id.radio_button_mag:
                sensorManager.registerListener(myListener, magSensor, SensorManager.SENSOR_DELAY_FASTEST);
                break;
            default:
                Toast.makeText(this, "No sensor was selected", Toast.LENGTH_LONG).show();
                // resume default values
                lastMarkedTimeInMilliSec = 0;
                flagSensorSamRateMeasure = false;
                textViewOutputSamRate.setText("");
        }
    }

    final SensorEventListener myListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            // skip sample rate measurement if flagSensorSamRateMeasure = false
            if (flagSensorSamRateMeasure) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        // we are not interested in the values
                        sampleRateCalculationDisplay();
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        sampleRateCalculationDisplay();
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        sampleRateCalculationDisplay();
                        break;
                    default:
                        // n.a.
                }
            }
        }

        private void sampleRateCalculationDisplay() {
            if (counterSamMeasured < TOTAL_NUM_MEASURED) {
                counterSamMeasured++;
            } else {
                // counter already reach TOTAL_NUM_MEASURED
                long currentTimeInMilliSec = System.currentTimeMillis();
                double samplingRate = TOTAL_NUM_MEASURED/
                        ((currentTimeInMilliSec - lastMarkedTimeInMilliSec) / 1000.0);
                // display sampling rate in the output textview
                MainActivity.this.textViewOutputSamRate.setText("Sampling Rate: "
                        + String.valueOf(samplingRate));
                // renew counter and time marker
                lastMarkedTimeInMilliSec = currentTimeInMilliSec;
                counterSamMeasured = 0;
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // n.a.
        }
    };
}
