package cn.nec.nlc.example.jamesli.activitytest53preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences1, false);
          // if you set it to true, you will override any previous values with the defaults.
    }

    @Override
    protected void onResume() {
        super.onResume();

        // from preferences1.xml
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean syncConnPref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_SYNC_CONN, true);
        String syncConnPrefType = sharedPref.getString(SettingsActivity.KEY_PREF_SYNC_CONN_TYPE, "");
        ((TextView) findViewById(R.id.textViewHello)).setText("syncConnPref: " + syncConnPref
            + "\nsyncConnPrefType: " + syncConnPrefType);

//        // from preferences2.xml
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean prefKeyAutoDelete = sharedPref.getBoolean(SettingsActivity.PREF_KEY_AUTO_DELETE, false);
//        int prefKeySmsDeleteLimit = sharedPref.getInt(SettingsActivity.PREF_KEY_SMS_DELETE_LIMIT, 500);
//        int prefKeyMmsDeleteLimit = sharedPref.getInt(SettingsActivity.PREF_KEY_MMS_DELETE_LIMIT, 50);
//        ((TextView) findViewById(R.id.textViewHello)).setText
//                ("prefKeyAutoDelete: " + prefKeyAutoDelete
//                + "\nprefKeySmsDeleteLimit: " + prefKeySmsDeleteLimit
//                + "\nprefKeyMmsDeleteLimit: " + prefKeyMmsDeleteLimit);
        Log.i("MainActivity", "onResume()");
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
