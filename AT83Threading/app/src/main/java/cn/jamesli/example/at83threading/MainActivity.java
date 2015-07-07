package cn.jamesli.example.at83threading;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity {
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private int mDelay = 500;


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
                new Thread(new LoadIconTask(R.drawable.painter)).start();
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

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // event can be run in different threads, e.g. onEvent, onEventBackgroundThread, onEventAsync
    // and onEventMainThread. since we need to update UI, the callbacks should be run in main UI
    // thread, so onEventMainThread is taken.
    public void onEventMainThread(ProgressBarVisibilityEvent event) {
        if (event.isVisible()) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        } else {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    public void onEventMainThread(ProgressBarUpdateEvent event) {
        mProgressBar.setProgress(event.getProgress());
    }

    public void onEventMainThread(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    private class LoadIconTask implements Runnable {
        private final int resId;

        // Runnable can come with parameters through constructor as follows, resId is a parameter
        LoadIconTask(int resId) {
            this.resId = resId;
        }

        public void run() {
            // set progressBar visible
            EventBus.getDefault().post(new ProgressBarVisibilityEvent(ProgressBar.VISIBLE));

            // load bitmap, update progressBar in the meantime
            final Bitmap tmp = BitmapFactory.decodeResource(getResources(), resId);
            for (int i = 1; i < 11; i++) {
                sleep();
                EventBus.getDefault().post(new ProgressBarUpdateEvent(i*10));
            }
            EventBus.getDefault().post(tmp);

            // set progressBar invisible
            EventBus.getDefault().post(new ProgressBarVisibilityEvent(ProgressBar.INVISIBLE));
        }

        private void sleep() {
            try {
                Thread.sleep(mDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
