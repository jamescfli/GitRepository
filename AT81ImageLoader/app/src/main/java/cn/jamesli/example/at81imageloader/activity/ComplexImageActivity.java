package cn.jamesli.example.at81imageloader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import cn.jamesli.example.at81imageloader.R;
import cn.jamesli.example.at81imageloader.fragment.ImageGridFragment;
import cn.jamesli.example.at81imageloader.fragment.ImageListFragment;

public class ComplexImageActivity extends FragmentActivity {

    private static final String STATE_POSITION = "STATE_POSITION";

    private ViewPager pager;    // flip left and right through pages of data

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_complex);

        int pagerPosition = savedInstanceState == null ? 0 : savedInstanceState.getInt(STATE_POSITION);

        pager = (ViewPager) findViewById(R.id.pager);   // @+id/pager ViewPager wrapper
        pager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(pagerPosition);    // Set the currently selected page.
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save current page for further reuse
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentPagerAdapter {
        // 2 fragments
        Fragment listFragment;
        Fragment gridFragment;

        ImagePagerAdapter(FragmentManager fm) {
            super(fm);
            listFragment = new ImageListFragment();
            gridFragment = new ImageGridFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return listFragment;
                case 1:
                    return gridFragment;
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_list);
                case 1:
                    return getString(R.string.title_grid);
                default:
                    return null;
            }
        }
    }
}
