package com.hiveview.dianshang.adapter;

import android.content.Context;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartRecord;
import com.hiveview.dianshang.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车列表适配器
 */
public class FullGiftItemAdapter extends BaseAdapter {
	private String tag = "FullGiftItemAdapter";
	private LayoutInflater mInflater;
	private Context context;
	List<String> list = new ArrayList<>();


	public class ViewHolder {

		TextView full_gift_goods_name;
		TextView full_gift_goods_number;

	}

	public FullGiftItemAdapter(Context context, List<ShoppingCartRecord> list) {
		super();
		this.context = context;
		mInflater = LayoutInflater.from(context);
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
			convertView = mInflater.inflate(R.layout.full_gift_item, null);
			holder = new ViewHolder();
			holder.full_gift_goods_name = (TextView) convertView.findViewById(R.id.full_gift_goods_name);
			holder.full_gift_goods_number = (TextView) convertView.findViewById(R.id.full_gift_goods_number);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.full_gift_goods_name.setText("11");
		holder.full_gift_goods_number.setText("X2");

		return convertView;
	}

}
