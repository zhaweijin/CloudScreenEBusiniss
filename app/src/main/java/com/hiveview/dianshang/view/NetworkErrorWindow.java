package com.hiveview.dianshang.view;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.utils.NetworkUtil;


public class NetworkErrorWindow extends PopupWindow {

    public NetworkErrorWindow(Context context) {
        super(context);
        this.ctx = context;
        init();
    }

    public NetworkErrorWindow(Context context, String contextStr, String btnPressStr, String btnCancelStr) {
        super(context);
        this.ctx = context;
        init();
        title.setText(contextStr);
        tv_go_setting.setText(btnPressStr);
        tv_exit.setText(btnCancelStr);

    }


    private Context ctx;
    private View container;
    private TextView tv_go_setting;
    private TextView tv_exit;
    private RelativeLayout setting_focus;
    private RelativeLayout exit_focus;
    private TextView title;

    private void init() {
        LayoutInflater mLayoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        container = mLayoutInflater.inflate(R.layout.no_network_window_layout_zb, null);
        title = (TextView) container.findViewById(R.id.tv_vipdialog_tips);
        tv_go_setting = (TextView) container.findViewById(R.id.tv_go_setting);
        tv_exit = (TextView) container.findViewById(R.id.tv_exit);
        setting_focus = (RelativeLayout) container.findViewById(R.id.rl_vipdialog_positive_txt);
        exit_focus = (RelativeLayout) container.findViewById(R.id.rl_vipdialog_negative_txt);
        setting_focus.setOnFocusChangeListener(changedFocus);
        exit_focus.setOnFocusChangeListener(changedFocus);
        setting_focus.setOnClickListener(onClick);
        exit_focus.setOnClickListener(onClick);
        this.setContentView(container);
//        this.setWidth(CloudScreenApplication.getInstance().screenWidth);//1920
//		this.setHeight(CloudScreenApplication.getInstance().screenHeight);//1080
        this.setWidth(ctx.getResources().getDimensionPixelSize(R.dimen.window_width_1280));
        this.setHeight(ctx.getResources().getDimensionPixelSize(R.dimen.window_height_720));
        this.setFocusable(true);
        this.setAnimationStyle(R.style.playererror_popu_in_out_style_zb);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(ctx.getResources().getDrawable(color.black));
        this.getBackground().setAlpha(200);
        this.update();
        setting_focus.requestFocus();
    }

    @SuppressLint("NewApi")
    public void showWindow() {
        if (null == ctx) {
            return;
        }
        /*if (ctx instanceof Activity) {
            Activity activity = (Activity) ctx;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
        }*/
        NetworkUtil util = new NetworkUtil(ctx);
        if(util.isConnected()){
            title.setText(R.string.player_loading_tip_zb);
        }else{
            title.setText(R.string.no_network_tip_zb);
        }
        this.showAtLocation(container, Gravity.CENTER,
                (int) ctx.getResources().getDimension(R.dimen.PlayerErrorWindow_showWindow_location_X_0),
                (int) ctx.getResources().getDimension(R.dimen.PlayerErrorWindow_showWindow_location_Y_0));

    }

    private OnFocusChangeListener changedFocus = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            onFocus(v, hasFocus);
        }
    };

    private void onFocus(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.rl_vipdialog_positive_txt:
                if (hasFocus) {
                	tv_go_setting.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                	tv_go_setting.setTextColor(Color.parseColor("#88FFFFFF"));
                }
                break;
            case R.id.rl_vipdialog_negative_txt:
                if (hasFocus) {
                    tv_exit.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    tv_exit.setTextColor(Color.parseColor("#88FFFFFF"));
                }
                break;
        }
    }

    private OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_vipdialog_positive_txt:
                    NetworkErrorWindow.this.dismiss();
                    clickListener.onPressClick();
                    break;
                case R.id.rl_vipdialog_negative_txt:
                    NetworkErrorWindow.this.dismiss();
                    clickListener.onCancelClick();
                    break;
            }
        }
    };
    private OnPressClickListener clickListener;

    public interface OnPressClickListener {
        void onPressClick();

        void onCancelClick();
    }

    public void setOnPressClickListener(OnPressClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
