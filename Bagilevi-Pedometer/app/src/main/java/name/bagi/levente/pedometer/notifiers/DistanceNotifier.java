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
 * Calculates and displays the distance walked.  
 * @author Levente Bagi
 */
public class DistanceNotifier implements StepListener, SpeakingTimer.Listener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;
    
    private float mDistance = 0;

    private PedometerSettings mSettings;
    private SpeakingUtils mSpeakingUtils;

    private boolean mIsMetric;
    private float mStepLength;

    public DistanceNotifier(Listener listener, PedometerSettings settings, SpeakingUtils speakingUtils) {
        mListener = listener;
        mSpeakingUtils = speakingUtils;
        mSettings = settings;
        reloadSettings();   // initiate mIsMetric and mStepLength
    }
    public void setDistance(float distance) {
        mDistance = distance;
        notifyListener();   // update with mDistance
    }
    
    public void reloadSettings() {
        mIsMetric = mSettings.isMetric();
        mStepLength = mSettings.getStepLength();
        notifyListener();   // update with mDistance
    }

    @Override
    public void onStep() {
        
        if (mIsMetric) {
            mDistance += (float)(// kilometers
                mStepLength // centimeters
                / 100000.0); // centimeters/kilometer
        }
        else {
            mDistance += (float)(// miles
                mStepLength // inches
                / 63360.0); // inches/mile
        }
        
        notifyListener();
    }
    
    private void notifyListener() {
        mListener.valueChanged(mDistance);
    }

    @Override
    public void passValue() {
        // Callback of StepListener - Not implemented
    }

    @Override
    public void speak() {
        if (mSettings.shouldTellDistance()) {
            if (mDistance >= .001f) {   // started to move, skip saying anything
                mSpeakingUtils.say(("" + (mDistance + 0.000001f)).substring(0, 4) + (mIsMetric ? " kilometers" : " miles"));
                // TODO: format numbers (no "." at the end)
                // TODO: in Chinese
            }
        }
    }

}

