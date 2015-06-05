package cn.jamesli.example.at78playactionbar;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Arrays;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private int scrollYRecord = 0;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final ToggleButton tButton = (ToggleButton) rootView.findViewById(R.id.toggleButtonPlayActionBar);
            tButton.setChecked(false);   // default value is checked
            tButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionBar actionBar = getActivity().getActionBar();
                    if (tButton.isChecked()) {
                        actionBar.hide();
                    } else {    // unChecked
                        actionBar.show();
                    }
                }
            });

            TextView textView = (TextView) rootView.findViewById(R.id.textViewInScrollView);
            // Prepare the contents
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < 100; i++)
                b.append("This is a simple test!\n");
            textView.setText(b.toString());

            final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
            scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver
                    .OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    int scrollY = scrollView.getScrollY();
                    ActionBar actionBar = getActivity().getActionBar();
                    // TODO: more delicate tuning should be conducted due to scrollY could change
                    // after ActionBar disappears.
                    if ((scrollY - scrollYRecord) >= 100) {
                        actionBar.hide();
                        scrollYRecord = scrollY;
                    } else if ((scrollY - scrollYRecord) <= -100) {
                        actionBar.show();
                        scrollYRecord = scrollY;
                    }
                }
            });
            return rootView;
        }
    }
}
