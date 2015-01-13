package cn.nec.nlc.example.activitytest9;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
   x<0         x>0
                ^
                |
    +-----------+-->  y>0
    |           |
    |           |
    |   screen  |
    |           |   / z<0
    |           |  /
    |           | /
Origin----------+/
    |[]  [ ]  []/
    +----------/+     y<0
              /
             /
           |/ z>0 (toward the sky)

    O: Origin (x=0,y=0,z=0)
 */

public class MyCompassView extends View {
	private Paint paint;
	private float position = 0;
	
	public MyCompassView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		paint = new Paint();
		// smooths out the edges of what is being drawn + has no impact on the 
		// interior of the shape
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2); // 0 to stroke in hairline mode (one pixel width)
		paint.setTextSize(25); 	// value must be > 0
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// The width of this view as measured in the most recent call to 
		// measure(). This should be used during measurement and layout 
		// calculations only.
		// getMeasuredHeight() is the whole height including the part outside
		// the screen / layout. getHeight() is the height in the layout only.
		// it is possible that getMeasuredHeight() < getHeight() when all 
		// content of the view is located within the screen / layout.
		int xPoint = getMeasuredWidth() / 2;
		int yPoint = getMeasuredHeight() / 2;
		
		float radius = (float) (Math.max(xPoint, yPoint) * 0.6);
		// center circle
		canvas.drawCircle(xPoint, yPoint, radius, paint);
		canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
		
		// 3.143 is an approximation for the circle
		// ?? -position in degrees
//		canvas.drawLine(xPoint, yPoint, 
//				(float) (xPoint + radius
//						* Math.sin((double) (-position) / 180 * 3.143)),
//				(float) (yPoint - radius
//						* Math.cos((double) (-position) / 180 * 3.143)), 
//				paint);
		canvas.drawLine(xPoint, yPoint, 
				(float) (xPoint + radius
						* Math.cos((double) (position) / 180 * 3.143)),
				(float) (yPoint - radius
						* Math.sin((double) (position) / 180 * 3.143)), 
				paint);
		// .. line from center to 
		canvas.drawText(String.valueOf(position), xPoint, yPoint, paint);
	}
	
	public void updateData(float position) {
		this.position = position;
		invalidate();
		// .. If view is visible, onDraw(Canvas) will be called at some point
		// UI Thread	: invalidate()
		// non-UI Thread: postInvalidate()
	}
}
