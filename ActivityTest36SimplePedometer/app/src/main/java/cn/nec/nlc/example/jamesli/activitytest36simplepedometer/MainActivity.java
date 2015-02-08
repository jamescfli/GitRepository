package cn.nec.nlc.example.jamesli.activitytest36simplepedometer;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements SensorEventListener {
    private TextView textViewStepCounter;
    private SensorManager sensorManager;

    private float[] accResultVector;
    private int stepCounter;
    private AccValuesOnZaxis accValuesOnZaxis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewStepCounter = (TextView) findViewById(R.id.textViewStepCounter);

        //initiate sensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accResultVector = new float[3];
    }

    @Override
    protected void onResume() {
        super.onResume();
        stepCounter = 0;    // set to null
        int smaListLength = 15;     // smoothing factor, empirical value
        int peakListLength = 7;     // peak detecting samples, empirical value
        float peakDetectThreshold = 3;  // acc has to be significant enough, empirical value
        accValuesOnZaxis = new AccValuesOnZaxis(smaListLength, peakListLength,
                peakDetectThreshold);
        textViewStepCounter.setText(R.string.textview_stepcounter_initial);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // leave the app by unregister sensor listener
        sensorManager.unregisterListener(this);
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
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch(sensorEvent.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                System.arraycopy(sensorEvent.values, 0, accResultVector, 0, 3);
                // add new value to the raw data list and derive sma list
                accValuesOnZaxis.addNewDataToRawList(accResultVector[2]);   // consider Z-axis only
                // check whether peak has appeared
                if (accValuesOnZaxis.findPeak()) {
                    stepCounter++;
                    // renew textView
                    textViewStepCounter.setText("Step Conducted: " + stepCounter);
                } // otherwise, wait for the next sensor updates
                break;
            default:
                Toast.makeText(this, "Unknown sensor type event", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // n.a.
    }

    public void onClickStartButton(View view) {
        Sensor linearAccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, linearAccSensor, SensorManager.SENSOR_DELAY_FASTEST);
        // notify the user the counting has started
        Toast.makeText(this, "Step counter started", Toast.LENGTH_LONG).show();
    }

    public void onClickResetButton(View view) {
        // unregister sensor
        sensorManager.unregisterListener(this);
        // reset textView to original one with 0 step
        textViewStepCounter.setText(R.string.textview_stepcounter_initial);
        stepCounter = 0;    // null step counter
        accValuesOnZaxis.clearSavedAccData();   // clear saved list
    }
}
