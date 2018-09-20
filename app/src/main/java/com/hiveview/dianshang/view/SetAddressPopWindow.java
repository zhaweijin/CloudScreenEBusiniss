package com.hiveview.dianshang.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hiveview.dianshang.R;

public class SetAddressPopWindow extends PopupWindow {
	private static final String TAG = "SetAddressPopWindow";
	//SetAddressWindowInterface
	private Context ctx;
	/*标题信息*/
	private String titleMessage;
	/*弹框布局*/
	private View container;
	
	/*回调接口*/
	private SetAddressWindowInterface addressWindowInterface;
	private Button setting;
	private Button skip;
	private TextView tvTitle;
	private Resources resources;

	public SetAddressPopWindow(Context ctx, String titleMessage,
			SetAddressWindowInterface addressWindowInterface) {
		super();
		resources = ctx.getResources();
		this.ctx = ctx;
		this.titleMessage = titleMessage;
		this.addressWindowInterface = addressWindowInterface;
		init();
	}
	

	public void init(){
		LayoutInflater mLayoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		container = mLayoutInflater.inflate(R.layout.setaddress_pop_layout, null);
		
		setting=(Button)container.findViewById(R.id.setting);
		skip=(Button)container.findViewById(R.id.skip);
		
		
		tvTitle =  (TextView) container.findViewById(R.id.title);
		tvTitle.setText(titleMessage);
		
		setting.setText(ctx.getResources().getString( R.string.setting));
		skip.setText(ctx.getResources().getString( R.string.skip));
		
		setting.setOnClickListener(onClick);
		skip.setOnClickListener(onClick);
		
		this.setContentView(container);
		WindowManager wm = (WindowManager)ctx
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
		this.setWidth(width);
		this.setHeight(height);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.setaddress_popu_in_out_style);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);
		this.setOutsideTouchable(false);
		this.update();
		
	}
	
	/***
	 * 
	 * @Title: setAddressWindow
	 * @author:yuanhui
	 * @Description: TODO 弹框显示在界面中间
	 */
	public void showWindow() {
		this.showAtLocation(container, Gravity.CENTER, 0, 0);
	}
	
	/***
	 * 点击事件监听
	 */
	View.OnClickListener onClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting:
				addressWindowInterface.onSetting();
				break;
			case R.id.skip:
				addressWindowInterface.onSkip();
				break;
			}
		}
	};
	

}
