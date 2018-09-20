package com.hiveview.dianshang.dialog;

import com.hiveview.dianshang.base.BDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.RefundAdapter;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.commodity.CommodityAPI;
import com.hiveview.dianshang.entity.order.OrderRecord;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfo;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfoData;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gavin on 2017/5/11.
 */

public class RefundDialog extends BDialog implements View.OnClickListener {
    private LayoutInflater mFactory = null;
    private Context context;
    private View mView;
    private ListView listView;
    private String tag = "RefundDialog";
    private OrderRecord orderRecord;
    public static ReceiverRefund receiverRefund;
    private OrderStatusInfo infoData;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;


    public RefundDialog(Context context, OrderRecord orderRecord) {
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.orderRecord = orderRecord;

        mFactory = LayoutInflater.from(context);
        mView = mFactory.inflate(R.layout.layout_refunddialog, null);

        setContentView(mView);
        getOrderStatusInfoData(orderRecord.getOrderSn());

        initView();

        //注册广播
        receiverRefund = new ReceiverRefund();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dianshang.damai.message_changed");
        context.registerReceiver(receiverRefund, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                sumit(context.getResources().getString(R.string.refundreason1));
                break;
            case R.id.btn2:
                sumit(context.getResources().getString(R.string.refundreason2));
                break;
            case R.id.btn3:
                sumit(context.getResources().getString(R.string.refundreason3));
                break;
            case R.id.btn4:
                sumit(context.getResources().getString(R.string.refundreason4));
                break;
            case R.id.btn5:
                sumit(context.getResources().getString(R.string.refundreason5));
                break;
            case R.id.btn6:
                sumit(context.getResources().getString(R.string.refundreason6));
                break;

            default:
                break;
        }
    }


    public void initView() {
        // phone=(Textw)mView.findViewById(R.id.phone);
        btn1 = (Button) mView.findViewById(R.id.btn1);
        btn2 = (Button) mView.findViewById(R.id.btn2);
        btn3 = (Button) mView.findViewById(R.id.btn3);
        btn4 = (Button) mView.findViewById(R.id.btn4);
        btn5 = (Button) mView.findViewById(R.id.btn5);
        btn6 = (Button) mView.findViewById(R.id.btn6);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);


    }

    public void sumit(String str) {
        if (infoData != null && infoData.getStatus() == orderRecord.getStatus()) {
            orderRefundService(str);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.dataexception), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 申请订单退款
     */
    public void orderRefundService(String reason) {
        Utils.print(tag, "orderRefundService");
        if (!Utils.isConnected(context)) {
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context, error_tips);
            return;
        }

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(context).userID);
            json.put("orderSn", orderRecord.getOrderSn()); //订单号
            json.put("reason", reason); //原因
            json.put("status", orderRecord.getStatus()); //添加订单状态
            json.put("totalQuantity", orderRecord.getTotalQuantity()); //数量
            json.put("serviceType", "1"); //退款类型: 2换货 1退款
            json.put("refundType", "2"); //1单件退款 ,2整单退款
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpSubmitOrderService(ConStant.APP_VERSION1, input, ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                        String error_tips = "";
                        if (Utils.isConnected(context)) {
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        } else {
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context, error_tips);
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == 0) {//正常
                            //成功的话
                            ApplyPromptDialog applyPromptDialog = new ApplyPromptDialog(context);
                            if (!applyPromptDialog.isShowing()) {
                                applyPromptDialog.show();
                            }
                        } else {
                            ToastUtils.showToast(context, statusData.getErrorMessage());
                        }


                    }
                });
        addSubscription(subscription);
    }

    public class ReceiverRefund extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (RefundDialog.this != null && RefundDialog.this.isShowing()) {
                RefundDialog.this.dismiss();
            }

        }

    }

  /*  public void dismissDialog() {
        if (RefundDialog.this != null && RefundDialog.this.isShowing()) {
            RefundDialog.this.dismiss();
        }
    }*/

    /**
     * 查看订单详情,总价,状态,快递信息
     */
    public void getOrderStatusInfoData(String orderSn) {
        Utils.print(tag, "getOrderStatusInfoData");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.userID);
            json.put("orderStatus",orderRecord.getStatus());
            json.put("orderSn", orderSn);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetOrderStatusInfoData(ConStant.APP_VERSION,input, ConStant.Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderStatusInfoData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(OrderStatusInfoData orderStatusInfoData) {
                        Utils.print(tag, "status==" + orderStatusInfoData.getErrorMessage());
                        if (orderStatusInfoData.getReturnValue() == -1)
                            return;
                        OrderStatusInfo info = orderStatusInfoData.getData();
                        infoData = info;

                        Utils.print(tag, "time=" + info.getDeliveryCorpCode() + ",xxx=" + info.getStatus());


                    }
                });
        addSubscription(subscription);
    }


}
