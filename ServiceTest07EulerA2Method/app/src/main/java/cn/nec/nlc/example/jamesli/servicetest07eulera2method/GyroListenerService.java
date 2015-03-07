package cn.nec.nlc.example.jamesli.servicetest07eulera2method;


import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

public class GyroListenerService extends Service implements SensorEventListener{
    // intent filter for broadcast receiver
    public static final String NOTIFICATION = "cn.nec.nlc.example.jamesli.servicetest07";
    // result to feedback in intent
    public static final String GYRO_CLASS_RESULT = "gyro.class.result";
    public static final String GYRO_EULER_RESULT = "gyro.euler.result";

    private final IBinder mBinder = new LocalBinder();

    // binder for activity access
    public class LocalBinder extends Binder {
        GyroListenerService getService() {
            return GyroListenerService.this;
        }
    }

    // variable for orientation calculation
    private long timestampCurrent;
    private long timestampUiUpdateMark;
    private static final float UI_UPDATE_INTERVAL = 2.0f;
    // unit trans
    private static final float NS2S = 1.0f / 1000000000.0f;
    private static final float EPSILON = 0.000000001f;
    private float[] absAngRotMatrixClass = new float[9];
    private float[] absAngRotMatrixEuler = new float[9];
    private float[] absEulerAngleClass = new float[3];
    private float[] absEulerAngleEuler = new float[3];
    private final float[] rotationQuaternionClass = new float[4];   // for classical method
    private final float[] rotationQuaternionEuler = new float[4];   // for Euler axis/angle method
    private boolean isFirstQuanternionCalFlag;
      // to initiate rotationQuaternionEuler with rotationQuaternionClass at the beginning

    // sensor registration
    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    public IBinder onBind(Intent intent) {
        // initiate
        timestampCurrent = 0;
        timestampUiUpdateMark = 0;
        initGyroMatrix();
        isFirstQuanternionCalFlag = true;     // set to the first quaternion calculation
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSensorManager.unregisterListener(this);
        return super.onUnbind(intent);
    }

    // SensorEventListener methods
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (timestampCurrent != 0) {    // skip the first read
                // compute the current rotationQuaternionClass with classical method
                computeQuaternionClass(sensorEvent);
                if (isFirstQuanternionCalFlag) {    // treat the first read differently
                    // save rotationQuaternionClass to rotationQuaternionEuler for further usage
                    for (int i = 0; i < 4; i++) {
                        // initiate rotationQuaternionEuler
                        rotationQuaternionEuler[i] = rotationQuaternionClass[i];
                    }
                    // record current time from sensorEvent
                    timestampCurrent = sensorEvent.timestamp;
                    float[] deltaRotationMatrix = new float[9];
                    // helper function to convert a rotation vector to a rotation matrix
                    SensorManager.getRotationMatrixFromVector(deltaRotationMatrix,
                            rotationQuaternionClass);
                    // User code should concatenate the delta rotation we computed with the current
                    // rotation in order to get the updated rotation.
                    // rotationCurrent = rotationCurrent * deltaRotationMatrix;
                    updateAbsAngRotMatrix(absAngRotMatrixClass, deltaRotationMatrix);
                    SensorManager.getOrientation(absAngRotMatrixClass, absEulerAngleClass);
                    for (int i = 0; i < 3; i++) {
                        absEulerAngleEuler[i] = absEulerAngleClass[i];
                    }
                    publishResultToUi();
                    isFirstQuanternionCalFlag = false;
                } else {    // not the first computation anymore
                    computeQuaternionEuler(sensorEvent);
                    // record current time from sensorEvent
                    timestampCurrent = sensorEvent.timestamp;
                    float[] deltaRotationMatrix = new float[9];
                    // helper function to convert a rotation vector to a rotation matrix
                    SensorManager.getRotationMatrixFromVector(deltaRotationMatrix,
                            rotationQuaternionClass);
                    // User code should concatenate the delta rotation we computed with the current
                    // rotation in order to get the updated rotation.
                    // rotationCurrent = rotationCurrent * deltaRotationMatrix;
                    updateAbsAngRotMatrix(absAngRotMatrixClass, deltaRotationMatrix);
                    SensorManager.getOrientation(absAngRotMatrixClass, absEulerAngleClass);
                    // publish the result through local broadcast manager if passing time interval
                    if (((timestampCurrent - timestampUiUpdateMark) * NS2S) > UI_UPDATE_INTERVAL) {
                        // send results through publishResultToUi() method
                        publishResultToUi();
                    }   // o.w. keep update the rotation matrix and euler angles
                }
            } else {
                timestampCurrent = sensorEvent.timestamp;
                timestampUiUpdateMark = timestampCurrent;
            }
        } else {
            throw new IllegalArgumentException("Somehow, type of the sensor is not Gyroscope.");
        }
    }

    private void computeQuaternionClass(SensorEvent sensorEvent) {
        // classical method suggested by Android SDK tutorial
        final float dT = (sensorEvent.timestamp - timestampCurrent) * NS2S; // in seconds
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
        rotationQuaternionClass[0] = sinThetaOverTwo * axisX;   // x in quaternion
        rotationQuaternionClass[1] = sinThetaOverTwo * axisY;   // y in quaternion
        rotationQuaternionClass[2] = sinThetaOverTwo * axisZ;   // z in quaternion
        rotationQuaternionClass[3] = cosThetaOverTwo;   // w in quaternion
    }

    private void computeQuaternionEuler(SensorEvent sensorEvent) {
        // TODO: finish Euler Axis/Angle alg.
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // n.a.
    }

    private void initGyroMatrix() {
        // initialise rotationAngleMatrix with identity matrix
        absAngRotMatrixClass[0] = 1.0f; absAngRotMatrixClass[1] = 0.0f; absAngRotMatrixClass[2] = 0.0f;
        absAngRotMatrixClass[3] = 0.0f; absAngRotMatrixClass[4] = 1.0f; absAngRotMatrixClass[5] = 0.0f;
        absAngRotMatrixClass[6] = 0.0f; absAngRotMatrixClass[7] = 0.0f; absAngRotMatrixClass[8] = 1.0f;

        absAngRotMatrixEuler[0] = 1.0f; absAngRotMatrixEuler[1] = 0.0f; absAngRotMatrixEuler[2] = 0.0f;
        absAngRotMatrixEuler[3] = 0.0f; absAngRotMatrixEuler[4] = 1.0f; absAngRotMatrixEuler[5] = 0.0f;
        absAngRotMatrixEuler[6] = 0.0f; absAngRotMatrixEuler[7] = 0.0f; absAngRotMatrixEuler[8] = 1.0f;
    }

    private void updateAbsAngRotMatrix(float[] absAngRotMatrix, float[] deltaRotationMatrix) {
        float[] absAngRotMatrixCopy = absAngRotMatrix.clone();
        absAngRotMatrix[0] = absAngRotMatrixCopy[0] * deltaRotationMatrix[0]
                + absAngRotMatrixCopy[1] * deltaRotationMatrix[3]
                + absAngRotMatrixCopy[2] * deltaRotationMatrix[6];
        absAngRotMatrix[1] = absAngRotMatrixCopy[0] * deltaRotationMatrix[1]
                + absAngRotMatrixCopy[1] * deltaRotationMatrix[4]
                + absAngRotMatrixCopy[2] * deltaRotationMatrix[7];
        absAngRotMatrix[2] = absAngRotMatrixCopy[0] * deltaRotationMatrix[2]
                + absAngRotMatrixCopy[1] * deltaRotationMatrix[5]
                + absAngRotMatrixCopy[2] * deltaRotationMatrix[8];
        absAngRotMatrix[3] = absAngRotMatrixCopy[3] * deltaRotationMatrix[0]
                + absAngRotMatrixCopy[4] * deltaRotationMatrix[3]
                + absAngRotMatrixCopy[5] * deltaRotationMatrix[6];
        absAngRotMatrix[4] = absAngRotMatrixCopy[3] * deltaRotationMatrix[1]
                + absAngRotMatrixCopy[4] * deltaRotationMatrix[4]
                + absAngRotMatrixCopy[5] * deltaRotationMatrix[7];
        absAngRotMatrix[5] = absAngRotMatrixCopy[3] * deltaRotationMatrix[2]
                + absAngRotMatrixCopy[4] * deltaRotationMatrix[5]
                + absAngRotMatrixCopy[5] * deltaRotationMatrix[8];
        absAngRotMatrix[6] = absAngRotMatrixCopy[6] * deltaRotationMatrix[0]
                + absAngRotMatrixCopy[7] * deltaRotationMatrix[3]
                + absAngRotMatrixCopy[8] * deltaRotationMatrix[6];
        absAngRotMatrix[7] = absAngRotMatrixCopy[6] * deltaRotationMatrix[1]
                + absAngRotMatrixCopy[7] * deltaRotationMatrix[4]
                + absAngRotMatrixCopy[8] * deltaRotationMatrix[7];
        absAngRotMatrix[8] = absAngRotMatrixCopy[6] * deltaRotationMatrix[2]
                + absAngRotMatrixCopy[7] * deltaRotationMatrix[5]
                + absAngRotMatrixCopy[8] * deltaRotationMatrix[8];
    }

    private void publishResultToUi() {
        Intent intent = new Intent(NOTIFICATION);
        // put result into intent bundle, in terms of float array
        intent.putExtra(GYRO_CLASS_RESULT, absEulerAngleClass);
        // feed back Euler A2 results after finished the algorithm
        intent.putExtra(GYRO_EULER_RESULT, absEulerAngleEuler);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
