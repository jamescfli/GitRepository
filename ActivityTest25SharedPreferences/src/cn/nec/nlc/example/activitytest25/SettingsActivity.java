package cn.nec.nlc.example.activitytest25;

import cn.nec.nlc.example.sharedpreftest1.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		// note: addPreferencesFromResource is deprecated
//		addPreferencesFromResource(R.xml.mypreferences);
//		// .. we could use PreferenceFragment instead, addPreferencesFromResource
//		// is not deprecated there.
//	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace
        	(android.R.id.content, new MyPreferenceFragment()).commit();
    }
	
	public static class MyPreferenceFragment extends PreferenceFragment 
		implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.mypreferences);
            
            // show the current value in the settings screen
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            	initSummary(getPreferenceScreen().getPreference(i));
            }
        }
        
        @Override
        public void onResume() {
        	super.onResume();
        	getPreferenceScreen().getSharedPreferences()
        		.registerOnSharedPreferenceChangeListener(this);
        }
        
        @Override
        public void onPause() {
        	super.onPause();
        	getPreferenceScreen().getSharedPreferences()
    			.unregisterOnSharedPreferenceChangeListener(this);
        }
        
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
        		String key) {
        	updatePreferences(findPreference(key));
        }

		private void initSummary(Preference preference) {
			if (preference instanceof PreferenceCategory) {
				PreferenceCategory cat = (PreferenceCategory) preference;
				for (int i = 0; i < cat.getPreferenceCount(); i++) {
					initSummary(cat.getPreference(i));
				}
			} else {
				updatePreferences(preference);
			}
		}
		
		private void updatePreferences(Preference findPreference) {
			if (findPreference instanceof EditTextPreference) {
				EditTextPreference editTextPref = (EditTextPreference) findPreference;
				findPreference.setSummary(editTextPref.getText());
			}
		}
    }
	
}