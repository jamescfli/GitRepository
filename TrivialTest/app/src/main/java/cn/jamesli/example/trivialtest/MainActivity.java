package cn.jamesli.example.trivialtest;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity {

    @InjectView(R.id.textView)
    private TextView mTextView;
    @InjectView(R.id.editView)
    private EditText mEditText;
    @InjectView(R.id.buttonSaveToFile)
    private Button buttonSaveToFile;
    @InjectView(R.id.buttonShowError)
    private Button buttonShowError;
    @InjectView(R.id.buttonClose)
    private Button buttonClose;
    @InjectView(R.id.listView)
    private ListView mListView;

    private View errorView;

    private static final String TAG = "MainActivity";

    private String editString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate() => start from here");

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        ViewParent viewParent = mainLayout.getParent();
        // prove FrameLayout was automatically wrapping the RelativeLayout by setContentView()
        Log.i(TAG, "The parent of mainlayout is " + viewParent);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, " => onClick()");
            }
        });

        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    Log.i(TAG, " => onTouch() - DOWN");
                } else if (action == MotionEvent.ACTION_UP) {
                    Log.i(TAG, " => onTouch() - UP");
                }
                return false;
                // false is likely (but not necessarily, in case of press and slide) to initiate
                // onClick later, true will not
            }
        });

        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                new String[] {"first", "second", "third", "fourth", "fifth"}));
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // or CHOICE_MODE_SINGLE
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setTitle("Item " + (position+1) + " was clicked");
            }
        });
//        // No response on ItemSelected
//        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "Item " + (position + 1) + " was selected", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                Toast.makeText(MainActivity.this, "Nothing was selected", Toast.LENGTH_LONG).show();
//            }
//        });

        buttonSaveToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get local time as part of the file name
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                Date date = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String filename = "myFile_" + dateFormat.format(date) + ".dat";
                File file = new File(MainActivity.this.getFilesDir(), filename);
//                // other options
//                1) external storage and save for public
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                2) external storage and save for private
//                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));  // auto-flush
                    final int SIZE = mListView.getAdapter().getCount();
                    for (int i = 0; i < SIZE; i++) {
                        out.println(mListView.getAdapter().getItem(i).toString());
                    }
                    if (out != null)
                        out.close();
                    if (out.checkError())
                        Log.i(TAG, "Error when closing the writer");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonShowError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showError();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContent();
            }
        });

//        // Activity can be terminated from here by
//        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        // save mEditText content is not necessary since the default implementation of
//        // onSaveInstanceState() saves infomation about the state of the activity's view hierarchy,
//        // such as the text in an EditText widget or the scroll position of a ListView
//        // To save additional state information for your activity, you must implement onSaveInstanceState()
//
//        // To make the following line effective, you can comment out super.onSaveInstanceState(outState);
//        editString = mEditText.getText().toString();
        Log.i(TAG, "onPause() => save edit string");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        mEditText.setText(editString);
        Log.i(TAG, "onRestart() => re-input edit string");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState()");
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
            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);   // call the following number
//            intent.setData(Uri.parse("content://contacts/people/"));
            intent.setAction(Intent.ACTION_SEARCH);     // start search
            intent.putExtra(SearchManager.QUERY, "ANDROID");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showError() {
        // not repeated infalte
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
            return;
        }

        ViewStub stub = (ViewStub)findViewById(R.id.error_layout);
        errorView = stub.inflate();
    }

    private void showContent() {
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
    }
}
