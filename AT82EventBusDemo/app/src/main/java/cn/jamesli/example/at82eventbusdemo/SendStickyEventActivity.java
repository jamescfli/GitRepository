package cn.jamesli.example.at82eventbusdemo;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.jamesli.example.at82eventbusdemo.utils.TextUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by jamesli on 15-7-2.
 */
public class SendStickyEventActivity extends BaseActivity {
    private EditText eventET;
    private Button sendBtn;
    private TextView receivedEventTV;

    private static final int WAIT_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_send_stkcky_event);

        initView();
    }

    private void initView() {
        eventET = (EditText)findViewById(R.id.event_content);
        receivedEventTV = (TextView)findViewById(R.id.receive_event);
        sendBtn = (Button)findViewById(R.id.send_event);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receivedEventTV.setText("");    // clear all previous display text
                // post first and receive after registration
                EventBus.getDefault().postSticky(TextUtils.getHintIfTextIsNull(eventET));
                sendBtn.setClickable(false);
                receivedEventTV.setText("Send sticky event success, wait for register after "
                        + WAIT_TIME / 1000 + "s\r\n\r\n");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // register
                        receivedEventTV.setText(receivedEventTV.getText() + "Register sticky success\r\n\r\n");
                        EventBus.getDefault().registerSticky(this);
                    }

                    // receive event
                    public void onEvent(String event) {
                        receivedEventTV.setText(receivedEventTV.getText() + "OnEvent success: " + event + "\r\n\r\n");
                        sendBtn.setClickable(true);
                        // unregister
                        EventBus.getDefault().unregister(this);
                        receivedEventTV.setText(receivedEventTV.getText() + "Unregister success");
                    }
                }, WAIT_TIME);
            }
        });
    }
}
