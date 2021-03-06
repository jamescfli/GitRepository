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
        + builder.setMessage(“Dialog message shown”)
        + builder.setCancelable(true/false)
        + builder.setPositiveButton(“I agree”, new OkOnClickListener());
        + builder.setNegativeButton(“No, no”, new CancelOnClickListener());
        + AlertDialog dialog = builder.create();
        + dialog.show();
    * both OkOnClickListener and CancelOnClickListener implements DialogInterface.OnClickListener interface

11. ContentProviderTest03SQLiteBasics
    * wrapper SQLiteOpenHelper: database create() execSQL(), onCreate() and onUpgrade()
    * DAO - Data Access Object - DataSource: SQLiteDataBase, SQLiteOpenHelper
    * ContentValues
    * cursor.moveToFirst(), cursor.isAfterLast(), cursor.moveToNext(), cursor.close(), cursor.getLong(), cursor.getString()
    * database.query(), database.delete(), database.insert()
    * ArrayAdapter, ListView.setAdapter(), Adapter.add(), Adapter.notifyDataSetChanged()

12. ContentProviderTest04Contacts
    * uses-permission android:name="android.permission.READ_CONTACTS"
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

16. ActivityTest26ThreadProgBar
    * ProgressBar in layout: style="?android:attr/progressBarStyleHorizontal"
    * ProgressBar.post(new Runnable), .setProgress(int), setMax in layout file
    * new Thread(runnable).start(), Thread.sleep(long inMilliSecs)

17. ActivityTest27AsyncTask
    * AsyncTask <TypeOfVarArgParams , ProgressValue , ResultValue>
    * doInBackground(TypeOfVarArgParams), onPostExecute(ProgressValue , ResultValue)
    * DefaultHttpClient client = new DefaultHttpClient();
    * HttpGet httpGet = new HttpGet(url);
    * HttpResponse execute = client.execute(httpGet);
    * InputStream content = execute.getEntity().getContent();
    * BufferedReader buffer = new BufferedReader(new InputStreamReader(content));

18. ActivityTest28Loader
    * Activity.getLoaderManager().initLoader(0, null, this);
    * Interface LoaderManager.LoaderCallbacks<SharedPreferences> onCreateLoader(), onLoadFinished(), onLoaderReset()
    * SharedPreferences.Editor .getInt(KEY, DEFAULT_VALUE), putInt(KEY, VALUE), SharedPreferencesLoader.persist(editor)
    * AsyncTaskLoader<SharedPreferences>, onContentChanged(), deliverResult(), forceLoad()
    * SharedPreferences.OnSharedPreferenceChangeListener loadInBackground(), onSharedPreferenceChanged(), onStartLoading()

19. ActivityTest29ImageLoader
    * ImageView.setImageBitmap()
    * ProgressDialog.dismiss(), show(), isShowing()
    * Thread.isAlive(), start(), sleep()
    * HttpEntity entity = response.getEntity(); byte[] bytes = EntityUtils.toByteArray(entity); 
    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

20. ActivityTest30JSON
    * StrictMode.ThreadPolicy.Builder().permitAll().build() = policy
    * StrictMode.setThreadPolicy(policy)
    * JSONObject.put(KEY, VALUE)
    * StringBuilder.append()
    * HttpClient = new DefaultHttpClient(), HttpGet = new HttpGet(url) -> HttpResponse -> StatusLine -> StatusCode -> 200
    * HttpResponse.getEntity() -> HttpEntity.getContent() -> InputStream => BufferedReader = new BufferedReader(new InputStreamReader(content)) -> line = reader.readLine() != null

21. GradleTest01~08 for Gradle practices

22. ActivityTest31DragDrop

23. ActivityTest09CompassOrientation - Sensor Test
    * XiaoMi Acc = 100, Mag = 50, Gyro = 100
    * T1 Acc = 120, Mag = 60, Gyro = 200

24. ActivityTest32Drawable
    * AssetManager, Activity.getAssets() from assets folder
    * InputStream open = AssetManager#open("apple.png");
    * Bitmap bitmap = BitmapFactory.decodeStream(open);
    * ImageView#setImageBitmap(bitmap)
    * InputStream#close()
    * Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.apple) from drawable folder

25. ActivityTest33CumtomDrawable

26. WidgetTest01RandomNumber

27. WidgetTest02UpdateWithService

28. ServiceTest02DownloadFile
    * IntentService, #onHandleIntent()
    * Environment.getExternalStorageDirectory()
    * URL#openConnection().getInputStream()
    * sendBroadcast(intent)
    * BroadcastReceiver @Override onReceive(), registerReceiver(BroadcastReceiver, IntentFilter) in onResume(), and unregisterReceiver(BroadcastReceiver) in onPause()

29. ServiceTest03BindLocalService
    * bindService(Intent, ServiceConnection, flags), unbindService(ServiceConnection)
    * access service by Service's public methods after defined in ServiceConnection'

30. ServiceTest04JobScheduler

31. BroadcastReceiverTest01DisplayCallNum
    * extends BroadcastReceiver, onReceive()
    * intent.getExtras().getString(TelephonyManager.EXTRA_STATE)
    * if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)), then String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

32. BroadcastReceiverTest02AlarmManager
    * with issue on vibrating for multiple times

33. NotificationTest01Builder
    * Notification n  = new Notification.Builder(this)
        .setContentTitle("New mail from " + "test@gmail.com")
        .setContentText("Subject")
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentIntent(pIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_launcher, "Call", pIntent)
        .addAction(R.drawable.ic_launcher, "More", pIntent)
        .addAction(R.drawable.ic_launcher, "And more", pIntent).build();
    * NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    * notificationManager.notify(0, n);

34. TestProject02Activity, TestProject02LayoutTest and TestProject02FunctionTest
    * JUnit example, test layout

35. TestProject03Async, TestProject03AsyncTest
    * JUnit, test AsyncTask

36. ActivityTest34SensSampleRate
    * Monitor sensors (accelerometer, gyroscope and magnetic field) sampling rate on the mobile

37. ActivityTest36SimplePedometer

38. ActivityTest35ActTransAnimation

39. ActivityTest37ViewAnimation

40. ActivityTest38Network

41. ActivityTest39Storage

42. ActivityTest40Soundpool

43. ActivityTest41SoundRecording
<table sytle="border: 0px;">
<tr>
<td><img width="400px" src="image_in_png/mediarecorder_state_diagram.gif" /></td>
</tr>
</table>

44. ActivityTest42Storage
    * test SharedPreferences, get, edit and commit
    * example is from [Android Official Site - Storage](https://developer.android.com/guide/topics/providers/document-provider.html)
    * storage directory:
        + getFilesDir(): /data/data/cn.nec.nlc.example.jamesli.activitytest42storage/files
        + getExternalFilesDir():
            - Docs: /storage/sdcard/Android/data/cn.nec.nlc.example.jamesli.activitytest42storage/files/Documents
            - Movies: /storage/sdcard/Android/data/cn.nec.nlc.example.jamesli.activitytest42storage/files/Movies
            - Music: /storage/sdcard/Android/data/cn.nec.nlc.example.jamesli.activitytest42storage/files/Music
            - Pictures: /storage/sdcard/Android/data/cn.nec.nlc.example.jamesli.activitytest42storage/files/Pictures
            - DCIM: /storage/sdcard/Android/data/cn.nec.nlc.example.jamesli.activitytest42storage/files/DCIM

45. ActivityTest43Gyroscope
    * Note: the app does not work with Smartisan T1 in Android 4.4, the reason is defing timestamp as float will lead a round-up problem in "dT = (sensorEvent.timestamp - timestamp) * NS2S" i.e. dT = 0.0, the solution is to define private *long* timestamp
    * The same issue applies to ActivityTest47CanvasView

46. ActivityTest44RequestFile
    * basesd on ActivityTest17PickImageWithIntent
    * [Loading Large Bitmaps Efficiently](http://developer.android.com/training/displaying-bitmaps/load-bitmap.html)
    * handle OOM (OutOfMemoryError Exception) from getContentResolver().openInputStream(returnUri) by setting options.inJustDecodeBounds = true;

47. ActivityTest45ImageZoom

48. ActivityTest46Resource
    * see examples from Erik Hellman's book - [Android Programming - Pushing the limits](http://atibook.ir/dl/en/Engineering/Computer%20Science/9781118717370_android_programming.pdf)
    * note commen error [Resources$NotFoundException](http://blog.csdn.net/zhouyingge1104/article/details/39338271)

49. ActivityTest47CanvasView
    * lifecycle of android.view.View
<table sytle="border: 0px;">
<tr>
<td><img width="400px" src="image_in_png/Android_view_lifecycle.png" /></td>
</tr>
</table>
    * usage of invalidate()

50. BroadcastReceivertest03NetMonitor
    * monitor Wifi and Mobile network connectivity through BroadcastReceiver
    * more specifically, WifiManager and ConnectivityManager
    * remember to get use permission in manifest.xml
<table sytle="border: 0px;">
<tr>
    <td>uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"</td>
</tr>
<tr>
    <td>uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"</td>
</tr>
<tr>
    <td>uses-permission android:name="android.permission.ACCESS_WIFI_STATE"</td>
</tr>
<tr>
    <td>uses-permission android:name="android.permission.CHANGE_WIFI_STATE"</td>
</tr>
</table>
    * [How to disable/enable Mobile Data on Android?](http://stackoverflow.com/questions/13171623/how-to-turn-on-3g-mobile-data-programmatically-in-android)

51. ActivityTest48SendToOtherActivity
    * sendIntent.setAction(Intent.ACTION_SEND);
    * sendIntent.putExtra(Intent.EXTRA_TEXT, messageSent);
    * sendIntent.setType("text/plain");
    * startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_text_to)));
    * share action in your ActionBar is made even easier with the introduction of **ActionProvider** in Android 4.0 (API Level 14)

52. ActivityTest49ReceiveFromOtherActivity
    * receive Intent.ACTION_SEND with text/plain and image/jpeg, or Intent.ACTION_SEND_MULTIPLE with multiple images image/*

53. ServiceTest05IntentService
    * IntentService class provides a straightforward structure for running an operation on a single background thread, i.e. handling long-running operations without affecting your UI's responsiveness.
    * Activity that sends work requests to the service uses an explicit Intent, so no intent-filter is needed in manifest.xml.
    * reformed IntentService to bindService structure with less frequent gyroscope updates to UI

54. ActivityTest50FileProvider
    * [Sharing Files](https://developer.android.com/training/secure-file-sharing/index.html)
    * untested and unverified

55. ActivityTest51SpeechRecog
    * example from [Android Programming - Pushing the limits](http://atibook.ir/dl/en/Engineering/Computer%20Science/9781118717370_android_programming.pdf) by applying SpeechRecognizer
    * implement RecognitionListener and its methods, esp. onResults() and onPartialResults()
    * issue: it seems like a Chinese recognition on Smartisan T1

56. Notes
    * cloned from [XiaoMi notes](https://github.com/MiCode/Notes) open source code

57. ServiceTest06SensorSampleRate
    * use bind service to measure sampling rate of each sensor and update UI with several-second interval
    * LocalBroadcastManager.getInstance(this).registerReceiver(BroadcastReceiver, IntentFilter) or unregisterReceiver(BroadcastReceiver)
    * LocalBroadcastManager.getInstance(this).sendBroadcast()

58. ServiceTest07EulerA2Method
    * Compare diff btw Classical method and Euler A2 method
    * intent.putExtra(TAG, floatArray[]); bundle.getFloatArray(TAG);

59. ActivityTest52Fragment
    * Put titles of Shakespeare.TITLES in TitlesFragment and Shakespeare.DIALOGUE in DetailsFragment/DetailsActivity
    * layout with both portrait and landscape display

60. ActivityTest53Preference
    * use PreferenceActivity, ref: [Android Settings](http://developer.android.com/guide/topics/ui/settings.html#ReadingPrefs)
    * ListPreference, CheckBoxPreference, Customized Preference, SeekBarPreference
    * PreferenceCategory and PreferenceScreen to combine multiple Preferences are in preferences2.xml
    * OnSharedPreferenceChangeListener (onSharedPreferenceChanged() method) interface and its registration in SettingsFragment

61. ActivityTest54PrefHeader
    * test Android Studio default PreferenceActivity
    * test SeekBar Preference (customized)

62. ActivityTest55DialogFrag
    * extends DialogFragment, DialogFragment.show(FragmentManager manager, String tag)
    * AlertDialog.Builder, builder.setMessage().setPositiveButton().setNegativeButton(), builder.create()
    * set list dialog: builder.setTitle(), setItems(), getResources().getStringArray()
    * set multiple selection list with builder.setMultiChoiceItems()
    * customized sign-in dialog: access dialog views within onClick method, EditText editTextUn = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.username);
    * use standard callback methods in DialogFragment such that results from Dialog can be passed to host activity, note to verify implementation of the callback method in host activity, try "mListener = (FireMissilesDialogListener) activity;"

63. ActivityTest56Notification
    * NotificationCompat.Builder
    * TaskStackBuilder
    * PendingIntent
    * NotificationManager

64. ActivityTest57ToastCustomized
    * Toast.setView(layout)
    * new Toast(getApplicationContext()), note that getApplicationContext() is to get the application context, which is different from Activity.this (i.e. Activity's context). they have different lifecycle.
    * toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);    // gravity, x-offset, y-offset, CENTER_VERTICAL is to place object in the vertical center of its container, not changing its size

65. ActivityTest58MaterialDesign

66. ActivityTest59RecyclerView

67. Bagilevi-Pedometer
    * upload a simple Pedometer project forked from [Bagilevi Android Pedometer](https://github.com/bagilevi/android-pedometer)
    * add comments after reading the code
    * Listener declaration and implementation has been described in this [graph](https://www.lucidchart.com/documents/view/fd333791-ded7-4b29-b25d-1a5bfe591005)

68. ActivityTest60SpeakChinese
    * try TextToSpeech class
    * in init() test whether the language is supported or not int result = ttsEnglish.setLanguage(Locale.US); if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
    * set volume through AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE); int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC); am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
    * speak ttsEnglish.speak(TEXT_TO_SPEAK_ENG, TextToSpeech.QUEUE_FLUSH, null);
    * last but not least release the resource: ttsEnglish.shutdown();

69. ServiceTest08StartAndBind
    * test the difference between startService() and bindService()
    * lifecycles of Service and Activity, and what happens when using the two methods together
    * details can be found in [here](https://www.lucidchart.com/publicSegments/view/551bb1e4-d1b8-48a1-b38b-40a20a00560a/image.png)
<table sytle="border: 0px;">
<tr>
<td><img width="800px" src="https://www.lucidchart.com/publicSegments/view/551bb1e4-d1b8-48a1-b38b-40a20a00560a/image.png" /></td>
</tr>
</table>
    * remember to unbindService in onDestroy() otherwise the app will leak ServiceConnection out (potential accumulated memory leakage)

        if (localService != null) {
            unbindService(mConnection);
        }

    after all tests, the best practice is still to check whether localService (initiated in onServiceConnected() with localService = myBinderLocalService.getService()) is null or not
    * in [Android document](http://developer.android.com/guide/components/bound-services.html) however, bound flag is suggested:

        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

70. ActivityTest61SensorList
    * SensorManager.getSensorList(Sensor.TYPE_ALL) returns List<Sensor>

71. ActivityTest62RotationVector
    * derive rotation vector (Euler Angles) by 
        - Sensor.TYPE_ROTATION_VECTOR, which seems to use all three sensors (Acc for gravity, Gyro, mainly magnetic filed), according to [this link](http://stackoverflow.com/questions/7027589/which-sensors-used-for-sensor-type-rotation-vector)
        - Magnetic field based Sensor Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR
        - and derived Magnetic field RV with getRotationMatrix() method
    * it seems Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR does not work since sensorEvent.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR was never TRUE in run time

72. ActivityTest63DynamicPlot
    * use Observer to update XYPlot dynamically
    * some further decoration to the plot
    * result:
<table sytle="border: 0px;">
    <tr>
        <td><img width="400px" src="image_in_png/activitytest63xyplot.png" /></td>
    </tr>
</table>

73. ActivityTest64Animation
    * some animation examples to a textview
    * source from the [CSDN instruction](http://blog.csdn.net/guolin_blog/article/details/43536355)
    * effects are Opacity/Alpha, Rotation, Move Out, Combo, Add AnimatorListenerAdapter, and finally Animation through xml file.

74. ActivityTest65
    * SIM card reader for phone number and serial number

75. ActivityTest66
    * Display multiple images through LruCache
    * Andriod Example: [description](http://developer.android.com/training/displaying-bitmaps/index.html) and [code](http://developer.android.com/downloads/samples/DisplayingBitmaps.zip)

76. ActivityTest67AIDLServer + ActivityTest67AIDLClient

77. AidlServer + AidlClient
    * cloned from [manishkpr/Android-AIDL-Example](https://github.com/manishkpr/Android-AIDL-Example)

78. ActivityTest68DisplayBitmaps
    * show image according to requested width and height
    * load images off the UI thread
    * solve the concurrency issue if using listview / gridview
    * reference project could be [marbarfa/ML-Example1](https://github.com/marbarfa/ML-Example1)
    * note the unit of 'cacheSize' for LruCache<String, Bitmap> is in Bytes rather than KB or MB
    * It is better to use ActivityManager#getMemoryClass() to get the memeory size of the activity in MB rather than Runtime
    * Cache on disk was unfinished on 2015/04/24

79. ActivityTest69UITest
    * Layout test
    * onepage/twopanes
    * UIs

80. ActivityTest70CustomView

81. ActivityTest71MaterialDesign
    * test RecyclerView + CardView
    * vector drawable
    * legacy API support

82. AT74ScheduleRepeatAlarm
    * test getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); or clearFlags()
    * use WakeLock to programmatically keep or release your CPU
    * test MyWakefulReceiver to keep your CPU on when handling through IntentService
    * test SampleAlarmReceiver to test scheduled alarm which starts service on scheduled time
    * implant SampleBootReceiver to the current package such that the alarm will be reactivated after reboot
    * wrap BootReceiver in ComponentName before adding to pacakge manager
    * use WakefulBroadcastReceiver to start wakefulService to prevent CPU from sleeping

83. AT75MapLocationContact
    * example from Coursera to test startActivityForResult()
    * test proguard from bundle.gradle minifyEnabled true

84. ModernArtUI
    * Coursera Android Part I, miniproject, Modern Art UI
    * getBackgroundColor of a View by ((ColorDrawable) aView.getBackground()).getColor();

85. AT77DisplayBitmaps
    * one of the Android examples

86. AT78PlayActionBar
    * let action bar listens to scrolling event and automatically hide itself if scrolling down, like Zhihu.com
    * view tree observer is used to register listeners that can be notified of global changes in the view tree, e.g. 'scrollView.getViewTreeObserver().addOnScrollChangedListener'

87. ReadSMS project
    * com.sonyericsson.readsms from one of Erik Hellman's reflection examples
    * revise the method of fetching cursor by managedQuery, to apply LoaderManager with multiple callbacks, introduced by [Understanding the LoaderManager - Alex Lockwood](http://www.androiddesignpatterns.com/2012/07/understanding-loadermanager.html)

88. AT80ContentResolver
    * to test ContentResolver (a wrapper for ContentProvider) and ContentValues for insert

89. CPT06ImageSample (CPT06LentItemMemo)
    * test ContentProivder (DbSchema, OpenHelper), ImageLoader, Fragment with ActionBar, Spanned, IntentService and etc.

90. AT81ImageLoader
    * GridFragment, ListFragment, GalleryFragment, and PagerFragment
    * Complex combination of Grid and List in ViewPagers
    * Universa Image Loader from [GitHub](https://github.com/nostra13/Android-Universal-Image-Loader)
    * ViewPager.setAdapter(new FragmentPagerAdapter(FragmentManager))
    * Scrolling, Fling pause for uploading in Menu options

91. AT82EventBusDemo
    * gradle for EventBus and Apache StringUtils
        - compile "org.apache.commons:commons-lang3:3.3.2"
        - compile 'de.greenrobot:eventbus:2.4.0'
    * EventBus.getDefault().register(this); in onStart(), EventBus.getDefault().unregister(this) in onStop()
    * EventBus.getDefault().post(some message);
    * EventBus.getDefault().postSticky(some message), registerSticky(this), and unregister(this)

92. AT83Threading
    * coursera Android course examples in [here](https://github.com/aporter/coursera-android/tree/master/Examples/ThreadingHandlerRunnable)
    * post Runnable through main UI thread's handler
    * coursera Android course examples in [here](https://github.com/aporter/coursera-android/blob/master/Examples/ThreadingHandlerMessages/src/course/examples/threading/threadinghandlermessages/HandlerMessagesActivity.java)
    * send UI updates through handler messages to UI thread
    * revise the message passing mechanism by EventBus in AT82EventBusDemo

93. AT84Network
    * project does not work since
        - Unable to resolve host "api.geonames.org"
        - on line: socket = new Socket(HOST, 80)
    * Permission has been granted
        - <uses-permission android:name="android.permission.INTERNET" />
        - <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    * Problem solved: does not turn WiFi or other internet connection on :p

94. AT85Notification
    * test customized notification bar
    * Problem: the sound "alarm_rooster.mp3" does not work properly


95. AT86

96. AT87CompassBySensor
    * apply onSizeChanged in custom view to control the position mParentCenterX/Y and
    * shrink Bitmap size by Bitmap.createScaledBitmap (Bitmap src, int dstWidth, int dstHeight, boolean filter)
    * fix screen orientation by android:screenOrientation="portrait" in manifest.xml activity

97. AT88SurfaceView
    * test SurfaceHolder.Callback
    * start new thread to update SurfaceView
    * reset the whole BubbleView in reset menu, BubbleView thread would not be simultaneously reset from the main thread

98. AT89GreenDaoTest
    * test the example from [Github GreenDAO](https://github.com/greenrobot/greenDAO/tree/master/DaoExample)
    * Subsittute SimpleCursorAdapter with LoaderManager with a CursorLoader
    * Substitute cursor.requery() with new requery
    * test some SQLite3 command, database saved on /data/data/cn.jamesli.example.at89greendaotest/databases/. which is not accessible by users connected through adb to a handset device (but it works on emulator-5554)
    * Revise ListView adapter by Loader and LoaderManager such that all database access operation (time-consuming) will be run in the background (not in the UI thread)
    * Put DAO into loader
    * Apply CursorLoader to make the loader even simpler

99. AT90AppListLoader
    * test [AppListLoader project](https://github.com/alexjlockwood/AppListLoader)

100. AT91HttpRequest
    * test [OkHttp](http://square.github.io/okhttp/)
        - compile 'com.squareup.okhttp:okhttp:2.4.0'
        - compile 'com.squareup.okio:okio:1.5.0' (required at runtime)
        - not run in the main thread
    * test POST method by OkHttp
    * test [Android Asynchronous Http Client](http://loopj.com/android-async-http/)
    * Solve problem of TextView content change when screen rotates from portrait to landscape

101. AT92BaiduMapTest
    * in BitBucket repository due to API key in Gradle
    * finally revise the customized dialog map setting through DialogFragment, which borrows the idea from AT94CustomizedAlertDialog and [Android UI Intro on Dialog](http://developer.android.com/guide/topics/ui/dialogs.html)
    * rewrite setContentView(R.id.) by LayoutInflater with setContentView(View) instead

102. AT93RoboGuiceTest
    * import jars as indicated by [Downloading and Installing](https://github.com/roboguice/roboguice/wiki/Installation)
        - compile 'org.roboguice:roboguice:3.+'
        - provided 'org.roboguice:roboblender:3.+'
        - compile 'com.google.code.findbugs:jsr305:1.3.9' for the optional Nullable annotation
    * still not solved, but identify the problem to the compiling library by reverting the problem to one single injection
    * Compiling issue solved: sometimes android studio couldn't install dependencies. When I got this problem , I use gradlew on console with assembleDebug, i.e. "./gradlew assembleDebug".
    * Issue: [java.lang.NullPointerException](https://github.com/roboguice/roboguice/issues/273) for Injections in CustomView. Work around at the moment by retrieving conventional findViewById()
    * Issue solved by [this link](http://stackoverflow.com/questions/30706138/cannot-inject-view-to-custom-class-with-roboguice): add RoboGuice.injectMembers(getContext(), this) and RoboGuice.getInjector(getContext()).injectViewMembers(this) in the view initialization
    * Detailed explanation on injection timing can be found in [this link](https://github.com/stephanenicolas/injectview/wiki/Injection-inside-Views)

103. AT94CustomizedAlertDialog
    * Simple Dialog
    * Customized xml Dialog
    * Date Picker Dialog
    * Notice Dialog by extending DialogFragment

104. AT95ImageCropper
    * test on android-crop, a image cropping activity from [Android Crop](https://github.com/jdamcd/android-crop) on GitHub
    * Solved gradle compile problem by running build through command line, i.e. ./gradlew to install Maven dependencies.

105. TrivialTest
    * to differentiate onTouch and onClick
    * onClick includes ACTION_DOWN and ACTION_UP, but those two actions does not necessarily lead to a Click (could be a swipe or slide)
    * if return true from onTouch, the click action will be omitted by View.OnClickListener after the touch
    * if return false, the onClick() will be initiated after the ACTION_UP
    * file storage, permission, internal and external, PrintWriter BufferedWriter and FileWriter, filename with Date and Time, local time by Calendar
    * test ViewStub in existing view and layout
    * set filename with local date and time

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = "myFile_" + dateFormat.format(date) + ".dat";
        File file = new File(<path>, <filename>);

106. AT96GoogleLocation
    * Use Google Play services location APIs, rather than Android framework location APIs (android.location)
    * Similar example can be found in this [link](https://github.com/googlesamples/android-play-location/blob/master/BasicLocationSample/app/src/main/java/com/google/android/gms/location/sample/basiclocationsample/MainActivity.java)
    * Connection ErrorCode = 1, due to Google Service accessibility

107. AT97SwipeRefreshLayout
    * from [link](https://developer.android.com/samples/SwipeRefreshLayoutBasic/project.html) to test Swipe Refresh
    * Generate random cheese list with fake delay for 3 seconds
    * add FAB - FloatingActionButton - to refresh the cheese list, note that SwipeRefreshLayout can only have a single child view, so use one single FrameLayout to contain both the listview and the fab

108. AT98FabTest
    * test both [shamanland](https://github.com/shamanland/floating-action-button) and [melnykov](https://github.com/makovkastar/FloatingActionButton) FAB - Floating Action Button

109. AT99SwipeRefreshWithGridview
    * same with AT97SwipeRefreshLayout but apply GridView instead

110. BT01ActionBarCompat
    * Add action bar items through menu.xml and manually in onCreateOptionsMenu()
    * Add PopupMenu to listItem
    * Show and Hide action bar when scrolling down and up the list view. Note: use onScrollStateChanged() in AbsListView.OnScrollListener(), rather than onScroll() method, to achieve more reasonable effects.

111. BT02BLEibrary
    * Study [Bluetooth-LE-Library-Android](https://github.com/alt236/Bluetooth-LE-Library---Android) with iBeacon nodes
    * Revise share to save the scan result in external cache directory
    * Add new TimeFormatter for saving files with Date and Time as file name

112. BT03EasyCursorTest
    * source from [EasyCursor](https://github.com/alt236/EasyCursor---Android)
    * wrap JSONArray with EasyJsonCursor and further with JsonLoader extending from com.commonsware.cwac.loaderex.acl.AbstractCursorLoader
    * AbstractCursorLoader requires [cwac-loaderex.jar](https://github.com/commonsguy/cwac-loaderex)
    * inputStream.available() returns an estimated number of bytes can be read or skipped without blocking for more input. o.w. one can use BufferStream to read the whole piece line by line with StringBuilder.
    * translate json to list by Gson tool, and wrap list to cursor with Loader in EasyObjectCursorExampleActivity
    * access item of EasyObjectCursor through getObject(String nameOfColumn)
    * start write integration test AndroidTestCase and use Assert.assertTrue .assertEquals

113. BT04SuperSwipeRefresh
    * to test [SuperSwipeRefreshLayout](https://github.com/nuptboyzhb/SuperSwipeRefreshLayout) suggested by Trinea
    * RecyclerView, ListView and ScrollView
    * Apply RecyclerView to decouple list from it's container

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // or
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        // or water-fall gridlayout
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

114. BT05AndroidSwipeLayout
    * try [Android Swipe Layout](https://github.com/daimajia/AndroidSwipeLayout)

115. BT06iBeaconTx
    * source [ALT Beacon Transmitter Android](https://github.com/AltBeacon/altbeacon-transmitter-android)
    * javadoc at [here](http://altbeacon.github.io/android-beacon-library/javadoc/)
    * Analysis on iBeacon ads header and payload
    <table sytle="border: 0px;">
    <tr>
    <td><img width="300px" src="image_in_png/iBeaconFrameStructure.png" /></td>
    </tr>
    </table>

116. BT07iBeaconScanner

117. BT08iBeaonRx
    * receive AltBeacon and save it to external cache as csv files

118. BT09iBeaconMonitoringAndRanging
    * get Monitoring and Ranging worked

119. BT10iBeaconTxRx
    * Try to accomodate Tx and Rx into one App
    * Change Rx from Activity to Fragment, found HostActivity
    * Add LDrawer to HostActivity
    * Add Tx to TxFragment
    * Add Wifi Tx and Rx for further field test, by Java reflection
    * Revise App name accordingly to BT10BeaconWifiTxRx

120. BT11LDrawerTest
    * Try LDrawer with nice animation on the action bar home button when opening drawer on the left

121. BT12StaggeredGrid
    * Learn [Android Staggered Grid](https://github.com/etsy/AndroidStaggeredGrid)