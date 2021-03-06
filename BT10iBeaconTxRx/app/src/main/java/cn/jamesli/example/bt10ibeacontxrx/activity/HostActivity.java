package cn.jamesli.example.bt10ibeacontxrx.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ikimuhendis.ldrawer.ActionBarDrawerToggle;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;

import cn.jamesli.example.bt10ibeacontxrx.R;
import cn.jamesli.example.bt10ibeacontxrx.fragment.BeaconRxFragment;
import cn.jamesli.example.bt10ibeacontxrx.fragment.BeaconTxFragment;
import cn.jamesli.example.bt10ibeacontxrx.fragment.WifiFindDirectionAroundLandmarkFragment;
import cn.jamesli.example.bt10ibeacontxrx.fragment.WifiFindFingerprintFragment;
import cn.jamesli.example.bt10ibeacontxrx.fragment.WifiFindLandmarkApFragment;
import cn.jamesli.example.bt10ibeacontxrx.fragment.WifiFingerprintMarkerFragment;
import cn.jamesli.example.bt10ibeacontxrx.fragment.WifiRxFragment;
import cn.jamesli.example.bt10ibeacontxrx.fragment.WifiTxFragment;

public class HostActivity extends Activity {
    private static final String TAG = "RxActivity";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;

    // Fragments
    private BeaconRxFragment beaconRxFragment;
    private BeaconTxFragment beaconTxFragment;
    private WifiRxFragment wifiRxFragment;
    private WifiTxFragment wifiTxFragment;
    private WifiFingerprintMarkerFragment wifiFingerprintMarkerFragment;
    private WifiFindLandmarkApFragment wifiFindLandmarkApFragment;
    private WifiFindDirectionAroundLandmarkFragment wifiFindDirectionAroundLandmarkFragment;
    private WifiFindFingerprintFragment wifiFindFingerprintFragment;

    private String[] valuesMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        initiateLDrawer();

        if (savedInstanceState == null) {
            beaconRxFragment = BeaconRxFragment.newInstance(valuesMenuList[0]);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, beaconRxFragment).commit();
        }
    }

    private void initiateLDrawer() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);

        drawerArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        valuesMenuList = getResources().getStringArray(R.array.menu_list_items);
        ArrayAdapter<String> adapterMenuList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, valuesMenuList);
        mDrawerList.setAdapter(adapterMenuList);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (beaconRxFragment == null) {
                            beaconRxFragment = BeaconRxFragment.newInstance(valuesMenuList[0]);
                        }
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, beaconRxFragment).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 1:
                        if (beaconTxFragment == null) {
                            beaconTxFragment = BeaconTxFragment.newInstance(valuesMenuList[1]);
                        }
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, beaconTxFragment).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 2:
                        if (wifiRxFragment == null) {
                            wifiRxFragment = WifiRxFragment.newInstance(valuesMenuList[2]);
                        }
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, wifiRxFragment).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 3:
                        if (wifiTxFragment == null) {
                            wifiTxFragment = WifiTxFragment.newInstance(valuesMenuList[3]);
                        }
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, wifiTxFragment).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 4:
                        if (wifiFingerprintMarkerFragment == null) {
                            wifiFingerprintMarkerFragment = WifiFingerprintMarkerFragment
                                    .newInstance(valuesMenuList[4]);
                        }
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, wifiFingerprintMarkerFragment).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 5:
                        if (wifiFindLandmarkApFragment == null) {
                            wifiFindLandmarkApFragment = WifiFindLandmarkApFragment
                                    .newInstance(valuesMenuList[5]);
                        }
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, wifiFindLandmarkApFragment).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 6:
                        if (wifiFindDirectionAroundLandmarkFragment == null) {
                            wifiFindDirectionAroundLandmarkFragment = WifiFindDirectionAroundLandmarkFragment
                                    .newInstance(valuesMenuList[6]);
                        }
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, wifiFindDirectionAroundLandmarkFragment).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 7:
                        if (wifiFindFingerprintFragment == null) {
                            wifiFindFingerprintFragment = WifiFindFingerprintFragment
                                    .newInstance(valuesMenuList[7]);
                        }
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, wifiFindFingerprintFragment).commit();
                        mDrawerLayout.closeDrawer(mDrawerList);
                        break;
                    case 8: // Exit
                        finish();
                        break;
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
