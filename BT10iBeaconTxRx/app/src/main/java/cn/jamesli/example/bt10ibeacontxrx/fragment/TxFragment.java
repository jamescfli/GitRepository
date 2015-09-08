package cn.jamesli.example.bt10ibeacontxrx.fragment;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;

import cn.jamesli.example.bt10ibeacontxrx.R;
import cn.jamesli.example.bt10ibeacontxrx.nicespinner.NiceSpinner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@TargetApi(21)
public class TxFragment extends Fragment {
    private static final String TAG = "TxFragment";
    private BeaconTransmitter mBeaconTransmitter;
    private Beacon mBeacon;

    // for UI
    private NiceSpinner mSpinnerTxPower;
    private NiceSpinner mSpinnerTxRate;
    private static final String[] TX_POWER_LEVEL = new String[] {
            "High -56dBm",
            "Medium -66dBm",
            "Low -76dBm"
    };
    private static final String[] TX_RATE_LEVEL = new String[] {
            "High 10Hz",
            "Medium 3Hz",
            "Low 1Hz"
    };
    private Button mButtonTxStart;
    private Button mButtonTxStop;
    private TextView mTextViewStatus;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TxFragment.
     */
    public static TxFragment newInstance() {
        TxFragment fragment = new TxFragment();
        return fragment;
    }

    public TxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewTxFragment = inflater.inflate(R.layout.fragment_tx, container, false);
        getActivity().setTitle("Transmitter");

        initiateUi(viewTxFragment);

        return viewTxFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBluetoothEnabled()) {
            if (Build.VERSION.SDK_INT >= 21) {
                // Check to see if the getBluetoothLeAdvertiser is available.
                // If not, this will throw an exception indicating we are not running Android L
                ((BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE))
                        .getAdapter().getBluetoothLeAdvertiser();   // enabled but can not use Advertiser
                mBeaconTransmitter = new BeaconTransmitter(getActivity(), new BeaconParser()
//                    .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));     // 02-15 required by iBeacon
                        .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
                mBeacon = new Beacon.Builder()
                        .setBluetoothName("MotoAsBeacon")   // seems not working when being observed
                        .setId1("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6") // 16bytes UUID for AltBeacon
                        .setId2("1")                                    // Major
                        .setId3("2")                                    // Minor
                        .setManufacturer(0x0118)    // for Radius Networks AltBeacon
//                    .setManufacturer(0x004C)    // Apple Inc. the real message would be (in reverse sequence) 4C, 00 before 02, 15
                        .setTxPower(-59)    // default value, 0xC5 = -59dBm received power at 1m from Tx
                                // .. i.e. Measured power is set by holding a receiver one meter from the beacon
                        .setDataFields(Arrays.asList(new Long[]{0l})) // data fields included in the beacon advertisement.
                        .build();
                mButtonTxStart.setEnabled(true);
                mButtonTxStop.setEnabled(false);
            } else {
                // Android does not support Beacon Tx function
                mButtonTxStart.setEnabled(false);
                mButtonTxStop.setEnabled(false);
                mTextViewStatus.setText("Status: Android 5.0+ is required for Beacon Tx.");
            }
        } else {
            Toast.makeText(getActivity(), "Please turn Bluetooth on!", Toast.LENGTH_LONG).show();
        }
    }

    private void initiateUi(View view) {
        mSpinnerTxPower = (NiceSpinner) view.findViewById(R.id.spinner_tx_power);
        mSpinnerTxRate = (NiceSpinner) view.findViewById(R.id.spinner_tx_rate);
        ArrayAdapter<String> adapterTxPower = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, TX_POWER_LEVEL);
        ArrayAdapter<String> adapterTxRate = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, TX_RATE_LEVEL);
        mSpinnerTxPower.setAdapter(adapterTxPower);
        mSpinnerTxRate.setAdapter(adapterTxRate);

        mButtonTxStart = (Button) view.findViewById(R.id.button_tx_start);
        mButtonTxStop = (Button) view.findViewById(R.id.button_tx_stop);
        mButtonTxStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBluetoothEnabled()) {
                    int flag = BeaconTransmitter.checkTransmissionSupported(getActivity());
                    if (flag == 0) {
                        initiateBeaconTxPowerAndRate();     // read from spinner selections
                        startBeaconTransmitting();
                        mButtonTxStart.setEnabled(false);
                        mButtonTxStop.setEnabled(true);
                    } else {
                        switch (flag) {
                            case BeaconTransmitter.NOT_SUPPORTED_MIN_SDK:   // value 1
                                mTextViewStatus.setText("Status: SDK version is less or equal to API 18.");
                                break;
                            case BeaconTransmitter.NOT_SUPPORTED_BLE:       // value 2
                                mTextViewStatus.setText("Status: BLE is not supported.");
                                break;
                            case BeaconTransmitter.NOT_SUPPORTED_CANNOT_GET_ADVERTISER:     // value 4
                                mTextViewStatus.setText("Status: could not get Advertiser. Either not supported by chipset or manufacturer");
                                break;
                            case BeaconTransmitter.NOT_SUPPORTED_CANNOT_GET_ADVERTISER_MULTIPLE_ADVERTISEMENTS: // 5
                                mTextViewStatus.setText("Status: could not get Multiple Advertiser. Either not supported by chipset or manufacturer");
                                break;
                            default:
                                mTextViewStatus.setText("Status: BLE does not work for unknown reason.");
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Please turn Bluetooth on!", Toast.LENGTH_LONG).show();
                }
            }
        });
        mButtonTxStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBeaconTransmitting();
                mButtonTxStart.setEnabled(true);
                mButtonTxStop.setEnabled(false);
            }
        });

        mTextViewStatus = (TextView) view.findViewById(R.id.text_view_tx_status);
        mTextViewStatus.setText("Status: ");
    }

    private void initiateBeaconTxPowerAndRate() {
        switch (mSpinnerTxPower.getSelectedIndex()) {
            case 0:
                mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
                break;
            case 1:
                mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
                break;
            case 2:
                mBeaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW);
                break;
        }
        switch (mSpinnerTxRate.getSelectedIndex()) {
            case 0:
                mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
                break;
            case 1:
                mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
                break;
            case 2:
                mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
                break;
        }
    }

    private void startBeaconTransmitting() {
        mBeaconTransmitter.startAdvertising(mBeacon, new AdvertiseCallback() {
            @Override
            public void onStartFailure(final int errorCode) {
                Log.e(TAG, "Advertisement start failed with code: " + errorCode);
                getActivity().runOnUiThread(new Runnable() {
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewStatus.setText("Status: iBeacon Tx has been started");
                    }
                });
            }
        });
    }

    private void stopBeaconTransmitting() {
        if (Build.VERSION.SDK_INT >= 21) {
            mBeaconTransmitter.stopAdvertising();
        }
        mTextViewStatus.setText("Status: iBeacon Tx has been shut down");
    }

    private boolean isBluetoothEnabled() {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
                    .getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopBeaconTransmitting();
    }

}
