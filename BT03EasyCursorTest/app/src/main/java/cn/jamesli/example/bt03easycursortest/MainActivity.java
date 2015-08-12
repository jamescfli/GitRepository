package cn.jamesli.example.bt03easycursortest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onEasyJsonCursorExampleClick(final View v) {
        startActivity(new Intent(this, EasyJsonCursorExampleActivity.class));
    }

    public void onEasyObjectCursorExampleClick(final View v) {
        startActivity(new Intent(this, EasyObjectCursorExampleActivity.class));
    }

    public void onEasySqlCursorExampleClick(final View v) {
//        startActivity(new Intent(this, EasySqlCursorExampleActivity.class));
    }
}
