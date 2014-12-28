## This repository is for all test Android projects, including:

1. ActivityTest15ExplicitIntent
    * Activity.startActivityForResult() and Activity.onActivityResult()
    * Intent.hasExtra(), Intent.getExtras(), Intent.putExtra()
    * Bundle.getIntent().getExtras()
    * home icon to go back to parent activity: android.R.id.home
    * Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    * ActionBar actionBar = Activity.getActionBar(), ActionBar.setHomeButtonEnabled(true)

2. ActivityTest16Browser
    * StrictMode.ThreadPolicy.Builder() and StrictMode.setThreadPolicy(policy)
    * Uri and URL
    * WebView.loadUrl()

3. ActivityTest17PickImageWithIntent
    * Intent.setType(), Intent.setAction(), Intent.addCategory(Intent.CATEGORY_OPENABLE);
    * startActivityForResult() and onActivityResult()
    * Bitmap.recycle()
    * getContentResolver().openInputStream(Intent.getData())
    * bitmap = BitmapFactory.decodeStream(stream)
    * ImageView.setImageBitmap(bitmap)

4. ActivityTest18ImplicitIntent
    * Spinner.setAdapter(adapter)
    * adapter = ArrayAdapter.createFromResource(this, R.array.intents, android.R.layout.simple_spinner_item) and Adapter.setDropDownViewResource()
    * Uri.parse(String) 
        Intent.ACTION_VIEW
        Intent.ACTION_CALL
        Intent.ACTION_DIAL
        Intent.ACTION_EDIT

5. ActivityTest19ActivityLifecycle
    * adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values)
    spinner.setAdapter(adapter)
    * Notification notification = new Notification.Builder(this)
        .setContentTitle(title line)
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentText(further description)
        .build();
    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    notificationManager.notify(id_of_notification, notification)

6. ActivityTest20ActionBarProg
    * ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM
    * MenuItem.setActionView(), expandActionView(), collapseActionView()
    * AsyncTask<String, Void, String> doInBackground() onPostExecute()

7. ActivityTest21ShareActionProvider
    * Menu.findItem().getActionProvider()
    * ShareActionProvider.setShareIntent()
    * Intent(Intent.ACTION_SEND) to Weibo or Wechat

8. ActivityTest22NavDrawer
    * 

9. ActivityTest23LogUsage
    * BuildConfig.DEBUG <=> public Boolean DEBUG = true/false(in release)
    * Log.d(Constants.LOG_TAG, “text content”); where Constants is a public interface

10. ActivityTest24SimpleDialog
    * Activity.showDialog(DIALOG_ID);
    * Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(“Dialog message shown”)
	builder.setCancelable(true/false)
	builder.setPositiveButton(“I agree”, new OkOnClickListener());
	builder.setNegativeButton(“No, no”, new CancelOnClickListener());
	AlertDialog dialog = builder.create();
	dialog.show();
    * both OkOnClickListener and CancelOnClickListener implements DialogInterface.OnClickListener interface

11. ContentProviderTest03SQLiteBasics
    * wrapper SQLiteOpenHelper: database create() execSQL(), onCreate() and onUpgrade()
    * DAO - Data Access Object - DataSource: SQLiteDataBase, SQLiteOpenHelper
    * ContentValues
    * cursor.moveToFirst(), cursor.isAfterLast(), cursor.moveToNext(), cursor.close(), cursor.getLong(), cursor.getString()
    * database.query(), database.delete(), database.insert()
    * ArrayAdapter, ListView.setAdapter(), Adapter.add(), Adapter.notifyDataSetChanged()

12. ContentProviderTest04Contacts
    * <uses-permission android:name="android.permission.READ_CONTACTS" />
    * Activity.getContacts()
    * ContactsContract.Data.DISPLAY_NAME, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.IN_VISIBLE_GROUP, ContactsContract.Contacts.CONTENT_URI
    * Activity.managedQuery() note: deprecated

13. ContentProviderTest05Todos
    * ContentResolver
    * ContentProvider.delete(), onCreate(), insert(), query(), update()
    * UriMatcher.addURI(), match()
    * throw new IllegalArgumentException() for switch(case) - default
    * AdapterView.AdapterContextMenuInfo
    * Loader, CursorLoader
    * Activity.onSaveInstanceState()

14. XmlPullParserTest01
    * factory = XmlPullParserFactory.newInstance(); factory.setNamespaceAware(true); XmlPullParser xpp = factory.newPullParser();
    * xpp.setInput(new StringReader(“some String”));
    * XmlPullParser.setInput(), .getEventType(), .next(), getName(), getText()
    * XmlPullParser.START_DOCUMENT, .END_DOCUMENT, .START_TAG, .END_TAG, TEXT

15. ActivityTest25SharedPreferences
    * res/xml/mypreferences.xml, addPreferencesFromResource(R.xml.mypreferences) in PreferenceFragment
    * getFragmentManager().beginTransaction().replace().commit();
    * SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    * prefs.getString(<key>, <default value if failing to retrieve preferences>)
    * PreferenceCategory, Preference.setKey(), .setTitle(), .setSummary()
    * PreferenceFragment.getPreferenceScreen(), PreferenceGroup.getPreferenceCount(), Preference.getSharedPreferences()
    * SharedPreferences.registerOnSharedPreferenceChangeListener(), .unregisterOnSharedPreferenceChangeListener()
    * writeConfiguration(), readFileFromInternalStorage(), readFileFromSDCard()