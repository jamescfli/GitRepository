package cn.jamesli.example.at89greendaotest;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import cn.jamesli.example.at89greendaotest.src_gen.DaoMaster;
import cn.jamesli.example.at89greendaotest.src_gen.DaoSession;
import cn.jamesli.example.at89greendaotest.src_gen.Note;
import cn.jamesli.example.at89greendaotest.src_gen.NoteDao;


public class MainActivity extends ListActivity {

    private SQLiteDatabase db;

    private EditText mEditText;

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private NoteDao mNoteDao;

    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DevOpenHelper(context, name, cursorFactory)
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        mNoteDao = mDaoSession.getNoteDao();

        String textColumn = NoteDao.Properties.Text.columnName;
        String orderBy = textColumn + " COLLATE LOCALIZED ASC";
        cursor = db.query(mNoteDao.getTablename(), mNoteDao.getAllColumns(), null, null,
                null, null, orderBy);
        String[] from = { textColumn, NoteDao.Properties.Comment.columnName };
        int[] to = { android.R.id.text1, android.R.id.text2 };  // Res ID in simple_list_item_2

        // This constant was deprecated in API level 11.
        // This option is discouraged, as it results in Cursor queries being performed
        // on the application's UI thread and thus can cause poor responsiveness or
        // even Application Not Responding errors.
        // As an alternative, use LoaderManager with a CursorLoader.
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, cursor, from, to);
        setListAdapter(adapter);

        mEditText = (EditText) findViewById(R.id.editTextNote);
        addUiListeners();
    }

    protected void addUiListeners() {
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && !mEditText.getText().toString().isEmpty()) {
                    addNote();
                    return true;
                }
                return false;
            }
        });

        final View button = findViewById(R.id.buttonAdd);
        button.setEnabled(false);
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean enable = s.length() != 0;   // if delete back to empty string, enable still = false
                button.setEnabled(enable);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // the Add button
    public void onMyButtonClick(View view) {
        addNote();
    }

    private void addNote() {
        String noteText = mEditText.getText().toString();
        mEditText.setText("");

        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());
        Note note = new Note(null, noteText, comment, new Date());
        mNoteDao.insert(note);
        Log.d("DaoExample", "Inserted new note, ID: " + note.getId());

        // requery() method was deprecated in API level 11
        // Reason: huge data lead to long reading/writing time to block the UI thread.
        // but so far, it perfectly works with ListView and its updates
        cursor.requery();
//        requeryCursorUpdateListview();    // not automatically update ListView, only renew after reboot + onCreate
    }

//    private void requeryCursorUpdateListview() {
//        String orderBy = NoteDao.Properties.Text.columnName + " COLLATE LOCALIZED ASC";
//        cursor = db.query(mNoteDao.getTablename(), mNoteDao.getAllColumns(), null, null,
//                null, null, orderBy);
//        // with bug, ListView does not update as originally did by cursor.requery()
//        adapter.notifyDataSetChanged();
//    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        mNoteDao.deleteByKey(id);
        Log.d("DaoExample", "Deleted note, ID: " + id);
        cursor.requery();
//        requeryCursorUpdateListview();
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
