package cn.nec.nlc.jamesli.tools.at68displaybitamps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import cn.nec.nlc.jamesli.tools.at68displaybitamps.DiskLruCache.DiskLruCache;

public class BitmapDiskCache {
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB e.g.
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    private Context mContext;

    public BitmapDiskCache(Context context) {
        mContext = context;
        File cacheDir = getDiskCacheDir(mContext, DISK_CACHE_SUBDIR);
        new InitDiskCacheTask().execute(cacheDir);
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
    // but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
//        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
//        // otherwise use internal cache dir
//        final String cachePath =
//                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
//                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
//                        context.getCacheDir().getPath();

        final String cachePath = context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    // 'cache' means both memory cache and disk cache
    public void addBitmapToCache(String key, Bitmap bitmap) {
        // for test purpose to comment
//        // Add to memory cache as before
//        if (getBitmapFromMemCache(key) == null) {
//            mMemoryCache.put(key, bitmap);
//        }

        // add to memory cache as before
        synchronized (mDiskCacheLock) {
            if (getBitmapFromDiskCache(key) == null) {
                // TODO to be finished
                mDiskLruCache.putBitmap(key, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            // wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) { }
            }
            if (mDiskLruCache != null) {
                // TODO to be finished
                return mDiskLruCache.getBitmap(key);
            }
        }
        return null;
    }
}
