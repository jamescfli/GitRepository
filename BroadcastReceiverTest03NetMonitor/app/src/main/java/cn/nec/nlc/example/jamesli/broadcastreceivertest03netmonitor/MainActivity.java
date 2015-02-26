package cn.nec.nlc.example.jamesli.broadcastreceivertest03netmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MainActivity extends ActionBarActivity {
    private TextView textViewWifiState;
    private TextView textViewCellState;
    private Button buttonWifiOn;
    private Button buttonWifiOff;
    private Button buttonCellOn;
    private Button buttonCellOff;
    private BroadcastReceiver mCell3gOrLteChecker;
    private BroadcastReceiver mHomeWifiChecker;
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private Method mDataMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWifiState = (TextView) findViewById(R.id.textViewWifiState);
        textViewCellState = (TextView) findViewById(R.id.textViewCellState);

        buttonWifiOn = (Button) findViewById(R.id.buttonWifiOn);
        buttonWifiOff = (Button) findViewById(R.id.buttonWifiOff);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        buttonWifiOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWifiManager.setWifiEnabled(true);
                buttonWifiOn.setEnabled(false);
                buttonWifiOff.setEnabled(true);
            }
        });
        buttonWifiOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWifiManager.setWifiEnabled(false);
                buttonWifiOn.setEnabled(true);
                buttonWifiOff.setEnabled(false);
            }
        });

        buttonCellOn = (Button) findViewById(R.id.buttonCellOn);
        buttonCellOff = (Button) findViewById(R.id.buttonCellOff);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            mDataMethod = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled",
                    boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        mDataMethod.setAccessible(true);
        buttonCellOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity.this.setMobileDataEnabled(MainActivity.this, true);
                try {
                    MainActivity.this.mDataMethod.invoke(mConnectivityManager, true);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                buttonCellOn.setEnabled(false);
                buttonCellOff.setEnabled(true);
            }
        });
        final Method finalMDataMethod = mDataMethod;
        buttonCellOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity.this.setMobileDataEnabled(MainActivity.this, false);
                try {
                    MainActivity.this.mDataMethod.invoke(mConnectivityManager, false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                buttonCellOn.setEnabled(true);
                buttonCellOff.setEnabled(false);
            }
        });

        // define two Broadcast Receivers: one for Wifi and the other for mobile network
        // check whether linked to wifi network
        mHomeWifiChecker = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    NetworkInfo networkInfo = intent.getParcelableExtra
                            (WifiManager.EXTRA_NETWORK_INFO);
                    if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                        MainActivity.this.textViewWifiState.setText("Wifi State: Disconnected");
                    } else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        WifiManager wifiManager = (WifiManager) context.getSystemService
                                (Context.WIFI_SERVICE);
                        // the following requires ACCESS_WIFI_STATE uses-permission
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        MainActivity.this.textViewWifiState.setText("Wifi State: Connected to "
                                + wifiInfo.getSSID());
                    }
                } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_DISABLED);
                    if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                        MainActivity.this.textViewWifiState.setText("Wifi State: turn down " +
                                "by System");
                    } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                        MainActivity.this.textViewWifiState.setText("Wifi State: turn on " +
                                "by System");
                    }
                }
            }
        };

        mCell3gOrLteChecker = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    boolean noConnectivity = intent.getBooleanExtra
                            (ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                    if (noConnectivity) {
                        MainActivity.this.textViewCellState.setText("Cell State: no connectivity");
                    } else {
                        int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE,
                                ConnectivityManager.TYPE_DUMMY);
                        if (networkType == ConnectivityManager.TYPE_MOBILE) {
                            // further provide info on cell network type
                            checkFor3gOrLte(context);
                        }
                    }
                }
            }

            private void checkFor3gOrLte(Context context) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService
                        (Context.TELEPHONY_SERVICE);
                switch (telephonyManager.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        MainActivity.this.textViewCellState.setText("Cell State: HSDPA");
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        MainActivity.this.textViewCellState.setText("Cell State: HSPA");
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        MainActivity.this.textViewCellState.setText("Cell State: HSPAP");
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        MainActivity.this.textViewCellState.setText("Cell State: HSUPA");
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        MainActivity.this.textViewCellState.setText("Cell State: LTE");
                        break;
                    default:
                        MainActivity.this.textViewCellState.setText("Cell State: low speed net");
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilterWifi = new IntentFilter();
        intentFilterWifi.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilterWifi.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mHomeWifiChecker, intentFilterWifi);
        IntentFilter intentFilterCell = new IntentFilter();
        intentFilterCell.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mCell3gOrLteChecker, intentFilterCell);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mHomeWifiChecker);
        unregisterReceiver(mCell3gOrLteChecker);
    }

    private void setMobileDataEnabled(Context context, boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
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
