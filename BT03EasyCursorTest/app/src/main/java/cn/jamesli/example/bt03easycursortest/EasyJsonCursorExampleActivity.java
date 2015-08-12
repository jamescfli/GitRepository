package cn.jamesli.example.bt03easycursortest;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import cn.jamesli.example.bt03easycursortest.database.loader.JsonLoader;
import uk.co.alt236.easycursor.EasyCursor;

/**
 * Created by jamesli on 15-8-11.
 */
public class EasyJsonCursorExampleActivity extends FragmentActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private final int LOADER_ID = 1;

    private SimpleCursorAdapter mAdapter;
    private Button mSaveQueryButton;
    private Loader<Cursor> mLoader = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursor_selection);

        final ListView mListView = (ListView) findViewById(android.R.id.list);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mSaveQueryButton = (Button) findViewById(R.id.buttonSave);
        findViewById(R.id.spinner_container).setVisibility(View.GONE);

        final String[] from = new String[] {"name", "gender", "age", "guid", "about"};
        final int[] to = new int[] {R.id.name, R.id.gender, R.id.age, R.id.guid, R.id.about};

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_json, null, from, to, 0);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
        // .. When enabled, user can quickly scroll through lists by dragging the fast scroll thumb
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mLoader = new JsonLoader(this);
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor != null && cursor.getCount() != 0 && cursor instanceof EasyCursor) {
            final EasyCursor eCursor = (EasyCursor) cursor;
            if (eCursor.getQueryModel() != null) {
                mSaveQueryButton.setVisibility(View.VISIBLE);
            } else {
                // since eCursor is not generated via an EasyQueryModel, so eCursor.getQueryModel() = null
                mSaveQueryButton.setVisibility(View.INVISIBLE);
            }
        } else {
            mSaveQueryButton.setVisibility(View.VISIBLE);
        }
        mAdapter.changeCursor(cursor);

//        // if with Fragment The list should now be shown.
//        if (isResumed()) {
//            setListShown(true);
//        } else {
//            setListShownNoAnimation(true);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }

    public void onExecuteClick(final View view) {
        // could be in onCreate() with initLoader(LOADER_ID, null, this)
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
