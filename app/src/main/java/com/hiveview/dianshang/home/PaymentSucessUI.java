package com.hiveview.dianshang.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.Button;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.order.OrderMain;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

/**
 * Created by carter on 6/14/17.
 */

public class PaymentSucessUI extends BaseActivity{


    @BindView(R.id.view_order)
    Button view_order;


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, PaymentSucessUI.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_sucess_alert_dialog);


        RxView.clicks(view_order)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent mIntent = new Intent("com.dianshang.damai.dismissorderdialog");
                        //发送广播
                        mContext.sendBroadcast(mIntent);
                        //为了处理在订单列表进入进行付款时，列表刷新，且定位第一个
                        OrderMain.currentPosition=0;
                        finish();
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            Intent mIntent = new Intent("com.dianshang.damai.dismissorderdialog");
            //发送广播
            mContext.sendBroadcast(mIntent);
            //为了处理在订单列表进入进行付款时，列表刷新，且定位第一个
            OrderMain.currentPosition=0;
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
