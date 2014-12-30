package cn.nec.nlc.example.activitytest30json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

// note that some URIs return a JSONObject object while others return a JSONArray
public class ParseBugzillaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Just for testing, allow network access in the main thread
		// NEVER use this in product code
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll()
			.build();
		StrictMode.setThreadPolicy(policy);
		
		setContentView(R.layout.activity_main);
		String input = readBugzilla();
		try {
			JSONObject json = new JSONObject(input);
			Log.i(ParseBugzillaActivity.class.getName(), json.toString());
			TextView textView = (TextView) findViewById(R.id.textview1);
			textView.setText(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// test write JSON
		writeJSON();
	}

	public String readBugzilla() {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("https://bugzilla.mozilla.org/rest/bug?assigned_to=lhenry@mozilla.com");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(ParseBugzillaActivity.class.getName(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	// write JSON by creating JSONObject or JSONArray and using toString() method
	public void writeJSON() {
		JSONObject object = new JSONObject();
		try {
			object.put("name", "Jack Hack");
			object.put("score", new Integer(200));
			object.put("current", new Double(152.32));
			object.put("nickname", "Hacker");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println(object);
	} 
}
