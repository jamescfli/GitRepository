This repository is for all test Android projects, including:

1) ActivityTest15ExplicitIntent
    a) Activity.startActivityForResult() and Activity.onActivityResult()
    b) Intent.hasExtra(), Intent.getExtras(), Intent.putExtra()
    c) Bundle.getIntent().getExtras()
    d) home icon to go back to parent activity: android.R.id.home
    e) Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    f) ActionBar actionBar = Activity.getActionBar(), ActionBar.setHomeButtonEnabled(true)

2) ActivityTest16Browser
    a) StrictMode.ThreadPolicy.Builder() and StrictMode.setThreadPolicy(policy)
    b) Uri and URL
    c) WebView.loadUrl()

3) ActivityTest17PickImageWithIntent
    a) Intent.setType(), Intent.setAction(), Intent.addCategory(Intent.CATEGORY_OPENABLE);
    b) startActivityForResult() and onActivityResult()
    c) Bitmap.recycle()
    d) getContentResolver().openInputStream(Intent.getData())
    e) bitmap = BitmapFactory.decodeStream(stream)
    f) ImageView.setImageBitmap(bitmap)

4) ActivityTest18ImplicitIntent
    a) Spinner.setAdapter(adapter)
    b) adapter = ArrayAdapter.createFromResource(this, R.array.intents, android.R.layout.simple_spinner_item) and Adapter.setDropDownViewResource()
    c) Uri.parse(String) 
        Intent.ACTION_VIEW
        Intent.ACTION_CALL
        Intent.ACTION_DIAL
        Intent.ACTION_EDIT

5) ActivityTest19ActivityLifecycle
    a) adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values)
    spinner.setAdapter(adapter)
    b) Notification notification = new Notification.Builder(this)
        .setContentTitle(title line)
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentText(further description)
        .build();
    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    notificationManager.notify(id_of_notification, notification)

6) ActivityTest20ActionBarProg
    a) ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM
    b) MenuItem.setActionView(), expandActionView(), collapseActionView()
    c) AsyncTask<String, Void, String> doInBackground() onPostExecute()

7) ActivityTest21ShareActionProvider
    a) Menu.findItem().getActionProvider()
    b) ShareActionProvider.setShareIntent()
    c) Intent(Intent.ACTION_SEND) to Weibo or Wechat

8) ActivityTest22NavDrawer
    a) 

9) ActivityTest23LogUsage
    a) BuildConfig.DEBUG <=> public Boolean DEBUG = true/false(in release)
    b) Log.d(Constants.LOG_TAG, “text content”); where Constants is a public interface

10) ActivityTest24SimpleDialog
    a) Activity.showDialog(DIALOG_ID);
    b) Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(“Dialog message shown”)
	builder.setCancelable(true/false)
	builder.setPositiveButton(“I agree”, new OkOnClickListener());
	builder.setNegativeButton(“No, no”, new CancelOnClickListener());
	AlertDialog dialog = builder.create();
	dialog.show();
    c) both OkOnClickListener and CancelOnClickListener implements DialogInterface.OnClickListener interface

11) ContentProviderTest03SQLiteBasics
    a) wrapper SQLiteOpenHelper: database create() execSQL(), onCreate() and onUpgrade()
    b) DAO - Data Access Object - DataSource: SQLiteDataBase, SQLiteOpenHelper
    c) ContentValues
    d) cursor.moveToFirst(), cursor.isAfterLast(), cursor.moveToNext(), cursor.close(), cursor.getLong(), cursor.getString()
    e) database.query(), database.delete(), database.insert()
    f) ArrayAdapter, ListView.setAdapter(), Adapter.add(), Adapter.notifyDataSetChanged()

12) ContentProviderTest04Contacts
    a) <uses-permission android:name="android.permission.READ_CONTACTS" />
    b) Activity.getContacts()
    c) ContactsContract.Data.DISPLAY_NAME, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.IN_VISIBLE_GROUP, ContactsContract.Contacts.CONTENT_URI
    d) Activity.managedQuery() note: deprecated

13) ContentProviderTest05Todos
    a) ContentResolver
    b) ContentProvider.delete(), onCreate(), insert(), query(), update()
    c) UriMatcher.addURI(), match()
    d) throw new IllegalArgumentException() for switch(case) - default
    e) AdapterView.AdapterContextMenuInfo
    f) Loader, CursorLoader
    g) Activity.onSaveInstanceState()

14) XmlPullParserTest01
    a) factory = XmlPullParserFactory.newInstance(); factory.setNamespaceAware(true); XmlPullParser xpp = factory.newPullParser();
    b) xpp.setInput(new StringReader(“some String”));
    c) XmlPullParser.setInput(), .getEventType(), .next(), getName(), getText()
    d) XmlPullParser.START_DOCUMENT, .END_DOCUMENT, .START_TAG, .END_TAG, TEXT

15) ActivityTest25SharedPreferences
    a) res/xml/mypreferences.xml, addPreferencesFromResource(R.xml.mypreferences) in PreferenceFragment
    b) getFragmentManager().beginTransaction().replace().commit();
    c) SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    d) prefs.getString(<key>, <default value if failing to retrieve preferences>)
    e) PreferenceCategory, Preference.setKey(), .setTitle(), .setSummary()
    f) PreferenceFragment.getPreferenceScreen(), PreferenceGroup.getPreferenceCount(), Preference.getSharedPreferences()
    g) SharedPreferences.registerOnSharedPreferenceChangeListener(), .unregisterOnSharedPreferenceChangeListener()