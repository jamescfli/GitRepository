package cn.nec.nlc.example.servicetest2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

public class DownloadService extends IntentService {

	private int result = Activity.RESULT_CANCELED;	// default value: operation canceled, value - 0;
	// all public for referring 
	public static final String URL = "urlpath";
	public static final String FILENAME = "filename";
	public static final String FILEPATH = "filepath";
	public static final String RESULT = "result";
	// for IntentFilter
	public static final String NOTIFICATION = "cn.nec.nlc.example.servicetest2";

	public DownloadService() {
		// constructor with service name
		super("DownloadService");	// name: used to name the worker thread, only for debugging
	}

	// will be called asynchronously by Android
	// invoked on the worker thread with a request to process
	// processing happens on a worker thread that runs independently from other application logic
	// it will hold up other requests to the same IntentService, but it will not hold up anything else
	@Override
	protected void onHandleIntent(Intent intent) {
		String urlPath = intent.getStringExtra(URL);	// from putExtra(String, String)
		String fileName = intent.getStringExtra(FILENAME);
		File output = new File(Environment.getExternalStorageDirectory(), fileName);
		// .. others like: getRootDirectory(), getDataDirectory(), getDownloadCacheDirectory()
		if (output.exists()) {	// already exists
			output.delete();	// delete it
		}

		InputStream stream = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(urlPath);
			stream = url.openConnection().getInputStream();
			InputStreamReader reader = new InputStreamReader(stream);
			fos = new FileOutputStream(output.getPath());
			int next = -1;
			while ((next = reader.read()) != -1) {
				fos.write(next);
			}
			// successfully finished
			result = Activity.RESULT_OK;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		publishResults(output.getAbsolutePath(), result);
	}

	private void publishResults(String outputPath, int result) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra(FILEPATH, outputPath);
		intent.putExtra(RESULT, result);
		// send Broadcast to Android system, then received by main
		// activity and handled by onRecieve in registered receiver
		sendBroadcast(intent);
		// Service -- sendBroadcast()	 --> 
		// Android System -- onReceive() --> 
		// BroadcastReceiver in Activity
	}
}