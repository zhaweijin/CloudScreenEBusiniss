package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.view.FocusRelativeLayout;

import java.util.List;


public class MaulCarouselAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private OnItemStateListener mListener;
    private List<String> mDatas;
    private int oldtotalSize = 0;

    private OnItemViewSelectedListener onItemSelectedListener;

    public MaulCarouselAdapter(Context context, List<String> mDatas) {
        mContext = context;
        this.mDatas = mDatas;
        this.onItemSelectedListener = (OnItemViewSelectedListener)mContext;
    }

    public void setOnItemStateListener(OnItemStateListener listener) {
        mListener = listener;
    }


    /**
     * 分页添加的数据
     * @param entities
     */
    public void addData(List<String> entities) {
        if (null != entities) {
            oldtotalSize = mDatas.size();
            this.mDatas.addAll(entities);
            notifyItemRangeChanged(oldtotalSize,entities.size());
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = View.inflate(mContext, R.layout.recycle_item, null);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;

        viewHolder.mName.setText(mDatas.get(position));

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView mName;
        FocusRelativeLayout focusRelativeLayout;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            focusRelativeLayout = (FocusRelativeLayout) itemView.findViewById(R.id.focus_layout);
            focusRelativeLayout.setOnItemSelectedListener(onItemSelectedListener);
            focusRelativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface OnItemStateListener {
        void onItemClick(View view, int position);
    }
}
