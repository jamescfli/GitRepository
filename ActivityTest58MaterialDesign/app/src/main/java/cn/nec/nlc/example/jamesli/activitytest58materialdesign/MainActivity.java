package cn.nec.nlc.example.jamesli.activitytest58materialdesign;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

//// java.lang.IllegalStateException:
//// You need to use a Theme.AppCompat theme (or descendant) with this activity
//public class MainActivity extends ActionBarActivity {

// The reason is because ActionBarActivity requires AppCompat theme to be applied, change to Activity
public class MainActivity extends Activity {
    private Button buttonStartOtherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enable transition
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        buttonStartOtherActivity = (Button) findViewById(R.id.buttonStartOtherActivity);
        buttonStartOtherActivity.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(new Explode());
                Intent intent = new Intent(MainActivity.this, OtherActivity.class);
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());

            }
        });
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
