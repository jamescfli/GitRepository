package cn.nec.nlc.example.jamesli.activitytest54prefheader;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

import java.util.List;

public class PreferenceWithHeaders extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // add a button to the header list
        if (hasHeaders()) {
            Button button = new Button(this);
            button.setText("Some Action");
            setListFooter(button);
        }
    }

    // populate the activity with the top-level headers

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    // this fragment shows the preferences for the first header
    public static class Prefs1Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Make sure default values are applied.  In a real app, you would
            // want this in a shared function that is used to retrieve the
            // SharedPreferences wherever they are needed.
            PreferenceManager.setDefaultValues(getActivity(), R.xml.advanced_preferences, false);
              // true if set back all to default value
            // load the preferences from xml resource
            addPreferencesFromResource(R.xml.fragmented_preferences);
        }
    }

    // this fragment contains a second-level set of preference that you can get to by tapping
    // an item in the first preference fragment
    public static class Prefs1FragmentInner extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // can retrieve arguments from preference xml
            Log.i("args", "Arguments: " + getArguments());
            // load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_preferences_inner);

        }
    }

    // this fragment shows the preferences for the second header
    public static class Prefs2Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // can retrieve arguments from preference xml
            Log.i("args", "Arguments: " + getArguments());
            // load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_dependencies);
        }
    }
}

