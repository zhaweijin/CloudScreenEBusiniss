package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.dialog.OrderdetailsDialog;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad on 2017/8/29.
 */

public class OrderdetailsListViewCheckAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    private List<OrderItemInfo> list=new ArrayList<>();
    private int select_item;

    public class ViewHolder {
        TextView commodity_name;
        TextView commodity_type;
        TextView commodity_price;
        TextView commodity_number;
        TextView price_type;
        LinearLayout container;

        SimpleDraweeView commodity_icon;
    }

    public OrderdetailsListViewCheckAdapter(Context context, List<OrderItemInfo> list) {
        super();
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_mustorderdetaillistview_check_item, null);
            holder = new ViewHolder();
            holder.commodity_name = (TextView) convertView.findViewById(R.id.commodity_name);
            holder.commodity_number = (TextView) convertView.findViewById(R.id.commodity_number);
            holder.commodity_type = (TextView) convertView.findViewById(R.id.commodity_type);
            holder.commodity_price = (TextView) convertView.findViewById(R.id.commodity_price);
            holder.price_type=(TextView)convertView.findViewById(R.id.price_type);

            holder.commodity_icon= (SimpleDraweeView) convertView.findViewById(R.id.commodity_icon);
            holder.container=(LinearLayout)convertView.findViewById(R.id.container);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.commodity_icon.setTag("item"+position);

        //Log.v("test",">>"+list.get(position));
        holder.commodity_name.setText(list.get(position).getGoodsName());
        holder.commodity_price.setText(Utils.getPrice(list.get(position).getPrice()));
        Utils.print("aa",">>"+list.get(position).getQuantity());
        holder.commodity_number.setText(context.getResources().getString(R.string.count)+list.get(position).getQuantity());
        FrescoHelper.setImage(holder.commodity_icon, Uri.parse(list.get(position).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_mustorderdetail_67),context.getResources().getDimensionPixelSize(R.dimen.height_mustorderdetail_67)));
        holder.commodity_type.setText(list.get(position).getSpecifications());
        //赠品的处理
        holder.container.setTag("view"+position);
        holder.container.removeAllViews();
        for(int i=0;i<2;i++){
            View giveView = LayoutInflater.from(context).inflate(R.layout.layout_giveview, null);
            if(i==0){
                ((LinearLayout)giveView).getChildAt(0).setVisibility(View.VISIBLE);
            }else{
                ((LinearLayout)giveView).getChildAt(0).setVisibility(View.INVISIBLE);
            }
            holder.container.addView(giveView);

        }

       /* select_item= OrderdetailsDialog.select_item;
        try{
            if(select_item == position){//选中
                holder.commodity_name.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.commodity_price.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.commodity_number.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.commodity_type.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.price_type.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                for(int i=0;i<holder.container.getChildCount();i++){
                    ((TextView)((LinearLayout)holder.container.getChildAt(i)).getChildAt(0)).setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                    ((TextView)((LinearLayout)holder.container.getChildAt(i)).getChildAt(1)).setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                    ((TextView)((LinearLayout)holder.container.getChildAt(i)).getChildAt(2)).setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                }
               *//* ((TextView)holder.container.getChildAt(0)).setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                ((TextView)holder.container.getChildAt(1)).setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                ((TextView)holder.container.getChildAt(2)).setTextColor(android.graphics.Color.parseColor("#FFFFFF"));*//*
            }else{
                holder.commodity_name.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                holder.commodity_price.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                holder.commodity_number.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                holder.commodity_type.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                holder.price_type.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                for(int i=0;i<holder.container.getChildCount();i++){
                    ((TextView)((LinearLayout)holder.container.getChildAt(i)).getChildAt(0)).setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                    ((TextView)((LinearLayout)holder.container.getChildAt(i)).getChildAt(1)).setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                    ((TextView)((LinearLayout)holder.container.getChildAt(i)).getChildAt(2)).setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                }
               *//* ((TextView)holder.container.getChildAt(0)).setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                ((TextView)holder.container.getChildAt(1)).setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                ((TextView)holder.container.getChildAt(2)).setTextColor(android.graphics.Color.parseColor("#3b3b3b"));*//*
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }*/


        return convertView;
    }

}
