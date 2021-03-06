package cn.jamesli.example.at91httprequest;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.Header;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String URL_REQUEST = "https://raw.github.com/square/okhttp/master/README.md";
    private static final String URL_POST = "http://www.roundsapp.com/post";

    private TextView textView;
    private String savedText;
    private static final String TEXTVIEW_CONTENT = "cn.jamesli.example.at91httprequest.TEXTVIEW_CONTENT";


    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ((TextView) findViewById(R.id.textView)) would like to be renewed with "Hello world"
        // when screen orientation is changed.
        textView = (TextView) findViewById(R.id.textView);

//        if (savedInstanceState != null) {
//            String savedText = savedInstanceState.getString(TEXTVIEW_CONTENT);
//            textView.setText(savedText);
//        }

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (null != savedText) {
            textView.setText(savedText);
        }

        ((Button) findViewById(R.id.buttonRequest)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // method 1: OkHttp
//                new OkHttpRequestAsyncTask().execute(URL_REQUEST);
                // method 2: Android Asynchronous Http Client
                client.get(URL_REQUEST, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        textView.setText(bytes.toString());
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        textView.setText("Failure: " + throwable.toString());
                    }
                });
            }
        });

        ((Button) findViewById(R.id.buttonPost)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OkHttpPostAsyncTask().execute(URL_POST, "Jesse", "Jake");
            }
        });
    }

    class OkHttpRequestAsyncTask extends AsyncTask<String, Void, String> {
        private Exception exception;

        @Override
        protected String doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                // When upgrading to 2.1.1 of ModernHttpClient, which uses OkHttp 2.0, my Xamarin
                // app stopped working on Android L devices. It continued to work with KitKat devices and below.
                // This appears to be an issue with Android L, not OkHttp.
            } catch (IOException e) {
                exception = e;
                return null;
            }

            return response.body().toString();
        }

        @Override
        protected void onPostExecute(String result) {
            if (null != exception) {
                textView.setText(exception.toString());
            } else {
                textView.setText(result);
            }
        }
    }

    class OkHttpPostAsyncTask extends AsyncTask<String, Void, String> {
        private Exception exception;
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            String json = bowlingJson(params[1], params[2]);

            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(params[0])
                    .post(body)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();   // failed to connect due to GFW
                return response.body().string();
            } catch (IOException e) {
                exception = e;
                return null;
            }
        }

        private String bowlingJson(String player1, String player2) {
            return "{'winCondition':'HIGH_SCORE',"
                    + "'name':'Bowling',"
                    + "'round':4,"
                    + "'lastSaved':1367702411696,"
                    + "'dateStarted':1367702378785,"
                    + "'players':["
                    + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
                    + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
                    + "]}";
        }

        @Override
        protected void onPostExecute(String result) {
            if (null != exception) {
                textView.setText(exception.toString());
            } else {
                textView.setText(result);
            }
        }

    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        textView.setText(savedInstanceState.getString(TEXTVIEW_CONTENT));
//    }


    @Override
    protected void onPause() {
        super.onPause();
    }

//    // Note: onSaveInstanceState() is only called if the Activity is being killed.
//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        // This method does not work since orientation change will not initiate onSaveInstanceState()
//        // and textView content is not saved accordingly.
//        outState.putString(TEXTVIEW_CONTENT, textView.getText().toString());
//    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        savedText = textView.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
