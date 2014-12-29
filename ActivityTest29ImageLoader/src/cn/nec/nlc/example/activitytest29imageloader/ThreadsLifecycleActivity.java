package cn.nec.nlc.example.activitytest29imageloader;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

public class ThreadsLifecycleActivity extends Activity {
	// make ProgDialog and Bitmap static so that the thread can access the latest attribute
	private static ProgressDialog dialog;
	private static Bitmap downloadBitmap;
	private static Handler handler;
	private ImageView imageView;
	private Thread downloadThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// create a handler to update UI
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				imageView.setImageBitmap(downloadBitmap);
				dialog.dismiss(); // Dismiss this dialog, removing it from the screen.
			}
		};
		// get the latest imageView after restart of the application
		imageView = (ImageView) findViewById(R.id.imageView1);
		Context context = imageView.getContext();
		System.out.println(context);
		// Did we already download the image?
		if (downloadBitmap != null) {
			imageView.setImageBitmap(downloadBitmap);
		}
		// check if the thread is already running
		downloadThread = (Thread) getLastNonConfigurationInstance();
		if (downloadThread != null && downloadThread.isAlive()) {
			// show(): start the dialog and display it on screen.
			dialog = ProgressDialog.show(this, "Download", "downloading");
		}
	}

	public void resetPicture(View view) {
		if (downloadBitmap != null) {
			downloadBitmap = null;
		}
		imageView.setImageResource(R.drawable.ic_launcher);
	}
	
	public void downloadPicture(View view) {
		dialog = ProgressDialog.show(this, "Download", "downloading");
		downloadThread = new MyThread();
		downloadThread.start();
	}
	
	// save the thread
	public Object onRetainNonConfigurationInstantce() {
		return downloadThread;
	}
	
	// dismiss dialog if activity is destroyed
	@Override
	protected void onDestroy() {
		if (dialog !=  null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		super.onDestroy();
	}
	
	// Utility method to download image from the internet
	static private Bitmap downloadBitmap(String url) throws IOException {
		HttpUriRequest request = new HttpGet(url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(request);
		
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			HttpEntity entity = response.getEntity();
			byte[] bytes = EntityUtils.toByteArray(entity);
			
			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			return bitmap;
		} else {
			throw new IOException("Download failed, HTTP response code " + statusCode 
					+ " - " + statusLine.getReasonPhrase());
		}
	}
	
	public static class MyThread extends Thread {
		@Override
		public void run() {
			try {
				// Simulate a slow network
				try {
					new Thread().sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				downloadBitmap = downloadBitmap("http://pic21.nipic.com/20120511/6167852_170704679113_2.jpg");
				// Update the user interface
				handler.sendEmptyMessage(0);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// not defined
			}
		}
	}
}
