package com.hiveview.dianshang.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.CustomProgressDialog;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by carter on 4/11/17.
 */

public class BDialog extends Dialog implements DialogInterface.OnDismissListener{

    private CompositeSubscription mCompositeSubscription;

    private String tag = "BDialog";
    public CustomProgressDialog mProgressDialog = null;
    private Context context;

    public BDialog(Context context){
        super(context);
        this.context = context;
    }

    public BDialog(Context context,int type){
        super(context,type);
        this.context = context;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }


    public CompositeSubscription getCompositeSubscription() {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        return this.mCompositeSubscription;
    }


    public void addSubscription(Subscription s) {
        if (s == null) {
            return;
        }

        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(s);
    }



    public void startProgressDialog() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new CustomProgressDialog(context);
            }
            Utils.print(tag, "startProgressDialog");
            //mProgressDialog.setMessage(msg);
//            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            mProgressDialog.startLoading();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public void stopProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                Utils.print(tag, "stopProgressDialog");
                mProgressDialog.stopLoading();
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
