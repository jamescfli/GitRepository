package cn.nec.nlc.jamesli.tools.activitytest62rotationvector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;


public class MainActivity extends ActionBarActivity implements SensorEventListener{
    private Button buttonStart;
    private Button buttonStop;
    private TextView textViewRvGyroYaw;
    private TextView textViewRvGyroPitch;
    private TextView textViewRvGyroRoll;
    private TextView textViewRvMagYaw;
    private TextView textViewRvMagPitch;
    private TextView textViewRvMagRoll;
    private TextView textViewRvRmYaw;
    private TextView textViewRvRmPitch;
    private TextView textViewRvRmRoll;

    private SensorManager mSensorManager;
    private Sensor mSensorRvGyro;
    private Sensor mSensorRvMag;
    private Sensor mSensorGravity;
    private Sensor mSensorGeomagnetic;

    // intermidiate values
    private float[] quaternion = new float[5];
    private float[] rotationMatrix = new float[9];
    private float[] eulerAngles = new float[3];
    private float[] gravityVector = new float[3];
    private float[] geomagneticVector = new float[3];

    // display format
    DecimalFormat displayFormat = new DecimalFormat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // associate UI
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        textViewRvGyroYaw = (TextView) findViewById(R.id.textViewRvGyroYaw);
        textViewRvGyroPitch = (TextView) findViewById(R.id.textViewRvGyroPitch);
        textViewRvGyroRoll = (TextView) findViewById(R.id.textViewRvGyroRoll);
        textViewRvMagYaw = (TextView) findViewById(R.id.textViewRvMagYaw);
        textViewRvMagPitch = (TextView) findViewById(R.id.textViewRvMagPitch);
        textViewRvMagRoll = (TextView) findViewById(R.id.textViewRvMagRoll);
        textViewRvRmYaw = (TextView) findViewById(R.id.textViewRvRmYaw);
        textViewRvRmPitch = (TextView) findViewById(R.id.textViewRvRmPitch);
        textViewRvRmRoll = (TextView) findViewById(R.id.textViewRvRmRoll);

        // sensor related
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorRvGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorRvMag = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorGeomagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // define buttons
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSensorRvGyro != null) {
                    mSensorManager.registerListener(MainActivity.this, mSensorRvGyro,
                        SensorManager.SENSOR_DELAY_FASTEST);
                }
                if (mSensorRvMag != null) {
                    mSensorManager.registerListener(MainActivity.this, mSensorRvMag,
                            SensorManager.SENSOR_DELAY_FASTEST);
                }
                if (mSensorGravity != null) {
                    mSensorManager.registerListener(MainActivity.this, mSensorGravity,
                            SensorManager.SENSOR_DELAY_FASTEST);
                }
                if (mSensorGeomagnetic != null) {
                    mSensorManager.registerListener(MainActivity.this, mSensorGeomagnetic,
                            SensorManager.SENSOR_DELAY_FASTEST);
                }
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorManager.unregisterListener(MainActivity.this);
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
            }
        });

        displayFormat.applyPattern("0.00");
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonStart.setEnabled(true);
        buttonStop.setEnabled(false);
        if (mSensorRvGyro == null) {
            Toast.makeText(this, "No RV sensor by Gyroscope support!", Toast.LENGTH_LONG).show();
            textViewRvGyroYaw.setText("N/A");
            textViewRvGyroPitch.setText("N/A");
            textViewRvGyroRoll.setText("N/A");
        }
        if (mSensorRvMag == null) {
            Toast.makeText(this, "No RV sensor by Magnetic Field support!", Toast.LENGTH_LONG).show();
            textViewRvMagYaw.setText("N/A");
            textViewRvMagPitch.setText("N/A");
            textViewRvMagRoll.setText("N/A");
        }
        if ((mSensorGravity == null) || (mSensorGeomagnetic == null)) {
            Toast.makeText(this, "Gravity or Magnetic sensor NOT exists!", Toast.LENGTH_LONG).show();
            textViewRvMagYaw.setText("N/A");
            textViewRvMagPitch.setText("N/A");
            textViewRvMagRoll.setText("N/A");
        }
        clearAllResultsInTextview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ROTATION_VECTOR:
                System.arraycopy(sensorEvent.values, 0, quaternion, 0, 5);
                mSensorManager.getRotationMatrixFromVector(rotationMatrix, quaternion);
                mSensorManager.getOrientation(rotationMatrix, eulerAngles);
                displayRvGyroInTextview();
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                // TODO TYPE_GEOMAGNETIC_ROTATION_VECTOR is not working as expected, reason??
                // this branch is never being activated
                // temporary solution is to use getRotationMatrix() instead
                System.arraycopy(sensorEvent.values, 0, quaternion, 0, 5);
                mSensorManager.getRotationMatrixFromVector(rotationMatrix, quaternion);
                mSensorManager.getOrientation(rotationMatrix, eulerAngles);
                displayRvMagInTextview();
                break;
            case Sensor.TYPE_GRAVITY:
                System.arraycopy(sensorEvent.values, 0, gravityVector, 0, 3);
                mSensorManager.getRotationMatrix(rotationMatrix, null, gravityVector, geomagneticVector);
                mSensorManager.getOrientation(rotationMatrix, eulerAngles);
                displayRvRmInTextview();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(sensorEvent.values, 0, geomagneticVector, 0, 3);
                mSensorManager.getRotationMatrix(rotationMatrix, null, gravityVector, geomagneticVector);
                mSensorManager.getOrientation(rotationMatrix, eulerAngles);
                displayRvRmInTextview();
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // n.a.
    }

    private void clearAllResultsInTextview() {
        textViewRvGyroYaw.setText("-");
        textViewRvGyroPitch.setText("-");
        textViewRvGyroRoll.setText("-");
        textViewRvMagYaw.setText("-");
        textViewRvMagPitch.setText("-");
        textViewRvMagRoll.setText("-");
        textViewRvRmYaw.setText("-");
        textViewRvRmPitch.setText("-");
        textViewRvRmRoll.setText("-");
    }

    private void displayRvGyroInTextview() {
        textViewRvGyroYaw.setText(displayFormat.format(Math.toDegrees(eulerAngles[0])));
        textViewRvGyroPitch.setText(displayFormat.format(Math.toDegrees(eulerAngles[1])));
        textViewRvGyroRoll.setText(displayFormat.format(Math.toDegrees(eulerAngles[2])));
    }

    private void displayRvMagInTextview() {
        textViewRvMagYaw.setText(displayFormat.format(Math.toDegrees(eulerAngles[0])));
        textViewRvMagPitch.setText(displayFormat.format(Math.toDegrees(eulerAngles[1])));
        textViewRvMagRoll.setText(displayFormat.format(Math.toDegrees(eulerAngles[2])));
    }

    private void displayRvRmInTextview() {
        textViewRvRmYaw.setText(displayFormat.format(Math.toDegrees(eulerAngles[0])));
        textViewRvRmPitch.setText(displayFormat.format(Math.toDegrees(eulerAngles[1])));
        textViewRvRmRoll.setText(displayFormat.format(Math.toDegrees(eulerAngles[2])));
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

}
