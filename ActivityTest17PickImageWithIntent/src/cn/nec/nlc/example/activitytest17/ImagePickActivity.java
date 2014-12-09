package cn.nec.nlc.example.activitytest17;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class ImagePickActivity extends Activity {
	private static final int REQUEST_CODE = 1;
	private Bitmap bitmap;
	private ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.result);
	}

	public void pickImage(View view) {
		Intent intent = new Intent();
		// ACTION_GET_CONTENT with MIME type */* and category CATEGORY_OPENABLE
		// -- Display all pickers for data that can be opened with 
		// ContentResolver.openInputStream(), allowing the user to pick one of 
		// them and then some data inside of it and returning the resulting URI 
		// to the caller. This can be used, for example, in an e-mail application
		// to allow the user to pick some data to include as an attachment.
		intent.setType("image/*"); // set an explicit MIME data type.
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		InputStream stream = null;
		if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
			try {
				// recycle unused bitmaps
				if (bitmap != null)
					bitmap.recycle();
				stream = getContentResolver().openInputStream(data.getData());
				bitmap = BitmapFactory.decodeStream(stream);
				imageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (stream !=  null)
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
	}
}
