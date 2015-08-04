package cn.jamesli.example.at97swiperefreshlayout;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import cn.jamesli.example.at97swiperefreshlayout.common.activities.SampleActivityBase;


public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (null == savedInstanceState) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SwipeRefreshLayoutBasicFragment fragment = SwipeRefreshLayoutBasicFragment.newInstance();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }

}
