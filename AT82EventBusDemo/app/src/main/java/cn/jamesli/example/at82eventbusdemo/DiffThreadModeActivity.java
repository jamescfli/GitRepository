package cn.jamesli.example.at82eventbusdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.jamesli.example.at82eventbusdemo.utils.TextUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by jamesli on 15-7-2.
 */
public class DiffThreadModeActivity extends BaseActivity {
    private static final String TAG = "DiffThreadModeActivity";
    
    private EditText eventET;
    private Button sendBtn;
    private TextView receivedEventTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_send_simplest_event);

        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    
    public void onEvent(String event) {
        Log.d(TAG, "onEvent");
        appendText("onEvent receive msg: " + event);
    }

    public void onEventBackgroundThread(String event) {
        Log.d(TAG, "onEventBackgroundThread");
        appendText("onEventBackgroundThread receive msg: " + event);
    }

    public void onEventAsync(String event) {
        Log.d(TAG, "onEventAsync");
        appendText("onEventAsync receive msg: " + event);
    }

    public void onEventMainThread(String event) {
        Log.d(TAG, "onEventMainThread");
        appendText("onEventMainThread receive msg: " + event);
    }

    private void appendText(String newText) {
        String threadInfo = ", current thread info: id is " + Thread.currentThread().getId()
                + ", content is " + Thread.currentThread().toString() + "}";
        receivedEventTV.setText(new StringBuilder(receivedEventTV.getText()).append("\r\n\r\n")
                .append(newText).append(threadInfo));
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
