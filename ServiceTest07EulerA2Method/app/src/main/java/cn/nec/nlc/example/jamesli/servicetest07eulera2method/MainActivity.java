package cn.nec.nlc.example.jamesli.servicetest07eulera2method;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    // result shown in textView's
    private TextView textViewGyroYClassResult;
    private TextView textViewGyroPClassResult;
    private TextView textViewGyroRClassResult;
    private TextView textViewGyroYEulerResult;
    private TextView textViewGyroPEulerResult;
    private TextView textViewGyroREulerResult;
    private TextView textViewGyroYMarkResult;
    private TextView textViewGyroPMarkResult;
    private TextView textViewGyroRMarkResult;

    // button control
    private Button buttonStartTracking;
    private Button buttonStopTracking;

    // service related
    private GyroListenerService mGyroListenerService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mGyroListenerService = ((GyroListenerService.LocalBinder) iBinder).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // when Service has been lost e.g. shutdown by Android system
            mGyroListenerService = null;
        }
    };

    // defene Broadcast Receiver to receive result to update UI
    private BroadcastReceiver mBroadcastReceiverUiUpdates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                absEulerAngleClass = bundle.getFloatArray(GyroListenerService.GYRO_CLASS_RESULT);
                absEulerAngleEuler = bundle.getFloatArray(GyroListenerService.GYRO_EULER_RESULT);
                // display them in the text view
                if (firstDisplayFeedbackFlag) {
                    // show absEulerAngleClass result in the mark textView's
                    displayEulerAnglesInMarkTextview(absEulerAngleClass);
                    firstDisplayFeedbackFlag = false;   // no longer update mark textview anymore
                }
                displayEulerAnglesInClassTextview(absEulerAngleClass);
                displayEulerAnglesInEulerTextview(absEulerAngleEuler);
            }

        }
    };

    // displayed data
    private float[] absEulerAngleClass = new float[3];
    private float[] absEulerAngleEuler = new float[3];
    private boolean firstDisplayFeedbackFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        associateTextViewAndButton();

        buttonStartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStartTracking.setEnabled(false);
                buttonStopTracking.setEnabled(true);
                firstDisplayFeedbackFlag = true;
                // start the GyroListenerService
                Intent serviceIntent = new Intent(MainActivity.this, GyroListenerService.class);
                MainActivity.this.bindService(serviceIntent, mServiceConnection,
                        Context.BIND_AUTO_CREATE);
                // register local broadcast receiver
                LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver
                        (mBroadcastReceiverUiUpdates,
                        new IntentFilter(GyroListenerService.NOTIFICATION));
            }
        });
        buttonStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStartTracking.setEnabled(true);
                buttonStopTracking.setEnabled(false);
                setToDefaultTextview();
                // unbind service and unregister broadcast receiver
                if (mGyroListenerService != null) {
                    MainActivity.this.unbindService(mServiceConnection);
                }
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .unregisterReceiver(mBroadcastReceiverUiUpdates);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // when resumed from other activity or just started
        setToDefaultTextview();
        buttonStartTracking.setEnabled(true);
        buttonStopTracking.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister monitoring service and broadcast receiver
        if (mGyroListenerService != null) {
            unbindService(mServiceConnection);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiverUiUpdates);
    }

    private void associateTextViewAndButton() {
        textViewGyroYClassResult = (TextView) findViewById(R.id.textViewClassYResult);
        textViewGyroPClassResult = (TextView) findViewById(R.id.textViewClassPResult);
        textViewGyroRClassResult = (TextView) findViewById(R.id.textViewClassRResult);

        textViewGyroYEulerResult = (TextView) findViewById(R.id.textViewEulerYResult);
        textViewGyroPEulerResult = (TextView) findViewById(R.id.textViewEulerPResult);
        textViewGyroREulerResult = (TextView) findViewById(R.id.textViewEulerRResult);

        textViewGyroYMarkResult = (TextView) findViewById(R.id.textViewMarkYResult);
        textViewGyroPMarkResult = (TextView) findViewById(R.id.textViewMarkPResult);
        textViewGyroRMarkResult = (TextView) findViewById(R.id.textViewMarkRResult);

        buttonStartTracking = (Button) findViewById(R.id.buttonStart);
        buttonStopTracking = (Button) findViewById(R.id.buttonStop);
    }

    private void setToDefaultTextview() {
        textViewGyroYClassResult.setText(R.string.result_class_y_default);
        textViewGyroPClassResult.setText(R.string.result_class_p_default);
        textViewGyroRClassResult.setText(R.string.result_class_r_default);
        textViewGyroYEulerResult.setText(R.string.result_euler_y_default);
        textViewGyroPEulerResult.setText(R.string.result_euler_p_default);
        textViewGyroREulerResult.setText(R.string.result_euler_r_default);
        textViewGyroYMarkResult.setText(R.string.result_mark_y_default);
        textViewGyroPMarkResult.setText(R.string.result_mark_p_default);
        textViewGyroRMarkResult.setText(R.string.result_mark_r_default);
    }

    private void displayEulerAnglesInClassTextview(float[] absEulerAngles) {
        textViewGyroYClassResult.setText(String.valueOf(Math.round(Math.toDegrees(absEulerAngles[0])*100)/100.0f));
        textViewGyroPClassResult.setText(String.valueOf(Math.round(Math.toDegrees(absEulerAngles[1])*100)/100.0f));
        textViewGyroRClassResult.setText(String.valueOf(Math.round(Math.toDegrees(absEulerAngles[2])*100)/100.0f));
    }

    private void displayEulerAnglesInEulerTextview(float[] absEulerAngles) {
        textViewGyroYEulerResult.setText(String.valueOf(Math.round(Math.toDegrees(absEulerAngles[0])*100)/100.0f));
        textViewGyroPEulerResult.setText(String.valueOf(Math.round(Math.toDegrees(absEulerAngles[1])*100)/100.0f));
        textViewGyroREulerResult.setText(String.valueOf(Math.round(Math.toDegrees(absEulerAngles[2])*100)/100.0f));
    }

    private void displayEulerAnglesInMarkTextview(float[] absEulerAngles) {
        textViewGyroYMarkResult.setText(String.valueOf(Math.round(Math.toDegrees(absEulerAngles[0])*100)/100.0f));
        textViewGyroPMarkResult.setText(String.valueOf(Math.round(Math.toDegrees(absEulerAngles[1])*100)/100.0f));
        textViewGyroRMarkResult.setText(String.valueOf(Math.round(Math.toDegrees(absEulerAngles[2])*100)/100.0f));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
