package cn.nec.nlc.example.jamesli.servicetest05intentservice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    private TextView textViewStatus;
    private Button buttonServiceOn;
    private Button buttonServiceOff;
    private DirectionView mDirectionView;
    private float markedDirectionAngle; // take 0 as example
    private int counterUiUpdates;

    // we use sendBroadcast for communication for the time being, mGyroListenerService will be
    // tested later, i.e. access through ServiceConnection
    private GyroListenerService mGyroListenerService;
    private boolean isServiceBound;

    // for service communication
    private Intent mServiceIntent;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            GyroListenerService.MyBinder myBinder = (GyroListenerService.MyBinder) iBinder;
            mGyroListenerService = myBinder.getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mGyroListenerService = null;
        }
    };
    // define BroadcastReceiver for receiving UI updates from Service
    private BroadcastReceiver receiverUiUpdates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // TO-DO: retrieve current direction on x-y plane (object-space)
                int resultCode = bundle.getInt(GyroListenerService.RESULT);
                if (resultCode == RESULT_OK) {
                    float thetaForDrawing = bundle.getFloat(GyroListenerService.CURRENT_DIRECTION);
                    mDirectionView.myDraw(thetaForDrawing -
                            (float) Math.toRadians(markedDirectionAngle));
                    textViewStatus.setText("UI has been updated by " + (counterUiUpdates++) +
                            " times");
                } else {    // RESULT_CANCELLED
                    textViewStatus.setText("Failed to get Gyro updates from Service");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        buttonServiceOn = (Button) findViewById(R.id.buttonServiceOn);
        buttonServiceOff = (Button) findViewById(R.id.buttonServiceOff);
        mDirectionView = (DirectionView) findViewById(R.id.directionView);

        isServiceBound = false;     // service bind flag = false

        buttonServiceOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mServiceIntent = new Intent(MainActivity.this, GyroListenerService.class);
//                MainActivity.this.startService(mServiceIntent);
                MainActivity.this.bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
                    // BIND_AUTO_CREATE: create service as long as binding exists
                isServiceBound = true;
                Toast.makeText(MainActivity.this, "Gyro Listener Service Connected!",
                        Toast.LENGTH_LONG).show();
            }
        });
        buttonServiceOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServiceBound) {
                    MainActivity.this.unbindService(mConnection);
                    isServiceBound = false;
                }
                Toast.makeText(MainActivity.this, "Gyro Listener Service Disconnected!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        counterUiUpdates = 0;
        markedDirectionAngle = 0;   // in degrees rather than radians
        registerReceiver(receiverUiUpdates, new IntentFilter(GyroListenerService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverUiUpdates);
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
