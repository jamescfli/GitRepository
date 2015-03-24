package cn.nec.nlc.example.jamesli.activitytest60speakchinese;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends ActionBarActivity {
    private static final String TEXT_TO_SPEAK_ENG = "This is a simple test.";
    private static final String TEXT_TO_SPEAK_CHN = "这是一个简单测试。";
    private TextView textViewDisplay;
    private Button buttonSysLanguage;
    private Button buttonChinese;
    private Button buttonEnglish;
    private TextToSpeech ttsChinese;
    private TextToSpeech ttsEnglish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewDisplay = (TextView) findViewById(R.id.textViewDisplay);
        buttonSysLanguage = (Button) findViewById(R.id.buttonSysLanguage);
        buttonChinese = (Button) findViewById(R.id.buttonChinese);
        buttonEnglish = (Button) findViewById(R.id.buttonEnglish);

        ttsChinese = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = ttsChinese.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        MainActivity.this.textViewDisplay.setText("Chinese is not available.");
                    } else {
                        MainActivity.this.textViewDisplay.setText("Chinese language is supported.");
                    }
                }
            }
        });

        ttsEnglish = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = ttsEnglish.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "US English is not available",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "US English is supported",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        // set system volume
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);

        // define click action for 3 buttons
        buttonSysLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get system country
                String country = getResources().getConfiguration().locale.getCountry();
                if (country.equals("CN")) {
                    // get language detail
                    String language = getLanguageEnv();
                    if (language != null) {
                        if (language.trim().equals("zh-CN")) {
                            textViewDisplay.setText("System: country = " + country + " language = simplified Chinese");
                        } else if (language.trim().equals("zh-TW")) {
                            textViewDisplay.setText("System: country = " + country + " language = traditional Chinese");
                        }
                    }
                } else {
                    // display other countries and their language code, default "US" + "en"
                    String language = getLanguageEnv();
                    textViewDisplay.setText("System: country = " + country + " language = " + language);
                }
            }
        });
        buttonChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ttsChinese.speak(TEXT_TO_SPEAK_CHN, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        buttonEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ttsEnglish.speak(TEXT_TO_SPEAK_ENG, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    private String getLanguageEnv() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ttsChinese.shutdown();
        ttsEnglish.shutdown();
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
