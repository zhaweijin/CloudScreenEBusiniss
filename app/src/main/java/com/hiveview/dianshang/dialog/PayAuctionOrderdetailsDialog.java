package com.hiveview.dianshang.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.OrderdetailsListViewCheckAdapter;
import com.hiveview.dianshang.adapter.PayAuctionOrderdetailsAdapter;
import com.hiveview.dianshang.base.BDialog;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.acution.cancelorder.CancelOrderData;
import com.hiveview.dianshang.entity.acution.unpay.order.DomyAuctionOrderVo;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderUpdateData;
import com.hiveview.dianshang.entity.address.UserAddress;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.entity.order.OrderInfo;
import com.hiveview.dianshang.entity.order.item.OrderItemData;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfo;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfoData;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfo;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfoData;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.home.PaymentPage;
import com.hiveview.dianshang.shoppingcart.OpShoppingCart;
import com.hiveview.dianshang.shoppingcart.ShoppingCartList;
import com.hiveview.dianshang.showcommodity.CommodityInfomation;
import com.hiveview.dianshang.usercenter.UserAdderssSelect;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by ThinkPad on 2017/11/22.
 */

public class PayAuctionOrderdetailsDialog extends BDialog implements View.OnClickListener {
    private LayoutInflater mFactory = null;
    private Context context;
    private String sn;
    private List<OrderInfo> orderInfoList;
    private String tag="PayAuctionOrderdetailsDialog";
    private PayAuctionOrderdetailsAdapter adapter;



    //控件
    private TextView orderNum;
    private ListView listView;
    private LinearLayout layout_usercenter;
    private Button usercenter;
    private LinearLayout layout_cancel;
    private Button cancel;
    private TextView state;
    private TextView name;
    private TextView phone;
    private TextView address;
    private TextView commodityprice;
    private TextView discountprice;
    private LinearLayout layout_discount;
    private TextView count;
    private TextView carriage;
    private TextView orderprice;
    private RelativeLayout layout_invoice;
    private ImageView invoice_status;
    private TextView invoice_type;
    //发票
    private LinearLayout layout_invoicetv;
    private LinearLayout layout_invoicetitle;
    private TextView invoicetitle;
    private LinearLayout layout_taxno;
    private TextView taxno;
    private LinearLayout layout_invoicecontent;
    private TextView invoicecontent;


    private RelativeLayout layout_pay;
    private TextView paytip;
    private TextView time_tv;
    //获取是否刷新界面的标志
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
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
    public static int select_item= -1;







    public PayAuctionOrderdetailsDialog(Context context, String sn,List<OrderInfo> orderInfoList,DomyAuctionOrderVo auctionOrderBean) {
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.sn=sn;
        this.orderInfoList=orderInfoList;
        this.auctionOrderBean=auctionOrderBean;

        mFactory = LayoutInflater.from(context);
        View mView = mFactory.inflate(R.layout.layout_pay_auctionorderdetailsdialog, null);
        setContentView(mView);
        preferences = context.getSharedPreferences("isRefreshOrder",
                context.MODE_WORLD_READABLE);
        editor = preferences.edit();
        initShoppingCartView(mView);
        initEvent();
        //初始化支付计时
      /*  Date curDate =  new Date(System.currentTimeMillis());
        TIME = curDate.getTime()-auctionOrderBean.getTimeStamp();*/
        long consumeTime = auctionOrderBean.getTimeStamp()-auctionOrderBean.getCreateTime();
        long totalTime = auctionOrderBean.getExpireTime()-auctionOrderBean.getCreateTime();
        TIME = totalTime-consumeTime;
        startTimer();

    }

    private void initShoppingCartView(View view){
        listView = (ListView)view.findViewById(R.id.list);
        //送货的信息
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
        //发票文本内容
        layout_invoicetv=(LinearLayout)view.findViewById(R.id.layout_invoicetv);
        layout_invoicetitle=(LinearLayout)view.findViewById(R.id.layout_invoicetitle);
        invoicetitle=(TextView)view.findViewById(R.id.invoicetitle);
        layout_taxno=(LinearLayout)view.findViewById(R.id.layout_taxno);
        taxno=(TextView)view.findViewById(R.id.taxno);
        layout_invoicecontent=(LinearLayout)view.findViewById(R.id.layout_invoicecontent);
        invoicecontent=(TextView)view.findViewById(R.id.invoicecontent);
        layout_invoicetv.setVisibility(View.GONE);


        usercenter.setOnClickListener(this);
        layout_pay.setOnClickListener(this);
        cancel.setOnClickListener(this);
        layout_invoice.setOnClickListener(this);
        //适配左边列表
        adapter=new PayAuctionOrderdetailsAdapter(context,orderInfoList);
        listView.setAdapter(adapter);
       //适配右边信息是根据地址变化而改变的
        orderNum.setText(context.getResources().getString(R.string.ordernum)+sn);
        state.setText(context.getResources().getString(R.string.waitingpayment));
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





        //初始化功能键的显示与否
        layout_usercenter.setVisibility(View.VISIBLE);
        layout_pay.setVisibility(View.VISIBLE);
        layout_cancel.setVisibility(View.VISIBLE);
        layout_invoice.setVisibility(View.VISIBLE);

        //处理当焦点落在功能键时，列表焦点状态的变化
        usercenter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(select_item!=-1){
                        select_item=-1;
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    if(listView.hasFocus()){
                        select_item=listView.getSelectedItemPosition();
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }

                }
            }
        });
        layout_pay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(select_item!=-1){
                        select_item=-1;
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    if(listView.hasFocus()){
                        select_item=listView.getSelectedItemPosition();
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }

                }

                //处理获得和失去焦点两种状态的视觉效果
                if(hasFocus){
                    paytip.setTextColor(context.getResources().getColor(android.R.color.white));
                }else{
                    paytip.setTextColor(android.graphics.Color.parseColor("#3B3B3B"));
                }
            }
        });
        cancel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(select_item!=-1){
                        select_item=-1;
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    if(listView.hasFocus()){
                        select_item=listView.getSelectedItemPosition();
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }

                }
            }
        });
        layout_invoice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(select_item!=-1){
                        select_item=-1;
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    if(listView.hasFocus()){
                        select_item=listView.getSelectedItemPosition();
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }

                }

                //处理获得和失去焦点两种状态的视觉效果
                if(hasFocus){
                    invoice_type.setTextColor(context.getResources().getColor(android.R.color.white));
                }else{
                    invoice_type.setTextColor(android.graphics.Color.parseColor("#3B3B3B"));
                }
            }

        });




        //左边列表选中时，字体颜色变化处理
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_item=position;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editor.putBoolean("isRefresh",false);
                editor.commit();
                CommodityInfomation.launch((Activity)context,orderInfoList.get(position).getGoodsSn(),ConStant.ORDER_TO_INFO);
            }
        });


    }


    //功能键按钮的点击监听
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
                for(int i=0;i<orderInfoList.size();i++){
                    if(i==orderInfoList.size()-1){
                        payName = payName + orderInfoList.get(i).getGoodsName();
                    }else{
                        payName = payName + orderInfoList.get(i).getGoodsName()+"-";
                    }
                }
                if(payName.length()>100){
                    payName = payName.substring(0,100);
                }
                PaymentPage.launch((Activity)context,sn,auctionOrderBean.getAmount(),payName);
                break;
            case R.id.cancel:
                getCancelAcutionOrder(sn);
                break;
            case R.id.layout_invoice:
                invoiceOperate();
                break;
            default:
                break;
        }
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


/*   @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //Toast.makeText(context, "订单详情的对话框消失", Toast.LENGTH_SHORT).show();
        *//*editor.putBoolean("isRefresh",true);
        editor.commit();*//*

    }*/

    @Override
    protected void onStop() {
        super.onStop();
        cancelTimer();
        RxBus.get().unregister(ConStant.obString_modify_shopping_cart,observable);
    }

    public static String format(String s){
        String str=s.replace("     ","#");
        str = str.replace("\n","#");//回车
        return str;
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
     * 取消拍卖的商品参加竞拍
     */
    public void getCancelAcutionOrder(String orderSn){
        Utils.print(tag,"getCancelAcutionOrder");

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
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                    }

                    @Override
                    public void onNext(CancelOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

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
                        select_item=0;
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }


                    }
                });
    }

    /**
     * 更新拍卖未支付的订单的发票信息
     */
    public void getUpdateAcutionInvoiceData(String orderSn,OpShoppingCart opShoppingCart){
        Utils.print(tag,"getUpdateAcutionInvoiceData");

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
    /**
     * 更新拍卖未支付的订单的地址信息
     */
    public void getUpdateAcutionReceiveData(String orderSn,int receiveId){
        Utils.print(tag,"getUpdateAcutionReceiveData");

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
            select_item=0;
            if(adapter!=null){
                adapter.notifyDataSetChanged();
            }
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



}



