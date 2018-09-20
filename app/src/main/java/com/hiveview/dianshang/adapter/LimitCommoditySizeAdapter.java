package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionRecord;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionSku;
import com.hiveview.dianshang.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\1\10 0010.
 */

public class LimitCommoditySizeAdapter extends BaseAdapter{
    private static final String TAG="LimitCommoditySizeAdapter";
    private PromotionRecord promotionRecord;
    private List<PromotionSku> list = new ArrayList<>();
    private Context mContext;

    public LimitCommoditySizeAdapter(Context context,PromotionRecord promotionRecord, List<PromotionSku> promotionSku){
        this.mContext=context;
        this.promotionRecord=promotionRecord;
        this.list = promotionSku;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_limitcommoditysize_item, parent, false);
            holder = new ViewHolder(convertView);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Utils.print(TAG,"promotionSku.size() is "+list.size());
        Utils.print(TAG,"getSpecValues() is "+promotionRecord.getJoinSkuList().get(position).getSpecValues()+"");
        Utils.print(TAG,"position is "+position);
        holder.limitCommoditySize.setText(promotionRecord.getJoinSkuList().get(position).getSpecValues()+"");
//        holder.limitCommoditySize.setText(promotionSkuList.get(position).getSpecValues()+"");
        Utils.print(TAG,"limitnum is "+promotionRecord.getLimitNum());
        if(promotionRecord.getLimitNum()==0){
            holder.multiply.setVisibility(View.GONE);
            holder.noLimitNum.setVisibility(View.VISIBLE);
            holder.noLimitNum.setText(mContext.getResources().getString(R.string.no_limit_num));
            holder.limitNum.setVisibility(View.GONE);
    } else {
            holder.multiply.setVisibility(View.VISIBLE);
            holder.noLimitNum.setVisibility(View.GONE);
            holder.limitNum.setVisibility(View.VISIBLE);
            holder.limitNum.setText(promotionRecord.getLimitNum() + "");
        }

        convertView.setTag(holder);
        return convertView;
    }

    class ViewHolder{
        private TextView limitCommoditySize,limitNum,multiply,noLimitNum;
        ViewHolder(View view){
            limitCommoditySize = (TextView) view.findViewById(R.id.limitcommoditySize);
            limitNum = (TextView) view.findViewById(R.id.limitNum);
            multiply = (TextView) view.findViewById(R.id.multiply);
            noLimitNum = (TextView) view.findViewById(R.id.noLimitNum);
        }
    }
}
