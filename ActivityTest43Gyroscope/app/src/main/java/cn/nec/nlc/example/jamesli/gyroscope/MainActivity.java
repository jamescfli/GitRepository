package cn.nec.nlc.example.jamesli.gyroscope;

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

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;


public class MainActivity extends ActionBarActivity implements SensorEventListener {
    // for gyroscope calculation
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] rotationQuaternion = new float[4];
    private static final float EPSILON = 0.000000001f;
    // example code by Google: float timestamp
    // this will cause some round-up problem when computing
    //      dT = (sensorEvent.timestamp - timestamp) * NS2S
    // in onSensorChanged for Smartisan T1 with Android 4.4, i.e. dT = 0.0
    private long timestamp;

    // UI
    private TextView textViewStatus;
    private TextView textViewGyroY; // yaw      - z
    private TextView textViewGyroP; // pitch    - x
    private TextView textViewGyroR; // roll     - y
    private Button buttonOn;
    private Button buttonOff;

    // sensor register
    private SensorManager mSensorManager;
    private Sensor gyroSensor;

    // gyroscope matrix and vector
//    private float[] rotationVector = new float[4];
    private float[] rotationAngleMatrix = new float[9];
    private float[] rotationEulerAngles = new float[3];
//    private float[] deltaRotationVectorOrientation = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        textViewGyroY = (TextView) findViewById(R.id.textViewGyroY);
        textViewGyroP = (TextView) findViewById(R.id.textViewGyroP);
        textViewGyroR = (TextView) findViewById(R.id.textViewGyroR);

        buttonOn = (Button) findViewById(R.id.buttonOn);
        buttonOff = (Button) findViewById(R.id.buttonOff);

        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOn.setEnabled(false);
                buttonOff.setEnabled(true);
                mSensorManager.registerListener(MainActivity.this, gyroSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
                textViewStatus.setText("Status: sensor listener is on.");
                initGyroMatrix(); // initiate with identity matrix
            }
        });

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOff.setEnabled(false);
                buttonOn.setEnabled(true);
                mSensorManager.unregisterListener(MainActivity.this);
                textViewStatus.setText("Status: sensor listener is off.");
                textViewGyroY.setText(R.string.text_gyroy_na);
                textViewGyroP.setText(R.string.text_gyrop_na);
                textViewGyroR.setText(R.string.text_gyror_na);
            }
        });
    }

    private void initGyroMatrix() {
        // initialise rotationAngleMatrix with identity matrix
        rotationAngleMatrix[0] = 1.0f; rotationAngleMatrix[1] = 0.0f; rotationAngleMatrix[2] = 0.0f;
        rotationAngleMatrix[3] = 0.0f; rotationAngleMatrix[4] = 1.0f; rotationAngleMatrix[5] = 0.0f;
        rotationAngleMatrix[6] = 0.0f; rotationAngleMatrix[7] = 0.0f; rotationAngleMatrix[8] = 1.0f;
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
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_GYROSCOPE:     // value = 4
                // this timestamp's delta rotation to be multiplied by the current rotation
                // after computing it from the gyro sample data
                if (timestamp != 0) {
                    final float dT = (sensorEvent.timestamp - timestamp) * NS2S;    // in seconds
                    // axis of the rotation sample, not normalized yet
                    // rate or rotation in rad/s around a device's x, y, and z axis
                    float axisX = sensorEvent.values[0];
                    float axisY = sensorEvent.values[1];
                    float axisZ = sensorEvent.values[2];

                    // calculate the angular speed of the sample
                    float omegaMagnitude = sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                    // normalize the rotation vector if it's big enough to get the axis
                    // (that is, EPSILON should represent your maximum allowable margin of error)
                    if (omegaMagnitude > EPSILON) {
                        axisX /= omegaMagnitude;
                        axisY /= omegaMagnitude;
                        axisZ /= omegaMagnitude;
                    }

                    // integrate around this axis with the angular speed by the timestep
                    // in order to get a delta rotation from this sample over the timestep
                    // we will convert this axis-angle representation of the delta rotation
                    // into a quaternion before turning it into the rotation matrix.
                    float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                    float sinThetaOverTwo = sin(thetaOverTwo);
                    float cosThetaOverTwo = cos(thetaOverTwo);
                    // in Quaternions
                    rotationQuaternion[0] = sinThetaOverTwo * axisX;   // x in quaternion
                    rotationQuaternion[1] = sinThetaOverTwo * axisY;   // y in quaternion
                    rotationQuaternion[2] = sinThetaOverTwo * axisZ;   // z in quaternion
                    rotationQuaternion[3] = cosThetaOverTwo;   // w in quaternion
                }

                timestamp = sensorEvent.timestamp;
                float[] deltaRotationMatrix = new float[9];
                // helper function to convert a rotation vector to a rotation matrix
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, rotationQuaternion);
                  // User code should concatenate the delta rotation we computed with the current rotation
                  // in order to get the updated rotation.
                  // rotationCurrent = rotationCurrent * deltaRotationMatrix;
                updateRvRotationMatrix(deltaRotationMatrix);    // update rotationAngleMatrix[]

                SensorManager.getOrientation(rotationAngleMatrix, rotationEulerAngles);
                textViewGyroY.setText("GyroY: " +
                        String.valueOf(Math.toDegrees(rotationEulerAngles[0])));
                textViewGyroP.setText("GyroP: " +
                        String.valueOf(Math.toDegrees(rotationEulerAngles[1])));
                textViewGyroR.setText("GyroR: " +
                        String.valueOf(Math.toDegrees(rotationEulerAngles[2])));
                break;
            default:
                Toast.makeText(this, "Unknown sensor type event", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateRvRotationMatrix(float[] deltaRotationMatrix) {
        float[] rvRotationMatrixCopy = rotationAngleMatrix.clone();
        rotationAngleMatrix[0] = rvRotationMatrixCopy[0] * deltaRotationMatrix[0]
                + rvRotationMatrixCopy[1] * deltaRotationMatrix[3]
                + rvRotationMatrixCopy[2] * deltaRotationMatrix[6];
        rotationAngleMatrix[1] = rvRotationMatrixCopy[0] * deltaRotationMatrix[1]
                + rvRotationMatrixCopy[1] * deltaRotationMatrix[4]
                + rvRotationMatrixCopy[2] * deltaRotationMatrix[7];
        rotationAngleMatrix[2] = rvRotationMatrixCopy[0] * deltaRotationMatrix[2]
                + rvRotationMatrixCopy[1] * deltaRotationMatrix[5]
                + rvRotationMatrixCopy[2] * deltaRotationMatrix[8];
        rotationAngleMatrix[3] = rvRotationMatrixCopy[3] * deltaRotationMatrix[0]
                + rvRotationMatrixCopy[4] * deltaRotationMatrix[3]
                + rvRotationMatrixCopy[5] * deltaRotationMatrix[6];
        rotationAngleMatrix[4] = rvRotationMatrixCopy[3] * deltaRotationMatrix[1]
                + rvRotationMatrixCopy[4] * deltaRotationMatrix[4]
                + rvRotationMatrixCopy[5] * deltaRotationMatrix[7];
        rotationAngleMatrix[5] = rvRotationMatrixCopy[3] * deltaRotationMatrix[2]
                + rvRotationMatrixCopy[4] * deltaRotationMatrix[5]
                + rvRotationMatrixCopy[5] * deltaRotationMatrix[8];
        rotationAngleMatrix[6] = rvRotationMatrixCopy[6] * deltaRotationMatrix[0]
                + rvRotationMatrixCopy[7] * deltaRotationMatrix[3]
                + rvRotationMatrixCopy[8] * deltaRotationMatrix[6];
        rotationAngleMatrix[7] = rvRotationMatrixCopy[6] * deltaRotationMatrix[1]
                + rvRotationMatrixCopy[7] * deltaRotationMatrix[4]
                + rvRotationMatrixCopy[8] * deltaRotationMatrix[7];
        rotationAngleMatrix[8] = rvRotationMatrixCopy[6] * deltaRotationMatrix[2]
                + rvRotationMatrixCopy[7] * deltaRotationMatrix[5]
                + rvRotationMatrixCopy[8] * deltaRotationMatrix[8];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // n.a.
    }
}
