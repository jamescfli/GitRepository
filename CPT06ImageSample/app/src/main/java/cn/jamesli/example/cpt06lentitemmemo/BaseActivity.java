package cn.jamesli.example.cpt06lentitemmemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import cn.jamesli.example.cpt06lentitemmemo.about.AboutFragment;
import cn.jamesli.example.cpt06lentitemmemo.description.DescriptionActivity;


public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cpsampleactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.about:
                showAboutDialog();
                // When you successfully handle a menu item, return true
                return true;
            case R.id.menu_description:
                showDescription();
                return true;
        }

        // If you don't handle the menu item, you should call the superclass implementation of
        // onOptionsItemSelected() (the default implementation returns false).
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        // revise AboutFragment to singleton mode
        DialogFragment newAboutFragment = AboutFragment.getInstance();
        // show (FragmentManager manager, String tag)
        // explicitly creating a transaction, adding the fragment to it with the given tag,
        // and committing it. This does not add the transaction to the back stack.
        newAboutFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void showDescription() {
        Intent descriptionIntent = new Intent(this, DescriptionActivity.class);
        descriptionIntent.putExtra(Constants.KEY_DESCRIPTION_ID, R.string.cpsample_contentprovidersampledemo_desc);
        descriptionIntent.putExtra(Constants.KEY_LINK_TEXTS_ID, R.array.cpsample_link_texts);
        descriptionIntent.putExtra(Constants.KEY_LINK_TARGETS_ID, R.array.cpsample_link_targets);
        startActivity(descriptionIntent);
    }

}
