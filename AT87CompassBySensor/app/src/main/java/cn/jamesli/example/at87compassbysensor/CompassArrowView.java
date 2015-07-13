package cn.jamesli.example.at87compassbysensor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jamesli on 15-7-12.
 */
public class CompassArrowView extends View {

    private static final int DESIRED_BITMAP_WIDTH = 250;

    // createScaledBitmap(): creates a new bitmap, scaled from an existing bitmap, when possible
    Bitmap mBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
            R.mipmap.arrow), DESIRED_BITMAP_WIDTH, DESIRED_BITMAP_WIDTH, true);
    int mBitmapWidth = mBitmap.getWidth();

    // Height and Width of Main View
    int mParentWidth;
    int mParentHeight;

    // Center of Main View
    int mParentCenterX;
    int mParentCenterY;

    // Top left position of this View
    int mViewTopY;
    int mViewLeftX;

    float mRotationInDegrees;

    public CompassArrowView(Context context) {
        super(context);
    }

    public CompassArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassArrowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // Compute location of compass arrow
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        mParentWidth = w;
        mParentHeight = h;
        // MOTO G: landscape w1132*h510, portrait w656*h958 ?? not symmetric

        mParentCenterX = mParentWidth / 2;
        mParentCenterY = mParentHeight / 2;

        mViewLeftX = mParentCenterX - mBitmapWidth / 2;
        mViewTopY = mParentCenterY - mBitmapWidth / 2;
    }

    // Redraw the compass arrow
    @Override
    protected void onDraw(Canvas canvas) {

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

    public void setDirectionInDegrees(float directionInDegrees) {
        this.mRotationInDegrees = directionInDegrees;
    }
}
