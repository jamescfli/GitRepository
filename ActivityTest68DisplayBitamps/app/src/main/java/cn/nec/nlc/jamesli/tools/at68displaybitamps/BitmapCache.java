package cn.nec.nlc.jamesli.tools.at68displaybitamps;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache {
    private LruCache<String, Bitmap> mMemoryCache;

    public BitmapCache() {
        // could be initiated in Activity#onCreate() as well
        // Also, you're using Runtime.maxMemory to compute your cache size.
        // This means you're requesting the maximum amount of memory that the whole VM is allowed to use.
        // Returns the maximum number of bytes the heap can expand to.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());     // in Bytes
        // Note: In this example, one eighth of the application memory is allocated for our cache.
        // On a normal/hdpi device this is a minimum of around 4MB (32/8). A full screen GridView
        // filled with images on a device with 800x480 resolution would use around 1.5MB
        // (800*480*4 bytes), so this would cache a minimum of around 2.5 pages of images in memory.
        final int cacheSize = maxMemory / 8;

//        // The more common approach is the use the value given back to you by the
//        // ActivityManager.getMemoryClass() method.
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        int memClassBytes = am.getMemoryClass() * 1024 * 1024;
//        int cacheSize = memClassBytes / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public BitmapCache(int cacheSize) {
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
