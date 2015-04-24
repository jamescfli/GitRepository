package cn.nec.nlc.jamesli.tools.at68displaybitamps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {
    private Button buttonLoadImage;
    private ImageView mImageView;
    private Bitmap mBitmap;     // mPlaceHolderBitmap in loadBitmap() method
    private BitmapCache mBitmapCache;
//    private BitmapWorkerTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLoadImage = (Button) findViewById(R.id.buttonLoadImage);
        mImageView = (ImageView) findViewById(R.id.imageView);
        // set the default image
//        mImageView.setImageResource(R.drawable.placeholder);    // 25K, 500*350px
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
        mBitmapCache = new BitmapCache(this);
//        mTask = new BitmapWorkerTask(this, mImageView, mBitmapCache);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadBitmap(R.drawable.jaguars_logo, mBitmap, mImageView);
            }
        });
    }

    public void loadBitmap(int resId, Bitmap mPlaceHolderBitmap, ImageView imageView) {
//        BitmapWorkerTask task = new BitmapWorkerTask(this, imageView);
//        task.execute(resId);
//        if (cancelPotentialWork(resId, imageView)) {
//            final BitmapWorkerTask task = new BitmapWorkerTask(this, imageView, mBitmapCache);
//            final AsyncDrawable asyncDrawable =
//                    new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
//            imageView.setImageDrawable(asyncDrawable);
//            task.execute(resId);
//        }

        final String imageKey = String.valueOf(resId);
        final Bitmap bitmap = mBitmapCache.getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            // already has the bitmap in cache
            mImageView.setImageBitmap(bitmap);
        } else {
            if (cancelPotentialWork(resId, imageView)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(this, imageView, mBitmapCache);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(resId);
            }
        }
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    // A helper method is used above to retrieve the task associated with a particular ImageView.
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
