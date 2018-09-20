package com.hiveview.dianshang.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hiveview.dianshang.R;


public class GeneralAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context context;
	List<String> list = new ArrayList<>();

	public class ViewHolder {
		TextView itemName;
	}

	public GeneralAdapter(Context context, List<String> list) {
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
			convertView = mInflater.inflate(R.layout.textview_item, null);
			holder = new ViewHolder();
			holder.itemName = (TextView) convertView.findViewById(R.id.item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//Log.v("test",">>"+list.get(position));
		holder.itemName.setText(list.get(position));



		return convertView;
	}

}
