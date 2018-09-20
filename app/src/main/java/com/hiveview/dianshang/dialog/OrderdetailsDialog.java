package com.hiveview.dianshang.dialog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.OrderdetailsListViewAdapter;
import com.hiveview.dianshang.adapter.ShoppingOrderAdapter;
import com.hiveview.dianshang.auction.AcutionInfomation;
import com.hiveview.dianshang.base.BDialog;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.acution.cancelorder.CancelOrderData;
import com.hiveview.dianshang.entity.acution.unpay.order.DomyAuctionOrderVo;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderUpdateData;
import com.hiveview.dianshang.entity.order.OrderInfo;
import com.hiveview.dianshang.entity.order.OrderRecord;
import com.hiveview.dianshang.entity.order.item.OrderGiftData;
import com.hiveview.dianshang.entity.order.item.OrderItemData;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfo;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfoData;
import com.hiveview.dianshang.entity.order.limit.OrderLimitData;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.home.PaymentPage;
import com.hiveview.dianshang.order.OrderMain;
import com.hiveview.dianshang.shoppingcart.ItemOnclickListener;
import com.hiveview.dianshang.shoppingcart.OpShoppingCart;
import com.hiveview.dianshang.showcommodity.CommodityInfomation;
import com.hiveview.dianshang.usercenter.UserAdderssSelect;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Gavin on 2017/5/5.
 */

public class OrderdetailsDialog extends BDialog implements ItemOnclickListener,OnItemViewSelectedListener,View.OnClickListener{


    private LayoutInflater mFactory = null;
    private Context context;
    private int status;
    private OrderLogisticsDialog orderLogisticsDialog;

    RecyclerView recyclerView;
    private LinearLayout layout_logistics;
    private Button logistics;
    private LinearLayout layout_function;
    private Button btn_function;
    private TextView orderNum;
    private TextView state;
    private TextView name;
    private TextView phone;
    private TextView address;
    private TextView commodityprice;
    private LinearLayout layout_discount;
    private TextView discountprice;
    private TextView count;
    private TextView carriage;
    private TextView orderprice;
    private TextView invoicetitle;
    private LinearLayout layout_invoicetitle;
    private TextView taxno;
    private LinearLayout layout_taxno;
    private TextView invoicecontent;
    private LinearLayout layout_invoicecontent;
    private LinearLayout layout_invoicetv;





    private String tag="OrderdetailsDialog";
    private String sn;
    private List<OrderItemInfo> orderItemInfoList;
    private OrderStatusInfo infoData;
    private Boolean b;
    private OrderRecord orderBean;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ShoppingOrderAdapter shoppingOrderAdapter;
    private boolean initRequestFocus = false;
    private ReceiverAfterSalesList receiverAfterSalesList;
    private ReceiverDismissorderdialog receiverDismissorderdialog;
    private RefundDialog refundDialog;
    private PopupWindow popupWindow;
    private ListView listView;
    private List<OrderItemInfo> itemInfosList;
    private OrderdetailsListViewAdapter adapter;
    public static int select_item_aftersale = -1;
    private int orderType;
    private List<OrderInfo> orderLeftList;
    //拍卖未支付功能键相关控件
    private LinearLayout layout_usercenter;
    private Button usercenter;
    private RelativeLayout layout_pay;
    private TextView paytip;
    private TextView time_tv;
    private LinearLayout layout_cancel;
    private Button cancel;
    private RelativeLayout layout_invoice;
    private ImageView invoice_status;
    private TextView invoice_type;
    //拍卖未支付右边信息的对象
    private DomyAuctionOrderVo auctionOrderBean;
    //发票类型
    private int invoiceType = ConStant.INVOICE_NULL;
    //发票填写的信息
    private String[] invoice_info_value;
    //地址
    private int receiveId=-1;
    //地址，发票广播
    private Observable<OpShoppingCart> observable;
    //地址信息
    private String[] address_info_value;
    //计时支付
    private CommodityCountDownTimer timer;
    private long TIME = 25 * 1000L;
    private final long INTERVAL = 1000L;
    /**
     * 服务器交互标志位
     */
    private boolean op_status = false;



    public OrderdetailsDialog(Context context,String sn,Boolean b,int status,OrderRecord orderBean,int orderType,List<OrderInfo> orderLeftList,DomyAuctionOrderVo auctionOrderBean) {
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.sn=sn;
        this.b=b;
        this.status=status;
        this.orderBean=orderBean;
        this.orderType=orderType;
        this.orderLeftList=orderLeftList;
        this.auctionOrderBean=auctionOrderBean;


        mFactory = LayoutInflater.from(context);
        View mView = mFactory.inflate(R.layout.layout_orderdetailsdialog, null);

        setContentView(mView);
        preferences = context.getSharedPreferences("isRefreshOrder",
                context.MODE_WORLD_READABLE);
        editor = preferences.edit();


        initShoppingCartView(mView);


    }

    public void showUI(){

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Utils.getScreenW(context);
        lp.height = Utils.getScrrenH(context);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);


        show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        editor.putBoolean("isRefresh",true);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            if(receiverDismissorderdialog!=null){
                context.unregisterReceiver(receiverDismissorderdialog);
            }
            if(observable!=null){
                RxBus.get().unregister(ConStant.obString_modify_shopping_cart,observable);
            }
            cancelTimer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initShoppingCartView(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        orderNum=(TextView)view.findViewById(R.id.orderNum);
        state=(TextView)view.findViewById(R.id.state);
        name=(TextView)view.findViewById(R.id.name);
        phone=(TextView)view.findViewById(R.id.phone);
        address=(TextView)view.findViewById(R.id.address);
        commodityprice=(TextView)view.findViewById(R.id.commodityprice);
        layout_discount=(LinearLayout)view.findViewById(R.id.layout_discount);
        discountprice=(TextView)view.findViewById(R.id.discountprice);
        count=(TextView)view.findViewById(R.id.count);
        carriage=(TextView)view.findViewById(R.id.carriage);
        orderprice=(TextView)view.findViewById(R.id.orderprice);
        invoicetitle=(TextView)view.findViewById(R.id.invoicetitle);
        taxno=(TextView)view.findViewById(R.id.taxno);
        layout_taxno=(LinearLayout)view.findViewById(R.id.layout_taxno);
        invoicecontent=(TextView)view.findViewById(R.id.invoicecontent);
        layout_logistics=(LinearLayout)view.findViewById(R.id.layout_logistics);
        logistics=(Button)view.findViewById(R.id.logistics);
        layout_function=(LinearLayout)view.findViewById(R.id.layout_function);
        btn_function=(Button)view.findViewById(R.id.btn_function);
        layout_invoicetv=(LinearLayout)view.findViewById(R.id.layout_invoicetv);
        layout_invoicetitle=(LinearLayout)view.findViewById(R.id.layout_invoicetitle);
        layout_invoicecontent=(LinearLayout)view.findViewById(R.id.layout_invoicecontent);
        layout_function.setVisibility(View.GONE);
        //拍卖未支付功能键
        layout_usercenter=(LinearLayout)view.findViewById(R.id.layout_usercenter);
        usercenter=(Button)view.findViewById(R.id.usercenter);
        layout_pay=(RelativeLayout)view.findViewById(R.id.layout_pay);
        paytip=(TextView)view.findViewById(R.id.paytip);
        time_tv=(TextView)view.findViewById(R.id.time);
        layout_cancel=(LinearLayout)view.findViewById(R.id.layout_cancel);
        cancel=(Button)view.findViewById(R.id.cancel);
        layout_invoice=(RelativeLayout)view.findViewById(R.id.layout_invoice);
        invoice_status=(ImageView)view.findViewById(R.id.invoice_status);
        invoice_type=(TextView)view.findViewById(R.id.invoice_type);
        if(orderType== OrderMain.ORDER_TYPE_AUCTON_NO){//只有是拍卖未支付才显示的控件
            layout_usercenter.setVisibility(View.VISIBLE);
            layout_pay.setVisibility(View.VISIBLE);
            layout_cancel.setVisibility(View.VISIBLE);
            layout_invoice.setVisibility(View.VISIBLE);
        }else{//非拍卖未支付订单的详情
            layout_usercenter.setVisibility(View.GONE);
            layout_pay.setVisibility(View.GONE);
            layout_cancel.setVisibility(View.GONE);
            layout_invoice.setVisibility(View.GONE);
        }
        if(!b){//不显示查看物流按钮
           // logistics.setVisibility(View.GONE);
            layout_logistics.setVisibility(View.GONE);
        }
        logistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"点击物流查看！",Toast.LENGTH_LONG).show();
                if(!Utils.isConnected(context)){
                    String error_tips = context.getResources().getString(R.string.error_network_exception);
                    ToastUtils.showToast(context,error_tips);
                }else{
                    if(infoData!=null){
                        orderLogisticsDialog = new OrderLogisticsDialog(context,infoData);
                        if(!orderLogisticsDialog.isShowing()){
                            orderLogisticsDialog.show();
                        }
                    }else{
                        String error_tips = context.getResources().getString(R.string.doexception);
                        ToastUtils.showToast(context,error_tips);
                    }

                }
            }
        });
        if(orderType== OrderMain.ORDER_TYPE_COMMON_YN){//普通订单
            btn_function.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //如果超过30天，则没有售后服务，点击弹toast
                    String function_tv=((Button)v).getText().toString();
                    Log.i("========","===功能键的文本内容=="+function_tv);
                    if(function_tv.equals(context.getResources().getString(R.string.refundtv))){

                        //注册广播
                        receiverAfterSalesList = new ReceiverAfterSalesList ();
                        //实例化过滤器并设置要过滤的广播
                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("com.dianshang.damai.message_changed");
                        context.registerReceiver(receiverAfterSalesList, intentFilter);

                        //弹出退款的对话框
                        refundDialog =new RefundDialog(context,orderBean);
                        if(!refundDialog.isShowing()){
                            refundDialog.show();
                        }
                        refundDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                // Toast.makeText(context, "适配器中的退款对话框消失!", Toast.LENGTH_SHORT).show();
                                if(receiverAfterSalesList!=null){
                                    context.unregisterReceiver(receiverAfterSalesList);
                                }
                                if(RefundDialog.receiverRefund!=null){
                                    // Toast.makeText(context, "注销退款广播!", Toast.LENGTH_SHORT).show();
                                    context.unregisterReceiver(RefundDialog.receiverRefund);
                                }

                            }
                        });

                    }else if(function_tv.equals(context.getResources().getString(R.string.aftersale))){
                        String time=String.valueOf(orderBean.getCompleteDate());
                        long totaltime=0;
                        if(time!=null&&time.length()>2){
                            time=time.substring(0,time.length()-3);
                            totaltime=Long.parseLong(time)+(long)30*86400;
                            Log.i("=========","===总时间=="+totaltime);
                        }
                        Log.i("=========","===系统当前时间=="+System.currentTimeMillis());
                        String currentTime=String.valueOf(System.currentTimeMillis());
                        currentTime=currentTime.substring(0,currentTime.length()-3);
                        long currentTimeLong=Long.parseLong(currentTime);
                        if(currentTimeLong>totaltime){//超过30天
                            ToastUtils.showToast(context,context.getResources().getString(R.string.aftersalestip));
                        }else{//未超过30天
                            //注册广播
                            receiverAfterSalesList = new ReceiverAfterSalesList ();
                            //实例化过滤器并设置要过滤的广播
                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction("com.dianshang.damai.message_changed");
                            context.registerReceiver(receiverAfterSalesList, intentFilter);
                            initPopupWindow();
                        }


                    }else if(function_tv.equals(context.getResources().getString(R.string.pay))){
                        String payName = "";
                        for(int i=0;i<orderBean.getOrderInfoList().size();i++){
                            if(i==orderBean.getOrderInfoList().size()-1){
                                payName = payName + orderBean.getOrderInfoList().get(i).getGoodsName();
                            }else{
                                payName = payName + orderBean.getOrderInfoList().get(i).getGoodsName()+"-";
                            }
                        }
                        if(payName.length()>100){
                            payName = payName.substring(0,100);
                        }
                        getOrderLimitBuy(orderBean.getOrderSn(),orderBean.getAmount(),payName);

                    }

                }
            });
            //适配左边列表
            getOrderItemInfoData(sn);
        }else{//拍卖订单
            List<OrderGiftData> fullGiftList =null;
            //获取左边列表数据
            List<OrderItemInfo> orderLeftData=new ArrayList<OrderItemInfo>();
            for(int i=0;i<orderLeftList.size();i++){
                OrderItemInfo infoBean=new OrderItemInfo();
                infoBean.setGoodsName(orderLeftList.get(i).getGoodsName());
                infoBean.setPrice(orderLeftList.get(i).getPrice());
                infoBean.setQuantity(orderLeftList.get(i).getQuantity());
                infoBean.setThumbnail(orderLeftList.get(i).getThumbnail());
                infoBean.setSpecifications(orderLeftList.get(i).getSpecifications());
                orderLeftData.add(infoBean);
            }
            orderItemInfoList=orderLeftData;
            initRecyclerView(fullGiftList,orderLeftData);
        }

        //适配右边信息
        if(orderType==OrderMain.ORDER_TYPE_AUCTON_NO){//拍卖未支付详情页右边适配数据是在列表点击时拿到的
            state.setText(context.getResources().getString(R.string.waitingpayment));
            initEvent();
            //初始化支付计时
           /*  Date curDate =  new Date(System.currentTimeMillis());
               TIME = curDate.getTime()-auctionOrderBean.getTimeStamp();*/
            long consumeTime = auctionOrderBean.getTimeStamp()-auctionOrderBean.getCreateTime();
            long totalTime = auctionOrderBean.getExpireTime()-auctionOrderBean.getCreateTime();
            TIME = totalTime-consumeTime;
            startTimer();
            orderNum.setText(context.getResources().getString(R.string.ordernum)+sn);
            commodityprice.setText("¥"+Utils.getPrice(auctionOrderBean.getPrice()));
            layout_discount.setVisibility(View.INVISIBLE);
            count.setText(auctionOrderBean.getQuantity()+"");
            carriage.setText("¥"+Utils.getPrice(auctionOrderBean.getFreight()));
            orderprice.setText("¥"+Utils.getPrice(auctionOrderBean.getAmount()));
            //地址初始化,,,,在503默认地址接口使用！！！
            if(auctionOrderBean.getConsignee().equals("-")){
                name.setText("");
            }else{
                name.setText(auctionOrderBean.getConsignee());
            }
            if(auctionOrderBean.getPhone().equals("-")){
                phone.setText("");
            }else{
                phone.setText(auctionOrderBean.getPhone());
            }
            if(auctionOrderBean.getAddress().equals("-")){
                address.setText("");
            }else{
                address.setText(auctionOrderBean.getAddress());
            }

            //发票信息的初始化，但不用显示，因为只有在交易关闭时显示
            if(auctionOrderBean.getIsInvoice()!=ConStant.INVOICE_NULL){//有发票
                if(auctionOrderBean.getInvoiceType().equals(context.getResources().getString(R.string.person))){
                    //个人发票
                    invoiceType=ConStant.INVOICE_PERSON;
                }else if(auctionOrderBean.getInvoiceType().equals(context.getResources().getString(R.string.company))){
                    //公司发票
                    invoiceType=ConStant.INVOICE_COMPANY;
                }
                invoice_status.setVisibility(View.VISIBLE);
                if(invoiceType==ConStant.INVOICE_COMPANY){//发票为公司
                    invoice_info_value=new String[4];
               /*  String valueFirst=" "+"#"+" "+"#"+" "+"#"+" ";
                invoice_info_value=valueFirst.split("#");*/
                    invoice_info_value[1]=auctionOrderBean.getInvoiceTitle();
                    invoice_info_value[2]=auctionOrderBean.getInvoiceId();
                    invoice_info_value[3]=auctionOrderBean.getInvoiceContent();
                }
            }else{//无发票
                invoiceType=ConStant.INVOICE_NULL;
                invoice_status.setVisibility(View.GONE);
            }
            layout_pay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    //处理获得和失去焦点两种状态的视觉效果
                    if(hasFocus){
                        paytip.setTextColor(context.getResources().getColor(android.R.color.white));
                    }else{
                        paytip.setTextColor(android.graphics.Color.parseColor("#3B3B3B"));
                    }
                }
            });
            layout_invoice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    //处理获得和失去焦点两种状态的视觉效果
                    if(hasFocus){
                        invoice_type.setTextColor(context.getResources().getColor(android.R.color.white));
                    }else{
                        invoice_type.setTextColor(android.graphics.Color.parseColor("#3B3B3B"));
                    }
                }
            });
            usercenter.setOnClickListener(this);
            layout_pay.setOnClickListener(this);
            cancel.setOnClickListener(this);
            layout_invoice.setOnClickListener(this);


        }else{//其他订单详情页右边都是进入该页面请求接口数据
            getOrderStatusInfoData(sn);
        }



        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(initRequestFocus){
                    initRequestFocus = false;
                    if(recyclerView.getChildCount()>0){
                        recyclerView.getChildAt(0).requestFocus();
                    }
                }
            }
        });

    }
    //拍卖未支付功能键按钮的点击监听
    @Override
    public void onClick(View v) {
        //正在交互
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            return;
        switch (v.getId()) {
            case R.id.usercenter:
               editor.putBoolean("isRefresh",false);
                editor.commit();
                if(address!=null&&!address.getText().toString().equals("")){//地址文本不为空时
                    UserAdderssSelect.launch((MainActivity)context,receiveId,true);
                }else{
                    UserAdderssSelect.launch((MainActivity)context,receiveId,false);
                }
                break;
            case R.id.layout_pay:
                if(address.getText().toString().equals("")){
                    ToastUtils.showToast(context,context.getResources().getString(R.string.error_address_null));
                    return;
                }

                //检测网络状态
                if(!Utils.isConnected(context)){
                    String error_tips = context.getResources().getString(R.string.error_network_exception);
                    ToastUtils.showToast(context,error_tips);
                    return;
                }

                //正在交互
                if(mProgressDialog!=null && mProgressDialog.isShowing())
                    return;
                String payName = "";
                for(int i=0;i<orderLeftList.size();i++){
                    if(i==orderLeftList.size()-1){
                        payName = payName + orderLeftList.get(i).getGoodsName();
                    }else{
                        payName = payName + orderLeftList.get(i).getGoodsName()+"-";
                    }
                }
                if(payName.length()>100){
                    payName = payName.substring(0,100);
                }
                //注册广播
                receiverDismissorderdialog = new ReceiverDismissorderdialog ();
                //实例化过滤器并设置要过滤的广播
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("com.dianshang.damai.dismissorderdialog");
                context.registerReceiver(receiverDismissorderdialog, intentFilter);
                PaymentPage.launch((Activity)context,sn,auctionOrderBean.getAmount(),payName);
                break;
            case R.id.cancel:
               getCancelAcutionOrder(sn);
               sendAcutionCancelOrderStatistics();
                break;
            case R.id.layout_invoice:
                invoiceOperate();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RxBus.get().post(ConStant.obString_select_commodity_type,"back");
    }

    /**
     * 查看订单详情
     */
    public void getOrderItemInfoData(String orderSn){
        Utils.print(tag,"getOrderItemData");
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.userID);
            json.put("orderSn",orderSn);
            json.put("pageIndex","1");
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetOrderItemData(ConStant.APP_VERSION,input,ConStant.Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderItemData>() {
                    @Override
                    public void onCompleted() {
                        /*if(orderItemInfoList!=null){
                            adapter = new OrderdetailsListViewCheckAdapter(context,orderItemInfoList);
                            listView.setAdapter(adapter);
                        }*/

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"===error="+e.getMessage());
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                    }

                    @Override
                    public void onNext(OrderItemData orderItemData) {
                        Utils.print(tag,"===status=="+orderItemData.getErrorMessage());
                        if(orderItemData.getReturnValue()==-1) {
                            ToastUtils.showToast(context,orderItemData.getErrorMessage());
                            return;
                        }


                        List<OrderGiftData> fullGiftList = orderItemData.getData().getFullGiftList();
                        if(fullGiftList!=null){
                            for (int i = 0; i < fullGiftList.size(); i++) {
                                Utils.print(tag, "name=" + fullGiftList.get(i).getGiftName());
                            }
                        }


                        List<OrderItemInfo> orderRecords = orderItemData.getData().getOrderContentDataVoPage().getRecords();
                        orderItemInfoList=orderRecords;
                        if(orderRecords!=null){
                            for (int i=0;i<orderRecords.size();i++){
                                Utils.print(tag,"===time="+orderRecords.get(i).getGoodsName()+",orderSn="+orderRecords.get(i).getGoodsTitle());
                            }
                        }


                        initRecyclerView(fullGiftList,orderRecords);

                    }
                });
        addSubscription(subscription);
    }

    private void initRecyclerView(List<OrderGiftData> fullGiftList,List<OrderItemInfo> orderRecords){

        recyclerView.setHasFixedSize(true);

        /**
         * 设置纵向滚动，也可以设置横向滚动
         */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context); //listview 垂直方向
        recyclerView.setLayoutManager(layoutManager);

        /**
         * 增加分割线
         */
        recyclerView.addItemDecoration(new DividerItemDecoration(
                context, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(context, R.color.shopping_cart_order_item_div)));


        shoppingOrderAdapter = new ShoppingOrderAdapter(context,fullGiftList,orderRecords,ShoppingOrderAdapter.ORDER_LIST,this);
        recyclerView.setAdapter(shoppingOrderAdapter);


        Utils.print(tag,"s ....."+shoppingOrderAdapter.getItemCount());

        initRequestFocus = true;
    }


    /**
     * 查看订单详情,总价,状态,快递信息
     */
    public void getOrderStatusInfoData(String orderSn){
        Utils.print(tag,"getOrderStatusInfoData");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.userID);
            json.put("orderSn",orderSn);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetOrderStatusInfoData(ConStant.APP_VERSION,input,ConStant.Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderStatusInfoData>() {
                    @Override
                    public void onCompleted() {
                        //infoData  数据对象，，，
                        if(infoData!=null){
                            orderNum.setText(context.getResources().getString(R.string.ordernum)+infoData.getOrderSn());
                            if(infoData.getStatus()==Status.pendingPayment.ordinal()){
                                state.setText(context.getResources().getString(R.string.waitingpayment));
                                if(orderType== OrderMain.ORDER_TYPE_COMMON_YN){
                                    layout_function.setVisibility(View.VISIBLE);
                                    btn_function.setText(context.getResources().getString(R.string.pay));
                                }

                            }else if(infoData.getStatus()==Status.pendingShipment.ordinal()||infoData.getStatus()==12){
                                state.setText(context.getResources().getString(R.string.sendgoods));
                                if(orderType== OrderMain.ORDER_TYPE_COMMON_YN){
                                    if(orderBean.getRefundStatus()!=null&&(orderBean.getRefundStatus()==2||orderBean.getRefundStatus()==1)){//已申请退款,无需再申请
                                        layout_function.setVisibility(View.GONE);
                                    }else{
                                        layout_function.setVisibility(View.VISIBLE);
                                        btn_function.setText(context.getResources().getString(R.string.refundtv));
                                    }
                                }


                            }else if(infoData.getStatus()==Status.pendingReceive.ordinal()){
                                state.setText(context.getResources().getString(R.string.receivinggoods));
                                if(orderType== OrderMain.ORDER_TYPE_COMMON_YN){
                                    /**
                                     * 待收货是否可退款标识 0没有申请退款 1 待发货已申请退款 2 待收货已申请退款
                                     */
                                    if(orderBean.getRefundStatus()!=null&&(orderBean.getRefundStatus()==2||orderBean.getRefundStatus()==1)){//已申请退款,无需再申请
                                        layout_function.setVisibility(View.GONE);
                                    }else{
                                        layout_function.setVisibility(View.VISIBLE);
                                        btn_function.setText(context.getResources().getString(R.string.refundtv));
                                    }
                                }


                            }else if(infoData.getStatus()==Status.completed.ordinal()){
                                state.setText(context.getResources().getString(R.string.completetransaction));
                                if(orderType== OrderMain.ORDER_TYPE_COMMON_YN){
                                    layout_function.setVisibility(View.VISIBLE);
                                    btn_function.setText(context.getResources().getString(R.string.aftersale));
                                }

                            }else{
                                state.setText(context.getResources().getString(R.string.tradingclosed));
                                layout_function.setVisibility(View.GONE);
                            }
                            if(infoData.getReceiveName().equals("-")){
                                name.setText("");
                            }else{
                                name.setText(infoData.getReceiveName());
                            }
                            if(infoData.getReceivePhone().equals("-")){
                                phone.setText("");
                            }else{
                                phone.setText(infoData.getReceivePhone());
                            }
                            if(infoData.getReceiveAddress().equals("-")){
                                address.setText("");
                            }else{
                                address.setText(infoData.getReceiveAddress());
                            }
							//调用处理数据的方法
                            printNum(commodityprice);
                            printNum(discountprice);
                            count.setText(infoData.getQuantity()+"");
                            printNum(carriage);
                            printNum(orderprice);
                            //commodityprice.setText(infoData.getPrice()+"");
                           // count.setText(infoData.getQuantity()+"");
                            //carriage.setText(infoData.getFreight()+"");
                            //orderprice.setText(infoData.getAmount()+"");
                            Log.i("=======","===IsInvoice=="+infoData.getIsInvoice());
                            if(infoData.getIsInvoice()==ConStant.INVOICE_NULL){//无发票 不显示
                                layout_invoicetv.setVisibility(View.GONE);
                            }else{//有发票
                                if(infoData.getInvoiceType().equals(context.getResources().getString(R.string.unit))){//公司  全显示
                                    layout_invoicetv.setVisibility(View.VISIBLE);
                                    layout_invoicetitle.setVisibility(View.VISIBLE);
                                    layout_taxno.setVisibility(View.VISIBLE);
                                    layout_invoicecontent.setVisibility(View.VISIBLE);
                                    invoicetitle.setText(context.getResources().getString(R.string.invoicetitle)+infoData.getInvoiceTitle());
                                    taxno.setText(context.getResources().getString(R.string.invoice_number)+infoData.getInvoiceId());
                                    invoicecontent.setText(context.getResources().getString(R.string.invoicecontent)+infoData.getInvoiceContent());
                                }else{//个人 不显示发票内容也无（发票号taxno）
                                    layout_invoicetv.setVisibility(View.VISIBLE);
                                    layout_invoicetitle.setVisibility(View.VISIBLE);
                                    invoicetitle.setText(context.getResources().getString(R.string.invocetitle));
                                    layout_invoicecontent.setVisibility(View.GONE);
                                    layout_taxno.setVisibility(View.GONE);
                                }
                            }


                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(OrderStatusInfoData orderStatusInfoData) {
                        Utils.print(tag,"status=="+orderStatusInfoData.getErrorMessage());
                        if(orderStatusInfoData.getReturnValue()==-1){
                            ToastUtils.showToast(context,orderStatusInfoData.getErrorMessage());
                            return;
                        }

                        OrderStatusInfo info = orderStatusInfoData.getData();
                        infoData=info;

                        Utils.print(tag,"time="+info.getDeliveryCorpCode()+",xxx="+info.getStatus());


                    }
                });
        addSubscription(subscription);
    }

    /**
     * 该方法用于修改订单界面中的小数数据，实现去除小数部分为0的数据的功能
     */
    private void printNum(TextView tv) {
        switch (tv.getId()){
            case R.id.commodityprice:
                //处理小数部分为0部分
//                                String s1 = Utils.getPrice(infoData.getPrice());
//                                String s1 = String.valueOf(infoData.getPrice());
//                                String s2 = s1.substring(s1.indexOf("."),s1.length());
//                                String s3 = s1.substring(0,s1.indexOf("."));
//                                if (s2.equals(".00")|| s2.equals(".0")){//如果线束不问为0，只显示整数部分
//                                    tv.setText("¥"+s3);
//                                }else if(s1.substring(s1.length()-1).equals("0")){//如果数据百分位为0，则显示的数据只保留到十分位
//                                    tv.setText("¥"+s1.substring(0,s1.indexOf(".")+2)+"");
//                                }else {//如果小数部分都不为0，输出整个数
//                                    tv.setText("¥"+Utils.getPrice(infoData.getPrice()));
//                                }

                tv.setText("¥"+Utils.getPrice(infoData.getPrice()));
                break;
            case R.id.discountprice:
                Log.i("====4444====","==价格=="+infoData.getDiscountPrice());
                tv.setText("-¥"+Utils.getPrice(infoData.getDiscountPrice()));

                if(tv.getText().toString().equals("-¥0")||tv.getText().toString().equals("-¥0.00")){//无优惠价格
                    layout_discount.setVisibility(View.INVISIBLE);
                }else{
                    layout_discount.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.carriage:
                tv.setText("¥"+Utils.getPrice(infoData.getFreight()));

                break;
            case R.id.orderprice:
                tv.setText("¥"+Utils.getPrice(infoData.getAmount()));

                break;
        };
    }

    /**
     * 状态
     */
    public enum Status {
        //Status.pendingPayment.ordinal()

        /** 等待付款 */
        pendingPayment,

        /** 等待发货 */
        pendingShipment,
        /**
         *  待收货
         */
        pendingReceive,

        /** 交易成功 */
        completed,

        /**
         * 退款处理中（用户发起-商户确认）
         */
        processing,

        /**
         * 退款中（商户确认）
         */
        refund_seller,

        /**
         * 退款中（家视）
         */
        refund_hiveview,

        /**
         * 已退款
         */
        refunded,

        /**
         * 换货单
         */
        exchange,

        /**
         * 交易关闭
         */
        close,

    }


    @Override
    public void onItemClickListener(int position) {
        Utils.print(tag,"postion=="+position);
        //正在交互
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            return;
        editor.putBoolean("isRefresh",false);
        editor.commit();
        if(orderType== OrderMain.ORDER_TYPE_COMMON_YN){//普通订单
            CommodityInfomation.launch((Activity)context,orderItemInfoList.get(position).getGoodsSn(),ConStant.ORDER_TO_INFO);
        }else if(orderType== OrderMain.ORDER_TYPE_AUCTON_NO){//拍卖未支付订单
            AcutionInfomation.launch((Activity)context,auctionOrderBean.getGoodSn());
        }else if(orderType== OrderMain.ORDER_TYPE_AUCTON_OTHER){//拍卖其他状态订单
            AcutionInfomation.launch((Activity)context,orderBean.getOrderInfoList().get(0).getGoodsSn());
        }

    }


    @Override
    public void OnItemViewSelectedListener(int position) {
        Utils.print(tag,"positon==="+position+",count="+recyclerView.getAdapter().getItemCount());
        if(position==recyclerView.getAdapter().getItemCount()-1){
            recyclerView.scrollToPosition(position);
        }
    }

    public class ReceiverAfterSalesList extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            if(popupWindow!=null&&popupWindow.isShowing()){
                popupWindow.dismiss();
            }
            if(refundDialog!=null&&refundDialog.isShowing()){
                refundDialog.dismiss();
            }
            if(OrderdetailsDialog.this!=null&&OrderdetailsDialog.this.isShowing()){
                OrderdetailsDialog.this.dismiss();
            }

        }

    }
    public class ReceiverDismissorderdialog extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            if(popupWindow!=null&&popupWindow.isShowing()){
                popupWindow.dismiss();
            }
            if(refundDialog!=null&&refundDialog.isShowing()){
                refundDialog.dismiss();
            }
            if(OrderdetailsDialog.this!=null&&OrderdetailsDialog.this.isShowing()){
                OrderdetailsDialog.this.dismiss();
            }

        }

    }

    public enum Location {

        LEFT,
        RIGHT,
        TOP,
        BOTTOM;

    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     */
    class popupDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
           // backgroundAlpha(1f);
            try{
                if(receiverAfterSalesList!=null){
                    context.unregisterReceiver(receiverAfterSalesList);
                }
            }catch (Exception e){
                e.printStackTrace();
            }




        }

    }

    protected void initPopupWindow(){
        View popupWindowView = LayoutInflater.from(context).inflate(R.layout.layout_aftersales, null);
        //内容，高度，宽度
            popupWindow = new PopupWindow(popupWindowView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
        //菜单背景色
       ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        popupWindow.setBackgroundDrawable(dw);
        //显示位置
        popupWindow.showAtLocation(btn_function, Gravity.CENTER, 0, 0);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());
        listView=(ListView)popupWindowView.findViewById(R.id.listView);


        int status=ConStant.ORDER_STATUS_UNPAY;
        if(orderBean.getStatus()==Status.pendingPayment.ordinal()){
            //待付款
            status=ConStant.ORDER_STATUS_UNPAY;
        }else if(orderBean.getStatus()==Status.pendingShipment.ordinal()||orderBean.getStatus()==12) {
            //待发货
            status=ConStant.ORDER_STATUS_UNDELIVERY;

        }else if(orderBean.getStatus()==Status.pendingReceive.ordinal()) {
            //待收货
            status=ConStant.ORDER_STATUS_UNTACK_DELIVERY;

        }else if(orderBean.getStatus()==Status.completed.ordinal()) {
            //交易完成
            status=ConStant.ORDER_STATUS_TRANSACTION_SUCESS;
        }else{//交易关闭
            status=ConStant.ORDER_STATUS_TRANSACTION_CLOSE;
        }

        getOrderItemInfoDataDialog(status);
        //售后服务商品列表的点击监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AfterSalesDialog afterSalesDialog = new AfterSalesDialog(context,itemInfosList.get(position),orderBean);
                if(!afterSalesDialog.isShowing()){
                    afterSalesDialog.show();
                }

            }
        });
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_item_aftersale=position;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity)context).getWindow().setAttributes(lp);
    }

    /**
     * 判断是否能够购买,是否已经超过限制购买
     */
    public void getOrderLimitBuy(String orderSn,double amount,String payName) {
        Utils.print(tag, "getOrderLimitBuy");
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }
        if(op_status)
            return;
        op_status = true;
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(context).userID);
            json.put("orderSn", orderSn); //购物车id
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpOrderLimitBuy(ConStant.APP_VERSION,input, ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderLimitData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                        op_status = false;
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                    }

                    @Override
                    public void onNext(OrderLimitData data) {
                        Utils.print(tag, "status==" + data.getErrorMessage() + ",value=" + data.getReturnValue());
                        op_status = false;
                        if (data.getReturnValue() == -1){
                            ToastUtils.showToast(context,data.getErrorMessage());
                            return;
                        }

                        Utils.print(tag, "canpuy==" + data.getData().isCanPay());
                        if(data.getData().isCanPay()){
                            //注册广播
                            receiverDismissorderdialog = new ReceiverDismissorderdialog ();
                            //实例化过滤器并设置要过滤的广播
                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction("com.dianshang.damai.dismissorderdialog");
                            context.registerReceiver(receiverDismissorderdialog, intentFilter);
                            PaymentPage.launch((Activity)context,orderSn,amount,payName);
                        }else{
                            ToastUtils.showToast(context,context.getResources().getString(R.string.order_limit_buy_tips));
                        }


                    }
                });
    }

    /**
     * 查看订单详情
     */
    public void getOrderItemInfoDataDialog(int status){
        Utils.print(tag,"getOrderItemData");
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(context).userID);
            json.put("orderStatus",status);
            json.put("orderSn",orderBean.getOrderSn());
            json.put("pageIndex","1");
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetOrderItemData(ConStant.APP_VERSION,input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderItemData>() {
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
                    public void onNext(OrderItemData orderItemData) {
                        Utils.print(tag,"status=="+orderItemData.getErrorMessage());
                        if(orderItemData.getReturnValue()==-1)
                            return;
                        List<OrderItemInfo> orderRecords = orderItemData.getData().getOrderContentDataVoPage().getRecords();
                        itemInfosList=orderRecords;

                        for (int i=0;i<orderRecords.size();i++){
                            Utils.print(tag,"time="+orderRecords.get(i).getGoodsName()+",orderSn="+orderRecords.get(i).getGoodsTitle());
                        }

                        //不能放在onCompleted里面,假如服务器数据异常，onNext　return 是拦截不了onCompleted执行
                        //适配右边弹出的列表 数据==itemInfosList
                        adapter = new OrderdetailsListViewAdapter(context,itemInfosList);
                        listView.setAdapter(adapter);
                    }
                });
        ((MainActivity)context).addSubscription(subscription);
    }
    private void initEvent(){
        observable = RxBus.get().register(ConStant.obString_modify_shopping_cart, OpShoppingCart.class);
        observable.subscribe(new Action1<OpShoppingCart>() {
            @Override
            public void call(OpShoppingCart op) {
                Utils.print(tag, "op=" + op.getKey());
                switch (op.getKey()){
                    case OpShoppingCart.MODIFY_INVOICE:
                        getUpdateAcutionInvoiceData(sn, op);
                        break;
                    case OpShoppingCart.MODIFY_ADDRESS:
                        address_info_value = op.getValue().split("#");
                        Utils.print(tag,"id=="+Integer.parseInt(address_info_value[0]));
                        receiveId = Integer.parseInt(address_info_value[0]);
                        getUpdateAcutionReceiveData(sn, receiveId);
                        break;
                }
            }
        });


    }
    /**
     * 更新拍卖未支付的订单的地址信息
     */
    public void getUpdateAcutionReceiveData(String orderSn,int receiveId){
        Utils.print(tag,"getUpdateAcutionReceiveData");
        if(op_status)
            return;
        op_status = true;
        startProgressDialog();
        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("orderSn",orderSn); //订单号
            json.put("receiveId", receiveId); //邮寄地址ID
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            sign = Utils.getMD5(domyshop_value);

        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpUpdateAcutionReceiveData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UnpayOrderUpdateData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String error_tips = "";
                        stopProgressDialog();
                        op_status=false;
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderUpdateData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        op_status=false;
                        stopProgressDialog();
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(context,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData()!=null){

                            Utils.print(tag,"orderSn=="+statusData.getData().getOrderSn());
                        }
                        if(address_info_value.length>1){
                            Utils.print(tag,"address=="+address_info_value[1]);
                            String address_str=format(address_info_value[1]);
                            String d[] = address_str.split("#");
                            for(int i=0;i<d.length;i++){
                                if(i==0){//名字
                                    name.setText(d[0]);
                                }else if(i==1){//电话
                                    phone.setText(d[1]);
                                }else{//地址
                                    address.setText(d[2]);
                                }
                            }
                        }else{//名字，电话，地址为空
                            name.setText("");
                            phone.setText("");
                            address.setText("");
                        }
                    }
                });

    }
    public static String format(String s){
        String str=s.replace("     ","#");
        str = str.replace("\n","#");//回车
        return str;
    }
    /**
     * 取消拍卖的商品参加竞拍
     */
    public void getCancelAcutionOrder(String orderSn){
        Utils.print(tag,"getCancelAcutionOrder");

        if(op_status)
            return;
        op_status = true;

        startProgressDialog();

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("orderSn",orderSn); //订单号
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            sign = Utils.getMD5(domyshop_value);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }


        Subscription s = RetrofitClient.getCommodityAPI()
                .httpCancelAcutionOrder(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CancelOrderData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                        stopProgressDialog();
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        op_status = false;
                    }

                    @Override
                    public void onNext(CancelOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();
                        op_status=false;
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(context,statusData.getErrorMessage());
                            return;
                        }
                        Utils.print(tag,"sn=="+statusData.getData().getOrderSn());

                        //状态改为交易关闭
                        state.setText(context.getResources().getString(R.string.tradingclosed));
                        //拍卖订单未支付的功能键全部隐藏
                        layout_usercenter.setVisibility(View.GONE);
                        layout_pay.setVisibility(View.GONE);
                        layout_cancel.setVisibility(View.GONE);
                        layout_invoice.setVisibility(View.GONE);
                        //保证返回订单列表后，列表数据刷新
                        //进度条出现处理已处理
                        Intent mIntent = new Intent("com.dianshang.damai.message_restore");
                        //发送广播
                        mIntent.putExtra("restore", true);
                        mIntent.putExtra("orderrefresh", true);
                        context.sendBroadcast(mIntent);
                        //发票显示处理
                        if(invoiceType==ConStant.INVOICE_NULL){//无发票
                            layout_invoicetv.setVisibility(View.GONE);
                        }else{//有发票
                            layout_invoicetv.setVisibility(View.VISIBLE);
                            if(invoiceType==ConStant.INVOICE_PERSON){//个人（只显示title）
                                layout_invoicetitle.setVisibility(View.VISIBLE);
                                invoicetitle.setText(context.getResources().getString(R.string.invocetitle));
                                layout_taxno.setVisibility(View.GONE);
                                layout_invoicecontent.setVisibility(View.GONE);
                            }else if(invoiceType==ConStant.INVOICE_COMPANY){//公司（title,识别号，内容，全部显示）
                                layout_invoicetitle.setVisibility(View.VISIBLE);
                                layout_taxno.setVisibility(View.VISIBLE);
                                layout_invoicecontent.setVisibility(View.VISIBLE);
                                invoicetitle.setText(context.getResources().getString(R.string.invoicetitle)+invoice_info_value[1]);
                                taxno.setText(context.getResources().getString(R.string.invoice_number)+invoice_info_value[2]);
                                invoicecontent.setText(context.getResources().getString(R.string.invoicecontent)+invoice_info_value[3]);
                            }
                        }
                        //功能键隐藏后，焦点的回落问题
                       /* select_item=0;
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }*/


                    }
                });
    }
    /**
     * 发票操作
     */
    private void invoiceOperate(){
        Utils.print(tag,"go invoice");

        InvoiceDialog.InvoiceKey invoiceKey = new InvoiceDialog.InvoiceKey();
        invoiceKey.setKey(invoiceType);
        if(invoiceType==ConStant.INVOICE_COMPANY){
            invoiceKey.setCompanyInfo(invoice_info_value[1]);
            invoiceKey.setCompanynumber(invoice_info_value[2]);
        }
        InvoiceDialog invoiceDialog = new InvoiceDialog(context,invoiceKey);
        invoiceDialog.showUI();
    }
    /**
     * 更新拍卖未支付的订单的发票信息
     */
    public void getUpdateAcutionInvoiceData(String orderSn,OpShoppingCart opShoppingCart){
        Utils.print(tag,"getUpdateAcutionInvoiceData");
        if(op_status)
            return;
        op_status = true;
        startProgressDialog();
        String input="";
        String sign="";
        String[] values = opShoppingCart.getValue().split("#");
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("orderSn",orderSn); //订单号
            if(opShoppingCart.getInvoiceType()==ConStant.INVOICE_NULL){
                json.put("isInvoice",false);
            }else if(opShoppingCart.getInvoiceType()==ConStant.INVOICE_PERSON){
                json.put("isInvoice",true);
                json.put("invoiceType",values[0]); //发票抬头
            }else if(opShoppingCart.getInvoiceType()==ConStant.INVOICE_COMPANY){
                json.put("isInvoice",true); //是否需要发票
                json.put("invoiceType", values[0]); //发票抬头
                json.put("invoiceTitle",Utils.formatInvalidString(values[1])); //发票标题
                json.put("invoiceContent",values[3]); //发票用途
                json.put("invoiceId",values[2]); //纳税人编号
            }
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            sign = Utils.getMD5(domyshop_value);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpUpdateAcutionInvoiceData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UnpayOrderUpdateData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String error_tips = "";
                        stopProgressDialog();
                        op_status=false;
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderUpdateData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        op_status=false;
                        stopProgressDialog();
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(context,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData()!=null){
                            Utils.print(tag,"orderSn=="+statusData.getData().getOrderSn());
                        }

                        invoice_info_value = opShoppingCart.getValue().split("#");
                        invoiceType = opShoppingCart.getInvoiceType();
                        if(invoiceType!=ConStant.INVOICE_NULL){
                            invoice_status.setVisibility(View.VISIBLE);
                        }else{
                            invoice_status.setVisibility(View.GONE);
                        }

                        if(layout_invoice.getVisibility()==View.GONE){//为处理在选择发票页面，但订单已交易关闭的情况
                            //发票显示处理
                            if(invoiceType==ConStant.INVOICE_NULL){//无发票
                                layout_invoicetv.setVisibility(View.GONE);
                            }else{//有发票
                                layout_invoicetv.setVisibility(View.VISIBLE);
                                if(invoiceType==ConStant.INVOICE_PERSON){//个人（只显示title）
                                    layout_invoicetitle.setVisibility(View.VISIBLE);
                                    invoicetitle.setText(context.getResources().getString(R.string.invocetitle));
                                    layout_taxno.setVisibility(View.GONE);
                                    layout_invoicecontent.setVisibility(View.GONE);
                                }else if(invoiceType==ConStant.INVOICE_COMPANY){//公司（title,识别号，内容，全部显示）
                                    layout_invoicetitle.setVisibility(View.VISIBLE);
                                    layout_taxno.setVisibility(View.VISIBLE);
                                    layout_invoicecontent.setVisibility(View.VISIBLE);
                                    invoicetitle.setText(context.getResources().getString(R.string.invoicetitle)+invoice_info_value[1]);
                                    taxno.setText(context.getResources().getString(R.string.invoice_number)+invoice_info_value[2]);
                                    invoicecontent.setText(context.getResources().getString(R.string.invoicecontent)+invoice_info_value[3]);
                                }
                            }
                        }
                    }
                });

    }


    public class CommodityCountDownTimer extends CountDownTimer {
        public CommodityCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long time = millisUntilFinished / 1000;

            Utils.print(tag,"time=="+time);
            long minute=0;
            long second=0;

            minute = time / 60;
            second = time - minute * 60;
            time_tv.setText(String.format("(%02d:%02d)", minute,second));

        }

        @Override
        public void onFinish() {
            time_tv.setText("(00:00)");

            //状态改为交易关闭
            Log.i("===333===","计时超时！！！");
            state.setText(context.getResources().getString(R.string.tradingclosed));
            //拍卖订单未支付的功能键全部隐藏
            layout_usercenter.setVisibility(View.GONE);
            layout_pay.setVisibility(View.GONE);
            layout_cancel.setVisibility(View.GONE);
            layout_invoice.setVisibility(View.GONE);
            //保证返回订单列表后，列表数据刷新
            //进度条出现处理已处理
            Intent mIntent = new Intent("com.dianshang.damai.message_restore");
            //发送广播
            mIntent.putExtra("restore", true);
            mIntent.putExtra("orderrefresh", true);
            context.sendBroadcast(mIntent);
            //发票显示处理
            if(invoiceType==ConStant.INVOICE_NULL){//无发票
                layout_invoicetv.setVisibility(View.GONE);
            }else{//有发票
                layout_invoicetv.setVisibility(View.VISIBLE);
                if(invoiceType==ConStant.INVOICE_PERSON){//个人（只显示title）
                    layout_invoicetitle.setVisibility(View.VISIBLE);
                    invoicetitle.setText(context.getResources().getString(R.string.invocetitle));
                    layout_taxno.setVisibility(View.GONE);
                    layout_invoicecontent.setVisibility(View.GONE);
                }else if(invoiceType==ConStant.INVOICE_COMPANY){//公司（title,识别号，内容，全部显示）
                    layout_invoicetitle.setVisibility(View.VISIBLE);
                    layout_taxno.setVisibility(View.VISIBLE);
                    layout_invoicecontent.setVisibility(View.VISIBLE);
                    invoicetitle.setText(context.getResources().getString(R.string.invoicetitle)+invoice_info_value[1]);
                    taxno.setText(context.getResources().getString(R.string.invoice_number)+invoice_info_value[2]);
                    invoicecontent.setText(context.getResources().getString(R.string.invoicecontent)+invoice_info_value[3]);
                }
            }
            //功能键隐藏后，焦点的回落问题
            /*select_item=0;
            if(adapter!=null){
                adapter.notifyDataSetChanged();
            }*/
            cancelTimer();
        }
    }


    /**
     * 开始倒计时
     */
    private void startTimer() {
        if (timer == null) {
            timer = new CommodityCountDownTimer(TIME, INTERVAL);
        }
        timer.start();
    }

    /**
     * 取消倒计时
     */
    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 发送拍卖取消订单的埋点统计到后台
     */
    private void sendAcutionCancelOrderStatistics(){
        HashMap<String,String> simpleMap=new HashMap<String,String>();
        simpleMap.put("tabNo","");
        simpleMap.put("actionType","Ec7001");
        simpleMap.put("actionInfo","");
        EBusinessApplication.getHSApi().addAction(simpleMap);
    }








}



