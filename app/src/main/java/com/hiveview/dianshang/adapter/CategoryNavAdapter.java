package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.entity.category.commodity.LevelCategoryRecored;
import com.hiveview.dianshang.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类二级页面 三级页面分类
 */
public class CategoryNavAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    private String tag = "CategoryNavAdapter";
    List<LevelCategoryRecored> list = new ArrayList<>();
    private int itemType = 1;
    public static final int ICON_TEXT = 1;  //二级页面分类
    public static final int TEXT = 2;  //三级页面分类

    /**
     * 选择置灰色背景
     */
    private int currentItemSelect = 0;

    /**
     * 当前焦点选择行
     */
    private int currentFocusItem = 0;

    /**
     * 当前焦点选择行页面跳转
     */
    private boolean selected=true;

    public class ViewHolderIcon {
        TextView category_title;

        RelativeLayout item;
    }

    public class ViewHolder {
        TextView catetory_title;
        RelativeLayout item;
    }

    public CategoryNavAdapter(Context context, List<LevelCategoryRecored> list, int type) {
        super();
        itemType = type;
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
        ViewHolderIcon viewHolderIcon = null;

        if (itemType == ICON_TEXT) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.category_item_icon_nav, null);
                viewHolderIcon = new ViewHolderIcon();
                viewHolderIcon.category_title = (TextView) convertView.findViewById(R.id.name);
                viewHolderIcon.item = (RelativeLayout) convertView.findViewById(R.id.item);
                convertView.setTag(viewHolderIcon);
            } else {
                viewHolderIcon = (ViewHolderIcon) convertView.getTag();
            }
            //Utils.print(tag,">>"+position);
            Utils.print(tag, "name=" + list.get(position).getName());
            viewHolderIcon.category_title.setText(list.get(position).getName());

            //再处理默认选择颜色
            if (currentItemSelect == position) {
                viewHolderIcon.category_title.setTextColor(context.getResources().getColor(R.color.white));
                viewHolderIcon.item.setBackgroundResource(R.drawable.category_item_listselected_selector);
            } else {
                //先处理焦点选择颜色
                if (currentFocusItem == position && selected) {
                    viewHolderIcon.category_title.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    viewHolderIcon.category_title.setTextColor(context.getResources().getColor(R.color.category_text_color));
                }
                viewHolderIcon.item.setBackgroundResource(R.drawable.category_item_listunselected_selector);
            }

        } else if (itemType == TEXT) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.category_item_nav, null);
                holder = new ViewHolder();
                holder.catetory_title = (TextView) convertView.findViewById(R.id.name);
                holder.item = (RelativeLayout) convertView.findViewById(R.id.item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.catetory_title.setText(list.get(position).getName());


            //再处理默认选择颜色
            if (currentItemSelect == position) {
                holder.catetory_title.setTextColor(context.getResources().getColor(R.color.white));
                holder.item.setBackgroundResource(R.drawable.category_item_listselected_selector);
            } else {
                //先处理焦点选择颜色
                if (currentFocusItem == position && selected) {
                    holder.catetory_title.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    holder.catetory_title.setTextColor(context.getResources().getColor(R.color.category_text_color));
                }
                holder.item.setBackgroundResource(R.drawable.category_item_listunselected_selector);
            }


        }

        return convertView;
    }


    /**
     * 默认选择行
     *
     * @param itemSelect
     */
    public void setCurrentItemSelect(int itemSelect) {
        Utils.print(tag, ">>>" + itemSelect);
        this.currentItemSelect = itemSelect;
    }

    /**
     * 当前焦点选择行
     *
     * @param itemSelect
     */
    public void setCurrentFocusItem(int itemSelect) {
        this.currentFocusItem = itemSelect;
        notifyDataSetChanged();
    }
   public void setCurrentFocusItem(int itemSelect,boolean selected){
       this.currentFocusItem = itemSelect;
       this.selected=selected;
       notifyDataSetChanged();
   }




}
