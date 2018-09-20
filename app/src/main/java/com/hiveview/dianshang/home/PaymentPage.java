package com.hiveview.dianshang.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.PaymentTipDialog;
import com.hiveview.dianshang.dialog.PurchaseAgreementDialog;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.netty.payment.DomyTcpMsgBodyVo;
import com.hiveview.dianshang.entity.notify.NotifyData;
import com.hiveview.dianshang.utils.QrcodeUtil;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 支付页面
 * Created by carter on 6/2/17.
 */

public class PaymentPage extends BaseActivity{

    private String tag = "PaymentPage";

    @BindView(R.id.weixin_text)
    TextView weixin_text;

    @BindView(R.id.zhifubao_text)
    TextView zhifubao_text;
    /**
     * 微信支付焦点响应
     */
    @BindView(R.id.layout_weixin_payment)
    RelativeLayout layout_weixin_payment;
    /**
     * 支付宝焦点响应
     */
    @BindView(R.id.layout_zhifubao_payment)
    RelativeLayout layout_zhifubao_payment;
    /**
     * 订单号显示
     */
    @BindView(R.id.order)
    TextView order;
    /**
     * 左侧价格前缀
     */
    @BindView(R.id.price1)
    TextView price1;
    /**
     * 左侧价格后缀
     */
    @BindView(R.id.price2)
    TextView price2;

    /**
     * 二维码顶部价格显示
     */
    @BindView(R.id.commodity_price)
    TextView commodity_price;

    /**
     * 二维码显示图片
     */
    @BindView(R.id.qr_image)
    ImageView qr_image;
    /**
     * 二维码底部支付类型图标
     */
    @BindView(R.id.payment_icon)
    ImageView payment_icon;
    /**
     * 二维码底部支付类型文字提示
     */
    @BindView(R.id.payment_type_text)
    TextView payment_type_text;

    APIServer apiServer;
    /**
     * 支付需要的唯一参数  订单号 价格 支付商品名
     */
    private String orderSn ="";
    private String price="";
    private String paymentName="";

    /**
     * 支付服务器端回调电商服务器端URL
     */
    private String notify_url="";


    /**
     * 支付退出提示对话框
     */
    private PaymentTipDialog paymentTipDialog;


    private String payment_url="";

    private boolean op_server = false;
    //保存订单列表是否首次加载的标志
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    private Observable<Boolean> observer;


    public static void launch(Activity activity, String adMessage) {
        Intent intent = new Intent(activity, PaymentPage.class);
        activity.startActivity(intent);
    }


    public static void launch(Activity activity, String orderSn,double price,String name) {
        Intent intent = new Intent(activity, PaymentPage.class);
        intent.putExtra("orderSn",orderSn);
        intent.putExtra("price",price);
        intent.putExtra("name",name);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_page);


        orderSn = getIntent().getStringExtra("orderSn");
        double p = getIntent().getDoubleExtra("price",0);

        price = Utils.getPrice(p);

        paymentName = getIntent().getStringExtra("name");
        paymentName = Utils.formatInvalidString(paymentName);
        if(Utils.getStringLength(paymentName)>80){
            paymentName = Utils.getShowKeyString(paymentName,80);//支付条件限制，太长无法支付
        }

        Utils.print(tag,orderSn+"---"+price+"---"+paymentName);

        apiServer = new APIServer(mContext);
        initEvent();
        initData();
        regeditReceiver();

        preferences = mContext.getSharedPreferences("isRefreshOrder",
                mContext.MODE_WORLD_READABLE);
        editor = preferences.edit();

        //发购物车刷新的广播
        Intent mIntent = new Intent("com.dianshang.damai.shopping");
        mIntent.putExtra("tag","refresh");
        //发送广播
        mContext.sendBroadcast(mIntent);


    }






    private void initData(){
        /*//微信
        //orderSn ="1221343181247875657400856612864";  //直接支付
        //orderSn ="1221343140420875594875859378176";  //购物车支付

        //支付宝
        //orderSn ="21343135947875231342244159488";  //直接支付
        //orderSn ="21343094057875166203948064769";  //购物车支付

        price = "0.02";
        paymentName="我的购物车";*/




        order.setText(getResources().getString(R.string.order_title)+orderSn);

        if(!price.contains(".")){
            price2.setVisibility(View.INVISIBLE);

            price1.setText(getResources().getString(R.string.jiner)+price);
        }else{
            price2.setVisibility(View.VISIBLE);

            price1.setText(getResources().getString(R.string.jiner)+price.substring(0,price.indexOf(".")));
            price2.setText(price.substring(price.indexOf("."),price.length()));
        }



        commodity_price.setText(getResources().getString(R.string.payment_price,price));

    }


    private void initEvent(){
        layout_weixin_payment.setOnFocusChangeListener(onFocusChangeListener);
        layout_zhifubao_payment.setOnFocusChangeListener(onFocusChangeListener);

        observer = RxBus.get().register(ConStant.obString_opt_cancel_acution_order,Boolean.class);
        observer.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean status) {
                Utils.print(tag,"op="+status);
                if(status){
                    finish();
                }
            }
        });
    }


    /**
     * 焦点事件,通过焦点加载二维码
     */
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                if(v.getId()==R.id.layout_weixin_payment){
                    weixin_text.setTextColor(getResources().getColor(R.color.white));

                }else if(v.getId()==R.id.layout_zhifubao_payment){
                    zhifubao_text.setTextColor(getResources().getColor(R.color.white));
                }
                loadQR();
            }else{
                if(v.getId()==R.id.layout_weixin_payment){
                    weixin_text.setTextColor(getResources().getColor(R.color.half_white));
                }else if(v.getId()==R.id.layout_zhifubao_payment){
                    zhifubao_text.setTextColor(getResources().getColor(R.color.half_white));
                }
            }

        }
    };


    /**
     * 加载二维码
     */
    private void loadQR(){
        Utils.print(tag,"loadQR=="+notify_url);
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }
        if(notify_url.equals("")){
            getNotifyUrl();
        }else{
            getRQUrl();
        }
    }

    /**
     * 获取二维码地址
     */
    private void getRQUrl(){
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                String qr_url="";
                Utils.print(tag,"start...");
                if(!Utils.isConnected(mContext)){
                    String error_tips = mContext.getResources().getString(R.string.error_network_exception);
                    ToastUtils.showToast(mContext,error_tips);
                    return;
                }
                if(layout_zhifubao_payment.isFocused()){
                    qr_url = apiServer.httpGetPaymentUrl(ConStant.ZHIFUBAO_PAMYMNET,Utils.changeY2F(price),paymentName,orderSn,notify_url);
                }else if(layout_weixin_payment.isFocused()){
                    qr_url = apiServer.httpGetPaymentUrl(ConStant.WEIXIN_PAMYMENT,Utils.changeY2F(price),paymentName,orderSn,notify_url);
                }
                subscriber.onNext(qr_url);
                subscriber.onCompleted();
            }
        });
        observable.subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error......");
                        ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.error_qr_network_exception));
                    }

                    @Override
                    public void onNext(Object o) {
                        payment_url = (String)o;
                        Utils.print(tag,"qr url="+payment_url);
                        if(payment_url!=null && !payment_url.equals("")){
                            payment_icon.setVisibility(View.VISIBLE);
                            payment_type_text.setVisibility(View.VISIBLE);
                            qr_image.setBackground(new QrcodeUtil().generateQRCode(mContext,payment_url));
                            if(layout_weixin_payment.isFocused()){
                                payment_icon.setBackgroundResource(R.drawable.weixin_icon);
                                payment_type_text.setText(getResources().getString(R.string.weixin_payment));
                            }else if(layout_zhifubao_payment.isFocused()){
                                payment_icon.setBackgroundResource(R.drawable.zhifubao_icon);
                                payment_type_text.setText(getResources().getString(R.string.zhifubao_payment));
                            }

                        }else{
                            ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.error_qr_network_exception));
                            qr_image.setBackground(null);
                            payment_icon.setVisibility(View.INVISIBLE);
                            payment_type_text.setVisibility(View.INVISIBLE);
                        }

                    }
                });

    }



    /**
     * 获取支付notifyURL
     */
    public void getNotifyUrl(){
        Utils.print(tag,"getNotifyUrl");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetNotifyUrl(ConStant.APP_VERSION,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NotifyData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                        ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.error_qr_network_exception));
                    }

                    @Override
                    public void onNext(NotifyData notifyData) {
                        Utils.print(tag,"status=="+notifyData.getErrorMessage());
                        if(notifyData.getReturnValue()==-1)
                            return;

                        Utils.print(tag,"url="+notifyData.getData().getNotifyUrl());
                        notify_url = notifyData.getData().getNotifyUrl();
                        getRQUrl();
                    }
                });
        addSubscription(s);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(Utils.isConnected(mContext)){
                getPaymentStatus(orderSn);
                return false;
            }else{
                exitTips();
            }
        }else if(keyCode==KeyEvent.KEYCODE_MENU){
            PurchaseAgreementDialog dialog =new PurchaseAgreementDialog(mContext);
            if(!dialog.isShowing()){
                dialog.show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }




    /**
     * 获取支付结果状态
     */
    public void getPaymentStatus(String orderSn){
        Utils.print(tag,"getPaymentStatus");
        startProgressDialog();
        if(op_server){
            return;
        }
        op_server = true;
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("orderSn",orderSn); //订单号
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }
        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetPaymentStatus(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        op_server=false;
                        stopProgressDialog();
                        Utils.print(tag,"error="+e.getMessage());
                        exitTips();
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        op_server=false;
                        stopProgressDialog();
                        if(statusData.getReturnValue()==0){
                            Utils.print(tag,"get payment status sucess-------");
                            PaymentSucessUI.launch((PaymentPage)mContext);  //必须采用activity
                            finish();
                        }else{
                            exitTips();
                        }
                    }
                });
        addSubscription(s);
    }



    private void exitTips(){
        paymentTipDialog = new PaymentTipDialog(mContext, new PaymentTipDialog.ConfirmOnClickListener() {
            @Override
            public void onContinuePayment() {
                paymentTipDialog.dismiss();
            }

            @Override
            public void onExit() {
                paymentTipDialog.dismiss();
                sendConfirmOrder();
                finish();
                editor.putBoolean("isRefresh",false);
                editor.commit();
            }

            @Override
            public void onDismiss() {

            }

        });
        paymentTipDialog.showUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(ConStant.obString_opt_cancel_acution_order,observer);
        unregisterReceiver(nettyReceiver);
    }


    private void sendConfirmOrder(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    apiServer.paymentConfirm(orderSn);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }



    BroadcastReceiver nettyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msgJson = intent.getStringExtra("msgJson");
            Utils.print(tag,"netty>>>"+msgJson);
            Gson gson = new Gson();
            DomyTcpMsgBodyVo domyTcpMsgBodyVo = gson.fromJson(msgJson,DomyTcpMsgBodyVo.class);
            //Utils.print(tag,domyTcpMsgBodyVo.getInfo().get(0).getAction());

            if(domyTcpMsgBodyVo.getInfo().get(0).getActionType()==ConStant.ACTION_TYPE_PAYMENT && domyTcpMsgBodyVo.getInfo().get(0).getContentId().equals(ConStant.NETTY_OP_SUCESS)){
                Utils.print(tag,"payment sucess-------");
                PaymentSucessUI.launch((PaymentPage)mContext);  //必须采用activity
                finish();
            }

        }
    };

    private void regeditReceiver() {
        Utils.print(tag,"regeditReceiver netty");
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConStant.ACTION_NETTRY_RECEIVER);
        registerReceiver(nettyReceiver, mFilter);
    }



}

