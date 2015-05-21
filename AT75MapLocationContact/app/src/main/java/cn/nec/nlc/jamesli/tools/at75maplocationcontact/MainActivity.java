package cn.nec.nlc.jamesli.tools.at75maplocationcontact;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

// MapLocationFromContactsActivity in Coursera MoCCA course
public class MainActivity extends Activity {

    // These variables are shorthand aliases for data items in Contacts-related database tables
    private static final String DATA_MIMETYPE = ContactsContract.Data.MIMETYPE;
    private static final Uri DATA_CONTENT_URI = ContactsContract.Data.CONTENT_URI;
    private static final String DATA_CONTACT_ID = ContactsContract.Data.CONTACT_ID;

    private static final String CONTACTS_ID = ContactsContract.Contacts._ID;
    private static final Uri CONTACTS_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

    private static final String STRUCTURED_POSTAL_CONTENT_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;
    private static final String STRUCTURED_POSTAL_FORMATTED_ADDRESS = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;

    private static final int PICK_CONTACT_REQUEST = 0;
    static String TAG = "MapLocationMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() The activity is initializing.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.mapButton);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Create Intent object for picking data from Contacts database
                    Intent intent = new Intent(Intent.ACTION_PICK, CONTACTS_CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(data.getData(), null, null, null, null);

            if (null != cursor && cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(CONTACTS_ID));
                String where = DATA_CONTACT_ID + " = ? AND " + DATA_MIMETYPE + " = ?";
                String[] whereParameters = new String[] {id, STRUCTURED_POSTAL_CONTENT_ITEM_TYPE};
                Cursor addCur = contentResolver.query(DATA_CONTENT_URI, null, where,
                        whereParameters, null);
                if (null != addCur && addCur.moveToFirst()) {
                    String formattedAddress = addCur.getString(addCur
                            .getColumnIndex(STRUCTURED_POSTAL_FORMATTED_ADDRESS));
                    if (null != formattedAddress) {
                        formattedAddress = formattedAddress.replace(' ', '+');
                        Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("geo:0,0?q=" + formattedAddress));
                        startActivity(geoIntent);
                    }
                }
                if (null != addCur) {
                    addCur.close();
                }
            }
            if (null != cursor) {
                cursor.close();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart() The activity is about to be restarted.");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart() The activity is about to become visible.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume() The activity has become visible (it is now \"resumed\")");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,
                "onPause() Another activity is taking focus (this activity is about to be \"paused\")");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop() The activity is no longer visible (it is now \"stopped\")");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy() The activity is about to be destroyed.");
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
