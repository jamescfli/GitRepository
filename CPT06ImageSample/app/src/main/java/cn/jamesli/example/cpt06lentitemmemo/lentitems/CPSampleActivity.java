package cn.jamesli.example.cpt06lentitemmemo.lentitems;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import cn.jamesli.example.cpt06lentitemmemo.BaseActivity;
import cn.jamesli.example.cpt06lentitemmemo.R;
import cn.jamesli.example.cpt06lentitemmemo.lentitems.LentItemListFragment.MasterCallback;
import cn.jamesli.example.cpt06lentitemmemo.provider.LentItemsContract;

public class CPSampleActivity extends BaseActivity implements MasterCallback, LentItemDisplayFragment.DisplayCallback {
    private static final String KEY_SPINNER_POS = "keySpinnerPos";

    private int mCurrSpinnerPos= 0;
    private boolean mTwoPane = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        if (getResources().getBoolean(R.bool.twoPane)) {
            mTwoPane = true;
        }

        if (null == savedInstanceState) {
            // Config change
            LentItemListFragment fragment = LentItemListFragment.newInstance(mTwoPane);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.demo_fragment_container, fragment);
            if (mTwoPane) {
                // TODO: add detail fragment; may be empty
                // detail fragment must be informed when loader of the listfragment ends
                // to get the id of the first item - which is highlighted by default
                transaction.add(null, null);
            }
            transaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SPINNER_POS, mCurrSpinnerPos);
    }

    @Override
    public void switchToForm(long itemId) {
        if (mTwoPane) {
            // Swap fragments
        } else {
            // Call formactivity
            Fragment f = LentItemFormFragment.newInstance(itemId);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.demo_fragment_container, f);
            transaction.addToBackStack("cpsample");
            transaction.commit();
        }
    }

    @Override
    public void displayItem(long itemId) {
        if (mTwoPane) {
            // Swap fragments
        } else {
            // Call formactivity
            Fragment f = LentItemDisplayFragment.newInstance(itemId);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.demo_fragment_container, f);
            transaction.addToBackStack("cpsample");
            transaction.commit();
        }
    }

    @Override
    public void addItem() {
        // It's the addition of a new item, so the id to pass is -1
        Fragment f = LentItemFormFragment.newInstance(-1);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.demo_fragment_container, f);
        transaction.addToBackStack("cpsample");
        transaction.commit();
    }

    public void deleteItem(final long itemId) {
        final ContentResolver resolver = getContentResolver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri delUri = ContentUris.withAppendedId(LentItemsContract.Items.CONTENT_URI, itemId);
                int resCount = resolver.delete(delUri, null, null);
                if (0 == resCount) {
                    // Do something
                }
            }
        }).start();
        getSupportFragmentManager().popBackStack();
    }
}
