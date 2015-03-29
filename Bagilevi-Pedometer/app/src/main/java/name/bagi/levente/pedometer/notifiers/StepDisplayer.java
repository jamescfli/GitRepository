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
import name.bagi.levente.pedometer.speak.SpeakingUtils;
import name.bagi.levente.pedometer.step.StepListener;

/**
 * Counts steps provided by StepDetector and passes the current
 * step count to the activity.
 */
public class StepDisplayer implements StepListener, SpeakingTimer.Listener {

    private int mCount = 0;
    private PedometerSettings mSettings;
    private SpeakingUtils mSpeakingUtils;

    public StepDisplayer(PedometerSettings settings, SpeakingUtils speakingUtils) {
        mSpeakingUtils = speakingUtils;
        mSettings = settings;
        notifyListener();
    }
    public void setSpeakingUtils(SpeakingUtils speakingUtils) {
        mSpeakingUtils = speakingUtils;
    }

    public void setSteps(int steps) {
        mCount = steps;
        notifyListener();
    }

    @Override
    public void onStep() {
        mCount ++;
        notifyListener();
    }
    public void reloadSettings() {
        notifyListener();
    }

    @Override
    public void passValue() {
    }
    
    

    //-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void stepsChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged((int)mCount);
        }
    }
    
    //-----------------------------------------------------
    // Speaking
    @Override
    public void speak() {
        if (mSettings.shouldTellSteps()) { 
            if (mCount > 0) {
                mSpeakingUtils.say("" + mCount + " steps conducted");
            }
        }
    }
    
    
}
