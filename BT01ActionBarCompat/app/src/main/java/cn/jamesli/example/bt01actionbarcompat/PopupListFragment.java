package cn.jamesli.example.bt01actionbarcompat;

import android.app.ActionBar;
import android.app.ListFragment;
import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;

//public class PopupListFragment extends ListFragment implements View.OnClickListener {
public class PopupListFragment extends ListFragment {
    private ActionBar mActionBar;
    private ListView mCheeseListView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Allow modification to the list, mainly removing operation
        ArrayList<String> items = new ArrayList<>();
        for (int i=0, SIZE = Cheeses.CHEESES.length; i < SIZE; i++) {
            items.add(Cheeses.CHEESES[i]);
        }
        setListAdapter(new PopupAdapter(items));

        mActionBar = getActivity().getActionBar();

        mCheeseListView = getListView();
        mCheeseListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // The following is more favourable solution
                final ListView lw = getListView();
                if (scrollState == 0)
                    Log.i("PopupListFragment", "scrolling stopped ...");
                if (view.getId() == lw.getId()) {
                    final int currentFirstVisibleItem = lw.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        // Scrolling down
                        mActionBar.hide();
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        // Scrolling up
                        mActionBar.show();
                    }
                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (mLastFirstVisibleItem < firstVisibleItem) {
//                    // Scrolling down
//                    mActionBar.hide();
//                } else if (mLastFirstVisibleItem > firstVisibleItem) {
//                    // Scrolling up
//                    mActionBar.show();
//                }
//                mLastFirstVisibleItem = firstVisibleItem;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // When press the list part not the button part
        super.onListItemClick(l, v, position, id);
        String item = (String) l.getItemAtPosition(position);
        Toast.makeText(getActivity(), "Item Clicked: " + item, Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onClick(final View v) {     // When click the button
//        v.post(new Runnable() {
//            @Override
//            public void run() {
//                showPopupMenu(v);
//            }
//        });
//    }

    private void showPopupMenu(View view) {
        final PopupAdapter adapter = (PopupAdapter) getListAdapter(); // method of ListFragment return ListAdapter

        final String item = (String) view.getTag();

        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu()); // with one item - remove
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_remove:
                        adapter.remove(item);   // remove by tag
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    class PopupAdapter extends ArrayAdapter<String> {
        PopupAdapter(ArrayList<String> items) {
            super(getActivity(), R.layout.list_item, android.R.id.text1, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Let ArrayAdapter inflate the layout
            View view = super.getView(position, convertView, parent);

            View popupButton = view.findViewById(R.id.button_popup);

            popupButton.setTag(getItem(position));

//            popupButton.setOnClickListener(PopupListFragment.this);

            popupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            showPopupMenu(v);   // access from inner class, so need to declare as final
                        }
                    });
                }
            });

            return view;
        }
    }
}
