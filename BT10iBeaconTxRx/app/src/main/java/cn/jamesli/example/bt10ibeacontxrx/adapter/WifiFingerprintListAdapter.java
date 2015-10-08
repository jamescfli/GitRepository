package cn.jamesli.example.bt10ibeacontxrx.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.jamesli.example.bt10ibeacontxrx.R;

/**
 * Created by jamesli on 15/10/6.
 */
public class WifiFingerprintListAdapter extends RecyclerView.Adapter<WifiFingerprintListAdapter.ViewHolder> {
    private List<WifiFingerprintDisplayItem> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewBssid;
        public TextView mTextViewEffectiveScanCounter;
        public TextView mTextViewAvgRssi;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewBssid = (TextView) itemView.findViewById(R.id.text_view_fingerprint_item_bssid);
            mTextViewEffectiveScanCounter = (TextView) itemView
                    .findViewById(R.id.text_view_fingerprint_item_eff_scan_counter);
            mTextViewAvgRssi = (TextView) itemView
                    .findViewById(R.id.text_view_fingerprint_item_avg_rssi);
        }
    }

    public WifiFingerprintListAdapter(List<WifiFingerprintDisplayItem> dataset) {
        mDataset = dataset;
    }

    @Override
    public WifiFingerprintListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fingerprint_display_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mTextViewBssid.setText(mDataset.get(position).getBssid());
        viewHolder.mTextViewEffectiveScanCounter.setText(mDataset.get(position).getEffScanCounter());
        viewHolder.mTextViewAvgRssi.setText(mDataset.get(position).getAvgRssi());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
