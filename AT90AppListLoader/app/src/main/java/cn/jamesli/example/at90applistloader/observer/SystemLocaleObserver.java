package cn.jamesli.example.at90applistloader.observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import cn.jamesli.example.at90applistloader.loader.AppListLoader;

/**
 * Created by jamesli on 15-7-19.
 */
public class SystemLocaleObserver extends BroadcastReceiver {
    private static final String TAG = "ADP_SystemLocaleObserver";
    private static final boolean DEBUG = true;

    private AppListLoader mLoader;

    public SystemLocaleObserver(AppListLoader loader) {
        mLoader = loader;
        IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
        mLoader.getContext().registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEBUG) Log.i(TAG, "+++ The observer has detected a locale change!" +
                " Notifying Loader.. +++");

        // Tell the loader about the change.
        mLoader.onContentChanged();
        // Called when Loader.ForceLoadContentObserver detects a change.
        // The default implementation checks to see if the loader is currently started;
        // if so, it simply calls forceLoad(); otherwise, it sets a flag
        // so that takeContentChanged() returns true.
        // Must be called from the process's main thread.
    }
}
