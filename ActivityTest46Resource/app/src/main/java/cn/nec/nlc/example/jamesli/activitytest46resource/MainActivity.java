package cn.nec.nlc.example.jamesli.activitytest46resource;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private EditText editTextNumber;
    private EditText editTextName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        editTextName = (EditText) findViewById(R.id.editTextName);
    }

    public void showWelcomeMessage(View view) {
        ((TextView) findViewById(R.id.textView)).setText(getString
                (R.string.personal_welcome_message, editTextName.getText().toString()));
    }

    public void showInboxCountMessage(View view) {
        showInboxCountMessage(Integer.parseInt(editTextNumber.getText().toString()),
                editTextName.getText().toString());
    }

    private void showInboxCountMessage(int inboxCount, String name) {
        Resources res = getResources();
        String inboxCountMessage = String.format(res.getQuantityString
                        (R.plurals.inbox_message_count, inboxCount), inboxCount, name);
        ((TextView) findViewById(R.id.textView)).setText(inboxCountMessage);
        displayCategories();
    }

    private void displayCategories() {
        ListView listView = (ListView) findViewById(R.id.listView);
        Resources res = getResources();
        String[] categories = res.getStringArray(R.array.default_categories);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, categories);
        listView.setAdapter(categoriesAdapter);
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
