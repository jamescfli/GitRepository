package cn.nec.nlc.example.activitytest18;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class CallIntentsActivity extends Activity {
	private Spinner spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    spinner = (Spinner) findViewById(R.id.spinner);
	    ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
	        R.array.intents, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	}

	public void onClick(View view) {
		int position = spinner.getSelectedItemPosition();
	    Intent intent = null;
	    switch (position) {
	    case 0:
	    	// Create an intent with a given action and for a given data url.
	    	intent = new Intent(Intent.ACTION_VIEW,
	    			Uri.parse("http://www.baidu.com"));
	    	// .. parse(): Creates a Uri which parses the given encoded URI string
	    	break;
	    case 1:
	    	intent = new Intent(Intent.ACTION_CALL,
	    			Uri.parse("tel:10086"));
	    	break;
	    case 2:
	    	intent = new Intent(Intent.ACTION_DIAL,
	    			Uri.parse("tel:10086"));
	    	break;
	    case 3:
	    	intent = new Intent(Intent.ACTION_VIEW,
	    			Uri.parse("geo:50.123,7.1434?z=19"));
	    	break;
	    case 4:
	    	intent = new Intent(Intent.ACTION_VIEW,
	    			Uri.parse("geo:0,0?q=query"));
	    	break;
	    case 5:
	    	intent = new Intent("android.media.action.IMAGE_CAPTURE");
	    	break;
	    case 6:
	    	intent = new Intent(Intent.ACTION_VIEW,
	    			Uri.parse("content://contacts/people/"));
	    	break;
	    case 7:
	    	intent = new Intent(Intent.ACTION_EDIT,
	    			Uri.parse("content://contacts/people/1"));
	    	break;
	    }
	    if (intent != null) {
	    	startActivityForResult(intent, 0); // REQUESTCODE = 0
	    }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// in most cases resultCode = 0 and != RESULT_OK, 
		// furthermore, data = null ??
	    if (resultCode == Activity.RESULT_OK && requestCode == 0) {
	    	// this part was not executed
	    	String result = data.getExtras().toString();
	    	Log.i("CallIntentsActivity", result);
//	    	String result = data.toURI();
	    	Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	    }
	}

}
