package cn.nec.nlc.example.activitytest28loader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

// Loader class allow you to load data asynchronously in an activity or fragment. 
// They can monitor the source of the data and deliver new results when the content changes. 
// They also persist data between configuration changes.
// If the result is retrieved by the Loader after the object has been disconnected from its 
// parent (activity or fragment), it can cache the data.
public class MainActivity extends Activity 
	implements LoaderManager.LoaderCallbacks<SharedPreferences> {
	private static final String KEY = "prefs";
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.prefs);
		// initLoader(): 
		// The first parameter is a unique ID which can be used by the callback 
		// class to identify that Loader later. 
		// The second parameter is a bundle which can be given to the callback class 
		// for more information.
		// The third parameter of initLoader() is the class which is called once the 
		// initialization has been started (callback class). This class must implement 
		// the LoaderManager.LoaderCallbacks interface.
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public Loader<SharedPreferences> onCreateLoader(int id, Bundle args) {
		return (new SharedPreferencesLoader(this));
	}
	
	@SuppressLint("CommitPrefEdits")
	@Override
	public void onLoadFinished(Loader<SharedPreferences> loader,
			SharedPreferences prefs) {
		int value = prefs.getInt(KEY, 0); // Key and Default value
		value += 1;
		textView.setText(String.valueOf(value));
		// update value
		SharedPreferences.Editor editor = prefs.edit(); // @SuppressLint("CommitPrefEdits")
		editor.putInt(KEY, value);
		SharedPreferencesLoader.persist(editor);
	}

	@Override
	public void onLoaderReset(Loader<SharedPreferences> arg0) {
		// not in use
	}
}
