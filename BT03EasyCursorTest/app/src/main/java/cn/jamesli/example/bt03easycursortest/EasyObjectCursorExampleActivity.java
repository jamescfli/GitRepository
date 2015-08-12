package cn.jamesli.example.bt03easycursortest;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import cn.jamesli.example.bt03easycursortest.container.JsonDataGsonModel;
import cn.jamesli.example.bt03easycursortest.database.loader.ObjectLoader;
import uk.co.alt236.easycursor.EasyCursor;
import uk.co.alt236.easycursor.objectcursor.EasyObjectCursor;

/**
 * Created by jamesli on 15-8-12.
 */
public class EasyObjectCursorExampleActivity extends FragmentActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private final int LOADER_ID = 1;

    private SimpleCursorAdapter mAdapter;
    private Button mSaveQueryButton;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursor_selection);
        final ListView mListView = (ListView) findViewById(android.R.id.list);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mSaveQueryButton = (Button) findViewById(R.id.buttonSave);
        findViewById(R.id.spinner_container).setVisibility(View.GONE);

        final String[] from = new String[]{"name", "gender", "age", "guid", "about"};
        final int[] to = new int[]{R.id.name, R.id.gender, R.id.age, R.id.guid, R.id.about};

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_json, null, from, to, 0);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(EasyObjectCursorExampleActivity.this,
//                        "Name: " + ((EasyObjectCursor) mAdapter.getItem(position)).getObject("name") + " was clicked",
//                        Toast.LENGTH_LONG).show();
//                // .. can also getObject others like "gender", "age", "guid", "about"
//            }
//        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ObjectLoader(this);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> arg0, final Cursor cursor) {
        if (cursor != null && cursor.getCount() != 0 && cursor instanceof EasyCursor) {
            final EasyCursor eCursor = (EasyCursor) cursor;
            if (eCursor.getQueryModel() != null) {
                mSaveQueryButton.setVisibility(View.VISIBLE);
            } else {
                mSaveQueryButton.setVisibility(View.INVISIBLE);
            }
        } else {
            mSaveQueryButton.setVisibility(View.INVISIBLE);
        }

        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> arg0) {
        mAdapter.changeCursor(null);
    }

    public void onExecuteClick(final View v) {
        getSupportLoaderManager().restartLoader(
                LOADER_ID,
                null,
                this);
    }
}
