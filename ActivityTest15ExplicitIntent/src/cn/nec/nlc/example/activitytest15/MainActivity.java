package cn.nec.nlc.example.activitytest15;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	// extra message key for outgoing intent
	public static final String MSG_KEY_MAIN = "MsgFromMainActivity";
	// constant to determine which sub-activity returns
	private static final int REQUEST_CODE = 10;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	}

	public void onClick(View view) {
		EditText text = (EditText) findViewById(R.id.inputforintent);
		// used later
	    String value = text.getText().toString();
	    // TODO 1 create new Intent(context, class)
	    // use the activity as context parameter
	    // and "ResultActivity.class" for the class parameter
	    Intent i = new Intent(this, ResultActivity.class);
	    
	    // pass the value of the EditText view to the sub-activity
	    i.putExtra(MSG_KEY_MAIN, value);

//	    // TODO 2 start second activity with
//	    // startActivity(intent);
//	    startActivity(i);
	    
	    // TODO 2.. now use 
	    // startActivityForResult(i, REQUEST_CODE);
	    startActivityForResult(i, REQUEST_CODE);
	}
	
	// TODO 3 Implement this method
	// assumes that "returnkey" is used as key to return the result
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
	    	if (data.hasExtra(ResultActivity.MSG_KEY_RESULT)) {
	    		String result = data.getExtras().getString(ResultActivity.MSG_KEY_RESULT);
	    		if (result != null && result.length() > 0) {
	    			Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
	    		}
	    	}
	    }
	}

} 
