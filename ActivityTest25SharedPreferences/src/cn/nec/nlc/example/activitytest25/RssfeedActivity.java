package cn.nec.nlc.example.activitytest25;

import cn.nec.nlc.example.sharedpreftest1.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class RssfeedActivity extends Activity {

	TextView tv;
//	ParseTask parseTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rssfeed);
		
		tv = (TextView) findViewById(R.id.textview1);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// revise the textview according to the new preference
		tv.setText(prefs.getString("url", "N/A")); // key and default value if fails
//		updateListContent();
	}
	
//	public void updateListContent() {
//		if (parseTask == null) {
//			parseTask = new ParseTask();
//			parseTask.setFragment(this);
//			SharedPreferences settings PreferenceManager.getDefaultSharedPreferences(getActivity());
//			String url = settings.getString("url", "http://www.baidu.com");
//			parseTask.execute(url);
//		}
//	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rssfeed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			// Launch settings activity
		    Intent i = new Intent(this, SettingsActivity.class);
		    startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
