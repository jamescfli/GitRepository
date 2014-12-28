package cn.nec.nlc.example.activitytest26threadprogbar;

import cn.nec.nlc.example.activitytest26handlerprogbar.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * To use Java thread, you have to handle the following requirements in code:
 * 	Synchronization with the main thread if you post back results to the user interface
 *	No default for canceling the thread
 * 	No default thread pooling
 * 	No default for handling configuration changes in Android
 */
public class MainActivity extends Activity {
	private ProgressBar progress;
	private TextView text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    progress = (ProgressBar) findViewById(R.id.progressBar1);
	    text = (TextView) findViewById(R.id.textView1);

	}

	public void startProgress(View view) {
	    // do something long
		Runnable runnable = new Runnable() {
		    @Override
		    public void run() {
		        for (int i = 0; i <= 10; i++) {
		        	final int value = i;
		        	doFakeWork();
		        	progress.post(new Runnable() {
		        		@Override
		        		public void run() {
		        			text.setText("Updating");
		        			progress.setProgress(value);
		        		}
		        	});
		        }
		    }
	    };
	    new Thread(runnable).start();
	}

	// Simulating something time-consuming
	private void doFakeWork() {
	    try {
	    	Thread.sleep(2000);
	    } catch (InterruptedException e) {
	    	e.printStackTrace();
	    }
	}
}
