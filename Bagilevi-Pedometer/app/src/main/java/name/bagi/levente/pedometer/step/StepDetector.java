/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package name.bagi.levente.pedometer.step;

import java.util.ArrayList;
import java.util.Arrays;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Detects steps and notifies all listeners (that implement StepListener).
 * @author Levente Bagi
 * @todo REFACTOR: SensorListener is deprecated
 */
public class StepDetector implements SensorEventListener
{
    private final static String TAG = "StepDetector";
    private float   mLimit = 10;    // default value for normal sensitivity
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int     mLastMatch = -1;
    
    private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();
    private StepListener stepListenerUseAndroidApi;
    
    public StepDetector() {
        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));   // 9.80665
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX))); // 60.0
    }
    
    public void setSensitivity(float sensitivity) {
        mLimit = sensitivity; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
          // extra high, very high, high, higher, medium, lower, low, very low, extra low
    }
    
    public void addStepListener(StepListener sl) {
        mStepListeners.add(sl);
    }

//    public void setStepListenerUseAndroidApi(StepListener sl) {
//        stepListenerUseAndroidApi = sl;
//    }
    
    //public void onSensorChanged(int sensor, float[] values) {
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor; 
        synchronized (this) {
            stepDetectorByBagilevi(event, sensor);
//            // test Android native step counter
//            if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
//                stepListenerUseAndroidApi.onStep();
//            }
        }
    }

    private void stepDetectorByBagilevi(SensorEvent event, Sensor sensor) {
        if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // not in use, could be in the future for opportunistic calibration
        }
        else {
            // j for mScale[j] usage
            int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
              // j = 0 case has not been considered
            if (j == 1) {
                float vSum = 0;
                for (int i=0 ; i<3 ; i++) {
                    // TODO: figure out why use mScale[1] Magnetic Field to scale Acc values
                    final float v = mYOffset + event.values[i] * mScale[j];
                    vSum += v;
                }
                // TODO: only k = 0 is used in arrays, k = 1~5 is for further extension
                int k = 0;
                float v = vSum / 3;

                float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                if (direction == - mLastDirections[k]) {
                    // Direction changed
                    int extType = (direction > 0 ? 0 : 1); // minimum or maximum?
                    mLastExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                    if (diff > mLimit) {

                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                        boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                        boolean isNotContra = (mLastMatch != 1 - extType);

                        // for debug
//                            Log.i(TAG, "mLimit = " + mLimit);
//                            Log.i(TAG, "mScale[] = " + Arrays.toString(mScale));
//                            Log.i(TAG, "mYOffset = " + mYOffset);
                        Log.i(TAG, "mLastDiff[] = " + Arrays.toString(mLastDiff));
                        Log.i(TAG, "mLastDirections[] = " + Arrays.toString(mLastDirections));
                        Log.i(TAG, "mLastValues[] = " + Arrays.toString(mLastValues));

                        Log.i(TAG, "mLastExtremes[0] = " + Arrays.toString(mLastExtremes[0]));
                        Log.i(TAG, "mLastExtremes[1] = " + Arrays.toString(mLastExtremes[1]));

                        Log.i(TAG, "mLastMatch = " + mLastMatch);
                        Log.i(TAG, "extType = " + extType);
                        Log.i(TAG, "diff > mLimit " + diff + " > " + mLimit);
                        Log.i(TAG, "current!! v = " + v);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            Log.i(TAG, "step");
                            for (StepListener stepListener : mStepListeners) {
                                // tell everyone, step is conducted
                                stepListener.onStep();
                            }
                            mLastMatch = extType;
                        }
                        else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

}