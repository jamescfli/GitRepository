package cn.jamesli.example.bt04superswiperefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.jamesli.example.bt04superswiperefresh.R;
import cn.jamesli.example.bt04superswiperefresh.view.SuperSwipeRefreshLayout;

public class ListViewActivity extends Activity {
    private SuperSwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, getData()));
        swipeRefreshLayout = (SuperSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        // swipeRefreshLayout may run in another thread
        View child = LayoutInflater.from(swipeRefreshLayout.getContext()).inflate(R.layout.layout_header, null);
        textView = (TextView) child.findViewById(R.id.text_view);
        textView.setText(getString(R.string.pull_down_refresh));
        // progressBar and imageView are at the same position
        progressBar = (ProgressBar) child.findViewById(R.id.pb_view);
        imageView = (ImageView) child.findViewById(R.id.image_view);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.down_arrow);
        RelativeLayout layoutHeader = (RelativeLayout) child.findViewById(R.id.head_container);
        RelativeLayout.LayoutParams marginLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        marginLayoutParams.topMargin = 800;
        layoutHeader.setLayoutParams(marginLayoutParams);
        progressBar.setVisibility(View.GONE);   // hide in the first place
        swipeRefreshLayout.setHeaderView(child);
        swipeRefreshLayout.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                textView.setText(getString(R.string.refreshing));
                // hide
                imageView.setVisibility(View.GONE);
                // show
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // stop refreshing and hide the layout header for refreshing
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                    }
                }, 2000);
            }

            @Override
            public void onPullDistance(int distance) {
                // similar to public void setDistanceToTriggerSync (int distance)'s distance
                System.out.println("debug:distance = " + distance);
            }

            @Override
            public void onPullEnable(boolean enable) {
                textView.setText(enable ? getString(R.string.release_to_refresh) : getString(R.string.pull_down_refresh));
                imageView.setVisibility(View.VISIBLE);
                imageView.setRotation(enable ? 180 : 0);
            }
        });
    }

    private List<String> getData() {
        List<String> data = new ArrayList<String>();
        data.add("item 1");
        data.add("item 2");
        data.add("item 3");
        data.add("item 4");
        data.add("item 5");
        data.add("item 6");
        data.add("item 7");
        data.add("item 8");
        data.add("item 9");
        data.add("item 10");
        data.add("item 11");
        data.add("item 12");
        data.add("item 13");
        data.add("item 14");
        data.add("item 15");
        data.add("item 16");
        data.add("item 17");
        data.add("item 18");
        return data;
    }

}
