package cn.nec.nlc.example.jamesli.servicetest05intentservice;


import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

public class GyroListenerService extends IntentService implements SensorEventListener {
    // for gyroscope calculation
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] rotationQuaternion = new float[4];
    private static final float EPSILON = 0.000000001f;
    // mark the current/past time
    private long timestamp;

    // sensor register
    private SensorManager mSensorManager;
    private Sensor gyroSensor;

    private float[] rotationAngleMatrix = new float[9];
    private float[] rotationEulerAngles = new float[3];

    public GyroListenerService() {
        // provide a default name to GyroListenerService IntentService
        super("GyroListenerService");
    }

    public GyroListenerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // register Gyroscope sensor listener
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
                  // GyroY - Math.toDegrees(rotationEulerAngles[0])
                  // GyroP - Math.toDegrees(rotationEulerAngles[1])
                  // GyroR - Math.toDegrees(rotationEulerAngles[2])
                break;
            default:
                break;
        }
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
