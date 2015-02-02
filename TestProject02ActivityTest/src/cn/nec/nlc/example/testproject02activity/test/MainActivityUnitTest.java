package cn.nec.nlc.example.testproject02activity.test;

import android.content.Intent;
import android.widget.Button;
import cn.nec.nlc.example.testproject02activity.MainActivity;

// class used for the test
public class MainActivityUnitTest extends android.test.ActivityUnitTestCase<MainActivity> {
	private int buttonId;
	private MainActivity activity;
	
	public MainActivityUnitTest() {
		super(MainActivity.class);
	}
	
	// Method is called before test execution
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				MainActivity.class);
		startActivity(intent, null, null);
		activity = getActivity();
	}
	
	public void testLayout() {
		buttonId = cn.nec.nlc.example.testproject02activity.R.id.button1;
		assertNotNull(activity.findViewById(buttonId));
		Button view = (Button) activity.findViewById(buttonId);
		assertEquals("Incorrect label of the button", "Start", view.getText());
	}
	
	public void testIntentTriggerViaOnClick() {
		buttonId = cn.nec.nlc.example.testproject02activity.R.id.button1;
		Button view = (Button) activity.findViewById(buttonId);
		assertNotNull("Button not allowed to be null", view);
		
		view.performClick();
		
		// TouchUtils cannot be used, only allowed in 
	    // InstrumentationTestCase or ActivityInstrumentationTestCase2 
	  
	    // Check the intent which was started
		Intent triggeredIntent = getStartedActivityIntent();
		assertNotNull("Intent was null", triggeredIntent);
		String data = triggeredIntent.getExtras().getString("URL");
		assertEquals("Incorrect data passed via the intent", "http://www.vogella.com", data);
	}
}
