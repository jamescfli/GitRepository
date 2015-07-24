package cn.jamesli.example.at93roboguicetest.controller;

import android.app.Activity;
import android.widget.Toast;

import com.google.inject.Inject;

import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;

// Use the current context, so much make it @ContextSingleton
// his means that there will be one AstroboyRemoteControl for every activity or
// service that requires one.
@ContextSingleton
public class AstroboyRemoteControl {
    // The Astroboy class has been decorated with @Singleton, so this instance of
    // Astroboy will be the same instance used elsewhere in our app.
    // Injecting an Activity is basically equivalent to "@Inject Context context",
    // and thus also requires @ContextSingleton. If you wanted, you could also
    // @Inject Application, Service, etc. wherever appropriate.

    @Inject Astroboy astroboy;
    @Inject Activity activity;

    public void brushTeeth() {
        Ln.d("Sent brushTeeth command to Astroboy");
        astroboy.brushTeeth();
    }

    public void say(String something) {
        Ln.d("Sent say(%s) command to Astroboy", something);
        astroboy.say(something);
    }

    public void setDestruct() {
        Toast.makeText(activity, "Your evil remote control has exploded! Now Astroboy is FREEEEEEEEEE!", Toast.LENGTH_LONG).show();
        activity.finish();
    }
}
