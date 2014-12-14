package cn.nec.nlc.example.activitytest21;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

public class MainActivity extends Activity {
	private ShareActionProvider provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);

	    // Get the ActionProvider for later usage
	    provider = (ShareActionProvider) menu.findItem(R.id.menu_share)
	        .getActionProvider();
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_share:
	      doShare();
	      break;
	    default:
	      break;
	    }
	    return true;  
	}

	public void doShare() {
	    // populate the share (to, e.g., weibo wechat ..) intent with data
	    Intent intent = new Intent(Intent.ACTION_SEND);
	    intent.setType("text/plain");
	    intent.putExtra(Intent.EXTRA_TEXT, "This is a message for you");
	    provider.setShareIntent(intent);
	} 
}
