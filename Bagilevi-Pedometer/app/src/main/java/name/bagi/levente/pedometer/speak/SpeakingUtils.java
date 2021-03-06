package name.bagi.levente.pedometer.speak;

import java.util.Locale;

import android.app.Service;
import android.speech.tts.TextToSpeech;
import android.text.format.Time;
import android.util.Log;

public class SpeakingUtils implements TextToSpeech.OnInitListener {
    private static final String TAG = "Utils";
//    private static final String UTTERANCE_ID = "Speak ID";  // for API 21 to TextToSpeech.speak()
    private Service mService;

    private static SpeakingUtils instance = null;

    // singleton mode to privatize the constructor
    private SpeakingUtils() {
        // n.a.
    }

    // getInstance() has to be static method for Class SpeakingUtils
    public static SpeakingUtils getInstance() {
        // only one object at once
        if (instance == null) {
            instance = new SpeakingUtils();
        }
        return instance;
    }
    
    public void setService(Service service) {
        mService = service;
    }
    
    /********** SPEAKING **********/
    
    private TextToSpeech mTts;
    private boolean mSpeak = false;
    private boolean mSpeakingEngineAvailable = false;

    public void initTTS() {
        // Initialize text-to-speech. This is an asynchronous operation.
        // The OnInitListener (second argument) is called after initialization completes.
        Log.i(TAG, "Initializing TextToSpeech...");
        mTts = new TextToSpeech(mService,
            this  // TextToSpeech.OnInitListener
            );
    }
    // used in Service#onDestroy()
    public void shutdownTTS() {
        Log.i(TAG, "Shutting Down TextToSpeech...");

        mSpeakingEngineAvailable = false;
        mTts.shutdown();
        Log.i(TAG, "TextToSpeech Shut Down.");

    }
    public void say(String text) {
        if (mSpeak && mSpeakingEngineAvailable) {
            // add UTTERANCE_ID for API 21
            mTts.speak(text,
                    TextToSpeech.QUEUE_ADD,  // Drop all pending entries in the playback queue.
                    null);
            // TODO: try to speak Chinese over here
        }
    }

    // Implements TextToSpeech.OnInitListener.
    @Override
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            // TODO: Locale.CHINA or CHINESE
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
            } else {
                Log.i(TAG, "TextToSpeech Initialized.");
                mSpeakingEngineAvailable = true;
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

    public void setSpeak(boolean speak) {
        mSpeak = speak;
    }

    public boolean isSpeakingEnabled() {
        return mSpeak;
    }

    public boolean isSpeakingNow() {
        return mTts.isSpeaking();
    }

    public void ding() {
        // n.a.
    }
    
    /********** Time **********/
    
    public static long currentTimeInMillis() {
        Time time = new Time();
        time.setToNow();
        return time.toMillis(false);
          // others:  toString() = in YYYYMMDDTHHMMSS format
    }
}