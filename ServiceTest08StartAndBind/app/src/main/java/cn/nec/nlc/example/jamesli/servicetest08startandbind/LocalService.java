package cn.nec.nlc.example.jamesli.servicetest08startandbind;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private final IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        Log.i(TAG, "[Service] onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "[Service] onStartCommand()");
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "[Service] onBind()");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "[Service] onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[Service] onDestroy()");
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }

}
