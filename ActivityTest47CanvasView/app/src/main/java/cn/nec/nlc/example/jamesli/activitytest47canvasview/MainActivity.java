package cn.nec.nlc.example.jamesli.activitytest47canvasview;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;


public class MainActivity extends ActionBarActivity implements SensorEventListener {
    private CanvasView mCanvasView;
    private Button buttonEstimate;
    private TextView textViewCurrentDirection;
    private TextView textViewDiffBtwCurrentAndEst;
    private EditText editTextEstimate;

    // variables required by gyroscope
    private static final float EPSILON = 0.000000001f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    // example code by Google: float timestamp
    // this will cause some round-up problem when computing
    //      dT = (sensorEvent.timestamp - timestamp) * NS2S
    // in onSensorChanged for Smartisan T1 with Android 4.4, i.e. dT = 0.0
    private long timestamp;
    private float[] gyro = new float[3];    // raw data from sensorEvent
    private final float[] deltaRotationVector = new float[4];
    private float[] deltaRotationMatrix = new float[9];
    private float[] gyroMatrix = new float[9];
    private float[] gyroOrientation = new float[3];

    // start from
    private float estimatedAngle = 0;

    private SensorManager mSensorManager;
    private Sensor mGyroSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCanvasView = (CanvasView) findViewById(R.id.canvasView);
        editTextEstimate = (EditText) findViewById(R.id.editTextEst);
        buttonEstimate = (Button) findViewById(R.id.buttonEst);
        buttonEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estimatedAngle = Float.valueOf(editTextEstimate.getText().toString());
            }
        });
        textViewCurrentDirection = (TextView) findViewById(R.id.textViewCurrentDirection);
        textViewDiffBtwCurrentAndEst = (TextView) findViewById(R.id.textViewDiffCurrentEst);

        initGyroMatrix();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

//        // get screen size for debug
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;
//        Log.i("screen getWidth", String.valueOf(screenWidth));
//        Log.i("screen getHeight", String.valueOf(screenHeight));
//        Log.i("canvas height in activity",String.valueOf(mCanvasView.getHeight()));
//        Log.i("canvas width in activity",String.valueOf(mCanvasView.getWidth()));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        // too fast may incur delay in the canvas
//        mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void initGyroMatrix() {
        // initialise gyroMatrix with identity matrix
        gyroMatrix[0] = 1.0f; gyroMatrix[1] = 0.0f; gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f; gyroMatrix[4] = 1.0f; gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f; gyroMatrix[7] = 0.0f; gyroMatrix[8] = 1.0f;
        timestamp = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_GYROSCOPE:
                System.arraycopy(sensorEvent.values, 0, gyro, 0, 3);
                computeGyro(gyro, sensorEvent.timestamp);
                float angle = (float) computeGyroOrientationYaw();
                textViewCurrentDirection.setText("Current Direction: "
                        + String.valueOf(Math.toDegrees(angle)));
                textViewDiffBtwCurrentAndEst.setText("Diff from Estimated: " +
                        String.valueOf(estimatedAngle - Math.toDegrees(angle)));
                mCanvasView.myDraw(angle-(float)Math.toRadians(estimatedAngle));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // n.a.
    }


    public double computeGyroOrientationYaw()
    {
        SensorManager.getOrientation(gyroMatrix, gyroOrientation);
        return gyroOrientation[0];  // along Yaw direction, in radians
    }

    public void computeGyro(float[] gyro, long eventTimestamp)
    {
        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0)
        {
            final float dT = (eventTimestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = gyro[0];
            float axisY = gyro[1];
            float axisZ = gyro[2];

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
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = sin(thetaOverTwo);
            float cosThetaOverTwo = cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
        }
        timestamp = eventTimestamp;

        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaRotationMatrix);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
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
