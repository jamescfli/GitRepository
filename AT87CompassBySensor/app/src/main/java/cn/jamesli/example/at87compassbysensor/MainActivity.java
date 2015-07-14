package cn.jamesli.example.at87compassbysensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;


public class MainActivity extends Activity implements SensorEventListener {
    @SuppressWarnings("unused")
    private String TAG = "SensorCompass";

    // Main View
    private RelativeLayout mFrame;

    private Sensor accelerometer;
    private Sensor magnetometer;
    private SensorManager mSensorManager;

    // Save sensor readings
    private float[] mGravity = null;
    private float[] mGeomagnetic = null;

    // Rotattion around Z axis
    private float mRotationInDegrees;

    // View, showing compass arrow
//    private CompassArrowView mCompassArrow;
    private CompassArrowSurfaceView mCompassArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrame = (RelativeLayout) findViewById(R.id.frame);

//        mCompassArrow = new CompassArrowView(getApplicationContext());
//        mFrame.addView(mCompassArrow);

        // try to use SurfaceView instead, to avoid blocking UI thread by frequently updating view
//        mCompassArrow = new CompassArrowSurfaceView(getApplicationContext());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        mCompassArrow.setDisplayDimension(displayMetrics);  // MOTO G w720*h1184
        mCompassArrow = new CompassArrowSurfaceView(getApplicationContext(), displayMetrics);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = (Sensor) mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        magnetometer = (Sensor) mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (null == accelerometer || null == magnetometer) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
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
        // Acquire accelerometer event data
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = new float[3];
            System.arraycopy(event.values, 0, mGravity, 0, 3);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = new float[3];
            System.arraycopy(event.values, 0, mGeomagnetic, 0, 3);
        }

        if (mGravity != null && mGeomagnetic != null) {
            float rotationMatrix[] = new float[9];

            // Users the accelerometer and magnetometer readings
            // to compute the device's rotation with respect to
            // a real world coordinate system

            boolean success = SensorManager.getRotationMatrix(rotationMatrix,
                    null, mGravity, mGeomagnetic);

            if (success) {

                float orientationMatrix[] = new float[3];

                // Returns the device's orientation given
                // the rotationMatrix

                SensorManager.getOrientation(rotationMatrix, orientationMatrix);

                // Get the rotation, measured in radians, around the Z-axis
                // Note: This assumes the device is held flat and parallel
                // to the ground

                float rotationInRadians = orientationMatrix[0];

                // Convert from radians to degrees
                mRotationInDegrees = (float) Math.toDegrees(rotationInRadians);

                // Request redraw
                mCompassArrow.setDirectionInDegrees(mRotationInDegrees);
                mCompassArrow.invalidate();   // when applying CompassArrowView

                // Reset sensor event data arrays
                mGravity = mGeomagnetic = null;

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // n.a.
    }
}
