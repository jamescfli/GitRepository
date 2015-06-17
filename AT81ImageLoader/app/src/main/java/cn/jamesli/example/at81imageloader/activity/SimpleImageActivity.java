package cn.jamesli.example.at81imageloader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import cn.jamesli.example.at81imageloader.Constants;
import cn.jamesli.example.at81imageloader.R;
import cn.jamesli.example.at81imageloader.fragment.ImageGalleryFragment;
import cn.jamesli.example.at81imageloader.fragment.ImageGridFragment;
import cn.jamesli.example.at81imageloader.fragment.ImageListFragment;
import cn.jamesli.example.at81imageloader.fragment.ImagePagerFragment;

public class SimpleImageActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use FRAGMENT_INDEX to control which type of Fragment to show
        int frIndex = getIntent().getIntExtra(Constants.Extra.FRAGMENT_INDEX, 0);
        Fragment fr;
        String tag;
        int titleRes;   // title associated with this activity
        switch (frIndex) {
            default:
            case ImageListFragment.INDEX:
                tag = ImageListFragment.class.getSimpleName();  // string e.g. Integer, Integer[], "" for anonymous class
                fr = getSupportFragmentManager().findFragmentByTag(tag);   // if ever generated
                if (fr == null) {
                    fr = new ImageListFragment();
                }
                titleRes = R.string.ac_name_image_list;
                break;
            case ImageGridFragment.INDEX:
                tag = ImageGridFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImageGridFragment();
                }
                titleRes = R.string.ac_name_image_grid;
                break;
            case ImagePagerFragment.INDEX:
                tag = ImagePagerFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImagePagerFragment();
                    fr.setArguments(getIntent().getExtras());
                }
                titleRes = R.string.ac_name_image_pager;
                break;
            case ImageGalleryFragment.INDEX:
                tag = ImageGalleryFragment.class.getSimpleName();
                fr = getSupportFragmentManager().findFragmentByTag(tag);
                if (fr == null) {
                    fr = new ImageGalleryFragment();
                }
                titleRes = R.string.ac_name_image_gallery;
                break;
        }
        // Fragment is prepared
        setTitle(titleRes);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fr, tag).commit();
        // tag: Optional name for the fragment, to later retrieve the fragment with
        // FragmentManager.findFragmentByTag(String)
    }
}
