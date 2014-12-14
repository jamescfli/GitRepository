package cn.nec.nlc.example.activitytest15;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {

	public static final String MSG_KEY_RESULT = "MsgFromResultActivity";
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_result);
		
		// get the Bundle with the intent data via the getIntent().getExtras()
		Bundle extra = getIntent().getExtras();
		
		// Get the value of the passed extra with the extras.getString("yourkey")
		String value = extra.getString(MainActivity.MSG_KEY_MAIN);
		
		// value should be placed in the TextView with the displayintentextra ID
		TextView tv = (TextView) findViewById(R.id.displayintentextra);
		tv.setText(value);
		
		// enable the home button
	    ActionBar actionBar = getActionBar();
	    actionBar.setHomeButtonEnabled(true);
//	    actionBar.setDisplayUseLogoEnabled(false);
//	    actionBar.setDisplayHomeAsUpEnabled(true); 
	}
	
	@Override
	public void finish() {
	    
		// TODO 1 create new Intent 
		// Intent intent = new Intent();
		Intent i = new Intent();
	    
		// TODO 2 read the data of the EditText field
		// with the id returnValue
		EditText et = (EditText) findViewById(R.id.returnValue);
	  
		// TODO 3 put the text from EditText
		// as String extra into the intent
		// use editText.getText().toString();
		String extra = et.getText().toString();
		i.putExtra(MSG_KEY_RESULT, extra);
	    
		// TODO 4 use setResult(RESULT_OK, intent); 
		// to return the Intent to the application
		setResult(RESULT_OK, i);
		super.finish();
	} 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home: // the home icon was pressed
	    	Intent intent = new Intent(this, MainActivity.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
	    	break;
	    	// Something else
	    case R.id.action_settings:
	    	Toast.makeText(this, "Settings was pressed", Toast.LENGTH_LONG).show();
	    	break;
	    default:
	    	break;
	    }
	    return super.onOptionsItemSelected(item);
	}
} 
