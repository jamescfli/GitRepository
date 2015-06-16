package cn.jamesli.example.cpt06lentitemmemo;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CPSampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Image loading is not the topic of this demo app,
        // simply use Universal Image loader without
        // any further explanation.
        // Universal image loader initialization:
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).build();
        ImageLoader.getInstance().init(config);
    }
}