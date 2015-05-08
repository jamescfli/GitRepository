package cn.nec.nlc.jamesli.tools.at72mdanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // we already did this in styles.xml
//        // inside your activity (if you did not enable transitions in your theme), go before setContentView()
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        // set an exit transition
//        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_main);
        ((SurfaceView) findViewById(R.id.surfaceView)).setVisibility(View.INVISIBLE);
    }

    public void revealSurfaceView(View view) {
        View myView = findViewById(R.id.surfaceView);
        int cx = (myView.getLeft() + myView.getRight()) / 2;    // left 48, right 948, cx = 498
        int cy = (myView.getTop() + myView.getBottom()) / 2;    // top 192, bottom 792, cy = 492
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()); // width 900, height 600
        // Added in API Level 21
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void hideSurfaceView(View view) {
        final View myView = findViewById(R.id.surfaceView);
        int cx = (myView.getLeft() + myView.getRight()) / 2;    // left 48, right 948, cx = 498
        int cy = (myView.getTop() + myView.getBottom()) / 2;    // top 192, bottom 792, cy = 492
        int initialRadius = myView.getWidth(); // width 900, height 600
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    public void startAnotherActivity(View view) {
        Intent intent = new Intent(this, TheOtherActivity.class);
        startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
