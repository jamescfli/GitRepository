package cn.nec.nlc.jamesli.tools.at68displaybitamps;

import android.graphics.Bitmap;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by jamesli on 15-4-24.
 */
public final class DiskLruCacheSimple {
    private final File directory;
    private final long maxSize;

    private DiskLruCacheSimple(File directory, long maxSize) {
        this.directory = directory;
        this.maxSize = maxSize;
    }

    /**
     * Opens the cache in {@code directory}, creating a cache if none exists there.
     *
     * @param directory  a writable directory
     * @param maxSize    the maximum number of bytes this cache should use to store
     * @throws IOException if reading or writing the cache directory fails
     */
    public static DiskLruCacheSimple open(File directory, long maxSize)
            throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }

        // prefer to pick up where we left off
        DiskLruCacheSimple cache = new DiskLruCacheSimple(directory, maxSize);

        // create a new empty cache
        if (directory.mkdirs() || directory.exists()) {
            cache = new DiskLruCacheSimple(directory, maxSize);
            return cache;
        } else {
            throw new FileNotFoundException("directory not found " + directory);
        }
    }

    // TODO: to be finished
    public void put(String key, Bitmap bitmap) {
    }
    // TODO: to be finished
    public Bitmap get(String key) {
        return null;
    }
}
