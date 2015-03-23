package name.bagi.levente.pedometer.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import name.bagi.levente.pedometer.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // addPreferencesFromResource was deprecated in PreferenceActivity
        addPreferencesFromResource(R.xml.preferences);
    }
}
