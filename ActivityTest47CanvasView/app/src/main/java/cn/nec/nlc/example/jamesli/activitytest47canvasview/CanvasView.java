package cn.nec.nlc.example.jamesli.activitytest47canvasview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

public class CanvasView extends View {
    float angleFromTarget;

    public CanvasView(Context context) {
        super(context);

    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        // for debug
//        Log.i("canvas getHeight",String.valueOf(canvas.getHeight()));
//        Log.i("canvas getWidth", String.valueOf(canvas.getWidth()));
        drawScale(canvas);
        drawDirectionPointer(canvas, angleFromTarget);

    }

    public void myDraw(float theta) {

        this.angleFromTarget = theta;
        invalidate();
    }

    public void drawScale(Canvas canvas) {
        float sx,sy,dx,dy;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);

        sx = (float)0.5*canvas.getWidth();  // from center of the canvas
        sy = 0;                             // from top of the canvas
        dx = (float)0.5*canvas.getWidth();  // to center of the canvas
        dy = canvas.getHeight()*((float)0.05);  // to 0.05 top of the canvas

        canvas.drawLine(sx,sy,dx,dy,paint);
    }

    public void drawDirectionPointer(Canvas canvas, float theta) {
        float x0,y0,x1,y1,len;
        x0 = canvas.getWidth()*(float)0.5;  // from center of the canvas
        y0 = canvas.getHeight()*(float)0.5; // from center of the canvas
        len = canvas.getHeight()*(float)0.3;    // radius = 0.3 height of the canvas
        x1 = x0+sin(-theta)*len;
        y1 = y0-cos(-theta)*len;

        drawArrow(canvas,x0,y0,x1,y1);
    }


    private void drawArrow(Canvas canvas,float x0,float y0,float x1,float y1){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);

        canvas.drawLine(x0,y0,x1,y1,paint);
        float a,b,L0,L,d,x,y;

        L0 = sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
        a = (x0-x1)/L0;
        b = (y0-y1)/L0;
        L = 20;
        d = (float)Math.PI/6;//30 degree
        for(int n=0;n<2;n++) {
            x = x1+L*(a*cos(d)-b*sin(n==0?d:-d));
            y = y1+L*(a*sin(n==0?d:-d)+b*cos(d));
            canvas.drawLine(x1,y1,x,y,paint);
        }
    }

}
