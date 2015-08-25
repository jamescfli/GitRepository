package cn.jamesli.example.bt07ibeaconscanner;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by jamesli on 15-8-19.
 */
public class BeaconReferenceApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "BeaconRefApp";
    // to set up background launching of an app when a user enters a beacon Region
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconSinceBoot = false;
    private MonitoringActivity monitoringActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
        // find Apple inc. iBeacon, 0xaabb is for AltBeacon
        mBeaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")); // AltBeacon
//                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")); // Apple Inc.
        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        // uniqueId field distinguish this Region
        Region region = new Region("BackgroundRegion", null, null, null);
        // .. uniqueId and id1, id2, id3 - UUID, Major, Minor
        // .. When constructing a range, any or all of these identifiers may be set to null,
        // .. which indicates that they are a wildcard and will match any value

        regionBootstrap = new RegionBootstrap(this, region);
        // automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);  // auto battery savor
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "did enter region");
        if (!haveDetectedBeaconSinceBoot) {
            Intent intent = new Intent(this, MonitoringActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
            // to keep multiple copies of this activity from getting created if the user has
            // already manually launched the app.
            startActivity(intent);
            haveDetectedBeaconSinceBoot = true;
        } else {
            if (monitoringActivity != null) {
                monitoringActivity.logToDisplay("I see a beacon again");
            } else {
                Log.d(TAG, "Sending notification");
                sendNotification();
            }
        }
    }

    @Override
    public void didExitRegion(Region region) {
        if (monitoringActivity != null) {
            monitoringActivity.logToDisplay("I no longer see a beacon");
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        if (monitoringActivity != null) {
            monitoringActivity.logToDisplay("I have just switched from seeing / not seeing beacons: " + state);
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Beacon Ref Application")
                .setContentText("An Beacon is nearby.")
                .setSmallIcon(R.mipmap.ic_launcher);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, MonitoringActivity.class));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        // TODO check the difference btw Context.NOTIFICATION_SERVICE and NOTIFICATION_SERVICE
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public void setMonitoringActivity(MonitoringActivity activity) {
        this.monitoringActivity = activity;
    }
}
