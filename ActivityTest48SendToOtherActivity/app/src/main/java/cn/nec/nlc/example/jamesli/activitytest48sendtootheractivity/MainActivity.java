package cn.nec.nlc.example.jamesli.activitytest48sendtootheractivity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {
    private Button buttonSendText;
    private EditText editTextInputText;

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
