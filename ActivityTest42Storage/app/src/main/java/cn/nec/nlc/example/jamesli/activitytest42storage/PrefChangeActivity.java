package cn.nec.nlc.example.jamesli.activitytest42storage;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class PrefChangeActivity extends ActionBarActivity {
    private final String PREFS_NAME = "MyPrefsFile.pref";
    private final String TAG = "LifeCycleTag";
    private TextView textViewHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewHello = (TextView) findViewById(R.id.textViewHello);

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);   // 0 ~ MODE_PRIVATE
        // getSharedPreferences() - Use this if you need multiple preferences files identified by
        // name, which you specify with the first parameter.
//        SharedPreferences settings = getPreferences(0);     // 0 ~ MODE_PRIVATE
//        // getPreferences() - Use this if you need only one preferences file for your Activity.
//        // Because this will be the only preferences file for your Activity, you don't supply a name.
        boolean silent = settings.getBoolean("silentMode", false);
        setSilent(silent);

        Log.i(TAG, "onCreate()");
    }

    private void setSilent(boolean silent) {
        textViewHello.setText("Silent is set to: " + String.valueOf(silent));
    }

    @Override
    protected void onRestart() {
        super.onStart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onStart();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onStart();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        // get silent value in preferences
        boolean silent = settings.getBoolean("silentMode", false);
        silent = !silent;   // flip it
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("silentMode", silent);

        // Commit the edits!
        editor.commit();

        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onStart();
        Log.i(TAG, "onDestroy()");
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
