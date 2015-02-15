package cn.nec.nlc.example.jamesli.activitytest42storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class PrefChangeActivity extends ActionBarActivity {
    private final String PREFS_NAME = "MyPrefsFile.pref";
    private final String FILENAME_INTERNAL = "MyInternalFile.txt";
    private final String FILENAME_EXTERNAL = "MyExternalFile.txt";
    private final String TAG = "LifeCycleTag";
    private final String LOG_TAG = "ActivityTest42Log";
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

        saveToInternalFile();

        // other useful methods
        // getFilesDir()
        //  Gets the absolute path to the filesystem directory where your internal files are saved.
        // getDir()
        //  Creates (or opens an existing) directory within your internal storage space.
        // deleteFile()
        //  Deletes a file saved on the internal storage.
        // fileList()
        //  Returns an array of files currently saved by your application.
        // getExternalFilesDir()
        //  use a private storage directory on the external storage by calling this method

        saveToExternalFile();

        Log.i(TAG, "onResume()");
    }

    private void saveToInternalFile() {
        // information to be saved in file
        Time time = new Time();
        time.setToNow();    // current time
        String string = "Time recorded: " + time.format2445() + "\n";

        try {
            FileOutputStream fos = openFileOutput(FILENAME_INTERNAL, Context.MODE_APPEND);
            // .. MODE_PRIVATE will create the file (or replace a file of the same name) and
            // make it private to your application.
            // MODE_APPEND: if the file already exists then write data to the end of the existing
            // file instead of erasing it.
            fos.write(string.getBytes());
            Toast.makeText(this, string + "was written to Internal Storage", Toast.LENGTH_LONG)
                    .show();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToExternalFile() {
        // Beginning with Android 4.4, WRITE_EXTERNAL_STORAGE is not required if you're reading or
        // writing only files that are private to your app.
        if (isExternalStorageWritable()) {
            File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                    FILENAME_EXTERNAL);
            // prepare for the content
            // other useful methods
            // getFilesDir()
            //  Gets the absolute path to the filesystem directory where your internal files are saved.
            // getDir(String name, int mode)
            //  Creates (or opens an existing) directory within your internal storage space.
            // deleteFile(String name)
            //  Deletes a file saved on the internal storage.
            // fileList()
            //  Returns an array of files currently saved by your application.
            // getExternalFilesDir()
            //  use a private storage directory on the external storage by calling this method
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getFilesDir(): " + getFilesDir().toString() + "\n");
            stringBuilder.append("fileList():\n");
            for (int i = 0, SIZE = fileList().length; i < SIZE; i++) {
                stringBuilder.append("\t" + fileList()[i].toString() + "\n");
            }
            stringBuilder.append("getExternalFilesDir():" + "\n"
                    + "\tDocs: " + getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "\n"
                    + "\tMovies: " + getExternalFilesDir(Environment.DIRECTORY_MOVIES).toString() + "\n"
                    + "\tMusic: " + getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "\n"
                    + "\tPictures: " + getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "\n"
                    + "\tDCIM: " + getExternalFilesDir(Environment.DIRECTORY_DCIM).toString() + "\n");
            // .. getExternalFilesDir():
            // /storage/sdcard/Android/data/cn.nec.nlc.example.jamesli.activitytest42storage/files/
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(stringBuilder.toString().getBytes());
                Toast.makeText(this, "External file was written to " + file.toString(),
                        Toast.LENGTH_LONG).show();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "External storage is not accessible!", Toast.LENGTH_LONG).show();
        }
    }

    // checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    // creates a directory for a new photo album in the public pictures directory
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
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
