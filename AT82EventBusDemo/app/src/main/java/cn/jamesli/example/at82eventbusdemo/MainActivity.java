package cn.jamesli.example.at82eventbusdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends BaseActivity {

    private Button sendSimplestEventBtn;
//    private Button sendEventSelfDefinedBtn;
//    private Button diffThreadModeBtn;
//    private Button sendOrderedEventBtn;
//    private Button sendStickyEventBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_main);

        initView();
    }

    private void initView() {
        sendSimplestEventBtn = (Button)findViewById(R.id.send_simplest_event);
        sendSimplestEventBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SendSimplestEventActivity.class));
            }
        });

//        sendEventSelfDefinedBtn = (Button)findViewById(R.id.send_event_self_defined);
//        sendEventSelfDefinedBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context, SendSelfDefinedEventActivity.class));
//            }
//        });
//
//        diffThreadModeBtn = (Button)findViewById(R.id.diff_thread_mode);
//        diffThreadModeBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context, DiffThreadModeActivity.class));
//            }
//        });
//
//        sendOrderedEventBtn = (Button)findViewById(R.id.send_ordered_event);
//        sendOrderedEventBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context, ViewPagerDemo.class));
//            }
//        });
//
//        sendStickyEventBtn = (Button)findViewById(R.id.send_sticky_event);
//        sendStickyEventBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(context, SendStickyEventActivity.class));
//            }
//        });
    }

}
