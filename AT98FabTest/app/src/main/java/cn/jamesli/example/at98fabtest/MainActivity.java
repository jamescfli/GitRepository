package cn.jamesli.example.at98fabtest;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ShowHideOnScroll;
//import com.melnykov.fab.FloatingActionButton;


public class MainActivity extends Activity {
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(android.R.id.list);

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                new String[] {  "Item 1", "Item 2", "Item 3", "Item 4", "Item 5",
                                "Item 6", "Item 7", "Item 8", "Item 9", "Item 10",
                                "Item 11", "Item 12", "Item 13", "Item 14", "Item 15"});

        mListView.setAdapter(mAdapter);

//        // for melnykov FAB
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.attachToListView(mListView);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Fab is clicked", Toast.LENGTH_LONG).show();
//            }
//        });

        // for shamanland FAB
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        // if not defined in the layout.xml
//        fab.setSize(FloatingActionButton.SIZE_MINI);
//        fab.setColor(Color.RED);
//        // NOTE invoke this method after setting new values for e.g. Size and Color
//        fab.initBackground();
//        // NOTE standard method of ImageView
//        fab.setImageResource(R.drawable.ic_refresh_white_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Fab is clicked", Toast.LENGTH_LONG).show();
            }
        });
        mListView.setOnTouchListener(new ShowHideOnScroll(fab) {
            @Override
            public void onScrollUp() {
                super.onScrollUp();
                // default show fab

            }
            @Override
            public void onScrollDown() {
                super.onScrollDown();
                // default hide fab
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
