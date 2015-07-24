package cn.jamesli.example.at93roboguicetest;

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

@ContentView(R.layout.main)
class MainActivity extends RoboActivity {
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.thumbnail)
    ImageView thumbnail;
    @InjectResource(R.mipmap.ic_launcher)
    Drawable icon;
    @InjectResource(R.string.app_name) String myName;
    @Inject
    LocationManager loc;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        name.setText( "Hello, " + myName );
    }
}
