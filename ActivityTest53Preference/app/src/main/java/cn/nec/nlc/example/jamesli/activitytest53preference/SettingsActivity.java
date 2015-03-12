package cn.nec.nlc.example.jamesli.activitytest53preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    // for preferences1.xml
    public static final String KEY_PREF_SYNC_CONN = "pref_sync";
    public static final String KEY_PREF_SYNC_CONN_TYPE = "pref_syncConnectionType";
    // for preferences2.xml
    public static final String PREF_KEY_AUTO_DELETE = "pref_key_auto_delete";
    public static final String PREF_KEY_SMS_DELETE_LIMIT = "pref_key_sms_delete_limit";
    public static final String PREF_KEY_MMS_DELETE_LIMIT = "pref_key_mms_delete_limit";



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preferences1);
        addPreferencesFromResource(R.xml.preferences2);
    }
}