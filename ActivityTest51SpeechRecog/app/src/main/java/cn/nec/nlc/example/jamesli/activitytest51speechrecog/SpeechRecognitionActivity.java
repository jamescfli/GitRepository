package cn.nec.nlc.example.jamesli.activitytest51speechrecog;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class SpeechRecognitionActivity extends ActionBarActivity {
    private SpeechRecognizer mSpeechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new MyRecognitionListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.destroy();
    }

    // onClick action for start button
    public void doSpeechRecognition(View view) {
        view.setEnabled(false);
        Intent recognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);  // default: false
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        mSpeechRecognizer.startListening(recognitionIntent);
    }

//    // onClick action for stop button
//    public void stopSpeechRecognition(View view) {
//        mSpeechRecognizer.stopListening();
//    }

    private class MyRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            // n.a.
        }

        @Override
        public void onBeginningOfSpeech() {
            ((TextView) findViewById(R.id.textViewResult)).setText("");
        }

        @Override
        public void onRmsChanged(float v) {
            // n.a.
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            // n.a.
        }

        @Override
        public void onEndOfSpeech() {
            findViewById(R.id.buttonStart).setEnabled(true);
        }

        @Override
        public void onError(int i) {
            findViewById(R.id.buttonStart).setEnabled(true);
            ((TextView) findViewById(R.id.textViewResult)).setText("Something wrong in " +
                    "in Speech Recognition");
        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> partialResults = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (partialResults != null && partialResults.size() > 0) {
                String bestResult = partialResults.get(0);
                ((TextView) findViewById(R.id.textViewResult)).setText(bestResult + ".");
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            ArrayList<String> partialResults = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (partialResults != null && partialResults.size() > 0) {
                String bestResult = partialResults.get(0);
                ((TextView) findViewById(R.id.textViewResult)).setText(bestResult);
            }
        }

        @Override
        public void onEvent(int i, Bundle bundle) {

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
