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
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothUtils = new BluetoothUtils(this);

        if (checkPrerequisites()) {
            // context + BeaconParser
            // format specified in the BeaconParser
            // Layout: Beacon type (2 bytes, 0x02-15), UUID 16byts 4-19, Major 2 bytes 20-21,
            // Minor 2 bytes 22-23, Measured Power 1 byte 25
            mBeaconTransmitter = new BeaconTransmitter(this, new BeaconParser()
                    .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));     // 02-15 required by iBeacon
//                    .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
            // .. bytes 0-1 of the BLE manufacturer advertisements are the two byte manufacturer code
            Beacon beacon = new Beacon.Builder()
                    .setBluetoothName("MotoAsBeacon")   // seems not working when being observed
                    .setId1("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6") // 16bytes UUID for AltBeacon
//                    .setId1("FE0E1948-76E5-4C81-9734-B8FD81792773") // MAC Generated: MacBook:~$uuidgen
                    .setId2("1")                                    // Major
                    .setId3("2")                                    // Minor
                    // IDs: the a list of the multi-part identifiers of the beacon. Together,
                    // these identifiers signify a unique beacon.
                    // The identifiers are ordered by significance for the purpose of grouping beacons
//                    .setManufacturer(0x0000) // A two byte code indicating the beacon manufacturer, some devices cannot detect beacons with a manufacturer code > 0x00ff
//                    .setManufacturer(0x0118)    // for Radius Networks AltBeacon
                    .setManufacturer(0x004C)    // Apple Inc. the real message would be (in reverse sequence) 4C, 00 before 02, 15
                    .setTxPower(-59)    // default value, 0xC5 = -59dBm received power at 1m from Tx
                    // .. i.e. Measured power is set by holding a receiver one meter from the beacon
                    .setDataFields(Arrays.asList(new Long[]{0l, 1l, 2l})) // data fields included in the beacon advertisement.
                    .build();
//            mBeaconTransmitter.startAdvertising(beacon);
            mBeaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {

                @Override
                public void onStartFailure(int errorCode) {
                    Log.e(TAG, "Advertisement start failed with code: " + errorCode);
                }

                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    Log.i(TAG, "Advertisement start succeeded.");
                }
            });
        } else {
            // requires Android 5.0
            int result = BeaconTransmitter.checkTransmissionSupported(this);
            switch (result) {
                case BeaconTransmitter.NOT_SUPPORTED_MIN_SDK:   // value 1
                    // TODO if BeaconTransmitter requires Android 5.0, how it would be possible to end it up here?
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconTransmitter.stopAdvertising();
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
//        if (!((BluetoothManager) getApplicationContext()
//                .getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled()) {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Bluetooth not enabled");
//            builder.setMessage("Please enable Bluetooth and restart this app.");
//            builder.setPositiveButton(android.R.string.ok, null);
//            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    finish();
//                }
//            });
//            builder.show();
//            return false;
//        }
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
