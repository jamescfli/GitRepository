package cn.jamesli.example.at82eventbusdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.jamesli.example.at82eventbusdemo.utils.TextUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.SubscriberExceptionEvent;

/**
 * Created by jamesli on 15-7-2.
 */
public class SendSimplestEventActivity extends BaseActivity {
    private EditText eventET;
    private Button sendBtn;
    private TextView receivedEventTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_send_simplest_event);
        
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(String event) {
        receivedEventTV.setText(event);
    }

    public void onEvent(SubscriberExceptionEvent event) {
        Log.e("event", "catch SubscriberExceptionEvent");
    }

    private void initView() {
        eventET = (EditText)findViewById(R.id.event_content);
        receivedEventTV = (TextView)findViewById(R.id.receive_event);
        sendBtn = (Button)findViewById(R.id.send_event);
        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(TextUtils.getHintIfTextIsNull(eventET));
            }
        });
    }
}
