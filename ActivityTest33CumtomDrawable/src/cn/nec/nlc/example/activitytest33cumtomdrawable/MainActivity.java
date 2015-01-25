package cn.nec.nlc.example.activitytest33cumtomdrawable;

import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    ImageView button = (ImageView) findViewById(R.id.image);
	    InputStream resource = getResources().openRawResource(R.drawable.apple);
	    Bitmap bitmap = BitmapFactory.decodeStream(resource);
	    // set background as the given Drawable
	    button.setBackground(new MyRoundCornerDrawable(bitmap));
	}

} 