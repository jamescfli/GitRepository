package cn.nec.nlc.example.testproject02activity.test;

import cn.nec.nlc.example.testproject02activity.MainActivity;
import cn.nec.nlc.example.testproject02activity.SecondActivity;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import cn.nec.nlc.example.testproject02activity.R;

public class MainActivityFunctionalTest extends
	ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity activity;

	// constructor
	public MainActivityFunctionalTest() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// apply before getActivity() to set the initial touch mode
		setActivityInitialTouchMode(false);	// not in the touch mode
		activity = getActivity();
	}

	public void testStartSecondActivity() throws Exception {
		// add monitor to check for the second activity
		ActivityMonitor monitor = getInstrumentation().		// access instrumentation
				addMonitor(SecondActivity.class.getName(), null, false);

		// find button and click it
		Button view = (Button) activity.findViewById(R.id.button1);

		// TouchUtils handles the sync with the main thread internally
		TouchUtils.clickView(this, view);	// InstrumentationTestCase and View

		// to click on a click, e.g., in a listview
		// listView.getChildAt(0);

		// wait 2 seconds for the start of the activity
		SecondActivity startedActivity = (SecondActivity) monitor
				.waitForActivityWithTimeout(2000);
		assertNotNull(startedActivity);

		// search for the textView
		TextView textView = (TextView) startedActivity.findViewById(R.id.resultText);

		// check that the TextView is on the screen
		ViewAsserts.assertOnScreen(startedActivity.getWindow().getDecorView(), textView);
		// validate the text on the TextView
		assertEquals("Text incorrect", "Started", textView.getText().toString());

		// press back and click again
		this.sendKeys(KeyEvent.KEYCODE_BACK);

		TouchUtils.clickView(this, view);
	}
} 
