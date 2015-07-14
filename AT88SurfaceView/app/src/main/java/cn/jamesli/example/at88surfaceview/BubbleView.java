package cn.jamesli.example.at88surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by jamesli on 15-7-13.
 */
public class BubbleView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "BubbleView";

    private final Bitmap mBitmap;
    private final int mBitmapHeightAndWidth, mBitmapHeightAndWidthAdj;

    private final int mDisplayWidth, mDisplayHeight;    // screen size
    private float mX, mY, mDx, mDy, mRotation;          // bubble position and orientation
    private final SurfaceHolder mSurfaceHolder;
    private final Paint mPainter = new Paint();
    private Thread mDrawingThread;

    private static final int MOVE_STEP = 1;
    private static final float ROT_STEP = 1.0f;

    public BubbleView(Context context, Bitmap bitmap, DisplayMetrics displayMetrics) {
        super(context);

        mBitmapHeightAndWidth = (int) getResources().getDimension(
                R.dimen.image_height_width);
        // stretch the bitmap to favourable original size
        this.mBitmap = Bitmap.createScaledBitmap(bitmap,
                mBitmapHeightAndWidth, mBitmapHeightAndWidth, false);

        mBitmapHeightAndWidthAdj = mBitmapHeightAndWidth / 2;

        mDisplayWidth = displayMetrics.widthPixels;
        mDisplayHeight = displayMetrics.heightPixels;

        Random r = new Random();
        // random position within the screen, keep the center within the surfaceView range
        mX = (float) r.nextInt(mDisplayWidth - mBitmapHeightAndWidth);  // MOTO G: w720*h1184 (1280-ActionBar)
        mY = (float) r.nextInt(mDisplayHeight - mBitmapHeightAndWidth);
//        mX = 100.0f;
//        mY = 100.0f;
        mDx = (float) r.nextInt(mDisplayWidth) / mDisplayWidth;
        mDx *= r.nextInt(2) == 1 ? MOVE_STEP : -1 * MOVE_STEP;      // move to left/right
        mDy = (float) r.nextInt(mDisplayHeight) / mDisplayHeight;
        mDy *= r.nextInt(2) == 1 ? MOVE_STEP : -1 * MOVE_STEP;      // move to up/down
        mRotation = 1.0f;   // initial value

        mPainter.setAntiAlias(true);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private void drawBubble(Canvas canvas) {
        canvas.drawColor(Color.DKGRAY);
        mRotation += ROT_STEP;  // in degrees
        canvas.rotate(mRotation, mX + mBitmapHeightAndWidthAdj, mY + mBitmapHeightAndWidthAdj);
        canvas.drawBitmap(mBitmap, mX, mY, mPainter);
    }

//    private void drawBubble(Canvas canvas, float mXInput, float mYInput) {
//        canvas.drawColor(Color.DKGRAY);
//        mRotation += ROT_STEP;  // in degrees
//        canvas.rotate(mRotation, mXInput + mBitmapHeightAndWidthAdj, mYInput + mBitmapHeightAndWidthAdj);
//        canvas.drawBitmap(mBitmap, mXInput, mYInput, mPainter);
//    }

    private boolean move() {
        mX += mDx;
        mY += mDy;
        if (mX < 0
                || mX > mDisplayWidth - mBitmapHeightAndWidth
                || mY < 0
                || mY > mDisplayHeight - mBitmapHeightAndWidth) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated() is called.");
        mDrawingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = null;
                while (!Thread.currentThread().isInterrupted() && move()) {
//                    Log.i(TAG, "update Canvas by lockCanvas().");
                    canvas = mSurfaceHolder.lockCanvas();
                    if (null != canvas) {
                        drawBubble(canvas);
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        });
        mDrawingThread.start();
        Log.i(TAG, "mDrawingThread is started!");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged() is called.");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed() is called.");
        if (null != mDrawingThread) {
            mDrawingThread.interrupt();
            Log.i(TAG, "mDrawingThread is interrupted!");
        }
    }

//    public void resetToCenter() {
//        Log.i(TAG, "resetToCenter() is called.");
//        mX = ((float) mDisplayWidth) / 2;
//        mY = ((float) mDisplayHeight) / 2;
//    }
}
