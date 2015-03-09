package cn.nec.nlc.example.jamesli.activitytest52fragment;

//import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class DetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        // for ActionBarActivity
        if (savedInstanceState == null) {     // for extends Activity
            setContentView(R.layout.details_activity_layout);
            // During initial setup, plug in the details fragment.
            DetailsFragment details = new DetailsFragment();
            // getIntent().getExtras().getInt("index") = selected list item
            details.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().replace(R.id.detailsActivity, details).commit();

        }

//        // for Activity
//        if (savedInstanceState == null) {
//            // During initial setup, plug in the details fragment.
//            DetailsFragment details = new DetailsFragment();
//            details.setArguments(getIntent().getExtras());
//            getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
//              // android.R.id.content gives you the root element of a view, without having to
//              // know its actual name/type/ID. Check out Get root view from current activity
//        }
    }
}