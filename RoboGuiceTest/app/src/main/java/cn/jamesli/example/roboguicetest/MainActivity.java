package cn.jamesli.example.roboguicetest;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

//// Simple Example 1
//@ContentView(R.layout.activity_main)
//public class MainActivity extends RoboActivity {
//    @InjectView(R.id.textView) TextView textView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        textView.setText("Finally, it works!");
//    }
//}

// Simple Example 2
@ContentView(R.layout.main)
public class MainActivity extends RoboActivity {
    @InjectView(R.id.name)                  TextView name;
    @InjectView(R.id.thumbnail)             ImageView thumbnail;
    @InjectResource(R.mipmap.ic_launcher)   Drawable icLauncher;
    @InjectResource(R.string.app_name)      String myName;
    @Inject                                 LocationManager loc;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name.setText( "Hello, " + myName );
        thumbnail.setImageDrawable(icLauncher);
    }
}

//// without Injection
//public class MainActivity extends Activity {
//    TextView textView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        textView = (TextView) findViewById(R.id.textView);
//        textView.setText("Finally, it works!");
//    }
//}
