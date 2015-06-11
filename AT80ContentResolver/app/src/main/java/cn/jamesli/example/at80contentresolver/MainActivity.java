package cn.jamesli.example.at80contentresolver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.UserDictionary;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
    private Button mButtonShowWords;
    private Button mButtonInsertWord;
    private TextView mTextViewWords;
    private ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonShowWords = (Button) findViewById(R.id.buttonShowWords);
        mButtonInsertWord = (Button) findViewById(R.id.buttonInsertWord);
        mTextViewWords = (TextView) findViewById(R.id.textViewWords);

        contentResolver = getContentResolver();

        mButtonShowWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] PROJECTION = new String[]{BaseColumns._ID, UserDictionary.Words.WORD};
                Cursor cursor = contentResolver.query(UserDictionary.Words.CONTENT_URI,
                        PROJECTION, null, null, null);
                if (cursor.moveToFirst()) {
                    StringBuilder textToShow = new StringBuilder();
                    do {
                        long id = cursor.getLong(0);
                        String word = cursor.getString(1);
                        textToShow.append(id + ":\t" + word + "\n");
                    } while (cursor.moveToNext());
                    mTextViewWords.setText(textToShow.toString());
                }
            }
        });
        mButtonInsertWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(UserDictionary.Words.WORD, "Beeblebrox");
                contentResolver.insert(UserDictionary.Words.CONTENT_URI, values);
                // use bulkInsert() method to add multiple records to the same URI
                // it takes an array of ContentValue objects instead of just one ContentValues object
            }
        });

//        // UPDATE
//        values.clear();
//        values.put(Words.WORD, "Zaphod");
//        Uri uri = ContentUris.withAppendedId(Words.CONTENT_URI, id);
//        long noUpdated = resolver.update(uri, values, null, null);

//        // DELETE
//        long noDeleted = resolver.delete
//                (Words.CONTENT_URI,
//                        Words.WORD + " = ? ",
//                        new String[]{"Zaphod"});
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
