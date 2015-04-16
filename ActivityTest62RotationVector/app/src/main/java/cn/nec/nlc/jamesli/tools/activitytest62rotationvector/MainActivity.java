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

import java.text.DecimalFormat;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;


public class MainActivity extends ActionBarActivity implements SensorEventListener{
    private Button buttonStart;
    private Button buttonStop;
    // TRV stands for Sensor.TYPE_ROTATION_VECTOR
    private TextView textViewRvTrvYaw;
    private TextView textViewRvTrvPitch;
    private TextView textViewRvTrvRoll;
    // Mag derived from Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR
    private TextView textViewRvMagYaw;
    private TextView textViewRvMagPitch;
    private TextView textViewRvMagRoll;
    // Rm - Gravity + Magnetic Field to get Rotation Matrix
    private TextView textViewRvRmYaw;
    private TextView textViewRvRmPitch;
    private TextView textViewRvRmRoll;
    // Gyro derived by Gyroscope output, initiated by Sensor.TYPE_ROTATION_VECTOR result
    private TextView textViewRvGyroYaw;
    private TextView textViewRvGyroPitch;
    private TextView textViewRvGyroRoll;

    private SensorManager mSensorManager;
    private Sensor mSensorRvGyro;
    private Sensor mSensorRvMag;
    private Sensor mSensorGravity;
    private Sensor mSensorGeomagnetic;
    private Sensor mSensorGyro;

    // intermidiate values
    private float[] quaternion = new float[5];
    private float[] rotationMatrix = new float[9];
    private float[] eulerAngles = new float[3];
    private float[] gravityVector = new float[3];
    private float[] geomagneticVector = new float[3];
    private float[] rotationMatrixGyro = new float[9];

    // gyroscoep calculation
    private static final float EPSILON = 0.1f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private long timeStamp = 0;
    private boolean isRotMatrixGyroInitiated;
    private float[] angleVelocityVector = new float[3];
    private float[] deltaRotationVector = new float[4];
    private float[] deltaRotationMatrix = new float[9];

    // display format
    DecimalFormat displayFormat = new DecimalFormat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // associate UI
        associateUiButtonsTexviews();

        // sensor related
        defineSensorsBySensormanager();

        // define buttons
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAllSensors();
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

        // regulate format for TextView display
        displayFormat.applyPattern("0.00");
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonStart.setEnabled(true);
        buttonStop.setEnabled(false);
        if (mSensorRvGyro == null) {
            Toast.makeText(this, "No RV sensor by Gyroscope support!", Toast.LENGTH_LONG).show();
            textViewRvTrvYaw.setText("N/A");
            textViewRvTrvPitch.setText("N/A");
            textViewRvTrvRoll.setText("N/A");
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
        if ((mSensorRvGyro == null) || (mSensorGyro == null)) {
            Toast.makeText(this, "RV sensor or Gyroscope NOT exists!", Toast.LENGTH_LONG).show();
            textViewRvGyroYaw.setText("N/A");
            textViewRvGyroPitch.setText("N/A");
            textViewRvGyroRoll.setText("N/A");
        }
        isRotMatrixGyroInitiated = false;   // turn out to be true after initialization
        clearAllResultsInTextview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this) {
            int sensorType = sensorEvent.sensor.getType();
            switch (sensorType) {
                case Sensor.TYPE_ROTATION_VECTOR:
                    System.arraycopy(sensorEvent.values, 0, quaternion, 0, 5);
                    mSensorManager.getRotationMatrixFromVector(rotationMatrix, quaternion);
                    mSensorManager.getOrientation(rotationMatrix, eulerAngles);
                    if (!isRotMatrixGyroInitiated) {
                        for (int i = 0; i < 9; i++) {
                            rotationMatrixGyro[i] = rotationMatrix[i];
                        }
                        // finished the initialization for pure Gyroscope result
                        isRotMatrixGyroInitiated = true;
                    }
                    displayRvTrvInTextview();
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
                case Sensor.TYPE_GYROSCOPE:
                    System.arraycopy(sensorEvent.values, 0, angleVelocityVector, 0, 3);
                    computeGyroResult(angleVelocityVector, sensorEvent.timestamp);
                    displayRvGyroInTextview();
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // n.a.
    }

    private void computeGyroResult(float[] angleVelocityVector, long timestampSensor) {
        if (timeStamp != 0) {
            final double dT = (timestampSensor - timeStamp) * NS2S;
            float axisX = angleVelocityVector[0];
            float axisY = angleVelocityVector[1];
            float axisZ = angleVelocityVector[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = (float)(omegaMagnitude * dT / 2.0f);
            float sinThetaOverTwo = sin(thetaOverTwo);
            float cosThetaOverTwo = cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;

            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            rotationMatrixGyro = matrixMultiplication(rotationMatrixGyro, deltaRotationMatrix);
            SensorManager.getOrientation(rotationMatrixGyro, eulerAngles);
        }
        timeStamp = timestampSensor;
    }

    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    private void associateUiButtonsTexviews() {
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        textViewRvTrvYaw = (TextView) findViewById(R.id.textViewRvTrvYaw);
        textViewRvTrvPitch = (TextView) findViewById(R.id.textViewRvTrvPitch);
        textViewRvTrvRoll = (TextView) findViewById(R.id.textViewRvTrvRoll);
        textViewRvMagYaw = (TextView) findViewById(R.id.textViewRvMagYaw);
        textViewRvMagPitch = (TextView) findViewById(R.id.textViewRvMagPitch);
        textViewRvMagRoll = (TextView) findViewById(R.id.textViewRvMagRoll);
        textViewRvRmYaw = (TextView) findViewById(R.id.textViewRvRmYaw);
        textViewRvRmPitch = (TextView) findViewById(R.id.textViewRvRmPitch);
        textViewRvRmRoll = (TextView) findViewById(R.id.textViewRvRmRoll);
        textViewRvGyroYaw = (TextView) findViewById(R.id.textViewRvGyroYaw);
        textViewRvGyroPitch = (TextView) findViewById(R.id.textViewRvGyroPitch);
        textViewRvGyroRoll = (TextView) findViewById(R.id.textViewRvGyroRoll);
    }

    private void defineSensorsBySensormanager() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorRvGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorRvMag = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorGeomagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    private void registerAllSensors() {
        if (mSensorRvGyro != null) {
            mSensorManager.registerListener(this, mSensorRvGyro,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mSensorRvMag != null) {
            mSensorManager.registerListener(this, mSensorRvMag,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mSensorGravity != null) {
            mSensorManager.registerListener(this, mSensorGravity,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mSensorGeomagnetic != null) {
            mSensorManager.registerListener(this, mSensorGeomagnetic,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (mSensorGyro != null) {
            mSensorManager.registerListener(this, mSensorGyro,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private void clearAllResultsInTextview() {
        textViewRvTrvYaw.setText("-");
        textViewRvTrvPitch.setText("-");
        textViewRvTrvRoll.setText("-");
        textViewRvMagYaw.setText("-");
        textViewRvMagPitch.setText("-");
        textViewRvMagRoll.setText("-");
        textViewRvRmYaw.setText("-");
        textViewRvRmPitch.setText("-");
        textViewRvRmRoll.setText("-");
        textViewRvGyroYaw.setText("-");
        textViewRvGyroPitch.setText("-");
        textViewRvGyroRoll.setText("-");
    }

    private void displayRvTrvInTextview() {
        textViewRvTrvYaw.setText(displayFormat.format(Math.toDegrees(eulerAngles[0])));
        textViewRvTrvPitch.setText(displayFormat.format(Math.toDegrees(eulerAngles[1])));
        textViewRvTrvRoll.setText(displayFormat.format(Math.toDegrees(eulerAngles[2])));
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

    private void displayRvGyroInTextview() {
        textViewRvGyroYaw.setText(displayFormat.format(Math.toDegrees(eulerAngles[0])));
        textViewRvGyroPitch.setText(displayFormat.format(Math.toDegrees(eulerAngles[1])));
        textViewRvGyroRoll.setText(displayFormat.format(Math.toDegrees(eulerAngles[2])));
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
