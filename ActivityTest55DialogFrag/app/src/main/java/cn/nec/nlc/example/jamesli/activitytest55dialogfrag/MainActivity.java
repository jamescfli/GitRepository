package cn.nec.nlc.example.jamesli.activitytest55dialogfrag;

import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private TextView textViewDisplay;
    private Button buttonStartDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewDisplay = (TextView) findViewById(R.id.textView);
        buttonStartDialog = (Button) findViewById(R.id.button);
        buttonStartDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // the following does not work
//                getFragmentManager().beginTransaction()
//                        .add(android.R.id.content, new FireMissilesDialogFragment()).commit();
                FragmentManager fm = getFragmentManager();
                FireMissilesDialogFragment fireMissilesDialog = new FireMissilesDialogFragment();
                // show(FragmentManager manager, String tag): display the dialog, adding the
                // fragment to the given FragmentManager. This is a convenience for explicitly
                // creating a transaction, adding the fragment to it with the given tag, and
                // committing it. This does not add the transaction to the back stack. When
                // the fragment is dismissed, a new transaction will be executed to remove it
                // from the activity.
                fireMissilesDialog.show(fm, "fire_missiles_dialog_fragment");
                  // manager	The FragmentManager this fragment will be added to.
                  // tag	    The tag for this fragment, as per FragmentTransaction.add.

            }
        });
    }

    public TextView getDisplayTextView() {
        return textViewDisplay;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
