This repository is for all test Android projects, including:

1) ActivityTest15ExplicitIntent
    a) Activity.startActivityForResult() and Activity.onActivityResult()
    b) Intent.hasExtra(), Intent.getExtras(), Intent.putExtra()
    c) Bundle.getIntent().getExtras()

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