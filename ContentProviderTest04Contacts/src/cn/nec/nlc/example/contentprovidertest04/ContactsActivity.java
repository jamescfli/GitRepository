package cn.nec.nlc.example.contentprovidertest04;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;

public class ContactsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		TextView contactView = (TextView) findViewById(R.id.contactview);
		
		Cursor cursor = getContacts();	// get all contacts
		
		while (cursor.moveToNext()) {
			// ContactsContract: contract between the contacts provider and applications
			String displayName = cursor.getString(cursor.getColumnIndex
					(ContactsContract.Data.DISPLAY_NAME));
			contactView.append("Name: ");
			contactView.append(displayName);
			contactView.append("\n");
		}
	}

	@SuppressWarnings("deprecation")
	private Cursor getContacts() {
		// run query
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID, 
				ContactsContract.Contacts.DISPLAY_NAME };
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
		        + ("1") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME 
				+ " COLLATE LOCALIZED ASC";
		
		return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
	}

}
