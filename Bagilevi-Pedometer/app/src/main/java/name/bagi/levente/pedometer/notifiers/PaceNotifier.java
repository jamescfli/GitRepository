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

import java.util.ArrayList;

import name.bagi.levente.pedometer.preferences.PedometerSettings;
import name.bagi.levente.pedometer.speak.SpeakingTimer;
import name.bagi.levente.pedometer.step.StepListener;
import name.bagi.levente.pedometer.speak.SpeakingUtils;

/**
 * Calculates and displays pace (steps / minute), handles input of desired pace,
 * notifies user if he/she has to go faster or slower.  
 * @author Levente Bagi
 */
public class PaceNotifier implements StepListener, SpeakingTimer.Listener {

    public interface Listener {
        public void paceChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    private int mCounter = 0;   // original no private signature
    
    private long mLastStepTime = 0;
    private long[] mLastStepDeltas = {-1, -1, -1, -1};
    private int mLastStepDeltasIndex = 0;
    private long mPace = 0;

    private PedometerSettings mSettings;
    private SpeakingUtils mSpeakingUtils;

    /** Desired pace, adjusted by the user */
    private int mDesiredPace;

    /** Should we speak? */
    private boolean mShouldTellFasterslower;

    /** When did the TTS speak last time */
    private long mSpokenAt = 0;

    public PaceNotifier(PedometerSettings settings, SpeakingUtils speakingUtils) {
        mSpeakingUtils = speakingUtils;
        mSettings = settings;
        mDesiredPace = mSettings.getDesiredPace();
        reloadSettings();
    }
    public void setPace(int pace) {
        mPace = pace;
        int avg = (int)(60*1000.0 / mPace);     // microseconds consumed by one step
        for (int i = 0; i < mLastStepDeltas.length; i++) {
            mLastStepDeltas[i] = avg;
        }
        notifyListener();
    }
    public void reloadSettings() {
        mShouldTellFasterslower = 
            mSettings.shouldTellFasterslower()
            && (mSettings.getMaintainOption() == PedometerSettings.M_PACE);
        notifyListener();
    }
    
    public void addListener(Listener l) {
        mListeners.add(l);
    }

    public void setDesiredPace(int desiredPace) {
        mDesiredPace = desiredPace;
    }

    @Override
    public void onStep() {
        long thisStepTime = System.currentTimeMillis();
        mCounter ++;
        
        // Calculate pace based on last x steps
        if (mLastStepTime > 0) {
            long delta = thisStepTime - mLastStepTime;
            
            mLastStepDeltas[mLastStepDeltasIndex] = delta;
            // memorize last step in a rotated fashion
            mLastStepDeltasIndex = (mLastStepDeltasIndex + 1) % mLastStepDeltas.length;
            
            long sum = 0;
            boolean isMeaningfull = true;
            for (int i = 0; i < mLastStepDeltas.length; i++) {
                if (mLastStepDeltas[i] < 0) {
                    isMeaningfull = false;
                    break;
                }
                sum += mLastStepDeltas[i];
            }
            if (isMeaningfull && sum > 0) {
                long avg = sum / mLastStepDeltas.length;
                mPace = 60*1000 / avg;
                
                // TODO: remove duplication. This also exists in SpeedNotifier
                if (mShouldTellFasterslower && !mSpeakingUtils.isSpeakingEnabled()) {
                    if (thisStepTime - mSpokenAt > 3000 && !mSpeakingUtils.isSpeakingNow()) {
                        float little = 0.10f;
                        float normal = 0.30f;
                        float much = 0.50f;
                        
                        boolean spoken = true;
                        if (mPace < mDesiredPace * (1 - much)) {
                            mSpeakingUtils.say("much faster!");
                        }
                        else
                        if (mPace > mDesiredPace * (1 + much)) {
                            mSpeakingUtils.say("much slower!");
                        }
                        else
                        if (mPace < mDesiredPace * (1 - normal)) {
                            mSpeakingUtils.say("faster!");
                        }
                        else
                        if (mPace > mDesiredPace * (1 + normal)) {
                            mSpeakingUtils.say("slower!");
                        }
                        else
                        if (mPace < mDesiredPace * (1 - little)) {
                            mSpeakingUtils.say("a little faster!");
                        }
                        else
                        if (mPace > mDesiredPace * (1 + little)) {
                            mSpeakingUtils.say("a little slower!");
                        }
                        else {
                            spoken = false;
                        }
                        if (spoken) {
                            mSpokenAt = thisStepTime;
                        }
                    }
                }
            }
            else {
                mPace = -1;
            }
        }
        mLastStepTime = thisStepTime;
        notifyListener();
    }
    
    private void notifyListener() {
        for (Listener listener : mListeners) {
            listener.paceChanged((int)mPace);
        }
    }

    @Override
    public void passValue() {
        // Not used
    }

    //-----------------------------------------------------
    // Speaking
    @Override
    public void speak() {
        if (mSettings.shouldTellPace()) {
            if (mPace > 0) {
                mSpeakingUtils.say(mPace + " steps per minute");
            }
        }
    }
    

}

