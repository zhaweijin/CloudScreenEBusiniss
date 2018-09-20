package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionGift;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionListData;
import com.hiveview.dianshang.utils.FrescoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017\11\22 0022.
 */

public class PresentAdapter extends BaseAdapter {
    private String tag = "PresentAdapter";
    private List<PromotionGift> list = new ArrayList<>();
    private Context mContext;
    private PromotionListData promotionData;

    public PresentAdapter() {
    }

    public PresentAdapter(List<PromotionGift> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.present_item, parent, false);
            holder.present_name = (TextView) convertView.findViewById(R.id.present_name);
            holder.present_num = (TextView) convertView.findViewById(R.id.num);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.present_icon = (SimpleDraweeView) convertView.findViewById(R.id.present_icon);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.present_name.setText(list.get(position).getGiftName());
        holder.price.setText(mContext.getResources().getString(R.string.price_mark)+list.get(position).getGiftPrice() + "");
        holder.present_num.setText("x"+list.get(position).getGiftNum()+"");
//        holder.present_icon.setImageURI(list.get(position).getImgUrl());
        FrescoHelper.setImage(holder.present_icon, Uri.parse(list.get(position).getImgUrl()),new ResizeOptions(300,300));
        convertView.setTag(holder);
        return convertView;
    }


    public static class ViewHolder {
        private TextView present_name;
        private TextView present_num;
        private TextView price;
        private SimpleDraweeView present_icon;
    }


}
