package cn.jamesli.example.bt17eventdispatchforui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by jamesli on 15/11/13.
 */
public class CustomViewGroup extends RelativeLayout {
    private static final String TAG = "ViewGroup";

    public CustomViewGroup(Context context) {
        super(context);
    }

    public CustomViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "CustomViewGroup.onTouchEvent: " + event.toString());
        return super.onTouchEvent(event);
//        return true;    // Case 5, consumed by ViewGroup
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(TAG, "CustomViewGroup.dispatchTouchEvent: " + ev.toString());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(TAG, "CustomViewGroup.onInterceptTouchEvent: " + ev.toString());
        return super.onInterceptTouchEvent(ev);
//        return true;    // Case 5, consumed by ViewGroup
    }
}
