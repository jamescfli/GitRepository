package cn.jamesli.example.bt17eventdispatchforui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jamesli on 15/11/13.
 */
public class CustomView extends View {
    private static final String TAG = "View";

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "CustomView.onTouchEvent: " + event.toString());
        return super.onTouchEvent(event);
//        return true;    // Case 3, consumed by View
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(TAG, "CustomeView.dispatchTouchEvent: " + event.toString());
        return super.dispatchTouchEvent(event);
    }
}
