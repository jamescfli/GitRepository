package cn.nec.nlc.jamesli.tools.at71materialdesign;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;


public class MainActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] myDataset = {"Card 1", "Card 2", "Card 3", "Card 4", "Card 5", "Card 6", "Card 7"};
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enable transitions
        //  getWindow(): Retrieve the current Window for the activity
        //  requestFeature: Enable extended screen features. This must be called before setContentView().
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
          // FEATURE_CONTENT_TRANSITIONS:
          // Flag for requesting that window content changes should be animated using a TransitionManager.
        setContentView(R.layout.activity_main);

        // CardView widgets can have shadows and rounded corners.
        mRecyclerView = (RecyclerView) findViewById(R.id.my_main_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyMainAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mImageView = (ImageView) findViewById(R.id.imageView);
            mImageView.setImageResource(R.drawable.heart);
        }
    }

    public void onSomeButtonClicked(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            // setExitTransition()
            // Sets the Transition that will be used to move Views out of the scene when starting a new Activity.
            getWindow().setExitTransition(new Explode());
            // Transition -> Visibility -> Explode
            Intent intent = new Intent(this, MyOtherActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            // Implement this feature without material design
            Intent intent = new Intent(this, MyOtherActivity.class);
            startActivity(intent);
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
