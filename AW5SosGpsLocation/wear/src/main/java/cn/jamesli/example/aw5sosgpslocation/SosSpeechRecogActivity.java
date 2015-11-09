package cn.jamesli.example.aw5sosgpslocation;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SosSpeechRecogActivity extends Activity {

    private TextView mTextViewStatus;
    private Button mButtonSosGps;

    private LocationManager mLocationManager;
    private LocationUpdateListener mLocationUpdateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextViewStatus = (TextView) stub.findViewById(R.id.text);
                mButtonSosGps = (Button) stub.findViewById(R.id.button_sos_gps);
                mButtonSosGps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            getCurrentGpsLocation();
                        } else {
                            mTextViewStatus.setText("Please turn on GPS ..");
                        }
                    }
                });
            }
        });
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationUpdateListener = new LocationUpdateListener();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        mLocationManager.removeUpdates(mLocationUpdateListener);
//    }

    private void getCurrentGpsLocation() {
        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationUpdateListener, getMainLooper());
    }

    private void displayLocationInTextView(Location location) {
        mTextViewStatus.setText("Latitude: " + location.getLatitude() + "\n"
                + "Longitude: " + location.getLongitude());
    }

    private class LocationUpdateListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            displayLocationInTextView(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
