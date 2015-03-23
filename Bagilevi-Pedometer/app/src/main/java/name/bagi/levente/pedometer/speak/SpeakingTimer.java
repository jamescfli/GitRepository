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

package name.bagi.levente.pedometer.speak;

import java.util.ArrayList;

import name.bagi.levente.pedometer.preferences.PedometerSettings;
import name.bagi.levente.pedometer.step.StepListener;

/**
 * Call all listening objects repeatedly. 
 * The interval is defined by the user settings.
 * @author Levente Bagi
 */
public class SpeakingTimer implements StepListener {

    private PedometerSettings mSettings;
    private SpeakingUtils mSpeakingUtils;   // for speaking
    private boolean mShouldSpeak;
    private float mInterval;
    private long mLastSpeakTime;
    
    public SpeakingTimer(PedometerSettings settings, SpeakingUtils speakingUtils) {
        mLastSpeakTime = System.currentTimeMillis();    // initiate with current time
        mSettings = settings;
        mSpeakingUtils = speakingUtils;
        reloadSettings();
    }
    public void reloadSettings() {
        // load only related preferences
        mShouldSpeak = mSettings.shouldSpeak();
        mInterval = mSettings.getSpeakingInterval();
    }

    @Override
    public void onStep() {
        long now = System.currentTimeMillis();
        long delta = now - mLastSpeakTime;
        
        if (delta / 60000.0 >= mInterval) { // unit in minutes
            mLastSpeakTime = now;
            notifyListeners();
        }
    }

    @Override
    public void passValue() {
        // not used
    }

    
    //-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void speak();    // implement in other class
    }

    // multiple Listeners to speak, e.g. steps, calories, distance and etc.
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }

    public void notifyListeners() {
        mSpeakingUtils.ding();
        for (Listener listener : mListeners) {
            listener.speak();
        }
    }

//    //-----------------------------------------------------
//    // check whether it is in Speaking mode
//    // If it is the case, don't start another speaking action
//    public boolean isSpeaking() {
//        return mSpeakingUtils.isSpeakingNow();
//    }
}

