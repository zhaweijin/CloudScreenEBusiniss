package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hiveview.dianshang.R;

import java.util.List;

/**
 * Created by Gavin on 2017/5/12.
 */

public class RefundAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;

    public RefundAdapter(Context context, List<String> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_itemrefund, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();

        vh.name.setText(list.get(position));
        return convertView;
    }
    public static class ViewHolder{
        private TextView name;
        public ViewHolder(View viewItem){
            name = (TextView) viewItem.findViewById(R.id.name);

        }
    }
}
