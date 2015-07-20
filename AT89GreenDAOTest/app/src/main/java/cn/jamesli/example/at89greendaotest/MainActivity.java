package cn.jamesli.example.at89greendaotest;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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


public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    private EditText mEditText;

    private static SQLiteDatabase db;
    private DaoMaster.DevOpenHelper helper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static NoteDao mNoteDao;

    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DevOpenHelper(context, name, cursorFactory)
        helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        mNoteDao = mDaoSession.getNoteDao();

//        String orderBy = NoteDao.Properties.Text.columnName + " COLLATE LOCALIZED ASC";
//        mCursor = db.query(mNoteDao.getTablename(), mNoteDao.getAllColumns(), null, null,
//                null, null, orderBy);

        String[] from = { NoteDao.Properties.Text.columnName, NoteDao.Properties.Comment.columnName };
        int[] to = { android.R.id.text1, android.R.id.text2 };  // Res ID in simple_list_item_2

        // set cursor or null in the first place and update later by onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                from, to, 0);   // cursor = null and will be loaded later, flags = 0
        setListAdapter(mAdapter);   // the only place to set ListAdapter

        getLoaderManager().initLoader(LOADER_ID, null, this);

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
        // Ensure ListView will be updated accordingly
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        mNoteDao.deleteByKey(id);
        Log.d("DaoExample", "Deleted note, ID: " + id);
        // Ensure ListView will be updated accordingly
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new NoteCursorLoader(getApplicationContext(), db, helper, mDaoMaster, mDaoSession, mNoteDao);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);  // CursorAdapter method
        // Swap in a new Cursor, returning the old Cursor. Unlike changeCursor(Cursor), the returned old Cursor is not closed.
//        mAdapter.changeCursor(data);  // Change the underlying cursor to a new cursor.
        // Difference: swap -> old cursor will be returned, change -> return void, old cursor is closed.
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


}
