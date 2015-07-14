package cn.jamesli.example.at87compassbysensor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by jamesli on 15-7-13.
 */
public class CompassArrowSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int DESIRED_BITMAP_WIDTH = 250;

    // createScaledBitmap(): creates a new bitmap, scaled from an existing bitmap, when possible
    Bitmap mBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
            R.mipmap.arrow), DESIRED_BITMAP_WIDTH, DESIRED_BITMAP_WIDTH, true);
    private int mBitmapWidth = mBitmap.getWidth();

    // Height and Width of Main View
    private int mParentWidth;
    private int mParentHeight;

    // Center of Main View
    private int mParentCenterX;
    private int mParentCenterY;

    // Top left position of this View
    private int mViewTopY;
    private int mViewLeftX;

    private float mRotationInDegrees;

    private final SurfaceHolder mSurfaceHolder;

    private Thread mDrawingThread = null;

    public CompassArrowSurfaceView(Context context) {
        super(context);

        mRotationInDegrees = 0; // initial value

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    public CompassArrowSurfaceView(Context context, DisplayMetrics displayMetrics) {
        super(context);

        mParentWidth = displayMetrics.widthPixels;
        mParentHeight = displayMetrics.heightPixels;

        mParentCenterX = mParentWidth / 2;
        mParentCenterY = mParentHeight / 2;

        mViewLeftX = mParentCenterX - mBitmapWidth / 2;
        mViewTopY = mParentCenterY - mBitmapWidth / 2;

        mRotationInDegrees = 0; // initial value

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawDirection(canvas);
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        mParentWidth = w;
//        mParentHeight = h;
//
//        mParentCenterX = mParentWidth / 2;
//        mParentCenterY = mParentHeight / 2;
//
//        mViewLeftX = mParentCenterX - mBitmapWidth / 2;
//        mViewTopY = mParentCenterY - mBitmapWidth / 2;
//    }

    private void drawDirection(Canvas canvas) {
        // Save the canvas, save the current matrix and clip onto a private stack
        canvas.save();

        // Rotate this View
        canvas.rotate((float) -mRotationInDegrees, mParentCenterX,
                mParentCenterY);

        // Redraw this View, (Bitmap bitmap, float left, float top, Paint paint)
        canvas.drawBitmap(mBitmap, mViewLeftX, mViewTopY, null);

        // Restore the canvas
        canvas.restore();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawingThread = new Thread(new Runnable() {
            public void run() {
                Canvas canvas = null;
                while (!Thread.currentThread().isInterrupted()) {
                    canvas = mSurfaceHolder.lockCanvas();
                    if (null != canvas) {
                        CompassArrowSurfaceView.this.onDraw(canvas);
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        });
        mDrawingThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != mDrawingThread)
            mDrawingThread.interrupt();
    }

    public void setDisplayDimension(DisplayMetrics displayMetrics) {

        mParentWidth = displayMetrics.widthPixels;
        mParentHeight = displayMetrics.heightPixels;
        // MOTO G: landscape w1132*h510, portrait w656*h958 ?? not symmetric

        mParentCenterX = mParentWidth / 2;
        mParentCenterY = mParentHeight / 2;

        mViewLeftX = mParentCenterX - mBitmapWidth / 2;
        mViewTopY = mParentCenterY - mBitmapWidth / 2;
    }

    public void setDirectionInDegrees(float directionInDegrees) {
        this.mRotationInDegrees = directionInDegrees;
    }
}
