package cn.jamesli.example.at83threading;

import android.widget.ProgressBar;

/**
 * Created by jamesli on 15-7-7.
 */
public class ProgressBarUpdateEvent {
    private int progress;

    public ProgressBarUpdateEvent(int progress) {
        this.progress = progress;
    }

    public void setProgress(int progress) {
        if ((progress > 100) || (progress < 0))
            throw new IllegalArgumentException("progress is not within [0, 100]!");
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
