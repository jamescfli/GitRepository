package cn.nec.nlc.jamesli.tools.at68displaybitamps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    private Context mContext;
    // The WeakReference to the ImageView ensures that the AsyncTask does not prevent the
    // ImageView and anything it references from being garbage collected.
    private final WeakReference<ImageView> imageViewReference;
    public int data = 0;    // originally was private, but referred in
    private int reqWidth;
    private int reqHeight;

    public BitmapWorkerTask(Context context, ImageView imageView) {
        mContext = context;
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        reqWidth = imageViewReference.get().getWidth();
        reqHeight = imageViewReference.get().getHeight();
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return decodeSampledBitmapFromResource(mContext.getResources(), data, reqWidth, reqHeight);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        // There’s no guarantee the ImageView is still around when the task finishes,
        // so you must also check the reference in onPostExecute().
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask =
                    getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

    }

    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static int calculateInSampleSize
            (BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of images
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;   // no shrinking

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width, but not too large.
            while ((halfHeight / inSampleSize) > reqHeight &&
                    (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource
            (Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds = true to check dimensions without actually
        // decoding the bitmap
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with proper dimension from inSampleSize and relieve inJustDecodeBounds option
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
