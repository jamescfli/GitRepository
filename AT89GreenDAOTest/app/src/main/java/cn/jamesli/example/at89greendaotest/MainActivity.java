package cn.jamesli.example.at89greendaotest;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import cn.jamesli.example.at89greendaotest.src_gen.NoteDao;


public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    private EditText mEditText;

    private NoteAsyncTaskLoader mNoteAsyncTaskLoader;

    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        mNoteAsyncTaskLoader.insert(noteText);
        // Ensure ListView will be updated accordingly
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        mNoteAsyncTaskLoader.deleteByKey(id);
        // Ensure ListView will be updated accordingly
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mNoteAsyncTaskLoader = new NoteAsyncTaskLoader(getApplicationContext());
        return mNoteAsyncTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);  // CursorAdapter method
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
