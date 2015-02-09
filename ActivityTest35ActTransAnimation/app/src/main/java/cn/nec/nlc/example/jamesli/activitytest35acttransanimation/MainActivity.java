package cn.nec.nlc.example.jamesli.activitytest35acttransanimation;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {
    private EditText et;
    private ViewGroup viewGroup;
    public static final String TAG = "MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.editView1);

        // allows setting animations on a layout container
        // and a change on the view hierarchy of this container will be animated
        LayoutTransition l = new LayoutTransition();
        // all transition types besides CHANGING are enabled by default.
        // enable CHANGING animations by calling this method with the CHANGING transitionType.
        l.enableTransitionType(LayoutTransition.CHANGING);
        viewGroup = (ViewGroup) findViewById(R.id.radio_button_group);
        viewGroup.setLayoutTransition(l);
    }

    public void onClickStartSecActivity(View view) {
        if (et.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input something", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra(TAG, et.getText().toString());
            // animation start-uptest
            // ActivityOptions.makeScaleUpAnimation (View source, int startX, int startY, int width, int height)
            // from API 16: new activity is scaled from a small originating area of the screen to its final full representation
            ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());
            // options in Bundle: Additional options for how the Activity should be started.
            startActivity(intent, options.toBundle());
//            // normal start-up
//            startActivity(intent);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
