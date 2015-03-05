package cn.nec.nlc.example.jamesli.servicetest06sensorsamplerate;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private RadioGroup radioGroupSensorType;
    private RadioGroup radioGroupSampleRate;
    private Button buttonSensorOn;
    private Button buttonSensorOff;
    private TextView textViewStatus;
    private TextView textViewSampleRate;    // updated after receiving the broadcast from service
    private int sensorType;
    private int sensorSampleRateLevel;

    // service related
    private SensorListenerService mSensorListenerService;
    private Intent mServiceIntent;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        // Called when a connection to the Service has been established
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("ServiceConnection", "onServiceConnected()");
            // with the IBinder of the communication channel to the Service
            mSensorListenerService = ((SensorListenerService.LocalBinder) iBinder).getService();
        }
        // Called when a connection to the Service has been lost
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("ServiceConnection", "onServiceDisconnected()");
            mSensorListenerService = null;
        }
    };

    // BR to retrieve sample rate updates
    private BroadcastReceiver receiverSampleRateUpdates = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("BroadcastReceiver", "onReceive()");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                float sampleRate = bundle.getFloat(SensorListenerService.SAMPLE_RATE);
                textViewSampleRate.setText("Sampling Rate: " + Math.round(sampleRate*100)/100.0f);
                    // take two digits
                Toast.makeText(MainActivity.this, "Updates received!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroupSensorType = (RadioGroup) findViewById(R.id.radioGroupSensorType);
        radioGroupSampleRate = (RadioGroup) findViewById(R.id.radioGroupSampleRate);
        buttonSensorOn = (Button) findViewById(R.id.buttonSensorOn);
        buttonSensorOff = (Button) findViewById(R.id.buttonSensorOff);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        textViewSampleRate = (TextView) findViewById(R.id.textViewSamRate);

        buttonSensorOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check whether sensor and its rate have been validate selected
                if (isSensorTypeAndRateSelected()) {
                    buttonSensorOn.setEnabled(false);
                    buttonSensorOff.setEnabled(true);
                    // disable radio group selection, failed
                    // there is no property given for radio group for disable it
                    // RadioGroup has no option to set it form XML, e.g. setEnable & setClickable
                    enableRadioButtons(false);
                    // provide info in status textView
                    displaySensorTypeSampleRateLevelInTextViewStatus();
                    // furthermore, start sensor listening service, and register broadcast listener
                    // set explicit intent
                    mServiceIntent = new Intent(MainActivity.this, SensorListenerService.class);
                    mServiceIntent.putExtra(SensorListenerService.SENSOR_TYPE, sensorType);
                    mServiceIntent.putExtra(SensorListenerService.SENSOR_SAMPLE_RATE_LEVEL,
                            sensorSampleRateLevel);
                    MainActivity.this.bindService(mServiceIntent, mServiceConnection,
                            Context.BIND_AUTO_CREATE);
                      // automatically create the service as long as the binding exists
                    // register LocalBroadcastReceiver
                    LocalBroadcastManager.getInstance(MainActivity.this)
                            .registerReceiver(receiverSampleRateUpdates,
                            new IntentFilter(SensorListenerService.NOTIFICATION));
                }
            }
        });
        buttonSensorOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSensorOn.setEnabled(true);
                buttonSensorOff.setEnabled(false);
                // enable radio group selection
                enableRadioButtons(true);
                // provide info in status textView
                textViewStatus.setText("Status: Sensor off");
                // reset invalid value for sensorType and sensorSampleRateLevel
                sensorType = -1;
                sensorSampleRateLevel = -1;
                // unbind service
                if (mSensorListenerService != null) {
                    MainActivity.this.unbindService(mServiceConnection);
                }
                // unregister broadcast receiver through LocalBroadcastReceiver
                LocalBroadcastManager.getInstance(MainActivity.this)
                        .unregisterReceiver(receiverSampleRateUpdates);
                textViewSampleRate.setText("Sampling Rate: n.a.");  // set text back to default
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonSensorOn.setEnabled(true);
        buttonSensorOff.setEnabled(false);
        // enable radio group selection
        enableRadioButtons(true);
        // provide info in status textView
        textViewStatus.setText("Status: Sensor off");
        // reset invalid value for sensorType and sensorSampleRateLevel
        sensorType = -1;
        sensorSampleRateLevel = -1;
        textViewSampleRate.setText("Sampling Rate: n.a.");  // set text back to default
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorListenerService != null) {
            MainActivity.this.unbindService(mServiceConnection);
        }
        // unregister broadcast receiver through LocalBroadcastReceiver
        LocalBroadcastManager.getInstance(MainActivity.this)
                .unregisterReceiver(receiverSampleRateUpdates);
    }

    private boolean isSensorTypeAndRateSelected() {
        switch (radioGroupSensorType.getCheckedRadioButtonId()) {
            case R.id.radioButtonAcc:
                sensorType = Sensor.TYPE_ACCELEROMETER; // value ~ 1
                break;
            case R.id.radioButtonGyro:
                sensorType = Sensor.TYPE_GYROSCOPE; // value ~ 4
                break;
            case R.id.radioButtonMag:
                sensorType = Sensor.TYPE_MAGNETIC_FIELD;    // value ~ 2
                break;
            default:
                Toast.makeText(this, "Please select a valid sensor type first!", Toast.LENGTH_LONG)
                        .show();
                return false;
        }
        switch (radioGroupSampleRate.getCheckedRadioButtonId()) {
            case R.id.radioButtonRateNormal:
                sensorSampleRateLevel = SensorManager.SENSOR_DELAY_NORMAL;   // value ~ 3
                break;
            case R.id.radioButtonRateUi:
                sensorSampleRateLevel = SensorManager.SENSOR_DELAY_UI;       // value ~ 2
                break;
            case R.id.radioButtonRateGame:
                sensorSampleRateLevel = SensorManager.SENSOR_DELAY_GAME;     // value ~ 1
                break;
            case R.id.radioButtonRateFastest:
                sensorSampleRateLevel = SensorManager.SENSOR_DELAY_FASTEST;  // value ~ 0
                break;
            default:
                Toast.makeText(this, "Then select a valid sensor sample rate!", Toast.LENGTH_LONG)
                        .show();
                return false;
        }
        return true;
    }

    private void enableRadioButtons(boolean enableFlag) {
        ((RadioButton) findViewById(R.id.radioButtonAcc)).setEnabled(enableFlag);
        ((RadioButton) findViewById(R.id.radioButtonGyro)).setEnabled(enableFlag);
        ((RadioButton) findViewById(R.id.radioButtonMag)).setEnabled(enableFlag);

        ((RadioButton) findViewById(R.id.radioButtonRateNormal)).setEnabled(enableFlag);
        ((RadioButton) findViewById(R.id.radioButtonRateUi)).setEnabled(enableFlag);
        ((RadioButton) findViewById(R.id.radioButtonRateGame)).setEnabled(enableFlag);
        ((RadioButton) findViewById(R.id.radioButtonRateFastest)).setEnabled(enableFlag);
    }

    private void displaySensorTypeSampleRateLevelInTextViewStatus() {
        String sensorTypeText;
        String sensorSampleRateLevelText;
        switch (sensorType) {
            case 1:
                sensorTypeText = "Acc";
                break;
            case 4:
                sensorTypeText = "Gyro";
                break;
            case 2:
                sensorTypeText = "Mag";
                break;
            default:
                throw new IllegalArgumentException("Current sensor type has not been declared!");
        }
        switch (sensorSampleRateLevel) {
            case 3:
                sensorSampleRateLevelText = "Normal";
                break;
            case 2:
                sensorSampleRateLevelText = "UI";
                break;
            case 1:
                sensorSampleRateLevelText = "Game";
                break;
            case 0:
                sensorSampleRateLevelText = "Fastest";
                break;
            default:
                throw new IllegalArgumentException("Current sensor sample rate level has not " +
                        "been declared!");
        }
        textViewStatus.setText("Status: Sensor " + sensorTypeText + " with sample rate "
                + sensorSampleRateLevelText);
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
