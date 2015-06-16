package cn.jamesli.example.cpt06lentitemmemo.description;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import cn.jamesli.example.cpt06lentitemmemo.BaseActivity;
import cn.jamesli.example.cpt06lentitemmemo.Constants;
import cn.jamesli.example.cpt06lentitemmemo.R;
import cn.jamesli.example.cpt06lentitemmemo.lentitems.CPSampleActivity;

public class DescriptionActivity extends BaseActivity {

    public static final String EXTRA_DESC_INSTANCE = "extraDescInstance";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set whether home should be displayed as an "up" affordance. Set this to true if
        // selecting "home" returns up by a single level in your UI rather than back to the
        // top level or front page.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        int descId = extras.getInt(Constants.KEY_DESCRIPTION_ID);
        int linkTextsId = extras.getInt(Constants.KEY_LINK_TEXTS_ID);
        int linkTargetsId = extras.getInt(Constants.KEY_LINK_TARGETS_ID);
        setContentView(R.layout.activity_fragment_container);
        if (savedInstanceState == null) {
            DescriptionFragment descFragment = DescriptionFragment.newInstance(descId, linkTextsId, linkTargetsId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.demo_fragment_container, descFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button
                // Use NavUtils to allow users to navigate up one level in the application structure
                NavUtils.navigateUpTo(this, new Intent(this, CPSampleActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}