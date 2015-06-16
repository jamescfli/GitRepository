package cn.jamesli.example.cpt06lentitemmemo.lentitems;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageHelper {
    // apply Universal Image Loader
    public static void loadImage(final Uri photoUri, final ImageView imageView,
                                 final View... viewsToMakeVisible) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)    // TODO customized for test
                .build();
        imageLoader.displayImage(photoUri.toString(), imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                // n.a.
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                // n.a.
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (viewsToMakeVisible != null) {
                    // views like labels, containers, and etc.
                    // originally set to be 'gone'
                    for (View v: viewsToMakeVisible) {
                        v.setVisibility(View.VISIBLE);
                    }
                }
                // imageView itself
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                // n.a.
            }
        });
    }
}
