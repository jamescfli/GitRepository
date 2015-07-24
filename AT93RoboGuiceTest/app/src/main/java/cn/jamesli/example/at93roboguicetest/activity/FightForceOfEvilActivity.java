package cn.jamesli.example.at93roboguicetest.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.inject.Inject;

import java.util.Random;

import cn.jamesli.example.at93roboguicetest.R;
import cn.jamesli.example.at93roboguicetest.controller.Astroboy;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

public class FightForceOfEvilActivity extends RoboActivity {
    @InjectView(R.id.expletive)
    TextView expletiveText;

    @InjectResource(R.anim.expletive_animation)
    Animation expletiveAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fight_evil);

        expletiveText.setAnimation(expletiveAnimation);
        expletiveAnimation.start();

        // Throw punches
        for (int i = 0; i < 10; i++)
            new AsyncPunch(this) {
                @Override
                protected void onSuccess(String expletive) throws Exception {
                    expletiveText.setText(expletive);

                    // Override onException() and onFinally() if needed
                }
            }.execute();
    }

    public static class AsyncPunch extends RoboAsyncTask<String> {
        @Inject
        Astroboy astroboy;
        @Inject
        Random random;

        public AsyncPunch(Context context) {
            super(context);
        }

        public String call() throws Exception {
            Thread.sleep(random.nextInt(5*1000));
            return astroboy.punch();
        }
    }
}
