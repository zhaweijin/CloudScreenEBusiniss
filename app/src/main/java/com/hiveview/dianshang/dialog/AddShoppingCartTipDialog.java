package com.hiveview.dianshang.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pConfig;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BDialog;
import com.hiveview.dianshang.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by carter on 4/11/17.
 */

public class AddShoppingCartTipDialog extends BDialog implements DialogInterface.OnDismissListener{

    private Context mContext;
    private LayoutInflater mFactory = null;
    private View mView = null;


    private ConfirmOnClickListener confirmOnClickListener;
    /**
     * 确认按钮
     */
    @BindView(R.id.ok)
    Button ok;

    /**
     * 取消按钮
     */
    @BindView(R.id.exit)
    Button exit;


    /**
     * 对话框提示信息
     */
    @BindView(R.id.title)
    TextView title;

    public AddShoppingCartTipDialog(Context context, ConfirmOnClickListener confirmOnClickListener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        this.confirmOnClickListener = confirmOnClickListener;
        mFactory = LayoutInflater.from(mContext);
        mView = mFactory.inflate(R.layout.layout_comfirm_add_shoppingcart, null);

        /*final WindowManager.LayoutParams WMLP = this.getWindow().getAttributes();
        WMLP.gravity = Gravity.CENTER;
        WMLP.width = mContext.getResources().getDimensionPixelSize(R.dimen.common_dialog_width);
        WMLP.height = mContext.getResources().getDimensionPixelSize(R.dimen.common_dialog_height);
        this.getWindow().setAttributes(WMLP);*/

        this.setContentView(mView);

        ButterKnife.bind(this);
        setOnDismissListener(this);
        ok.setOnClickListener(onClickListener);
        exit.setOnClickListener(onClickListener);
        ok.setOnFocusChangeListener(onFocusChangeListener);
        exit.setOnFocusChangeListener(onFocusChangeListener);
    }


    public void setTitle(String titleMessage){
        title.setText(titleMessage);
    }


    public void setOkName(String okName){
        ok.setText(okName);
    }

    public void setCancelName(String cancelName){
        exit.setText(cancelName);
    }


    public void showUI(){

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = mContext.getResources().getDimensionPixelSize(R.dimen.common_dialog_width_933);
        lp.height = mContext.getResources().getDimensionPixelSize(R.dimen.common_dialog_height_427);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        show();
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ok:
                    confirmOnClickListener.onContinuePayment();
                    break;
                case R.id.exit:
                    confirmOnClickListener.onExit();
                    break;
            }
        }
    };


    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                ((Button)v).setTextColor(mContext.getResources().getColor(android.R.color.white));
            }else{
                ((Button)v).setTextColor(mContext.getResources().getColor(android.R.color.black));
            }
        }
    };

    public interface ConfirmOnClickListener{
        void onContinuePayment();
        void onExit();
        void onDismiss();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(confirmOnClickListener!=null)
            confirmOnClickListener.onDismiss();
    }



}
