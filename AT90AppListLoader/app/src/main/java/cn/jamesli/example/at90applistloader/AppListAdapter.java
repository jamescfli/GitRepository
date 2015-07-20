package cn.jamesli.example.at90applistloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jamesli.example.at90applistloader.loader.AppEntry;

/**
 * Created by jamesli on 15-7-19.
 */
public class AppListAdapter extends ArrayAdapter<AppEntry> {
    private LayoutInflater mInflater;

    public AppListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.list_item_icon_text, parent, false);
        } else {
            view = convertView;
        }

        AppEntry item = getItem(position);
        ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(item.getIcon());
        ((TextView) view.findViewById(R.id.text)).setText(item.getLabel());

        return view;
    }

    public void setData(List<AppEntry> data) {
        clear();    // Remove all elements from the list
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                add(data.get(i));   // Adds the specified object at the end of the array
            }
        }
    }
}
