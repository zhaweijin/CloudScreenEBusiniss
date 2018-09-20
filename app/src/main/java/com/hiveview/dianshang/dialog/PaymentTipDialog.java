package com.hiveview.dianshang.dialog;


import android.content.Context;
import android.content.DialogInterface;
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

public class PaymentTipDialog extends BDialog implements DialogInterface.OnDismissListener{

    private Context mContext;
    private LayoutInflater mFactory = null;
    private View mView = null;


    private ConfirmOnClickListener confirmOnClickListener;
    /**
     * 确认按钮
     */
    @BindView(R.id.payment)
    Button payment;

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

    public PaymentTipDialog(Context context, ConfirmOnClickListener confirmOnClickListener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        this.confirmOnClickListener = confirmOnClickListener;
        mFactory = LayoutInflater.from(mContext);
        mView = mFactory.inflate(R.layout.layout_payment_exit_dialog, null);

        final WindowManager.LayoutParams WMLP = this.getWindow().getAttributes();
        WMLP.gravity = Gravity.CENTER;
        this.getWindow().setAttributes(WMLP);

        this.setContentView(mView);

        ButterKnife.bind(this);
        setOnDismissListener(this);
        payment.setOnClickListener(onClickListener);
        exit.setOnClickListener(onClickListener);
    }



    public void showUI(){

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Utils.getScreenW(mContext);
        lp.height = Utils.getScrrenH(mContext);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        show();
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.payment:
                    confirmOnClickListener.onContinuePayment();
                    break;
                case R.id.exit:
                    confirmOnClickListener.onExit();
                    break;
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
        confirmOnClickListener.onDismiss();
    }



}
