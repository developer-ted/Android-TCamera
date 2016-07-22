package com.tedkim.android.tcamera.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tedkim.android.tcamera.R;
import com.tedkim.android.tcamera.interfaces.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = PhotoAdapter.class.getSimpleName();

    private Activity mActivity;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    private List<String> mListData;
    private int mSelectedPosition = -1;

    public PhotoAdapter(Activity activity) {
        mActivity = activity;
        mListData = new ArrayList<>();
    }

    public void setSelectedItem(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    /**
     * Add the entire data
     */
    public void addAllData(List<String> listData) {
        if (mListData != null) {
            mListData.addAll(listData);
            notifyDataSetChanged();
        }
    }

    /**
     * Delete all data
     */
    public void deleteAllData() {
        if (mListData != null) {
            mListData.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder viewHolderRow = (ViewHolder) viewHolder;
        viewHolderRow.textTitle.setText(mListData.get(position));

        viewHolderRow.layoutRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRecyclerViewItemClickListener.onItemClick(null, position);
            }
        });

        if (mSelectedPosition == position) {
            viewHolderRow.radio.setChecked(true);
        } else {
            viewHolderRow.radio.setChecked(false);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_photo_dialog, viewGroup, false));
    }

    /**
     * ViewHolder class
     */
    private class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layoutRow;
        public TextView textTitle;
        public RadioButton radio;

        public ViewHolder(View v) {
            super(v);
            layoutRow = (LinearLayout) v.findViewById(R.id.layoutRow);
            textTitle = (TextView) v.findViewById(R.id.textTitle);
            radio = (RadioButton) v.findViewById(R.id.radio);
        }
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener l) {
        this.mOnRecyclerViewItemClickListener = l;
    }
}