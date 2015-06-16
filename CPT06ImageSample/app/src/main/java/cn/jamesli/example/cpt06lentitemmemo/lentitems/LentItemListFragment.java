package cn.jamesli.example.cpt06lentitemmemo.lentitems;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import cn.jamesli.example.cpt06lentitemmemo.R;
import cn.jamesli.example.cpt06lentitemmemo.provider.LentItemsContract;

public class LentItemListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    interface MasterCallback {
        void addItem();
        void displayItem(long itemId);
    }

    /**
     * The key for storing fragment state
     */
    private static final String KEY_STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * The key for storing the two pane mode
     */
    private static final String KEY_TWOPANE = "keyTwoPane";
    /**
     * The unique identifier of the loader.
     */
    private static final int ITEMLIST_LOADER = 10;
    /**
     * The position of the currently activated item in the listview
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    /**
     * The callback for this fragment to handle user interaction.
     */
    private MasterCallback mCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof MasterCallback)) {
            throw new IllegalArgumentException("Embedding activity must implement "
                    + MasterCallback.class.getName());
        }
        mCallback = (MasterCallback) activity;
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                (Cursor) null,
                new String[]{LentItemsContract.Items.NAME},
                new int[]{android.R.id.text1},
                0
        );
        this.setListAdapter(adapter);
        getLoaderManager().initLoader(ITEMLIST_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cpsample_master, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Restore the previously serialized activated item position
        if ((savedInstanceState != null)
                && savedInstanceState.containsKey(KEY_STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(KEY_STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition !=  ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position
            outState.putInt(KEY_STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_master_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                ((MasterCallback) getActivity()).addItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        this.setActivatedPosition(position);
        mCallback.displayItem(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (getActivity() !=  null) {
            return new CursorLoader(getActivity(),
                    LentItemsContract.Items.CONTENT_URI,
                    LentItemsContract.Items.PROJECTION_ALL,
                    null,
                    null,
                    LentItemsContract.Items.SORT_ORDER_DEFAULT);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (getListAdapter() !=  null) {
            // Swap in a new Cursor, returning the old Cursor.
            // Unlike changeCursor(Cursor), the returned old Cursor is not closed.
            ((SimpleCursorAdapter) this.getListAdapter()).swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((SimpleCursorAdapter) this.getListAdapter()).swapCursor(null);
    }

    public static LentItemListFragment newInstance(boolean isInTwoPaneMode) {
        LentItemListFragment f = new LentItemListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_TWOPANE, isInTwoPaneMode);
        f.setArguments(bundle);
        return f;
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }
}
