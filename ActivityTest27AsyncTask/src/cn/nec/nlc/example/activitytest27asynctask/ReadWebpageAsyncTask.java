package cn.nec.nlc.example.activitytest27asynctask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

// Disadvantage: AsyncTask does not handle configuration changes automatically
// i.e. if the activity is recreated, the programmer has to handle that in coding.
// A common solution to this is to declare the AsyncTask in a retained headless fragment.
public class ReadWebpageAsyncTask extends Activity {

	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.TextView01);
	}

	// AsyncTask <TypeOfVarArgParams , ProgressValue , ResultValue>
	// AsyncTask class encapsulates the creation of a background process and 
	// the synchronization with the main thread. It also supports reporting 
	// progress of the running tasks.
	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
		// TypeOfVarArgParams is passed into doInBackground() method as input
		// ProgressValue is used for progress information and ResultValue must 
		// be returned from doInBackground() method and is passed to 
		// onPostExecute() as a parameter.
		// This method runs automatically in a separate Thread.
		@Override
		protected String doInBackground(String... urls) { // TypeOfVarArgParams
			String response = "";
		    for (String url : urls) {
		    	// Default implementation of an HTTP client. 
		    	// This class replaces HttpClient in HttpClient 3.
		    	DefaultHttpClient client = new DefaultHttpClient(); 
		    	// .. HttpParams: represents a collection of HTTP protocol and framework parameters.
		    	
		    	// retrieve whatever information (in the form of an entity) is 
		    	// identified by the Request-URI.
		        HttpGet httpGet = new HttpGet(url); // HttpGet(URI uri)
		        try {
		        	HttpResponse execute = client.execute(httpGet);
		        	// getEntity(): Obtains the message entity of this response, if any. 
		        	// The entity is provided by calling setEntity. Return HttpEntity
		        	// HttpEntity: An entity that can be sent or received with an HTTP message. 
		        	// Entities can be found in some requests and in responses, where they are optional.
		        	InputStream content = execute.getEntity().getContent();
		        	// .. HttpEntity.getContent() creates a new InputStream object of the entity
		        	
		        	// BufferedReader wraps an existing Reader and buffers the input, typical usage
		        	BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		        	String s = "";
		        	while ((s = buffer.readLine()) != null) {
		        		response += s;
		        	}
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
		    }
		    return response;
		}

		// onPostExecute() method synchronizes itself again with the user 
		// interface thread and allows it to be updated. This method is called
		// by the framework once the doInBackground() method finishes.
		@Override
		protected void onPostExecute(String result) { // ProgressValue , ResultValue
			textView.setText(result);
		}
	}

	public void onClick(View view) {
		DownloadWebPageTask task = new DownloadWebPageTask();
		// An AsyncTask is started via the execute() method.
		// execute() method calls the doInBackground() and the onPostExecute()
		task.execute(new String[] { "http://www.baidu.com" });
	}

}
