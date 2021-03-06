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

package name.bagi.levente.pedometer.notifiers;


import name.bagi.levente.pedometer.preferences.PedometerSettings;
import name.bagi.levente.pedometer.speak.SpeakingTimer;
import name.bagi.levente.pedometer.step.StepListener;
import name.bagi.levente.pedometer.speak.SpeakingUtils;

/**
 * Calculates and displays the approximate calories.  
 * @author Levente Bagi
 */
public class CaloriesNotifier implements StepListener, SpeakingTimer.Listener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;

    private static double METRIC_RUNNING_FACTOR = 1.02784823;   // 45% higher than walking
    private static double IMPERIAL_RUNNING_FACTOR = 0.75031498;

    private static double METRIC_WALKING_FACTOR = 0.708;
    private static double IMPERIAL_WALKING_FACTOR = 0.517;

    private float mCalories = 0;    // changed from double to float
    
    private PedometerSettings mSettings;
    private SpeakingUtils mSpeakingUtils;
    
    private boolean mIsMetric;
    private boolean mIsRunning;
    private float mStepLength;
    private float mBodyWeight;

    public CaloriesNotifier(Listener listener, PedometerSettings settings, SpeakingUtils speakingUtils) {
        mListener = listener;   // implementation of valueChanged(float value)
        mSpeakingUtils = speakingUtils;
        mSettings = settings;
        reloadSettings();
    }
    public void setCalories(float calories) {
        mCalories = calories;
        notifyListener();
    }
    public void reloadSettings() {
        mIsMetric = mSettings.isMetric();
        mIsRunning = mSettings.isRunning();
        mStepLength = mSettings.getStepLength();
        mBodyWeight = mSettings.getBodyWeight();
        notifyListener();
    }
    public void resetValues() {
        mCalories = 0;
    }
    
    public void setMetric(boolean isMetric) {
        mIsMetric = isMetric;
    }
    public void setStepLength(float stepLength) {
        mStepLength = stepLength;
    }

    // StepListener callback function
    @Override
    public void onStep() {
        if (mIsMetric) {
            mCalories += 
                (mBodyWeight * (mIsRunning ? METRIC_RUNNING_FACTOR : METRIC_WALKING_FACTOR))
                // Distance:
                * mStepLength // centimeters
                / 100000.0; // centimeters/kilometer
        }
        else {
            mCalories += 
                (mBodyWeight * (mIsRunning ? IMPERIAL_RUNNING_FACTOR : IMPERIAL_WALKING_FACTOR))
                // Distance:
                * mStepLength // inches
                / 63360.0; // inches/mile            
        }
        notifyListener();
    }
    
    private void notifyListener() {
        // call the callback function in StepService
        mListener.valueChanged(mCalories);
    }

    @Override
    public void passValue() {
        // n.a.
    }

    @Override
    public void speak() {
        if (mSettings.shouldTellCalories()) {
            if (mCalories > 0) {
                // in English
                mSpeakingUtils.say("" + (int)mCalories + " calories burned");
                // TODO: in Chinese
            }
        }
        
    }

}

