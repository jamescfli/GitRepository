package cn.nec.nlc.jamesli.tools.activitytest64animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private Button buttonAnimation;
    private TextView textViewHelloWorld;
    private RadioGroup radioGroupAniType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAnimation = (Button)  findViewById(R.id.buttonAnimation);
        buttonAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testValueAnimator();

                ObjectAnimator animator;
                switch (radioGroupAniType.getCheckedRadioButtonId()) {
                    case R.id.radioButtonAlpha:
                        animator = ObjectAnimator.ofFloat(textViewHelloWorld, "alpha", 1f, 0f, 1f);
                        animator.setDuration(5000);
                        animator.start();
                        break;
                    case R.id.radioButtonRotation:
                        animator = ObjectAnimator.ofFloat(textViewHelloWorld, "rotation", 0f, 360f);
                        animator.setDuration(5000);
                        animator.start();
                        break;
                    case R.id.radioButtonMoveOut:
                        float curTranslationX = textViewHelloWorld.getTranslationX();
                        animator = ObjectAnimator.ofFloat(textViewHelloWorld, "translationX", curTranslationX, -700f, curTranslationX);
                        animator.setDuration(5000);
                        animator.start();
                        break;
                    case R.id.radioButtonEnlarge:
                        animator = ObjectAnimator.ofFloat(textViewHelloWorld, "scaleY", 1f, 3f, 1f);
                        animator.setDuration(5000);
                        animator.start();
                        break;
                    case R.id.radioButtonAniCombo:
                        ObjectAnimator moveIn = ObjectAnimator.ofFloat(textViewHelloWorld, "translationX", -700f, 0f);
                        ObjectAnimator rotate = ObjectAnimator.ofFloat(textViewHelloWorld, "rotation", 0f, 360f);
                        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(textViewHelloWorld, "alpha", 1f, 0f, 1f);
                        AnimatorSet animSet = new AnimatorSet();
                        animSet.play(rotate).with(fadeInOut).after(moveIn);
                        animSet.setDuration(5000);
                        animSet.start();
                        break;
                    case R.id.radioButtonChangeText:
                        animator = ObjectAnimator.ofFloat(textViewHelloWorld, "scaleX", 1f, 1.5f, 1f);
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                textViewHelloWorld.setText("Text Changed");
                            }
                        });
                        animator.start();
                        break;
                    case R.id.radioButtonByXml:
                        Animator anim = AnimatorInflater.loadAnimator(MainActivity.this.getApplicationContext(), R.animator.anim_file);
                        anim.setTarget(textViewHelloWorld);
                        anim.start();
                        break;
                }

            }
        });
        textViewHelloWorld = (TextView) findViewById(R.id.textViewHelloWorld);
        radioGroupAniType = (RadioGroup) findViewById(R.id.radioButtonGroup);
    }

    private void testValueAnimator() {
        // ValueAnimator: used in few scenarios
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                Log.d("TAG", "cuurent value is " + currentValue);
            }
        });
        anim.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
