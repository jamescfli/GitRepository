package cn.nec.nlc.example.activitytest32drawable;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// method 1 - load png image from assets folder
		// Get the AssetManager
	    AssetManager manager = getAssets();
	    // read a Bitmap from Assets
	    InputStream open = null;
	    try {
	    	open = manager.open("apple.png");
	    	Bitmap bitmap = BitmapFactory.decodeStream(open);
	    	// Assign the bitmap to an ImageView in this layout
	    	ImageView view = (ImageView) findViewById(R.id.imageView1);
	    	view.setImageBitmap(bitmap);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } finally {
	    	if (open != null) {
	    		try {
	    			open.close();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    } 
	    
//		// method 2 - load png image from res/drawable folder
//	    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
//    	// Assign the bitmap to an ImageView in this layout
//    	ImageView view = (ImageView) findViewById(R.id.imageView1);
//    	view.setImageBitmap(bitmap);
	}

}
