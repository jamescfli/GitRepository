package cn.jamesli.example.bt06ibeacontx;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;

import cn.jamesli.example.bt06ibeacontx.util.BluetoothUtils;

@TargetApi(21)      // Suppress Warning Lint
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private BeaconTransmitter mBeaconTransmitter;
    private BluetoothUtils mBluetoothUtils;
    private Beacon mBeacon;
    private TextView mTextViewStatus;

    private Spinner mSpinnerTxPower;
    private Spinner mSpinnerTxFrequency;
    private static final String[] TX_POWER_LEVEL = new String[] {
            "Tx Power High -56dBm",
            "Tx Power Medium -66dBm",
            "Tx Power Low -76dBm"
    };
    private static final String[] TX_FREQUENCY_LEVEL = new String[] {
            "Freq High 10Hz",
            "Freq Medium 3Hz",
            "Freq Low 1Hz"
    };

    private Button mButtonStart;
    private Button mButtonStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewStatus = (TextView) findViewById(R.id.text_view_status);
        mSpinnerTxPower = (Spinner) findViewById(R.id.spinnerTxPower);
        mSpinnerTxFrequency = (Spinner) findViewById(R.id.spinnerTxFrequency);
        mButtonStart = (Button) findViewById(R.id.button_start);
        mButtonStop = (Button) findViewById(R.id.button_stop);

        uiComponentInitiate();

        // prepare for open Bluetooth in checkPrerequisites()
        mBluetoothUtils = new BluetoothUtils(this);
        if (checkPrerequisites()) {
            // context + BeaconParser
            // format specified in the BeaconParser
            // Layout: Beacon type (2 bytes, 0x02-15), UUID 16byts 4-19, Major 2 bytes 20-21,
            // Minor 2 bytes 22-23, Measured Power 1 byte 25
            mBeaconTransmitter = new BeaconTransmitter(this, new BeaconParser()
//                    .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));     // 02-15 required by iBeacon
                    .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
            // .. bytes 0-1 of the BLE manufacturer advertisements are the two byte manufacturer code
            mBeacon = new Beacon.Builder()
                    .setBluetoothName("MotoAsBeacon")   // seems not working when being observed
                    .setId1("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6") // 16bytes UUID for AltBeacon
//                    .setId1("DF7E1C79-43E9-44FF-886F-1D1F7DA6997A") // UUID from TimedBeaconSimulator, or MacBook:~$uuidgen
                    .setId2("1")                                    // Major
                    .setId3("2")                                    // Minor
                    // IDs: the a list of the multi-part identifiers of the beacon. Together,
                    // these identifiers signify a unique beacon.
                    // The identifiers are ordered by significance for the purpose of grouping beacons
//                    .setManufacturer(0x0000) // A two byte code indicating the beacon manufacturer, some devices cannot detect beacons with a manufacturer code > 0x00ff
                    .setManufacturer(0x0118)    // for Radius Networks AltBeacon
//                    .setManufacturer(0x004C)    // Apple Inc. the real message would be (in reverse sequence) 4C, 00 before 02, 15
                    .setTxPower(-59)    // default value, 0xC5 = -59dBm received power at 1m from Tx
                    // .. i.e. Measured power is set by holding a receiver one meter from the beacon
                    .setDataFields(Arrays.asList(new Long[]{0l})) // data fields included in the beacon advertisement.
                    .build();
//            // transmit with highest frequency (10Hz) and highest Tx power (-56dBm)
//            mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
//            mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);

            mButtonStart.setEnabled(true);
            mButtonStop.setEnabled(false);
        } else {
            // requires Android 5.0
            int result = BeaconTransmitter.checkTransmissionSupported(this);
            switch (result) {
                case BeaconTransmitter.NOT_SUPPORTED_MIN_SDK:   // value 1
                    Log.i(TAG, "SDK version is less or equal to API 18.");
                    break;
                case BeaconTransmitter.NOT_SUPPORTED_BLE:       // value 2
                    Log.i(TAG, "BLE is not supported.");
                    break;
                case BeaconTransmitter.NOT_SUPPORTED_CANNOT_GET_ADVERTISER:     // value 4
                    Log.i(TAG, "Could not get Advertiser. Either not supported by chipset or manufacturer");
                    break;
                case BeaconTransmitter.NOT_SUPPORTED_CANNOT_GET_ADVERTISER_MULTIPLE_ADVERTISEMENTS: // 5
                    Log.i(TAG, "Could not get Multiple Advertiser. Either not supported by chipset or manufacturer");
                    break;
                default:
                    Log.i(TAG, "Turning on BLE or some other cases.");
            }
        }
    }

    private void uiComponentInitiate() {
        ArrayAdapter<String> adapterTxPower = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TX_POWER_LEVEL);
        ArrayAdapter<String> adapterTxFreq = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TX_FREQUENCY_LEVEL);
        mSpinnerTxPower.setAdapter(adapterTxPower);
        mSpinnerTxFrequency.setAdapter(adapterTxFreq);
//        mSpinnerTxPower.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//                        mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
//                        Toast.makeText(MainActivity.this, "High Tx Power is set", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 1:
//                        mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
//                        Toast.makeText(MainActivity.this, "Medium Tx Power is set", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 2:
//                        mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW);
//                        Toast.makeText(MainActivity.this, "Low Tx Power is set", Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        Toast.makeText(MainActivity.this, "Wrong Selection!", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // n.a.
//            }
//        });
//        mSpinnerTxFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 0:
//                        mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
//                        Toast.makeText(MainActivity.this, "10Hz Beacon Tx", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 1:
//                        mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
//                        Toast.makeText(MainActivity.this, "3Hz Beacon Tx", Toast.LENGTH_SHORT).show();
//                        break;
//                    case 2:
//                        mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
//                        Toast.makeText(MainActivity.this, "1Hz Beacon Tx", Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        Toast.makeText(MainActivity.this, "Wrong Selection!", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // n.a.
//            }
//        });
        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBeaconTxPowerAndFrequency();
                startBeaconTransmitting();
                mButtonStart.setEnabled(false);
                mButtonStop.setEnabled(true);
                mSpinnerTxPower.setEnabled(false);
                mSpinnerTxFrequency.setEnabled(false);
            }
        });
        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBeaconTransmitting();
                mButtonStart.setEnabled(true);
                mButtonStop.setEnabled(false);
                mSpinnerTxPower.setEnabled(true);
                mSpinnerTxFrequency.setEnabled(true);
            }
        });
    }

    private void initiateBeaconTxPowerAndFrequency() {
        switch (mSpinnerTxPower.getSelectedItemPosition()) {
            case 0:
                mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
//                Toast.makeText(MainActivity.this, "High Tx Power is Set", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                // Note: medium is the default value
                mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
//                Toast.makeText(MainActivity.this, "Medium Tx Power is Set", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW);
//                Toast.makeText(MainActivity.this, "Low Tx Power is Set", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(MainActivity.this, "Wrong Selection!", Toast.LENGTH_SHORT).show();
        }
        switch (mSpinnerTxFrequency.getSelectedItemPosition()) {
            case 0:
                mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
//                Toast.makeText(MainActivity.this, "10Hz Beacon Tx Rate", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
//                Toast.makeText(MainActivity.this, "3Hz Beacon Tx Rate", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
//                Toast.makeText(MainActivity.this, "1Hz Beacon Tx Rate", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(MainActivity.this, "Wrong Selection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startBeaconTransmitting() {
        mBeaconTransmitter.startAdvertising(mBeacon, new AdvertiseCallback() {
            @Override
            public void onStartFailure(final int errorCode) {
                Log.e(TAG, "Advertisement start failed with code: " + errorCode);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewStatus.setText("failed to start iBeacon Tx due to error: "
                                + errorCode);
                    }
                });
            }
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i(TAG, "Advertisement start succeeded.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewStatus.setText("iBeacon Tx has been started");
                    }
                });
            }
        });
    }

    private void stopBeaconTransmitting() {
        mBeaconTransmitter.stopAdvertising();
        mTextViewStatus.setText("iBeacon Tx has been shut down");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBeaconTransmitting();
    }

    @TargetApi(21)
    private boolean checkPrerequisites() {
        if (Build.VERSION.SDK_INT < 18) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not supported by this device's operating system");
            builder.setMessage("You will not be able to transmit as a Beacon");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            builder.show();
            return false;
        }
        if (!mBluetoothUtils.isBluetoothLeSupported()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not supported by this device");
            builder.setMessage("You will not be able to transmit as a Beacon");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            builder.show();
            return false;
        }
        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        try {
            // Check to see if the getBluetoothLeAdvertiser is available.
            // If not, this will throw an exception indicating we are not running Android L
            ((BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE))
                    .getAdapter().getBluetoothLeAdvertiser();   // enabled but can not use Advertiser
        } catch (Exception e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE advertising unavailable");
            builder.setMessage("Sorry, the operating system on this device does not support Bluetooth LE advertising.  As of July 2014, only the Android L preview OS supports this feature in user-installed apps.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }

            });
            builder.show();
            return false;
        }
        return true;
    }

    // startActivityForResult from BluetoothUtils#askUserToEnableBluetoothIfNeeded()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothUtils.REQUEST_ENABLE_BT && Activity.RESULT_CANCELED == resultCode) {
            Toast.makeText(this, "Please enable Bluetooth and restart app.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
