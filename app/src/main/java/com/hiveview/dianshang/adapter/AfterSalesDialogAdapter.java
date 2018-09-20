package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.dialog.AfterSalesDialog;
import com.hiveview.dianshang.entity.AfterSaleslistEntity;

import java.util.List;

/**
 * Created by Gavin on 2017/5/10.
 */

public class AfterSalesDialogAdapter extends BaseAdapter {
    private Context context;
    private List<AfterSaleslistEntity> list;
    private int arrayId;


    public AfterSalesDialogAdapter(Context context, List<AfterSaleslistEntity> list, int arrayId) {
        this.context = context;
        this.list = list;
        this.arrayId = arrayId;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_for_common, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();

        vh.item_name.setText(list.get(position).getName());
        vh.page_left.setImageDrawable(list.get(position).getPageLeft());
        vh.item_setting.setText(list.get(position).getItemName());
        vh.page_right.setImageDrawable(list.get(position).getPageRight());
        return convertView;
    }

    public static class ViewHolder{
        private TextView item_name;
        private ImageView page_left;
        private TextView item_setting;
        private ImageView page_right;
        public ViewHolder(View viewItem){
            item_name = (TextView) viewItem.findViewById(R.id.item_name);
            page_left=(ImageView)viewItem.findViewById(R.id.page_left);
            item_setting=(TextView)viewItem.findViewById(R.id.item_setting);
            page_right=(ImageView)viewItem.findViewById(R.id.page_right);
        }
    }
}
