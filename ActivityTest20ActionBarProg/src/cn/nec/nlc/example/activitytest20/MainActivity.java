package cn.nec.nlc.example.activitytest20;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

	private MenuItem menuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    ActionBar actionBar = getActionBar();
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
	        | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_load:
	    	 menuItem = item;
	    	 menuItem.setActionView(R.layout.progressbar);
	    	 // Expand the action view associated with this menu item.
	    	 menuItem.expandActionView();
	    	 TestTask task = new TestTask();
	    	 task.execute("test");
	    	 break;
	    case R.id.menu_settings:
	    	Toast.makeText(this, "Settings was pressed", Toast.LENGTH_LONG).show();
	    default:
	    	break;
	    }
	    return true;
	}

	private class TestTask extends AsyncTask<String, Void, String> {

	    @Override
	    protected String doInBackground(String... params) {
	    	// Simulate something long running
	    	try {
	    		Thread.sleep(2000); // for 2 seconds
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}
	    	return null;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	    	menuItem.collapseActionView();
	    	menuItem.setActionView(null);
	    }
	};
} 
