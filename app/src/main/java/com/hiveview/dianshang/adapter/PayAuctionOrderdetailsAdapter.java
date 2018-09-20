package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.dialog.PayAuctionOrderdetailsDialog;
import com.hiveview.dianshang.entity.order.OrderInfo;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;

import java.util.ArrayList;
import java.util.List;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.Utils;

/**
 * Created by ThinkPad on 2017/11/23.
 */

public class PayAuctionOrderdetailsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private List<OrderInfo> list=new ArrayList<>();
    private int select_item;

    public class ViewHolder {
        TextView commodity_name;
        TextView commodity_type;
        TextView commodity_price;
        TextView commodity_number;
        TextView price_type;
        ImageView first_line;

        SimpleDraweeView commodity_icon;
    }

    public PayAuctionOrderdetailsAdapter(Context context, List<OrderInfo> list) {
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
            convertView = mInflater.inflate(R.layout.layout_orderdetaillistview_check_item, null);
            holder = new ViewHolder();
            holder.commodity_name = (TextView) convertView.findViewById(R.id.commodity_name);
            holder.commodity_number = (TextView) convertView.findViewById(R.id.commodity_number);
            holder.commodity_type = (TextView) convertView.findViewById(R.id.commodity_type);
            holder.commodity_price = (TextView) convertView.findViewById(R.id.commodity_price);
            holder.price_type=(TextView)convertView.findViewById(R.id.price_type);
            holder.first_line=(ImageView)convertView.findViewById(R.id.first_line);

            holder.commodity_icon= (SimpleDraweeView) convertView.findViewById(R.id.commodity_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.commodity_icon.setTag("item"+position);

        //Log.v("test",">>"+list.get(position));
        if(position==0){
            holder.first_line.setVisibility(View.VISIBLE);
        }else{
            holder.first_line.setVisibility(View.GONE);
        }
        holder.commodity_name.setText(list.get(position).getGoodsName());
        holder.commodity_price.setText(Utils.getPrice(list.get(position).getPrice()));
        Utils.print("aa",">>"+list.get(position).getQuantity());
        holder.commodity_number.setText(context.getResources().getString(R.string.count)+list.get(position).getQuantity());
        FrescoHelper.setImage(holder.commodity_icon, Uri.parse(list.get(position).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.gap_addressmanager_67),context.getResources().getDimensionPixelSize(R.dimen.gap_addressmanager_67)));
        holder.commodity_type.setText(list.get(position).getSpecifications());

        select_item= PayAuctionOrderdetailsDialog.select_item;
        try{
            if(select_item == position){//选中
                holder.commodity_name.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.commodity_price.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.commodity_number.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.commodity_type.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                holder.price_type.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));

            }else{
                holder.commodity_name.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                holder.commodity_price.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                holder.commodity_number.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                holder.commodity_type.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                holder.price_type.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }


        return convertView;
    }

}
