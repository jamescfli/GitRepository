package cn.nec.nlc.example.jamesli.servicetest08startandbind;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private LocalService localService;
    private Button buttonStart;
    private Button buttonStop;
    private Button buttonBind;
    private Button buttonUnbind;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "[Activity] onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "[Button] Start pressed");
                Intent intent= new Intent(MainActivity.this, LocalService.class);
                startService(intent);
            }
        });
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "[Button] Stop pressed");
                Intent intent= new Intent(MainActivity.this, LocalService.class);
                stopService(intent);
            }
        });
        buttonBind = (Button) findViewById(R.id.buttonBind);
        buttonBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "[Button] Bind pressed");
                Intent intent = new Intent(MainActivity.this, LocalService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
                  // + Context.BIND_DEBUG_UNBIND is from Bagilevi-Pedometer
            }
        });
        buttonUnbind = (Button) findViewById(R.id.buttonUnbind);
        buttonUnbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "[Button] UnBind pressed");
                unbindService(mConnection);
            }
        });
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "[Activity] onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "[Activity] onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "[Activity] onStop()");
        super.onStop();
        // unbindService(mConnection); should be here. o.w. ServiceConnection leak
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "[Activity] onDestroy()");
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.i(TAG, "[ServiceConnection] onServiceConnected()");
            LocalService.MyBinder b = (LocalService.MyBinder) binder;
            localService = b.getService();
        }
        // onServiceDisconnected() is called when remote service crash (or killed by the System).
        // So, if a service running in a different process than your client fails on some exception,
        // you lose the connection and get the callback.
        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "[ServiceConnection] onServiceDisconnected()");
            localService = null;
        }
    };

}
