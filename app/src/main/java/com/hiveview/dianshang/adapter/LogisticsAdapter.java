package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.dialog.OrderLogisticsDialog;
import com.hiveview.dianshang.entity.TracesBean;

import java.util.List;


/**
 * Created by Gavin on 2017/5/8.
 */

public class LogisticsAdapter extends BaseAdapter {
    private Context context;
    private List<TracesBean> list;
    private int select_item=-1;

    public LogisticsAdapter(Context context, List<TracesBean> list) {
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_itemlogistics, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();
        vh.information.setText(list.get(position).getTime()+"  "+list.get(position).getStation());
      /*  if(position==0){
            vh.img.setImageResource(R.drawable.circle);
           // vh.information.setTextColor(android.graphics.Color.parseColor("#ffffff"));
        }else{
            vh.img.setImageResource(R.drawable.uncircle);
        }*/
        select_item= OrderLogisticsDialog.select_item;
        try{
            if(this.select_item == position){//点击项
                vh.img.setImageResource(R.drawable.circle);
                //vh.information.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
            }else{//未点击
                vh.img.setImageResource(R.drawable.uncircle);
               // vh.information.setTextColor(android.graphics.Color.parseColor("#9A9A9A"));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }


        return convertView;
    }
    public static class ViewHolder{
        private TextView information;
        private ImageView img;
        public ViewHolder(View viewItem){
            information = (TextView) viewItem.findViewById(R.id.information);
            img=(ImageView)viewItem.findViewById(R.id.img);
        }
    }
}
