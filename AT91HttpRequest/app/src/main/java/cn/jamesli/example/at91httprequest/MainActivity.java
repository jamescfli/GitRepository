package cn.jamesli.example.at91httprequest;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String URL = "https://raw.github.com/square/okhttp/master/README.md";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OkHttpRequestAsyncTask().execute(URL);
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
            ((TextView) findViewById(R.id.textView)).setText(result);
        }
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
