package cn.jamesli.example.at93roboguicetest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.inject.Inject;

import cn.jamesli.example.at93roboguicetest.R;
import cn.jamesli.example.at93roboguicetest.controller.AstroboyRemoteControl;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.astroboy_activity_main)
public class AstroboyMasterConsoleActivity extends RoboActivity {

    @InjectView(R.id.self_destruct) Button selfDestructButton;
    @InjectView(R.id.say_text)      EditText sayText;
    @InjectView(R.id.brush_teeth)   Button brushTeethButton;
    @InjectView(tag="fightevil")    Button fightEvilButton;    // can also use tags if needed

    @Inject AstroboyRemoteControl remoteControl;
    @Inject Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sayText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                remoteControl.say(textView.getText().toString());
                textView.setText(null);
                return true;
            }
        });

        brushTeethButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteControl.brushTeeth();
            }
        });

        selfDestructButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(2000);
                remoteControl.setDestruct();
            }
        });
        fightEvilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AstroboyMasterConsoleActivity.this, FightForceOfEvilActivity.class));
            }
        });
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
