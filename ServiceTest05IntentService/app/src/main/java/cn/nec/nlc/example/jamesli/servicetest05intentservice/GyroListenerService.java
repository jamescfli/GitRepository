package cn.nec.nlc.example.jamesli.servicetest05intentservice;


//import android.app.IntentService;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

public class GyroListenerService extends Service implements SensorEventListener {
    public static final String NOTIFICATION =
            "cn.nec.nlc.example.jamesli.servicetest05intentservice";
    public static final String CURRENT_DIRECTION = "current.direction.on.X-Y.plane";
    public static final String RESULT = "result";

    private final IBinder mBinder = new MyBinder();
    public class MyBinder extends Binder {
        GyroListenerService getService() {
            return GyroListenerService.this;
        }
    }

    // for gyroscope calculation
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] rotationQuaternion = new float[4];
    private static final float EPSILON = 0.000000001f;
    private static final float REPORT_INTERVAL = 0.5f;     // e.g. report result every 1 sec
    // mark the current/past time
    private long timestamp; // for sensor data updates
    private long timestampUI;   // for UI updates through sending broadcast

    // sensor register
    private SensorManager mSensorManager;
    private Sensor mGyroSensor;

    private float[] rotationAngleMatrix = new float[9];
    private float[] rotationEulerAngles = new float[3];

//    public GyroListenerService() {
//        // provide a default name to GyroListenerService IntentService
//        super("GyroListenerService");   // to name the worker thread, only for debugging
//    }


    @Override
    public void onCreate() {
        super.onCreate();
        initGyroMatrix();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // initiate
        timestamp = 0;
        timestampUI = 0;
        mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSensorManager.unregisterListener(this);
        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_GYROSCOPE:     // value = 4
                deriveEularAngles(sensorEvent);
                // send back the result to UI every REPORT_INTERVAL seconds
                if (((timestamp - timestampUI) * NS2S) > REPORT_INTERVAL) {
                    publishResultToUi();
                    timestampUI = timestamp;
                } // otherwise, keep listening sensor change events
                break;
            default:
                break;
        }
    }

    private void deriveEularAngles(SensorEvent sensorEvent) {
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

        timestamp = sensorEvent.timestamp;
        float[] deltaRotationMatrix = new float[9];
        // helper function to convert a rotation vector to a rotation matrix
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, rotationQuaternion);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
        updateRvRotationMatrix(deltaRotationMatrix);    // update rotationAngleMatrix[]

        SensorManager.getOrientation(rotationAngleMatrix, rotationEulerAngles);
        // GyroY - Math.toDegrees(rotationEulerAngles[0])
        // GyroP - Math.toDegrees(rotationEulerAngles[1])
        // GyroR - Math.toDegrees(rotationEulerAngles[2])
    }

    private void publishResultToUi() {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, Activity.RESULT_OK);
        intent.putExtra(CURRENT_DIRECTION, rotationEulerAngles[0]);     // direction on x-y plane
        sendBroadcast(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // n.a.
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

    private void initGyroMatrix() {
        // initialise rotationAngleMatrix with identity matrix
        rotationAngleMatrix[0] = 1.0f; rotationAngleMatrix[1] = 0.0f; rotationAngleMatrix[2] = 0.0f;
        rotationAngleMatrix[3] = 0.0f; rotationAngleMatrix[4] = 1.0f; rotationAngleMatrix[5] = 0.0f;
        rotationAngleMatrix[6] = 0.0f; rotationAngleMatrix[7] = 0.0f; rotationAngleMatrix[8] = 1.0f;
    }
}
