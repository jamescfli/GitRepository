package cn.jamesli.example.modernartui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.ArrayList;


public class MainActivity extends Activity {
    private static final int VIEWS_NUMBER = 6;
    private ArrayList<View> mViews;
    private int[] mViewsActualBgColors = new int[VIEWS_NUMBER];
    private int[] mViewsBaseBgColors = new int[VIEWS_NUMBER];
    private SeekBar seekBarForColorChange;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViews = new ArrayList<View>();

        mViews.add((View) findViewById(R.id.viewRed));
        mViews.add((View) findViewById(R.id.viewWhite));
        mViews.add((View) findViewById(R.id.viewBlue));
        mViews.add((View) findViewById(R.id.viewGreen));
        mViews.add((View) findViewById(R.id.viewPurple));
        mViews.add((View) findViewById(R.id.viewOrange));

        for (int i = 0; i < VIEWS_NUMBER; i++) {
            mViewsActualBgColors[i] = ((ColorDrawable) mViews.get(i).getBackground()).getColor();
            mViewsBaseBgColors[i] = mViewsActualBgColors[i];
        }

        seekBarForColorChange = (SeekBar) findViewById(R.id.mySeekBar);
        seekBarForColorChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                calculateBackgroundColor(progress);
                updateViewBackgroundColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // N.A.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // N.A.
            }
        });
    }

    private void calculateBackgroundColor(int progress) {
        for (int i = 0; i < (VIEWS_NUMBER); i++){
            int baseRed = Color.red(mViewsBaseBgColors[i]);
            int baseGreen = Color.green(mViewsBaseBgColors[i]);
            int baseBlue = Color.blue(mViewsBaseBgColors[i]);
            int distRed = 255 - (2 * baseRed);
            int distGreen = 255 - (2 * baseGreen);
            int distBlue = 255 - (2 * baseBlue);
            int calcRed = baseRed + (distRed * progress / 100);
            int calcGreen = baseGreen + (distGreen * progress / 100);
            int calcBlue =  baseBlue  + (distBlue * progress / 100);
            mViewsActualBgColors[i] = Color.rgb(calcRed, calcGreen, calcBlue);
        }
    }

    private void updateViewBackgroundColor() {
        for (int i = 0; i < VIEWS_NUMBER; i++) {
            mViews.get(i).setBackgroundColor(mViewsActualBgColors[i]);
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
        if (id == R.id.action_more_info) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog);
            Button visitMoma = (Button) dialog.findViewById(R.id.btnVisitMoma);
            Button notNow = (Button) dialog.findViewById(R.id.btnNotNow);
            dialog.show();
            visitMoma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.moma.org"));
                    startActivity(intent);
                    dialog.dismiss();
                }
            });
            notNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        return true;
    }
}
