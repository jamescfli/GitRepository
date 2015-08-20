package cn.jamesli.example.bt02blelibrary;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.jamesli.example.bt02blelibrary.adapter.LeDeviceListAdapter;
import cn.jamesli.example.bt02blelibrary.container.BluetoothLeDeviceStore;
import cn.jamesli.example.bt02blelibrary.utils.BluetoothLeScanner;
import cn.jamesli.example.bt02blelibrary.utils.BluetoothUtils;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.easycursor.objectcursor.EasyObjectCursor;

// ActionBarActivity was deprecated, use AppCompatActivity instead.
public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private TextView mTvBluetoothLeStatus;
    private TextView mTvBluetoothStatus;
    private TextView mTvItemCount;
    protected ListView mList;
    protected View mEmpty;

    // BLE related
    private BluetoothUtils mBluetoothUtils;
    private BluetoothLeScanner mScanner;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothLeDeviceStore mDeviceStore;

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            // TODO strange? can not find foo iBeacon transmitter's name, set by
            // Beacon.Builder().setBluetoothName("MotoAsBeacon")
            if (deviceLe.getName() == null) {
                Log.d(TAG, deviceLe.getAddress().toString() + deviceLe.getName().toString());
            }
            mDeviceStore.addDevice(deviceLe);
            final EasyObjectCursor<BluetoothLeDevice> c = mDeviceStore.getDeviceCursor();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.swapCursor(c);
                    updateItemCount(mLeDeviceListAdapter.getCount());
                }
            });
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvBluetoothLeStatus = (TextView) findViewById(R.id.tvBluetoothLe);
        mTvBluetoothStatus = (TextView) findViewById(R.id.tvBluetoothStatus);
        mTvItemCount = (TextView) findViewById(R.id.tvItemCount);
        mList = (ListView) findViewById(android.R.id.list);
        mEmpty = (View) findViewById(android.R.id.empty);

        mList.setEmptyView(mEmpty);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothLeDevice device = mLeDeviceListAdapter.getItem(position);
                if (device == null) return;
                final Intent intent = new Intent(MainActivity.this, DeviceDetailsActivity.class);
                intent.putExtra(DeviceDetailsActivity.EXTRA_DEVICE, device);
                startActivity(intent);
            }
        });
        mDeviceStore = new BluetoothLeDeviceStore(this);
        mBluetoothUtils = new BluetoothUtils(this);
        mScanner = new BluetoothLeScanner(mLeScanCallback, mBluetoothUtils);
        updateItemCount(0);     // no scan, no BLEs
    }

    @Override
    protected void onResume() {
        super.onResume();
        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();

        if (mIsBluetoothOn) {
            mTvBluetoothStatus.setText(R.string.on);
        } else {
            mTvBluetoothStatus.setText(R.string.off);
        }

        if (mIsBluetoothLePresent) {
            mTvBluetoothLeStatus.setText(R.string.supported);
        } else {
            mTvBluetoothLeStatus.setText(R.string.not_supported);
        }

        invalidateOptionsMenu();    // recreate from onCreateOptionsMenu(android.view.Menu)
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop scan
        mScanner.scanLeDevice(-1, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (!mScanner.isScanning()) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
        }
        if (mList.getCount() > 0) {
//            menu.findItem(R.id.menu_share).setVisible(true);
            menu.findItem(R.id.menu_save_to_file).setVisible(true);
        } else {
//            menu.findItem(R.id.menu_share).setVisible(false);
            menu.findItem(R.id.menu_save_to_file).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.menu_scan:
                startScan();
                break;
            case R.id.menu_stop:
                mScanner.scanLeDevice(-1, false);
                invalidateOptionsMenu();
                break;
            case R.id.menu_save_to_file:
                showSaveResultDialog();
                break;
            case R.id.menu_about:
                showAboutDialog();
                break;
//            case R.id.menu_share:
//                mDeviceStore.shareDataAsEmail(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void startScan() {
        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        mDeviceStore.clear();
        updateItemCount(0);

        mLeDeviceListAdapter = new LeDeviceListAdapter(this, mDeviceStore.getDeviceCursor());
        mList.setAdapter(mLeDeviceListAdapter);

        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        if (mIsBluetoothOn && mIsBluetoothLePresent) {
            mScanner.scanLeDevice(-1, true);    // -1 ~ delay duration
            invalidateOptionsMenu();
        }
    }

    private void showSaveResultDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_save)
                .setMessage(getString(R.string.save_dialog_text, mDeviceStore.getFileSavingDirectory().toString()))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDeviceStore.saveDataInExternalStorage();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Scan result has not been saved!",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    private void showAboutDialog() {
        final int paddingSizeDp = 5;
        final float scale = getResources().getDisplayMetrics().density;
        final int dpAsPixels = (int) (paddingSizeDp * scale + 0.5f);

        final TextView textView = new TextView(this);
        final SpannableString text = new SpannableString((getString(R.string.about_dialog_text)));

        textView.setText(text);
        // Controls whether links such as urls and email addresses are automatically found and converted to clickable links.
        textView.setAutoLinkMask(MainActivity.this.RESULT_OK);
        // Traverses links in the text buffer and scrolls if necessary.
        // Supports clicking on links with DPad Center or Enter. Otherwise, they are just simple text.
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);    // padding in pixels

        // Linkify take a piece of text and a regular expression and turns all of the regex
        // matches in the text into clickable links. This is particularly useful for matching
        // things like email addresses, web urls, etc. and making them actionable.
        // addLinks(): turns all occurrences of the link types indicated in the mask into clickable links
        // Linkify.ALL includes: Email, Map, Phone Number, Web Urls
        Linkify.addLinks(text, Linkify.ALL);

        // Show about dialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_about)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Auto dismiss the dialog
                    }
                })
                .setView(textView)
                .show();
    }

    private void updateItemCount(final int count) {
        mTvItemCount.setText(getString(R.string.formatter_item_count, String.valueOf(count)));
    }
}
