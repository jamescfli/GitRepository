package cn.jamesli.example.at90applistloader.loader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.io.File;

import cn.jamesli.example.at90applistloader.AppListAdapter;

/**
 * Created by jamesli on 15-7-19.
 */
public class AppEntry {
    private final AppListLoader mLoader;    // for some default and outdated values
    private final ApplicationInfo mInfo;    // -> mApkFile
    private final File mApkFile;    // install file
    private String mLabel;          // package name
    private Drawable mIcon;         // icon of app
    private boolean mMounted;

    public AppEntry(AppListLoader loader, ApplicationInfo info) {
        mLoader = loader;
        mInfo = info;
        // Full path to the base APK
        mApkFile = new File(info.sourceDir);
    }

    // getters
    public ApplicationInfo getApplicationInfo() {
        return mInfo;
    }

    public String getLabel() {
        return mLabel;
    }

    public Drawable getIcon() {
        if (mIcon == null) {
            if (mApkFile.exists()) {
                // Retrieve the current graphical icon associated with this item
                mIcon = mInfo.loadIcon(mLoader.getPm());
                return mIcon;
            } else {
                mMounted = false;
                // load default drawable - android.R.drawable.sym_def_app_icon - as icon
            }
        } else if (!mMounted) { // install on SD card and not mounted
            if (mApkFile.exists()) {
                mMounted = true;
                mIcon = mInfo.loadIcon(mLoader.getPm());
                return mIcon;
            }
        } else {
            return mIcon;
        }
        return mLoader.getContext().getResources().getDrawable(android.R.drawable.sym_def_app_icon);
    }

    @Override
    public String toString() {
        return mLabel;
    }

    public void loadLabel(Context context) {
        if (mLabel == null || !mMounted) {
            mMounted = false;
            mLabel = mInfo.packageName; // public
        } else {
            mMounted = true;
            CharSequence label = mInfo.loadLabel(context.getPackageManager());
            mLabel = label != null ? label.toString() : mInfo.packageName;
        }
    }
}
