package cn.jamesli.example.bt01actionbarcompat;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.sample_main);   // one single Fragment
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        // Manually add items, (int groupId, int itemId, int order, int titleRes)
//        // groupId	The group identifier that this item should be part of. This can also be
//        //      used to define groups of items for batch state changes. Normally use NONE if
//        //      an item should not be in a group.
//        // itemId	Unique item ID. Use NONE if you do not need a unique ID.
//        // order	The order for the item. Use NONE if you do not care about the order. See getOrder().
//        // titleRes	Resource identifier of title string.
//        MenuItem locaitonItem = menu.add(0, R.id.menu_location, 0, R.string.menu_location);
//        // .. R.id.menu_location can be found in ids.xml
//        locaitonItem.setIcon(R.drawable.ic_action_location);
//
//        MenuItemCompat.setShowAsAction(locaitonItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        // .. Options: SHOW_AS_ACTION_IF_ROOM, SHOW_AS_ACTION_WITH_TEXT, SHOW_AS_ACTION_ALWAYS etc.
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        switch (id) {
//            case R.id.menu_refresh:
//                // Here we might start a background refresh task
//                Toast.makeText(getApplicationContext(), "start refresh task", Toast.LENGTH_LONG).show();
//                return true;
//
//            case R.id.menu_location:
//                // Here we might call LocationManager.requestLocationUpdates()
//                Toast.makeText(getApplicationContext(), "request location", Toast.LENGTH_LONG).show();
//                return true;
//
//            case R.id.menu_settings:
//                // Here we would open up our settings activity
//                Toast.makeText(getApplicationContext(), "start setting activity", Toast.LENGTH_LONG).show();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
