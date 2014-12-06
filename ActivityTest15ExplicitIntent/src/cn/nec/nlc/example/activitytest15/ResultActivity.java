package cn.nec.nlc.example.activitytest15;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends Activity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_result);
		
		// get the Bundle with the intent data via the getIntent().getExtras()
		Bundle extra = getIntent().getExtras();
		
		// Get the value of the passed extra with the extras.getString("yourkey")
		String value = extra.getString(MainActivity.MSG_KEY);
		
		// value should be placed in the TextView with the displayintentextra ID
		TextView tv = (TextView) findViewById(R.id.displayintentextra);
		tv.setText(value);
		
	}
	
} 
