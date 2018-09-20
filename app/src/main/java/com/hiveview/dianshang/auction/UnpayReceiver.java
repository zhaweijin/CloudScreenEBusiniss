package com.hiveview.dianshang.auction;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.acution.netty.DomyAcutionTcpMsgBodyVo;
import com.hiveview.dianshang.entity.acution.netty.DomyAuctionTcpVo;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderData;
import com.hiveview.dianshang.shoppingcart.ShoppingCartList;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.Utils;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hiveview.dianshang.utils.httputil.RetrofitClient.getCommodityAPI;

/**
 * Created by zwj on 4/16/18.
 */

public class UnpayReceiver extends BroadcastReceiver{

    private String tag = "netty";
    private Context mContext;

    Subscription s;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        String msgJson = intent.getStringExtra("msgJson");
        Utils.print(tag, "netty>>>" + msgJson+",isForeground="+isForeground(mContext));
        if(!isForeground(mContext)){
            Utils.print(tag,"return");
            return;
        }

        Gson gson = new Gson();
        DomyAcutionTcpMsgBodyVo domyAcutionTcpMsgBodyVo = gson.fromJson(msgJson, DomyAcutionTcpMsgBodyVo.class);
        DomyAuctionTcpVo domyAuctionTcpVo = domyAcutionTcpMsgBodyVo.getInfo().get(0);
        if(domyAuctionTcpVo.getActionType()== ConStant.ACTION_TYPE_CREATE_ACUTION_ORDER && domyAuctionTcpVo.getContentId().equals(ConStant.NETTY_OP_SUCESS)){
            Utils.print(tag,"receive unpay acution order");
            getAcutionUnpayData();
        }else if(domyAuctionTcpVo.getActionType()== ConStant.ACTION_TYPE_CANCEL_ACUTION_ORDER && domyAuctionTcpVo.getContentId().equals(ConStant.NETTY_OP_SUCESS)){
            Utils.print(tag,"receive cancel acution order");
            RxBus.get().post(ConStant.obString_opt_cancel_acution_order,true);
        }
    }



    /**
     * 获取拍卖未支付的订单信息
     */
    public void getAcutionUnpayData(){
        Utils.print(tag,"getAcutionUnpayData");
        s = getCommodityAPI()
                .httpGetAcutionUnpayData(Utils.getAcutionUnpayInput(mContext).getInput(),Utils.getAcutionUnpayInput(mContext).getSign())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UnpayOrderData>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        String error_tips = "";
                        Utils.print(tag,"1error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        if(statusData.getReturnValue()==-1){
                            return;
                        }

                        Utils.print(tag,"isHaveUnpayOrder=="+statusData.getData().isHaveUnpayOrder());
                        if(statusData.getData().isHaveUnpayOrder()){
                            ShoppingCartList.launch(mContext,ConStant.ACUTION_PAYMENT,statusData.getData());
                        }
                        s.unsubscribe();
                    }
                });
    }



    private boolean isForeground(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo: processes) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
