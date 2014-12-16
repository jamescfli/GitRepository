package cn.nec.nlc.example.activitytest24;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	// constant for identifying the dialog
	private static final int DIALOG_ALERT = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	// adjust this method if you have more than 
	// one button pointing to this method
	public void onClick(View view) {
		showDialog(DIALOG_ALERT);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	  switch (id) {
	    case DIALOG_ALERT:
	      Builder builder = new AlertDialog.Builder(this);
	      builder.setMessage("This ends the activity");
	      builder.setCancelable(false);	// default: true
	      builder.setPositiveButton("I agree", new OkOnClickListener());
	      builder.setNegativeButton("No, no", new CancelOnClickListener());
	      AlertDialog dialog = builder.create();
	      dialog.show();
	  }
	  return super.onCreateDialog(id);
	}

	private final class OkOnClickListener implements
    	DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			MainActivity.this.finish();
		}
	}
	
	private final class CancelOnClickListener implements
		DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(getApplicationContext(), 
					"Cancel selected, activity continues", Toast.LENGTH_LONG)
					.show();
		}
	}
}
