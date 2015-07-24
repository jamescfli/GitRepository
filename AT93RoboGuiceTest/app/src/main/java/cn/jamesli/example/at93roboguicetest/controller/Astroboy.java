package cn.jamesli.example.at93roboguicetest.controller;

import android.app.Application;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Random;

// There's only one Astroboy, so make it a @Singleton.
// This means that there will be only one instance of Astroboy in the entire app.
// Any class that requires an instance of Astroboy will get the same instance.
// This also means this class needs to be thread safe, of course
@Singleton
public class Astroboy {
    @Inject
    Application application;    // current context may change, use application context
    @Inject
    Vibrator vibrator;  // context.getSystemService(VIBRATOR_SERVICE)
    @Inject
    Random random;      // no special bindings

    public void say(String something) {
        Toast.makeText(application, "Astroboy says, \"" + something + "\"", Toast.LENGTH_LONG).show();
    }

    public void brushTeeth() {
        vibrator.vibrate(new long[]{0, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50,  }, -1);
    }

    public String punch() {
        final String expletives[] = new String[]{"POW!", "BANG!", "KERPOW!", "OOF!"};
        return expletives[random.nextInt(expletives.length)];
    }
}
