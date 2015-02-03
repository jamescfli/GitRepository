package cn.nec.nlc.example.testproject03async.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cn.nec.nlc.example.testproject03async.MainActivity;
import cn.nec.nlc.example.testproject03async.MainActivity.IJobListener;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.Button;

import cn.nec.nlc.example.testproject03async.R;

public class AsyncTaskTester extends ActivityUnitTestCase<MainActivity> {
	private MainActivity activity;
	
	public AsyncTaskTester() {
		super(MainActivity.class);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
		// intent + savedInstanceState, lastNonConfigurationInstance
		startActivity(intent, null, null);
		activity = getActivity();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSomeAsyncTask() throws Throwable {
		// create CountDownLatch for which the test can wait
		final CountDownLatch latch = new CountDownLatch(1); 
		// .. 1 ~ number of times countDown() must be invoked to pass test
		
		activity.setListener(new IJobListener() {
			@Override
			public void executionDone() {
				latch.countDown();
			}
		});
		
		// execute the async task on the UI thread, KEY!
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				Button button = (Button) activity.findViewById(R.id.button1);
				button.performClick();
			}
		});
		
		boolean await = latch.await(30, TimeUnit.SECONDS);
		assertTrue(await);
	}
}
