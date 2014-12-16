package cn.nec.nlc.example.activitytest23;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Note: BuildConfig is provided by the ADT plugin for Eclipse, 
		// and isn't a part of the SDK tools/common build stuff
		// ADT(r17)中添加了一个新功能可以允许开发者只在Debug模式下允许某些代码
		if (BuildConfig.DEBUG) {
			// e-ERROR, w-WARN, i-INFO, d-DEBUG, v-VERBOSE
			Log.d(Constants.LOG_TAG, "onCreate called");
		}
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
