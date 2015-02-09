package cn.nec.nlc.example.jamesli.activitytest37viewanimation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class AnimationExampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAnimation(View view) {
        float dest = 0;
        ImageView aniView = (ImageView) findViewById(R.id.imageView1);
        switch (view.getId()) {
            case R.id.Button01: // Rotate
                dest = 360;
                // View.getRotation(): return the degrees that view is rotated around the pivot point
                if (aniView.getRotation() == 360) { // if already rotated to 360, then back to 0
                    System.out.println(aniView.getAlpha());
                    // .. get opacity of the view  from 0.0(transparent) to 1.0(opaque)
                    dest = 0;
                }
                // Constructs and returns an ObjectAnimator that animates between float values
                ObjectAnimator animation1 = ObjectAnimator.ofFloat(aniView, "rotation", dest);
                // target: object whose property is to be animated
                // propertyName: name of the property being animated
                // values:
                //  A single value implies that that value is the one being animated to.
                //  Two values imply starting and ending values.
                //  More than two values imply a starting value, values to animate through along the way, and an ending value
                animation1.setDuration(2000);
                animation1.start();
//                // Show how to load an animation from XML
//                Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.myanimation);
//                animation1.setAnimationListener(this);
//                animatedView1.startAnimation(animation1);
                break;
            case R.id.Button02: // Animate
                // shows how to define an animation via code
                // also use an Interpolator (BuounceInterpolator)
                Paint paint = new Paint();
                TextView aniTextView = (TextView) findViewById(R.id.textView1);
                float measureTextCenter = paint.measureText(aniTextView.getText().toString());
                dest = 0 - measureTextCenter;
                if (aniTextView.getX() < 0) {
                    dest = 0;
                }
                ObjectAnimator animation2 = ObjectAnimator.ofFloat(aniTextView, "x", dest);
                animation2.setDuration(2000);
                animation2.start();
                break;
            case R.id.Button03: // Fade
                // demonstrate fading and adding an AnimationListener
                dest = 1;
                if (aniView.getAlpha() > 0) {
                    dest = 0;
                }
                ObjectAnimator animation3 = ObjectAnimator.ofFloat(aniView, "alpha", dest);
                animation3.setDuration(2000);
                animation3.start();
                break;
            case R.id.Button04: // Group
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(aniView, "alpha", 0f);
                fadeOut.setDuration(2000);
                ObjectAnimator mover = ObjectAnimator.ofFloat(aniView, "translationX", -500f, 0f);
                mover.setDuration(2000);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(aniView, "alpha", 0f, 1f);
                fadeIn.setDuration(2000);
                AnimatorSet animatorSet = new AnimatorSet();
                // fadeOut first and then mover + fadeIn simultaneously
                animatorSet.play(mover).with(fadeIn).after(fadeOut);
                animatorSet.start();
                break;
            default:
                break;
        }
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
        if (id == R.id.item1) {
            Intent intent = new Intent(this, HitActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
