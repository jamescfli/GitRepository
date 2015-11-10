package cn.jamesli.example.aw5sosgpslocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private TextView mTextViewStatus;
    private Button mButtonSosGps;

    private LocationManager mLocationManager;
    private LocationUpdateListener mLocationUpdateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewStatus = (TextView) findViewById(R.id.text_view_mobile_status);
        mButtonSosGps = (Button) findViewById(R.id.button_mobile_sos_gps);
        mButtonSosGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getCurrentGpsLocation();
                } else {
                    mTextViewStatus.setText("Pls turn on GPS ..");
                }
            }
        });

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationUpdateListener = new LocationUpdateListener();

//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, mLocationUpdateListener);
//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, mLocationUpdateListener);
//        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        displayLocationInTextView(location);

//        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//            mTextViewStatus.setText("Pls turn GPS on ...");
//            //返回开启GPS导航设置界面
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(intent);
//            return;
//        }

//        String bestProvider = mLocationManager.getBestProvider(getCriteria(), true);
//        Location location = mLocationManager.getLastKnownLocation(bestProvider);
//        if (location != null) {
//            mTextViewStatus.setText("Initial Result \n" +
//                    "Latitude: " + location.getLatitude() + "\n" +
//                    "Longitude: " + location.getLongitude());
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(mLocationUpdateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        // update if >= 2 secs in time, or >= 2 m movement
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, mLocationUpdateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mLocationManager.removeUpdates(mLocationUpdateListener);
    }

    private void getCurrentGpsLocation() {
        // it seems NETWORK_PROVIDER is more critical than GPS_PROVIDER for valid location feedback
//        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationUpdateListener, getMainLooper());
//        mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationUpdateListener, getMainLooper());
        // looper	a Looper object whose message queue will be used to implement the callback mechanism,
        //          or null to make callbacks on the calling thread
        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationUpdateListener, null);
        mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationUpdateListener, null);
        mTextViewStatus.setText("Request a single update ..");
        Log.i("MainActivity", "GPS_Provider - " + mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        Log.i("MainActivity", "NETWORK_Provider - " + mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void displayLocationInTextView(Location location) {
        if (location != null) {
            mTextViewStatus.setText("Latitude: " + location.getLatitude() + "\n"
                    + "Longitude: " + location.getLongitude() + "\n"
                    + "Timestamp: " + location.getTime());
        } else {
            mTextViewStatus.setText("location is null .. " + System.currentTimeMillis());
        }
    }

    private class LocationUpdateListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.i("MainActivity", "GPS_Provider - " + mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
            Log.i("MainActivity", "NETWORK_Provider - " + mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
            displayLocationInTextView(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    mTextViewStatus.setText("GPS available");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    mTextViewStatus.setText("GPS out of service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    mTextViewStatus.setText("GPS temporarily unavailable");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Criteria getCriteria(){
        Criteria criteria=new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}
