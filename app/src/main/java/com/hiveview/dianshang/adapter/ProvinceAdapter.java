package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.dialog.AreasSelectDialog;
import com.hiveview.dianshang.entity.address.Province;

import java.util.List;


public class ProvinceAdapter extends android.widget.BaseAdapter {
	private Context context;
	private List<Province> list;
	private int select_item;



	public ProvinceAdapter(Context context, List<Province> list) {
		super();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_itemprovince, parent, false);
			// convertView.setMinimumHeight(minimumHeight);
			convertView.setTag(new ViewHolder(convertView));
		}
		ViewHolder vh = (ViewHolder) convertView.getTag();
		vh.textView.setText(list.get(position).getName());
		select_item= AreasSelectDialog.select_item_provinces;
		try{
			if(select_item == position){
				vh.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.sp_43));   //选中的Item字体：43sp
				vh.textView.setTextColor(android.graphics.Color.parseColor("#ffffff"));                //android.graphics.Color.parseColor("#ffffff")
				convertView.setBackground(context.getResources().getDrawable(R.drawable.provinces_focus));
			}
			else{
				vh.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.sp_24));   //未选中的Item字体：24sp
				vh.textView.setTextColor(android.graphics.Color.parseColor("#7d7d7d"));//android.graphics.Color.parseColor("#7d7d7d")
				convertView.setBackground(context.getResources().getDrawable(R.drawable.provinces_focus));
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}

		return convertView;
	}
	/*public void addAll(Collection<?extends String> collection){
		list.clear();
		list.addAll(collection);
		notifyDataSetChanged();
	}*/
	public static class ViewHolder{
		private TextView textView;
		public ViewHolder(View viewItem){
			textView = (TextView) viewItem.findViewById(R.id.tv);
		}
	}
}
