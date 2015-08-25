package cn.jamesli.example.bt06ibeacontx.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.lang.annotation.Target;

/**
 * Created by jamesli on 15-8-18.
 */
public final class BluetoothUtils {
    public final static int REQUEST_ENABLE_BT = 2001;
    private final Activity mActivity;
    private final BluetoothAdapter mBluetoothAdapter;

    public BluetoothUtils(final Activity activity) {
        mActivity = activity;
        // API 18 BluetoothManager
        final BluetoothManager btManager = (BluetoothManager) mActivity
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = btManager.getAdapter(); // null => not have Bluetooth
//        // .. or you can do the same by
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void askUserToEnableBluetoothIfNeeded() {
        if (isBluetoothLeSupported() && (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public boolean isBluetoothLeSupported() {
        return mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean isBluetoothOn() {
        if (mBluetoothAdapter == null) {
            return false;
        } else {
            return mBluetoothAdapter.isEnabled();
        }
    }
}
