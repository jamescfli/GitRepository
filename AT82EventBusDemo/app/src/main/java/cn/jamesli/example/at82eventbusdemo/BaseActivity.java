package cn.jamesli.example.at82eventbusdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Created by jamesli on 15-7-2.
 */
public class BaseActivity extends Activity {
    protected Context context;

    protected void onCreate(Bundle savedInstanceState, int layoutResId) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResId);
        context = getApplicationContext();
//        AppUtils.initActionBar(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return false;
    }
}
