package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.dialog.OrderdetailsDialog;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gavin on 2017/5/26.
 */

public class OrderdetailsListViewAdapter extends BaseAdapter {
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

        SimpleDraweeView commodity_icon;
    }

    public OrderdetailsListViewAdapter(Context context, List<OrderItemInfo> list) {
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
            convertView = mInflater.inflate(R.layout.layout_orderdetaillistview_item, null);
            holder = new ViewHolder();
            holder.commodity_name = (TextView) convertView.findViewById(R.id.commodity_name);
            holder.commodity_number = (TextView) convertView.findViewById(R.id.commodity_number);
            holder.commodity_type = (TextView) convertView.findViewById(R.id.commodity_type);
            holder.price_type=(TextView)convertView.findViewById(R.id.price_type);
            holder.commodity_price = (TextView) convertView.findViewById(R.id.commodity_price);

            holder.commodity_icon= (SimpleDraweeView) convertView.findViewById(R.id.commodity_icon);

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
        FrescoHelper.setImage(holder.commodity_icon, Uri.parse(list.get(position).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.gap_addressmanager_67),context.getResources().getDimensionPixelSize(R.dimen.gap_addressmanager_67)));
        holder.commodity_type.setText(list.get(position).getSpecifications());

        select_item= OrderdetailsDialog.select_item_aftersale;
        try{
            if(select_item == position){//选中
                holder.commodity_name.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.commodity_price.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.commodity_number.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.commodity_type.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.price_type.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));

            }else{
                holder.commodity_name.setTextColor(android.graphics.Color.parseColor("#000000"));
                holder.commodity_price.setTextColor(android.graphics.Color.parseColor("#000000"));
                holder.commodity_number.setTextColor(android.graphics.Color.parseColor("#000000"));
                holder.commodity_type.setTextColor(android.graphics.Color.parseColor("#000000"));
                holder.price_type.setTextColor(android.graphics.Color.parseColor("#000000"));
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }


        return convertView;
    }

}
