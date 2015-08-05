package cn.jamesli.example.at99swiperefreshwithgridview;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

public class MultiSwipeRefreshLayout extends SwipeRefreshLayout {

    private View[] mSwipeableChildren;  // view containers

    public MultiSwipeRefreshLayout(Context context) {
        super(context);
    }

    public MultiSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Set the children which can trigger a refresh by swiping down when they are visible,
    // such that the child views are swipeable, more specifically GridView and EmptyView in this project
    public void setSwipeableChildren(final int... ids) {
        assert ids != null;
        mSwipeableChildren = new View[ids.length];
        for (int i = 0, SIZE = ids.length; i < SIZE; i++) {
            mSwipeableChildren[i] = findViewById(ids[i]);
        }
    }

    @Override
    public boolean canChildScrollUp() {
        if (mSwipeableChildren != null && mSwipeableChildren.length > 0) {
            // Check if any of the children can not scroll up
            for (View view : mSwipeableChildren) {
                if (view != null && view.isShown() && !canViewScrollUp(view)) { // reached the top
                    return false;   // to start the gesture
                }
            }
        }
        return true;
    }

    // Check whether a {@link View} can scroll up from its current position
    private static boolean canViewScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            // For ICS - Android Ice Cream Sandwich - and above, we can call
            return ViewCompat.canScrollVertically(view, -1);    // -1 up, +1 down
        } else {
            // TODO figure out the logic
            if (view instanceof AbsListView) {  // including both Grid and ListView
                // Manually check the first visible item and the child view's top value
                final AbsListView listView = (AbsListView) view;
                return listView.getChildCount() > 0 &&
                        (listView.getFirstVisiblePosition() > 0 ||  // Returns the position for the first item displayed on screen
                            listView.getChildAt(0).getTop() < listView.getPaddingTop());
            } else {
                // For all others
                return view.getScrollY() > 0;   // top position of the view > 0 means
            }
        }
    }

}
