package cn.nec.nlc.example.contentprovidertest04;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;

public class ContactsActivity extends Activity {
    private static final String TAG = "ContactsActivity";

	@Override
    @SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Log.i(TAG, "onCreate");

        TextView contactView = (TextView) findViewById(R.id.contact_view);
		
//		Cursor cursor = getContactNames();	// get all contact names
//
//		while (cursor.moveToNext()) {
//			// ContactsContract: contract between the contacts provider and applications
//			String displayName = cursor.getString(cursor.getColumnIndex
//					(ContactsContract.Data.DISPLAY_NAME));
//			contactView.append("Name: ");
//			contactView.append(displayName);
//			contactView.append("\n");
//		}

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(Contacts.People.CONTENT_URI, null, null, null, null);

        String namesAndNumbers = getContactNameNumbers(cursor);
        contactView.setText(namesAndNumbers);
	}

	@SuppressWarnings("deprecation")
	private Cursor getContactNames() {
		// run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID, 
				ContactsContract.Contacts.DISPLAY_NAME };
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
		        + ("1") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME 
				+ " COLLATE LOCALIZED ASC"; // Ascending order
		
		return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
	}

    @SuppressWarnings("deprecation")
    public String getContactNameNumbers(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumn = cursor.getColumnIndex(Contacts.People.NAME);
            int phoneColumn = cursor.getColumnIndex(Contacts.People.NUMBER);

            StringBuilder returnNamesAndNumbers = new StringBuilder();

            do {
                returnNamesAndNumbers.append(cursor.getString(nameColumn) + " : "
                        + cursor.getString(phoneColumn) + "\n");
                // note the phone number part does not work
                Log.i(TAG, "Phone Number " + phoneColumn + ": " + cursor.getString(phoneColumn));
            } while (cursor.moveToNext());

            return returnNamesAndNumbers.toString();
        }

        return null;
    }
}
