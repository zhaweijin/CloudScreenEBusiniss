package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.dialog.OrderdetailsDialog;

import java.util.List;

/**
 * Created by ThinkPad on 2017/11/27.
 */

public class GiveawayAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    private int select_item;


    public GiveawayAdapter(Context context, List<String> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_giveview, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();

        vh.giveawayname.setText(list.get(position));
        vh.giveawaycount.setText("X2");
        /*
        * 暂时先屏蔽掉，等拍卖开发后再放开！！！！！
        * */
       // select_item= OrderdetailsDialog.select_item_giveway;
        try{
            if(select_item == position){//选中
                vh.giveawayname.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
                vh.giveawaycount.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
            }else{
                vh.giveawayname.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
                vh.giveawaycount.setTextColor(android.graphics.Color.parseColor("#3b3b3b"));
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return convertView;
    }


    public static class ViewHolder {
        private TextView giveawayname;
        private TextView giveawaycount;


        public ViewHolder(View viewItem) {
            giveawayname = (TextView) viewItem.findViewById(R.id.giveawayname);
            giveawaycount = (TextView) viewItem.findViewById(R.id.giveawaycount);

        }

    }
}
