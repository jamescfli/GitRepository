package cn.nec.nlc.example.jamesli.activitytest37viewanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Random;


public class HitActivity extends Activity {
    private ObjectAnimator animation1;
    private ObjectAnimator animation2;
    private Button button;
    private Random random;
    private int width;
    private int height;
    private AnimatorSet set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        random = new Random();

        set = createAnimation();
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int nextX = random.nextInt(width);
                int nextY = random.nextInt(height);
                animation1 = ObjectAnimator.ofFloat(button, "x", button.getX(), nextX);
                animation1.setDuration(1400);
                animation2 = ObjectAnimator.ofFloat(button, "y", button.getY(), nextY);
                animation2.setDuration(1400);
                set.playTogether(animation1, animation2);
                set.start();
            }
        });
    }

    public void onClick(View view) {
        String string = button.getText().toString();
        int hitTarget = Integer.valueOf(string) + 1;
        button.setText(String.valueOf(hitTarget));
    }

    private AnimatorSet createAnimation() {
        int nextX = random.nextInt(width);
        int nextY = random.nextInt(height);
        button = (Button) findViewById(R.id.button1);
        animation1 = ObjectAnimator.ofFloat(button, "x", nextX);
        animation1.setDuration(1400);
        animation2 = ObjectAnimator.ofFloat(button, "y", nextY);
        animation2.setDuration(1400);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animation1, animation2);
        return set;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
