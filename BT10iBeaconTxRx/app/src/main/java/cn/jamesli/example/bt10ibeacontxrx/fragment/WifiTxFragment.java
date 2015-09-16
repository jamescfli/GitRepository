package cn.jamesli.example.bt10ibeacontxrx.fragment;


import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.jamesli.example.bt10ibeacontxrx.R;
import cn.jamesli.example.bt10ibeacontxrx.nicespinner.NiceSpinner;
import cn.jamesli.example.bt10ibeacontxrx.wifitutils.WifiApManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WifiTxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WifiTxFragment extends Fragment {
    private static final String TAG = "WifiTxFragment";

    // for WiFi AP
    private WifiConfiguration mWifiConfig;
    private WifiApManager mWifiApManager;

    // for UI
    private TextView mTextViewWifiBssid;
    private EditText mEditTextWifiSsid;
    private NiceSpinner mSpinnerWifiAuthAlg;
    private Button mButtonWifiStart;
    private Button mButtonWifiStop;
    private static final String[] WIFI_AUTH_ALGORITHM = new String[] {
            "LEAP, Network EAP",
            "OPEN, for WPA/WPA2",
            "SHARED, static WEP"
    };
    private TextView mTextViewWifiStatus;
    private static String mFragmentTitle;

    public static WifiTxFragment newInstance(String fragmentTitle) {
        WifiTxFragment fragment = new WifiTxFragment();
        mFragmentTitle = fragmentTitle;
        return fragment;
    }

    public WifiTxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWifiApManager = new WifiApManager(getActivity());

        // set default value for mWifiConfig
        mWifiConfig = mWifiApManager.getWifiApConfiguration();  // retrieve existing value
        if (mWifiConfig.BSSID == null || mWifiConfig.BSSID.length() == 0) {
            mWifiConfig.BSSID = mWifiApManager.getLocalMacAddress();
        }
        if (mWifiConfig.SSID == null || mWifiConfig.SSID.length() == 0) {
            mWifiConfig.SSID = getString(R.string.wifi_default_wifi_ssid);
        }
        // set default for WPA/WPA2
        mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewWifiTxFragment = inflater.inflate(R.layout.fragment_wifi_tx, container, false);
        getActivity().setTitle(mFragmentTitle);
        initiateUi(viewWifiTxFragment);
        return viewWifiTxFragment;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
        if (mWifiApManager.isWifiApEnabled()) {
            mTextViewWifiStatus.setText("Status: WiFi AP has been started.");
            mButtonWifiStart.setEnabled(false);
            mButtonWifiStop.setEnabled(true);
        } else {
            mTextViewWifiStatus.setText("Status: WiFi AP has NOT been started.");
            mButtonWifiStart.setEnabled(true);
            mButtonWifiStop.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();
        if (mWifiApManager.isWifiApEnabled()) {
            // turn WiFi AP off before go
            mWifiApManager.setWifiApEnabled(mWifiConfig, false);
        }
    }

    private void initiateUi(View view) {
        mTextViewWifiBssid = (TextView) view.findViewById(R.id.text_view_wifi_bssid);
        String bssid = (mWifiConfig.BSSID != null && mWifiConfig.BSSID.length() > 0) ?
                mWifiConfig.BSSID : getString(R.string.wifi_default_wifi_bssid);
        mTextViewWifiBssid.setText(bssid);
        mEditTextWifiSsid = (EditText) view.findViewById(R.id.edit_text_wifi_ssid);
        String ssid = (mWifiConfig.SSID != null && mWifiConfig.SSID.length() > 0) ?
                mWifiConfig.SSID : getString(R.string.wifi_default_wifi_ssid);
        mEditTextWifiSsid.setText(ssid);
        mSpinnerWifiAuthAlg = (NiceSpinner) view.findViewById(R.id.spinner_wifi_auth_alg);
        ArrayAdapter<String> adapterWifiAuthAlg = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, WIFI_AUTH_ALGORITHM);
        mSpinnerWifiAuthAlg.setAdapter(adapterWifiAuthAlg);
        mSpinnerWifiAuthAlg.setSelectedIndex(1);    // OPEN as default value
        mButtonWifiStart = (Button) view.findViewById(R.id.button_wifi_tx_start);
        mButtonWifiStop = (Button) view.findViewById(R.id.button_wifi_tx_stop);
        mButtonWifiStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWifiConfigurationBeforeStart();
                if (mWifiApManager.setWifiApEnabled(mWifiConfig, true)) {
                    if (mWifiApManager.isWifiApEnabled()) {
                        mTextViewWifiStatus.setText("Status: WiFi AP has been started");
                        mButtonWifiStart.setEnabled(false);
                        mButtonWifiStop.setEnabled(true);
                        // freeze EditText and Spinner
                        mEditTextWifiSsid.setEnabled(false);
                        mSpinnerWifiAuthAlg.setEnabled(false);
                    } else {
                        mTextViewWifiStatus.setText("Status: for some reason, " +
                                "WiFI AP is still disabled after pressing start button.");
                    }
                } else {
                    mTextViewWifiStatus.setText("Status: for some reason, " +
                            "WiFI AP has not been started properly.");
                }
            }
        });
        mButtonWifiStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWifiApManager.setWifiApEnabled(mWifiConfig, false)) {
                    if (mWifiApManager.isWifiApEnabled()) {
                        mTextViewWifiStatus.setText("Status: for some reason, " +
                                "WiFI AP is still enabled after pressing stop button.");
                    } else {
                        mTextViewWifiStatus.setText("Status: WiFi AP has been stopped");
                        mButtonWifiStart.setEnabled(true);
                        mButtonWifiStop.setEnabled(false);
                        // release EditText and Spinner
                        mEditTextWifiSsid.setEnabled(true);
                        mSpinnerWifiAuthAlg.setEnabled(true);
                    }
                } else {
                    mTextViewWifiStatus.setText("Status: note WiFi AP has not been stopped properly.");
                }
            }
        });
        mTextViewWifiStatus = (TextView) view.findViewById(R.id.text_view_wifi_tx_status);
    }

    private void setWifiConfigurationBeforeStart() {
        // read SSID
        String ssid = mEditTextWifiSsid.getText().toString();
        if (ssid != null && ssid.length() > 0) {
            mWifiConfig.SSID = ssid;
        }
        switch (mSpinnerWifiAuthAlg.getSelectedIndex()) {
            case 0:
                mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.LEAP);
                break;
            case 1:
                mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                break;
            case 2:
                mWifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                break;
        }
    }


}
