package com.hiveview.dianshang.dialog;

import com.hiveview.dianshang.base.BDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hiveview.dianshang.R;

import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.commodity.CommodityAPI;
import com.hiveview.dianshang.entity.message.MessageData;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Gavin on 2017/5/10.
 */

public class ApplyPromptDialog extends BDialog {
    private LayoutInflater mFactory = null;
    private Context context;
    private View mView;
    public static Button confirm;
    private TextView tip;
    private TextView title;
    private String tag="ApplyPromptDialog";

    public ApplyPromptDialog(Context context){
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;

        mFactory = LayoutInflater.from(context);
        mView = mFactory.inflate(R.layout.layout_applypromptdialog, null);
        setContentView(mView);

        initView();
    }

    public void initView(){
        confirm=(Button)mView.findViewById(R.id.confirm);
        title=(TextView)mView.findViewById(R.id.title);
        tip=(TextView)mView.findViewById(R.id.tip);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent("com.dianshang.damai.message_changed");
               // mIntent.putExtra("position", "0");
                //发送广播
                context.sendBroadcast(mIntent);
                ApplyPromptDialog.this.dismiss();
            }
        });
        getHintMessage();
    }
    /**
     * 获取提示信息
     */
    public void getHintMessage(){
        Utils.print(tag,"getHintMessage");
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetHintMessage(ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MessageData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                    }

                    @Override
                    public void onNext(MessageData messageData) {
                        Utils.print(tag,"status=="+messageData.getErrorMessage());
                        Utils.print(tag,"s=="+messageData.getData().getDescription());
                        tip.setText(messageData.getData().getDescription());
                        title.setText(messageData.getData().getTitle());

                    }
                });
        addSubscription(subscription);
    }


}
