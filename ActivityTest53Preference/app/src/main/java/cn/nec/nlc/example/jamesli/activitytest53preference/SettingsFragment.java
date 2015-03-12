package cn.nec.nlc.example.jamesli.activitytest53preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

// suggest to use PreferenceFragment to control the display of your settings instead of
// PreferenceActivity when possible.
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences1);
        ListPreference listPreference = (ListPreference) findPreference
                (SettingsActivity.KEY_PREF_SYNC_CONN_TYPE);
        listPreference.setSummary("Sync Conn Type: "
                + listPreference.getEntry() + " - " +listPreference.getValue());
    }

    @Override
    public void onResume() {
        super.onResume();
        // register in order to let onSharedPreferenceChanged() valid
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
          // Note: When you call registerOnSharedPreferenceChangeListener(), the preference manager
          // does not currently store a strong reference to the listener. You must store a strong
          // reference to the listener, or it will be susceptible to garbage collection. We
          // recommend you keep a reference to the listener in the instance data of an object that
          // will exist as long as you need the listener.
//        // For example, in the following code, the caller does not keep a reference to the listener.
//        // As a result, the listener will be subject to garbage collection, and it will fail at some
//        // indeterminate time in the future:
//        prefs.registerOnSharedPreferenceChangeListener(
//                // Bad! The listener is subject to garbage collection!
//                new SharedPreferences.OnSharedPreferenceChangeListener() {
//                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//                        // listener implementation
//                    }
//                });
//        // Instead, store a reference to the listener in an instance data field of an object that
//        // will exist as long as the listener is needed:
//        SharedPreferences.OnSharedPreferenceChangeListener listener =
//                new SharedPreferences.OnSharedPreferenceChangeListener() {
//                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//                        // listener implementation
//                    }
//                };
//        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    // Listener method was not initiated, must register the listener in the first place
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsActivity.KEY_PREF_SYNC_CONN_TYPE)) {
            ListPreference listPreference = (ListPreference) findPreference(key);
            listPreference.setSummary("Sync Conn Type: "
                    + listPreference.getEntry() + " - " +listPreference.getValue());
        }
    }
}
