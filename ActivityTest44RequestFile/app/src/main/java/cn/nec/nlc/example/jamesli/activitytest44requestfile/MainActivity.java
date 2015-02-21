package cn.nec.nlc.example.jamesli.activitytest44requestfile;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity {
    private Intent mRequestFileIntent;
    private static final int REQUEST_CODE = 1;
    private ParcelFileDescriptor mInputPFD;
    private Bitmap bitmap;
    private ImageView mImageView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mTextView = (TextView) findViewById(R.id.textView);

        mRequestFileIntent = new Intent(Intent.ACTION_PICK);
        mRequestFileIntent.setType("image/jpg");
//        mRequestFileIntent.setAction(Intent.ACTION_GET_CONTENT);
//        mRequestFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        Log.i("MainActivity", "onCreate()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("MainActivity", "onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MainActivity", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "onDestroy()");
    }

    public void requestImage(View view) {
        startActivityForResult(mRequestFileIntent, REQUEST_CODE);
          // 1 ~ requestCode, return to onActivityResult()
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnIntent) {
        InputStream stream = null;
        // check the result state to see if the selection works or not
        if ((resultCode != RESULT_OK) || (requestCode != REQUEST_CODE)) {
            // Exit without doing anything else
            return;
        } else {
            // get the file's content URI from the incoming Intent
            Uri returnUri = returnIntent.getData();
            /*
             * Try to open the file for "read" access using the
             * returned URI. If the file isn't found, write to the
             * error log and return.
             */
            try {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;
//                // display a 100x100 pixel thumbnail, as shown in the following
//                mImageView.setImageBitmap(
//                        decodeSampledBitmapFromResource(getResources(), R.id.myimage, 100, 100));
                mImageView.setImageBitmap(decodeSampledBitmapFromUri(returnUri,
                        mImageView.getWidth(), mImageView.getHeight()));
                // ImageView 788x656, XiaoMi note 1280x720 display size
                // Smartisan T1 resolution = 1920 x 1080, 445 PPI
                mTextView.setText("ImageView dimension: " + mImageView.getHeight()
                        + " x " + mImageView.getWidth() + "\n"
                        + "Screen dimension: " + screenHeight + " x " + screenWidth);

//                // test OOM: Out of Memory Exception may pop out, getResources().getDisplayMetrics()
//                if (bitmap != null)
//                    bitmap.recycle();
//                stream = getContentResolver().openInputStream(returnUri);
//                // display in imageView
//                bitmap = BitmapFactory.decodeStream(stream);
//                mImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("MainActivity", "File not found.");
                return;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                Log.e("MainActivity", "out of memory for openInputStream");
            } finally {
                if (stream !=  null)
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
//            // Get a regular file descriptor for the file
//            FileDescriptor fd = mInputPFD.getFileDescriptor();
//            // ...
        }
    }

    private Bitmap decodeSampledBitmapFromUri(Uri returnUri, int reqWidth, int reqHeight)
            throws FileNotFoundException {
        /*
         * Get the content resolver instance for this context, and use it
         * to get a ParcelFileDescriptor for the file.
         */
        mInputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
          // throws FileNotFoundException
        // Get a regular file descriptor for the file
        FileDescriptor fd = mInputPFD.getFileDescriptor();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (bitmap != null)
            bitmap.recycle();
        bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
        // Calculate inSampleSize, display a reqWidth*reqHeight pixel thumbnail
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);

//        // getContentResolver().openInputStream() does not work properly
//        InputStream inputStream = null;
//        inputStream = getContentResolver().openInputStream(returnUri);
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        bitmap = BitmapFactory.decodeStream(inputStream, null, options);
//        // Calculate inSampleSize, display a reqWidth*reqHeight pixel thumbnail
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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
