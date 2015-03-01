package cn.nec.nlc.example.jamesli.activitytest50fileprovider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    // The path to the root of this app's internal storage
    private File mPrivateRootDir;
    // The path to the "images" subdirectory
    private File mImagesDir;
    // Array of files in the images subdirectory
    private File[] mImageFiles;
    // Array of filenames corresponding to mImageFiles
    private String[] mImageFilenames;
    private Intent mResultIntent;
    private ListView mFileListView;

    // Initialize the Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up an Intent to send back to apps that request a file
        mResultIntent = new Intent
                ("cn.nec.nlc.example.jamesli.activitytest50fileprovider.ACTION_RETURN_FILE");
        // Get the files/ subdirectory of internal storage
        mPrivateRootDir = getFilesDir();
        // Get the files/images subdirectory;
        mImagesDir = new File(mPrivateRootDir, "images");
        // Get the files in the images subdirectory
        mImageFiles = mImagesDir.listFiles();
        // Set the Activity's result to null to begin with, as default
        setResult(Activity.RESULT_CANCELED, null);
        /*
         * Display the file names in the ListView mFileListView.
         * Back the ListView with the array mImageFilenames, which
         * you can create by iterating through mImageFiles and
         * calling File.getAbsolutePath() for each File
         */
        // Define a listener that responds to clicks on a file in the ListView
        mFileListView = (ListView) findViewById(R.id.listView);
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            /*
             * When a filename in the ListView is clicked, get its
             * content URI and send it to the requesting app
             */
            public void onItemClick(AdapterView<?> adapterView,
                                    View view,
                                    int position,
                                    long rowId) {
                /*
                 * Get a File for the selected file name.
                 * Assume that the file names are in the
                 * mImageFilename array.
                 */
                 File requestFile = new File(MainActivity.this.mImageFilenames[position]);
                /*
                 * Most file-related method calls need to be in
                 * try-catch blocks.
                 */
                // Use the FileProvider to get a content URI
                Uri fileUri = null;
                try {
                    fileUri = FileProvider.getUriForFile(
                            MainActivity.this,
                            "cn.nec.nlc.example.jamesli.activitytest50fileprovider.fileprovider",
                            requestFile);
                } catch (IllegalArgumentException e) {
                    Log.e("File Selector", "The selected file can't be shared: " +
                            requestFile);
                }
                if (fileUri != null) {
                    // Grant temporary read permission to the content URI
                    mResultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    // Put the Uri and MIME type in the result Intent
                    mResultIntent.setDataAndType(
                            fileUri,
                            getContentResolver().getType(fileUri));
                    // Set the result
                    MainActivity.this.setResult(Activity.RESULT_OK, mResultIntent);
                } else {
                    mResultIntent.setDataAndType(null, "");
                    MainActivity.this.setResult(RESULT_CANCELED, mResultIntent);
                }
            }
        });
    }

    // for Done button click
    public void onDoneClick(View v) {
        // Associate a method with the Done button
        finish();
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
