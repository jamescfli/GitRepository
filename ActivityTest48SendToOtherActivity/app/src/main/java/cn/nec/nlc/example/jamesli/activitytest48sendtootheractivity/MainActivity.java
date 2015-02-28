package cn.nec.nlc.example.jamesli.activitytest48sendtootheractivity;

import android.app.Activity;
import android.content.Intent;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;


public class MainActivity extends Activity {
    private Button buttonSendText;
    private EditText editTextInputText;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInputText = (EditText) findViewById(R.id.editTextInput);
        buttonSendText = (Button) findViewById(R.id.buttonSendText);
        buttonSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageSent = editTextInputText.getText().toString();
                if (messageSent != null || messageSent != "") {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, messageSent);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources()
                            .getText(R.string.send_text_to)));
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_share, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
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
        } else if (id == R.id.menu_item_share) {
            String messageSent = editTextInputText.getText().toString();
            if (messageSent != null || messageSent != "") {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, messageSent);
                sendIntent.setType("text/plain");
                setShareIntent(sendIntent);
                startActivity(Intent.createChooser(sendIntent, getResources()
                        .getText(R.string.send_text_to)));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
