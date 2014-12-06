package cn.nec.nlc.example.activitytest15;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public static final String MSG_KEY = "MsgFromMainActivity";
	
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
	    i.putExtra(MSG_KEY, value);

	    // TODO 2 start second activity with
	    // startActivity(intent);
	    startActivity(i);
	}

} 
