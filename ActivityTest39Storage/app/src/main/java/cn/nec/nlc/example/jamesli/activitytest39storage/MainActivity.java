package cn.nec.nlc.example.jamesli.activitytest39storage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private final String FILENAME = "hello_file.txt";
    private final String PREF_NAME = "MyPrefFile.txt";
    private TextView textViewHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewHello = (TextView) findViewById(R.id.textViewHello);
    }

    public void onClickWriteFile(View view) {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write("hello world".getBytes());
            fos.write("This is a test1.".getBytes());
            fos.write("This is a test2.".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickWritePref(View view) {
        SharedPreferences settings = getSharedPreferences(PREF_NAME, 0);
        boolean silent = settings.getBoolean("silentMode", false);
        // flip the silent
        if (!silent) {
            silent = true;
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("silentMode", silent);
            // commit edits
            editor.commit();
        }
        // show shared preferences result in textView
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("silentMode", false)) {
            textViewHello.setText("Yes");
        } else {
            textViewHello.setText("No");
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
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
