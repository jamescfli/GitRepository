package cn.nec.nlc.example.jamesli.activitytest53preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

// suggest to use PreferenceFragment to control the display of your settings instead of
// PreferenceActivity when possible.
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
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
