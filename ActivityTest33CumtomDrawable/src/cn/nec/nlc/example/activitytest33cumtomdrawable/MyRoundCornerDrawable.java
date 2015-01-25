package cn.nec.nlc.example.activitytest33cumtomdrawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class MyRoundCornerDrawable extends Drawable {
	private Paint paint;

	public MyRoundCornerDrawable(Bitmap bitmap) {
	    BitmapShader shader;	// draw a bitmap as a texture
	    shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
	    		Shader.TileMode.CLAMP);	// tileX, tileY
	    paint = new Paint();
	    paint.setAntiAlias(true);	// round edge
	    paint.setShader(shader);	// set shader object
	}

	@Override
	public void draw(Canvas canvas) {
	    int height = getBounds().height();	// get Drawable bounds
	    int width = getBounds().width();
	    RectF rect = new RectF(0.0f, 0.0f, width, height);
	    canvas.drawRoundRect(rect, 30, 30, paint);
	}

	@Override
	public void setAlpha(int alpha) {	// 0 ~ full transparent, 255 ~ full opaque
	    paint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	    paint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
	    return PixelFormat.TRANSLUCENT;
	}

} 
