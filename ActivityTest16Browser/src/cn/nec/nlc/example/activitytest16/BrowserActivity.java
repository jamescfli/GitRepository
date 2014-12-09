package cn.nec.nlc.example.activitytest16;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.WebView;
//import android.widget.TextView;
import android.widget.Toast;

public class BrowserActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// To keep this example simple, we allow network access
	    // in the user interface thread
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
	        .permitAll().build();
	    StrictMode.setThreadPolicy(policy);

	    setContentView(R.layout.activity_main);
	    Intent intent = getIntent();
//	    TextView text = (TextView) findViewById(R.id.textView);
	    WebView webview = (WebView) findViewById(R.id.webview);

	    
	    // To get the action of the intent use
	    String action = intent.getAction();
	    if (!action.equals(Intent.ACTION_VIEW)) {
	    	throw new RuntimeException("Should not happen");
	    }
	    
	    // To get the data use
	    Uri data = intent.getData();
	    URL url;
	    try {
	    	url = new URL(data.getScheme(), data.getHost(), data.getPath());
	    	webview.loadUrl(url.toString());
	    	Log.i("BROWSER_ACT", url.toString());
//	    	BufferedReader rd = new BufferedReader(new InputStreamReader(url.openStream()));
//	    	String line = "";
//	    	while ((line = rd.readLine()) != null) {
//	    		text.append(line);
//	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
}
