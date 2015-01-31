package cn.nec.nlc.example.broadcastreceivertest1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tv;
	public static MainActivity mActivityThis = null;
	
	public TextView getTextView() {
		return tv;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.textview1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mActivityThis = this;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mActivityThis = null;
	}
}
