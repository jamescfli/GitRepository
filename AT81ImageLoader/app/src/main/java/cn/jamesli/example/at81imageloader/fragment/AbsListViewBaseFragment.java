package cn.jamesli.example.at81imageloader.fragment;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import cn.jamesli.example.at81imageloader.Constants;
import cn.jamesli.example.at81imageloader.R;
import cn.jamesli.example.at81imageloader.activity.SimpleImageActivity;


// further config activity menu with hidden items, pauseOnScroll/pauseOnFling through
// com.nostra13.universalimageloader.core.listener.PauseOnScrollListener
public abstract class AbsListViewBaseFragment extends BaseFragment {

    protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
    protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";

    protected AbsListView listView;

    // default value for menu checkbox
    protected boolean pauseOnScroll = false;
    protected boolean pauseOnFling = true;

    @Override
    public void onResume() {
        super.onResume();
        applyScrollListener();
    }

    // This is called right before the menu is shown, every time it is shown.
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem pauseOnScrollItem = menu.findItem(R.id.item_pause_on_scroll);
        pauseOnScrollItem.setVisible(true); // the item is already there, but invisible
        pauseOnScrollItem.setChecked(pauseOnScroll);    // false by default

        MenuItem pauseOnFlingItem = menu.findItem(R.id.item_pause_on_fling);
        pauseOnFlingItem.setVisible(true);
        pauseOnFlingItem.setChecked(pauseOnFling);      // true by default
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_pause_on_scroll:
                pauseOnScroll = !pauseOnScroll; // flip toggle
                item.setChecked(pauseOnScroll);
                applyScrollListener();
                return true;
            case R.id.item_pause_on_fling:
                pauseOnFling = !pauseOnFling;   // flip toggle
                item.setChecked(pauseOnFling);
                applyScrollListener();
                return true;
            default:    // other selections will be handled in BaseFragment.onOptionsItemSelected()
                return super.onOptionsItemSelected(item);
        }
    }

    protected void startImagePagerActivity(int position) {
        Intent intent = new Intent(getActivity(), SimpleImageActivity.class);
        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
        intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
        startActivity(intent);
    }

    private void applyScrollListener() {
        listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));
    }
}
