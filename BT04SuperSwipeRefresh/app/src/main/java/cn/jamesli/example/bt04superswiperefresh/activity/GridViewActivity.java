package cn.jamesli.example.bt04superswiperefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.jamesli.example.bt04superswiperefresh.R;
import cn.jamesli.example.bt04superswiperefresh.adapter.RecyclerAdapter;
import cn.jamesli.example.bt04superswiperefresh.view.SuperSwipeRefreshLayout;

public class GridViewActivity extends Activity {

    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private SuperSwipeRefreshLayout swipeRefreshLayout;

    // Header View
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // water falling grid layout with 2 vertical columns
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        // .. or mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        // to set layout dynamically, which can not be down by ListView

        mAdapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(mAdapter);

        // SuperSwipeRefreshLayout
        swipeRefreshLayout = (SuperSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setHeaderView(createHeaderView());
        swipeRefreshLayout.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                textView.setText(getString(R.string.refreshing));
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                    }
                }, 2000);
            }

            @Override
            public void onPullDistance(int distance) {

            }

            @Override
            public void onPullEnable(boolean enable) {
                textView.setText(enable ? getString(R.string.release_to_refresh) : getString(R.string.pull_down_refresh));
                imageView.setVisibility(View.VISIBLE);
                imageView.setRotation(enable? 180 : 0); // use the same down arrow to notify pull down and release to refresh
            }
        });

        initDatas();
    }

    private View createHeaderView() {
        View headerView = LayoutInflater.from(swipeRefreshLayout.getContext())
                .inflate(R.layout.layout_header, null);
        progressBar = (ProgressBar) headerView.findViewById(R.id.pb_view);
        textView = (TextView) headerView.findViewById(R.id.text_view);
        textView.setText(getString(R.string.pull_down_refresh));
        imageView = (ImageView) headerView.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.down_arrow);
        progressBar.setVisibility(View.GONE);
        return headerView;
    }

    private void initDatas() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            list.add("item " + i);
        }
        mAdapter.addAll(list, 0);
    }


}
