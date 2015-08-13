package cn.jamesli.example.bt04superswiperefresh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.jamesli.example.bt04superswiperefresh.R;
import cn.jamesli.example.bt04superswiperefresh.viewholder.BaseViewHolder;
import cn.jamesli.example.bt04superswiperefresh.viewholder.ChildViewHolder;

/**
 * Created by jamesli on 15-8-13.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context mContext;
    private List<String> mDataSet;

    public RecyclerAdapter(Context context) {
        mContext = context;
        mDataSet = new ArrayList<String>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_child, viewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder baseViewHolder, int position) {
        ChildViewHolder textViewHolder = (ChildViewHolder) baseViewHolder;
        textViewHolder.bindView(mDataSet.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    protected void removeAll(int position, int itemCount) {
        for (int i = 0; i < itemCount; i++) {
            mDataSet.remove(position);
        }
        // Notify any registered observers:
        // itemCount items previously located at positionStart have been removed from the data set
        notifyItemRangeRemoved(position, itemCount);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public void add(String text, int position) {
        mDataSet.add(position, text);
        notifyItemInserted(position);
    }

    public void addAll(List<String> list, int position) {
        mDataSet.addAll(position, list);
        notifyItemRangeInserted(position, list.size());
    }
}
