package cn.jamesli.example.at93roboguicetest.view;

import android.content.Context;
import android.location.LocationManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

import cn.jamesli.example.at93roboguicetest.R;
import roboguice.RoboGuice;
import roboguice.inject.InjectView;

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
//        // Method 1: Resource R.layout.view_custom + ViewGroup this (CustomView) + attachToRoot true
//        LayoutInflater.from(context).inflate(R.layout.view_custom, this, true);
//        // Method 2: equivalent
//        LayoutInflater inflater = LayoutInflater.from(context);
//        inflater.inflate(R.layout.view_custom, this, true);
        // Equivalent to getSystemService(), by tracing LayoutInflater
        // public static LayoutInflater from(Context context) {
        //      LayoutInflater layoutInflater =
        //          (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // or LayoutInflater layoutInflater = LayoutInflater.from(context)

        // Solution to App Crash:
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_custom, this, true);
        // inject all memebers, e.g. LocationManager loc in the field of the CustomView
        // injectMembers is also required by getInjector()
        RoboGuice.injectMembers(getContext(), this);
        // as described, inject all view members by this command
        RoboGuice.getInjector(getContext()).injectViewMembers(this);
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // ref: https://github.com/stephanenicolas/injectview/wiki/Injection-inside-Views
        // injection takes place here,
        // right after the call to super.onFinishInflate
        Toast.makeText(getContext(), "view id should be associated by now", Toast.LENGTH_LONG).show();
    }
}