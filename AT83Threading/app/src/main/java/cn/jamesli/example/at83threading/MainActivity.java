package cn.jamesli.example.at83threading;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity {
    private final static int SET_PROGRESS_BAR_VISIBILITY = 0;
    private final static int PROGRESS_UPDATE = 1;
    private final static int SET_BITMAP = 2;

    private ImageView mImageView;
    private ProgressBar mProgressBar;
//    private Bitmap mBitmap;
    private int mDelay = 500;
//    private final Handler handler = new Handler();  // Handler in the MainActivity

    static class UIHandler extends Handler {
        WeakReference<MainActivity> mParent;

        public UIHandler(WeakReference<MainActivity> parent) {
            mParent = parent;
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity parent = mParent.get(); // Returns the referent of the reference object
            if (null != parent) {
                switch (msg.what) {
                    case SET_PROGRESS_BAR_VISIBILITY: {
                        parent.getProgressBar().setVisibility((Integer) msg.obj);
                        break;
                    }
                    case PROGRESS_UPDATE: {
                        parent.getProgressBar().setProgress((Integer) msg.obj);
                        break;
                    }
                    case SET_BITMAP: {
                        parent.getImageView().setImageBitmap((Bitmap) msg.obj);
                    }
                }
            }
        }
    }
    private Handler handler = new UIHandler(new WeakReference<MainActivity>(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        final Button button = (Button) findViewById(R.id.loadButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new LoadIconTask(R.drawable.painter, handler)).start();
            }
        });

        final Button otherButton = (Button) findViewById(R.id.otherButton);
        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "I'm Working",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class LoadIconTask implements Runnable {
        private final int resId;
        private final Handler handler;

        // Runnable can come with parameters through constructor as follows, resId is a parameter
        LoadIconTask(int resId, Handler handler) {
            this.resId = resId;
            this.handler = handler;
        }

        public void run() {
            // set progressBar visible
            Message msg = handler.obtainMessage(SET_PROGRESS_BAR_VISIBILITY, ProgressBar.VISIBLE);
            handler.sendMessage(msg);

            // load bitmap, update progressBar in the meantime
            final Bitmap tmp = BitmapFactory.decodeResource(getResources(), resId);
            for (int i = 1; i < 11; i++) {
                sleep();
                msg = handler.obtainMessage(PROGRESS_UPDATE, i*10);
                handler.sendMessage(msg);
            }
            msg = handler.obtainMessage(SET_BITMAP, tmp);
            handler.sendMessage(msg);

            // set progressBar invisible
            msg = handler.obtainMessage(SET_PROGRESS_BAR_VISIBILITY,
                    ProgressBar.INVISIBLE);
            handler.sendMessage(msg);
        }

        private void sleep() {
            try {
                Thread.sleep(mDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
