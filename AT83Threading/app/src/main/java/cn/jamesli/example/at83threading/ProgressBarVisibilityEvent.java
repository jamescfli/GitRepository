package cn.jamesli.example.at83threading;

import android.widget.ProgressBar;

/**
 * Created by jamesli on 15-7-7.
 */
public class ProgressBarVisibilityEvent {
    private boolean isVisible;

    public ProgressBarVisibilityEvent(int visibility) {
        if (ProgressBar.VISIBLE == visibility) {
            isVisible = true;
        } else if (ProgressBar.INVISIBLE == visibility) {
            isVisible = false;
        } else {
            throw new IllegalArgumentException("visibility in ProgressBarVisibilityEvent is " +
                    "neither VISIBLE or INVISIBLE!");
        }
    }

    public void setVisibility(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
