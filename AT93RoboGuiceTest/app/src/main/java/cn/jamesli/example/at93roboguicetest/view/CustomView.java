package cn.jamesli.example.at93roboguicetest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.jamesli.example.at93roboguicetest.R;
import roboguice.inject.InjectView;

/**
 * Created by jamesli on 15-7-24.
 */
public class CustomView extends LinearLayout {
    @InjectView(R.id.close_tv)
    private Button buttonCloseTv;
    @InjectView(R.id.tv_status)
    private TextView textViewStatus;

    public CustomView(Context context) {
        super(context);
        initializeView(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    public void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_custom, this, true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        buttonCloseTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewStatus.setText("Closed");
            }
        });
        textViewStatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewStatus.setText("Open");
            }
        });
    }
}
