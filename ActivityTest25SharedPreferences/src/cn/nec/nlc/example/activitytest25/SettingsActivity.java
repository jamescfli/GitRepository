package cn.nec.nlc.example.activitytest25;

import cn.nec.nlc.example.sharedpreftest1.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
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
	
	public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.mypreferences);
        }
    }
	
}