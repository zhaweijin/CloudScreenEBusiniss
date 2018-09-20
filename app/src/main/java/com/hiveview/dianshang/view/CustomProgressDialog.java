package com.hiveview.dianshang.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiveview.dianshang.R;


public class CustomProgressDialog extends Dialog {
	private Context mContext = null;
	private ImageView mLoadingImg = null;
	private TextView mMsgTv = null;
    private LayoutInflater mFactory = null;
    private View mView = null;

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	public CustomProgressDialog(Context context) {
		//super(context, R.style.CustomProgressDialog);
		super(context, R.style.CustomDialog);
		mContext = context;
    	mFactory = LayoutInflater.from(mContext);
    	mView = mFactory.inflate(R.layout.custom_progress_dialog, null);
    	
		
		final WindowManager.LayoutParams WMLP = this.getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;

		this.getWindow().setAttributes(WMLP);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		

		mLoadingImg = (ImageView) mView.findViewById(R.id.progress_dialog_loading_img);

		mLoadingImg.setBackgroundResource(R.drawable.dialog_loading_process);
		
		mMsgTv = (TextView) mView.findViewById(R.id.progress_dialog_msg_tv);
		mMsgTv.setVisibility(View.GONE);
		
		this.setContentView(mView);
		
	}

	public void startLoading() {
		AnimationDrawable animationDrawable = (AnimationDrawable)mLoadingImg.getBackground();  
        
        if(!animationDrawable.isRunning())
        {  
            animationDrawable.start();  
        }
        
	}

	public void stopLoading(){
		AnimationDrawable animationDrawable = (AnimationDrawable)mLoadingImg.getBackground();  
        
        if(!animationDrawable.isRunning())
        {  
            animationDrawable.stop();  
        }
	}
	
	public void setMessage(String strMessage) {
		if (mMsgTv != null) {
			mMsgTv.setText(strMessage);
		}
	}
}
