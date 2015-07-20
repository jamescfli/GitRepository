package cn.jamesli.example.at90applistloader;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

import cn.jamesli.example.at90applistloader.loader.AppEntry;
import cn.jamesli.example.at90applistloader.loader.AppListLoader;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(android.R.id.content) == null) {
            AppListFragment listFragment = new AppListFragment();
            // android.R.id.content gives you the root element of a view,
            // without having to know its actual name/type/ID.
            fm.beginTransaction().add(android.R.id.content, listFragment).commit();
            // android.R.id.content => R.id.frame e.g. if we have activity_main.xml
        }
    }

    public static class AppListFragment extends ListFragment
            implements LoaderManager.LoaderCallbacks<List<AppEntry>> {
        private static final String TAG = "ADP_AppListFragment";
        private static final boolean DEBUG = true;

        // custome adapter
        private AppListAdapter mAdapter;

        private static final int LOADER_ID = 1;

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            // participate in populating the options menu by receiving a call to
            // onCreateOptionsMenu(Menu, MenuInflater) and related methods.
            setHasOptionsMenu(true);

            mAdapter = new AppListAdapter(getActivity());
            // default content for a ListFragment has a TextView that can be shown when list is empty
            setEmptyText("No applications");
            setListAdapter(mAdapter);
            // whether the list is being displayed
            setListShown(false);

            if (DEBUG) {
                Log.i(TAG, "+++ Calling initLoader()! +++");
            } else {
                Log.i(TAG, "+++ Reconnecting with existing Loader (id '1')... +++");
            }

            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        @Override
        public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args) {
            if (DEBUG) Log.i(TAG, "+++ onCreateLoader() called! +++");
            return new AppListLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<AppEntry>> loader, List<AppEntry> data) {
            if (DEBUG) Log.i(TAG, "+++ onLoadFinished() called! +++");
            mAdapter.setData(data);

            if (isResumed()) {  // true for the duration of onResume() + onPause()
                setListShown(true);     // with loading animation
            } else { // it is possible when loading is finished the fragment is not in Resumed mode
                // then no animation is required
                setListShownNoAnimation(true);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<AppEntry>> loader) {
            if (DEBUG) Log.i(TAG, "+++ onLoadReset() called! +++");
            mAdapter.setData(null);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_main, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_configure_locale:
                    configureLocale();
                    return true;
            }
            return false;
        }

        private void configureLocale() {
            Loader<AppEntry> loader = getLoaderManager().getLoader(LOADER_ID);
            if (loader != null) {
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                // respond by SystemLocaleObserver BroadcastReceiver
            }
        }
    }

}
