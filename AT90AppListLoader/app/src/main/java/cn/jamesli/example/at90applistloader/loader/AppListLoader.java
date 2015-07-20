package cn.jamesli.example.at90applistloader.loader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.jamesli.example.at90applistloader.observer.InstalledAppsObserver;
import cn.jamesli.example.at90applistloader.observer.SystemLocaleObserver;

/**
 * Created by jamesli on 15-7-19.
 */
public class AppListLoader extends AsyncTaskLoader<List<AppEntry>> {
    private static final String TAG = "ADP_AppListLoader";
    private static final boolean DEBUG = true;

    private final PackageManager mPm;

    // Hold a reference to the loader's data
    private List<AppEntry> mApps;

    public AppListLoader(Context context) {
        super(context);
        mPm = getContext().getPackageManager();
    }

    @Override
    public List<AppEntry> loadInBackground() {
        if (DEBUG) Log.i(TAG, "+++ loadInBackground() called! +++");

        // Retrieve all installed applications
        List<ApplicationInfo> apps = mPm.getInstalledApplications(0);

        if (apps == null) {
            apps = new ArrayList<ApplicationInfo>();
        }

        // Create array of entries and load their labels
        List<AppEntry> entries = new ArrayList<>(apps.size());
        for (int i = 0; i < apps.size(); i++) {
            AppEntry entry = new AppEntry(this, apps.get(i));
            entry.loadLabel(getContext());
            entries.add(entry);
        }

        // Sort the list
        Collections.sort(entries, ALPHA_COMPARATOR);

        return entries;
    }

    @Override
    public void deliverResult(List<AppEntry> data) {
        if (isReset()) {
            if (DEBUG) Log.w(TAG, "+++ Warning! An async query came in while the Loader was reset! +++");
            if (data != null) {
                releaseResources(data);
                return;
            }
        }

        // Hold reference to the old data so that it doesn't get garbage collected
        // We must protect it until the new data has been delivered
        List<AppEntry> oldApps = mApps;
        mApps = data;

        if (isStarted()) {
            if (DEBUG) Log.i(TAG, "+++ Delivering results to the LoaderManager for" +
                    " the ListFragment to display! +++");
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldApps != null && oldApps != data) {
            if (DEBUG) Log.i(TAG, "+++ Releasing any old data associated with this Loader. +++");
            releaseResources(oldApps);
        }
    }

    @Override
    protected void onStartLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStartLoading() called! +++");

        if (mApps != null) {
            if (DEBUG) Log.i(TAG, "+++ Delivering previously loaded data to the client...");
            deliverResult(mApps);
        }

        // Register the observers that will notify the Loader when changes are made.
        if (mAppsObserver == null) {
            mAppsObserver = new InstalledAppsObserver(this);
        }
        if (mLocaleObserver == null) {
            mLocaleObserver = new SystemLocaleObserver(this);
        }

        if (takeContentChanged()) {
            // When the observer detects a new installed application, it will call
            // onContentChanged() on the Loader, which will cause the next call to
            // takeContentChanged() to return true. If this is ever the case (or if
            // the current data is null), we force a new load.
            if (DEBUG) Log.i(TAG, "+++ A content change has been detected... so force load! +++");
            forceLoad();
        } else if (mApps == null) {
            // If the current data is null... then we should make it non-null! :)
            if (DEBUG) Log.i(TAG, "+++ The current data is data is null... so force load! +++");
            forceLoad();    // this will ignore a previously loaded data set and load a new one
        }
    }

    @Override
    protected void onStopLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStopLoading() called! +++");

        // The Loader has been put in a stopped state, so we should attempt to
        // cancel the current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is; Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        if (DEBUG) Log.i(TAG, "+++ onReset() called! +++");

        // Ensure the loader is stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'apps'.
        if (mApps != null) {
            releaseResources(mApps);
            mApps = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
        if (mAppsObserver != null) {
            getContext().unregisterReceiver(mAppsObserver);
            mAppsObserver = null;
        }

        if (mLocaleObserver != null) {
            getContext().unregisterReceiver(mLocaleObserver);
            mLocaleObserver = null;
        }
    }

    @Override
    public void onCanceled(List<AppEntry> apps) {
        if (DEBUG) Log.i(TAG, "+++ onCanceled() called! +++");

        // Attempt to cancel the current asynchronous load.
        super.onCanceled(apps);

        // The load has been canceled, so we should release the resources
        // associated with 'mApps'.
        releaseResources(apps);
    }

    @Override
    public void forceLoad() {
        if (DEBUG) Log.i(TAG, "+++ forceLoad() called! +++");
        super.forceLoad();
    }

    private void releaseResources(List<AppEntry> apps) {
        // For a simple List, there is nothing to do. For something like a Cursor,
        // we would close it in this method. All resources associated with the
        // Loader should be released here.
    }

    // An observer to notify the Loader when new apps are installed/updated.
    private InstalledAppsObserver mAppsObserver;

    // The observer to notify the Loader when the system Locale has been changed.
    private SystemLocaleObserver mLocaleObserver;

    /**
     * Performs alphabetical comparison of {@link AppEntry} objects. This is
     * used to sort queried data in loadInBackground.
     */
    private static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
        Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AppEntry object1, AppEntry object2) {
            return sCollator.compare(object1.getLabel(), object2.getLabel());
        }
    };

    public PackageManager getPm() {
        return mPm;
    }
}
