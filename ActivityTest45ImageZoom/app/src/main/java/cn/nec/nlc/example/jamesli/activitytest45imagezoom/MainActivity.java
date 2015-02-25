package cn.nec.nlc.example.jamesli.activitytest45imagezoom;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnTouchListener,
        GestureDetector.OnGestureListener {
    private static final String TAG = "MainActivity";
    private static final int FLING_MIN_DISTANCE = 100;
    private static final int FLING_MIN_VELOCITY = 200;

    private ImageView mImageView;
    private Button mButtonScaleDown;
    private Button mButtonScaleUp;
    private FrameLayout layoutFrameLayout;
    private LinearLayout layoutImage;
    private Bitmap bitmap;
    private int id = 0;
    private int displayWidth;
    private int displayHeight;
    private float scaleWidth = 1;
    private float scaleHeight = 1;
    private GestureDetector mGestureDetector;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the screen size, describe the size and density of this display
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

//        Bundle bundle = this.getIntent().getExtras();
//        Integer imageId = bundle.getInt("imageId");
//        Log.i(TAG, "onCreate, imageId = " + imageId);

//        if (bitmap != null)
//            bitmap.recycle();
//        bitmap = BitmapFactory.decodeResource(getResources(), imageId);
//        mImageView = (ImageView)findViewById(R.id.myImageView);
//        mImageView.setImageBitmap(bitmap);
//        mImageView.setOnTouchListener(this);
//        mImageView.setLongClickable(true);


        if (bitmap != null)
            bitmap.recycle();
        mImageView = (ImageView)findViewById(R.id.myImageView);
        bitmap = decodeSampledBitmapFromResource(getResources(), R.raw.pink_heart, 500, 500);
        mImageView.setImageBitmap(bitmap);

        // only two referred layouts
        layoutFrameLayout = (FrameLayout)findViewById(R.id.layoutFrameLayout);  // whole framelayout
        layoutImage = (LinearLayout)findViewById(R.id.layoutImage);             // image scroll view
        mButtonScaleDown = (Button)findViewById(R.id.buttonScaleDown);
        mButtonScaleUp = (Button)findViewById(R.id.buttonScaleUp);

        mButtonScaleDown.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleDown();
            }
        });

        mButtonScaleUp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleUp();
            }
        });
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

    private static int calculateInSampleSize(
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
                // because inSampleSize in BitmapFactory.Options has to be 2^n, not others
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "onDown", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onDown...");
        return false;
    }

    /* 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN,
     * 多个ACTION_MOVE, 1个ACTION_UP触发
     * 参数解释：
     * e1：第1个ACTION_DOWN MotionEvent
     * e2：最后一个ACTION_MOVE MotionEvent
     * velocityX：X轴上的移动速度，像素/秒
     * velocityY：Y轴上的移动速度，像素/秒
     * 触发条件 ：
     * X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
     * @see android.view.GestureDetector$OnGestureListener#onFling(android.view.MotionEvent,
     * android.view.MotionEvent, float, float)
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onFling...");

        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            // Fling left

            Toast.makeText(this, "Fling Left", Toast.LENGTH_SHORT).show();
        } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
                && Math.abs(velocityX) > FLING_MIN_VELOCITY) {

            // Fling right

            Toast.makeText(this, "Fling Right", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onLongPress...");

    }

    // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onScroll...");

        return false;
    }

    // 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
    // 注意和onDown()的区别，强调的是没有松开或者拖动的状态
    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onShowPress...");

    }

    // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onSingleTapUp...");

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onTouch...");

        // Set button visible
        mButtonScaleDown.setVisibility(View.VISIBLE);
        mButtonScaleUp.setVisibility(View.VISIBLE);

        return  mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        super.onTouchEvent(event);

        Log.i(TAG, "onTouchEvent");
        // Set button visible
        mButtonScaleDown.setVisibility(View.VISIBLE);
        mButtonScaleUp.setVisibility(View.VISIBLE);

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        super.onKeyDown(keyCode, event);

        Log.i(TAG, "onKeyDown...");
        // Set button visible
        mButtonScaleDown.setVisibility(View.VISIBLE);
        mButtonScaleUp.setVisibility(View.VISIBLE);

        return true;
    }

    private void scaleDown()    {
        int bmpWidth= bitmap.getWidth();
        int bmpHeight= bitmap.getHeight();

        Log.i(TAG, "bmpWidth = " + bmpWidth + ", bmpHeight = " + bmpHeight);

        // scale down ratio
        double scale=0.8;
        // dimension after scale-down
        scaleWidth=(float) (scaleWidth*scale);
        scaleHeight=(float) (scaleHeight*scale);
        // generate resized bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // convert from matrix to bmpWidth and bmpHeight
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bmpWidth,bmpHeight,matrix,true);

        if (id==0) {
            // delete the previous content of default imageView
            layoutImage.removeView(mImageView);
        } else {
            // delete the previous-scaled imageView
            layoutImage.removeView((ImageView) findViewById(id));
        }

        // generate scaled imageView，put it in resized bitmap，then put it in the layout
        id++;
        ImageView imageView = new ImageView(this);
        imageView.setId(id);
        imageView.setImageBitmap(resizeBmp);
        layoutImage.addView(imageView);
        Log.i(TAG, "imageView.getWidth() = " + imageView.getWidth()
                + ", imageView.getHeight() = " + imageView.getHeight());
        setContentView(layoutFrameLayout);
        // enable scale-up bottom after reached the max size
        mButtonScaleUp.setEnabled(true);
        mButtonScaleUp.setTextColor(Color.MAGENTA);
    }

    private void scaleUp() {
        int bmpWidth= bitmap.getWidth();
        int bmpHeight= bitmap.getHeight();

        Log.i(TAG, "bmpWidth = " + bmpWidth + ", bmpHeight = " + bmpHeight);

        // scale up ratio
        double scale=1.25;  // = 0.8 for scale down
        // dimension after scale-up
        scaleWidth=(float)(scaleWidth*scale);
        scaleHeight=(float)(scaleHeight*scale);
        // generate resized bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bmpWidth,
                bmpHeight,matrix,true);

        if(id==0) {
            // delete the previous content of default imageView
            layoutImage.removeView(mImageView);
        } else {
            // delete the previous-scaled imageView
            layoutImage.removeView((ImageView)findViewById(id));
        }

        // generate scaled imageView，put it in resized bitmap，then put it in the layout
        id++;
        ImageView imageView = new ImageView(this);
        imageView.setId(id);
        imageView.setImageBitmap(resizeBmp);
        layoutImage.addView(imageView);
        setContentView(layoutFrameLayout);
        // if scaling-up over the boundary of the screen, disable the scale-up button
        if( scaleWidth * scale * bmpWidth > bmpWidth * 3 ||
                scaleHeight * scale * bmpHeight > bmpWidth * 3 ||
                scaleWidth * scale * bmpWidth > displayWidth * 5 ||
                scaleHeight * scale * bmpHeight > displayHeight * 5) {
            mButtonScaleUp.setEnabled(false);
            mButtonScaleUp.setTextColor(Color.GRAY);
        } else {
            mButtonScaleUp.setEnabled(true);
            mButtonScaleUp.setTextColor(Color.MAGENTA);
        }
    }

}
