package cn.jamesli.example.bt12staggeredgrid;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;


public class StaggeredGridActivityFragment extends FragmentActivity {

    private static final String TAG = "StaggeredGridActFrag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staggered_grid_activity_fragment);

        setTitle("SGV");

        final FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            final StaggeredGridFragment fragment = new StaggeredGridFragment();
            fm.beginTransaction().add(android.R.id.content, fragment).commit();
        }
    }


    private class StaggeredGridFragment extends Fragment implements
            AbsListView.OnScrollListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
        private StaggeredGridView mGridView;
        private boolean mHasRequestedMore;
        private SampleAdapter mAdapter;

        private ArrayList<String> mData;

        @Override
        public void onCreate(@Nullable final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            // .. if set, the fragment lifecycle will be slightly different when an activity is
            // recreated:
            // onDestroy() will not be called (but onDetach() still will be, because the fragment
            //      is being detached from its current activity).
            // onCreate(Bundle) will not be called since the fragment is not being re-created.
            // onAttach(Activity) and onActivityCreated(Bundle) will still be called.
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_sgv, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mGridView = (StaggeredGridView) getView().findViewById(R.id.grid_view);

            if (savedInstanceState == null) {
                final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View header = layoutInflater.inflate(R.layout.list_item_header_footer, null);
                View footer = layoutInflater.inflate(R.layout.list_item_header_footer, null);
                TextView txtHeaderTitle = (TextView) header.findViewById(R.id.txt_title);
                TextView txtFooterTitle = (TextView) footer.findViewById(R.id.txt_title);
                txtHeaderTitle.setText("THE HEADER!");
                txtFooterTitle.setText("THE FOOTER!");
                mGridView.addHeaderView(header);
                mGridView.addFooterView(footer);
            }

            if (mAdapter == null) {
                mAdapter = new SampleAdapter(getActivity(), R.id.txt_line1);
            }

            if (mData == null) {
                mData = SampleData.generateSampleData();
            }

            for (String data : mData) {
                mAdapter.add(data);
            }

            mGridView.setAdapter(mAdapter);
            mGridView.setOnScrollListener(this);
            mGridView.setOnItemClickListener(this);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.d(TAG, "onScrollStateChanged: " + scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.d(TAG, "onScroll firstVisibleItem: " + firstVisibleItem +
                    " visibleItemCount: " + visibleItemCount +
                    " totalItemCount: " + totalItemCount);
            if (!mHasRequestedMore) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen >= totalItemCount) {
                    Log.d(TAG, "onScroll lastInScreen - so load more items");
                    mHasRequestedMore = true;
                    onLoadMoreItems();
                }
            }
        }

        private void onLoadMoreItems() {
            final ArrayList<String> sampleData = SampleData.generateSampleData();
            for (String data : sampleData) {
                mAdapter.add(data);
            }
            // stash all the data in our backing store
            mData.addAll(sampleData);
            // notify the adapter that we can update now
            mAdapter.notifyDataSetChanged();
            mHasRequestedMore = false;
        }

        // the following does not work since OnItemClick (i.e. single click) has been handled
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Item Long Clicked: " + position, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
