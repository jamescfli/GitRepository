package cn.nec.nlc.example.jamesli.activitytest41soundrecording;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaCodecInfo;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class SoundRecordingActivity extends ActionBarActivity {
    private MediaRecorder recorder;
    private File audioFile = null;
    private static final String TAG = "SoundRecordingActivity";
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.start);
        stopButton = (Button) findViewById(R.id.stop);
    }

    public void startRecording(View view) throws IOException {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        // get external storage directory from Environment
        // This directory may not currently be accessible if
        //      it has been mounted by the user on their computer,
        //      has been removed from the device,
        //      or some other problem has happened.
        // Traditionally this is an SD card, but it may also be implemented as built-in storage in a device.
        File sampleDir = Environment.getExternalStorageDirectory();
        try {
            // insert time to the file name
            Time time = new Time();
            time.setToNow();
            // Creates an empty temporary file in the given directory
            // (String prefix, String suffix, File directory)
            audioFile = File.createTempFile("sound-DT"+time.format2445(), ".3gp", sampleDir);
        } catch (IOException e) {
            Log.e(TAG, "SD Card access error");
            return;
        }
        // State Transition:
        // Initial -> Initialized -> DataSourceConfigured -> Prepared -> Recording -> Initial -> Released
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);     // Initialized
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // DataSourceConfigured
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audioFile.getAbsolutePath());
        recorder.prepare();     // Prepared
        recorder.start();       // Recording
    }

    public void stopRecording(View view) {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        recorder.stop();    // or reset() to go back to Initial
        recorder.release();     // Released
        addRecordingToMediaLibrary();
    }

    protected void addRecordingToMediaLibrary() {
        // used to store a set of values that the ContentResolver can process
        ContentValues values = new ContentValues(4);    // 4 is the given initial size
        long current = System.currentTimeMillis();
        // size of 4 for values, all in String
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audioFile.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        // .. time the file was added to the media provider Units are seconds since 1970
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp"); // MIME type of the file
        values.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());   // data stream for the file

        // provides applications access to the content model
        ContentResolver contentResolver = getContentResolver();

        // The content:// style URI for the "primary" external storage volume.
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // Inserts a row into a table at the given uri.
        Uri newUri = contentResolver.insert(base, values);

        // Request the media scanner to scan a file and add it to the media database
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
        Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();
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
