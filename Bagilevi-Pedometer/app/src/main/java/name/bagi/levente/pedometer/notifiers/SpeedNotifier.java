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
import name.bagi.levente.pedometer.speak.SpeakingUtils;

/**
 * Calculates and displays pace (steps / minute), handles input of desired pace,
 * notifies user if he/she has to go faster or slower.
 * 
 * Uses {@link name.bagi.levente.pedometer.notifiers.PaceNotifier}, calculates speed as product of pace and step length.
 * 
 * @author Levente Bagi
 */
public class SpeedNotifier implements PaceNotifier.Listener, SpeakingTimer.Listener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;
    
//    private int mCounter = 0;
    private float mSpeed = 0;

    private boolean mIsMetric;
    private float mStepLength;

    private PedometerSettings mSettings;
    private SpeakingUtils mSpeakingUtils;

    /** Desired speed, adjusted by the user */
    private float mDesiredSpeed;
    
    /** Should we speak? */
    private boolean mShouldTellFasterslower;
    private boolean mShouldTellSpeed;
    
    /** When did the TTS speak last time */
    private long mSpokenAt = 0;
    
    public SpeedNotifier(Listener listener, PedometerSettings settings, SpeakingUtils speakingUtils) {
        mListener = listener;
        mSpeakingUtils = speakingUtils;
        mSettings = settings;
        mDesiredSpeed = mSettings.getDesiredSpeed();
        reloadSettings();
    }
    public void setSpeed(float speed) {
        mSpeed = speed;
        notifyListener();
    }
    public void reloadSettings() {
        mIsMetric = mSettings.isMetric();
        mStepLength = mSettings.getStepLength();
        mShouldTellSpeed = mSettings.shouldTellSpeed();
        mShouldTellFasterslower = 
            mSettings.shouldTellFasterslower()
            && (mSettings.getMaintainOption() == PedometerSettings.M_SPEED);
        notifyListener();
    }
    public void setDesiredSpeed(float desiredSpeed) {
        mDesiredSpeed = desiredSpeed;
    }
    
    private void notifyListener() {
        mListener.valueChanged(mSpeed);
    }

    @Override
    public void paceChanged(int value) {
        if (mIsMetric) {
            mSpeed = // kilometers / hour
                value * mStepLength // centimeters / minute
                / 100000f * 60f; // centimeters/kilometer
        }
        else {
            mSpeed = // miles / hour
                value * mStepLength // inches / minute
                / 63360f * 60f; // inches/mile 
        }
        tellFasterSlower();
        notifyListener();
    }
    
    /**
     * Say slower/faster, if needed.
     */
    private void tellFasterSlower() {
        if (mShouldTellFasterslower && mSpeakingUtils.isSpeakingEnabled()) {
            long now = System.currentTimeMillis();
            if (now - mSpokenAt > 3000 && !mSpeakingUtils.isSpeakingNow()) {
                float little = 0.10f;
                float normal = 0.30f;
                float much = 0.50f;
                
                boolean spoken = true;
                if (mSpeed < mDesiredSpeed * (1 - much)) {
                    mSpeakingUtils.say("much faster!");
                }
                else
                if (mSpeed > mDesiredSpeed * (1 + much)) {
                    mSpeakingUtils.say("much slower!");
                }
                else
                if (mSpeed < mDesiredSpeed * (1 - normal)) {
                    mSpeakingUtils.say("faster!");
                }
                else
                if (mSpeed > mDesiredSpeed * (1 + normal)) {
                    mSpeakingUtils.say("slower!");
                }
                else
                if (mSpeed < mDesiredSpeed * (1 - little)) {
                    mSpeakingUtils.say("a little faster!");
                }
                else
                if (mSpeed > mDesiredSpeed * (1 + little)) {
                    mSpeakingUtils.say("a little slower!");
                }
                else {
                    spoken = false;
                }
                if (spoken) {
                    mSpokenAt = now;
                }
            }
        }
    }

    @Override
    public void passValue() {
        // Not used
    }

    @Override
    public void speak() {
        if (mSettings.shouldTellSpeed()) {
            if (mSpeed >= .01f) {
                mSpeakingUtils.say(("" + (mSpeed + 0.000001f)).substring(0, 4) + (mIsMetric ? " kilometers per hour" : " miles per hour"));
            }
        }
        
    }

}

