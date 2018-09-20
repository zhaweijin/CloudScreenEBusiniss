package com.hiveview.dianshang.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.OtherAuctionOrderdetailsAdapter;
import com.hiveview.dianshang.adapter.PayAuctionOrderdetailsAdapter;
import com.hiveview.dianshang.base.BDialog;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.order.OrderInfo;
import com.hiveview.dianshang.entity.order.item.OrderItemData;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfo;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfoData;
import com.hiveview.dianshang.shoppingcart.OpShoppingCart;
import com.hiveview.dianshang.showcommodity.CommodityInfomation;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

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

public class OtherAuctionOrderdetailsDialog extends BDialog {
    private LayoutInflater mFactory = null;
    private Context context;
    //是否显示物流按钮的标志
    private boolean b;
    private String sn;
    private String tag="OtherAuctionOrderdetailsDialog";
    private List<OrderItemInfo> orderItemInfoList;
    private OtherAuctionOrderdetailsAdapter adapter;
    public static int select_item= -1;


    //获取是否刷新界面的标志
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    private ListView listView;
    private TextView state;
    private TextView orderNum;
    private TextView name;
    private TextView phone;
    private TextView address;
    private TextView commodityprice;
    private TextView discountprice;
    private LinearLayout layout_discount;
    private TextView count;
    private TextView carriage;
    private TextView orderprice;
    private LinearLayout layout_logistics;
    private Button logistics;
    private LinearLayout layout_invoice;
    private LinearLayout layout_invoicetitle;
    private TextView invoicetitle;
    private LinearLayout layout_taxno;
    private TextView taxno;
    private LinearLayout layout_invoicecontent;
    private TextView invoicecontent;
    private List<OrderInfo> orderInfoList;
    private OrderStatusInfo infoData;
    private OrderLogisticsDialog orderLogisticsDialog;

    public OtherAuctionOrderdetailsDialog(Context context, String sn, Boolean b,List<OrderInfo> orderInfoList) {
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.b = b;
        this.sn = sn;
        this.orderInfoList=orderInfoList;
        mFactory = LayoutInflater.from(context);
        View mView = mFactory.inflate(R.layout.layout_other_auctionorderdetailsdialog, null);
        setContentView(mView);
        preferences = context.getSharedPreferences("isRefreshOrder",
                context.MODE_WORLD_READABLE);
        editor = preferences.edit();
        initShoppingCartView(mView);
    }

    private void initShoppingCartView(View view){
        listView = (ListView)view.findViewById(R.id.list);
        state=(TextView)view.findViewById(R.id.state);
        orderNum=(TextView)view.findViewById(R.id.orderNum);
        name=(TextView)view.findViewById(R.id.name);
        phone=(TextView)view.findViewById(R.id.phone);
        address=(TextView)view.findViewById(R.id.address);
        commodityprice=(TextView)view.findViewById(R.id.commodityprice);
        layout_discount=(LinearLayout)view.findViewById(R.id.layout_discount);
        discountprice=(TextView)view.findViewById(R.id.discountprice);
        count=(TextView)view.findViewById(R.id.count);
        carriage=(TextView)view.findViewById(R.id.carriage);
        orderprice=(TextView)view.findViewById(R.id.orderprice);
        layout_logistics=(LinearLayout)view.findViewById(R.id.layout_logistics);
        logistics=(Button)view.findViewById(R.id.logistics);
        //发票整体布局
        layout_invoice=(LinearLayout)view.findViewById(R.id.layout_invoice);
        //发票抬头整体布局
        layout_invoicetitle=(LinearLayout)view.findViewById(R.id.layout_invoicetitle);
        //发票抬头文本
        invoicetitle=(TextView)view.findViewById(R.id.invoicetitle);
        //发票号整体布局
        layout_taxno=(LinearLayout)view.findViewById(R.id.layout_taxno);
        //发票号文本
        taxno=(TextView)view.findViewById(R.id.taxno);
        //发票内容整体布局
        layout_invoicecontent=(LinearLayout)view.findViewById(R.id.layout_invoicecontent);
        //发票内容文本
        invoicecontent=(TextView)view.findViewById(R.id.invoicecontent);

        if(!b){//不显示查看物流按钮
            // logistics.setVisibility(View.GONE);
            layout_logistics.setVisibility(View.GONE);
        }

        logistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "点击物流按钮！！！", Toast.LENGTH_SHORT).show();
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
        //处理当焦点落在功能键时，列表焦点状态的变化
        logistics.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    select_item=-1;
                    if(adapter!=null){
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    select_item=listView.getSelectedItemPosition();
                    if(adapter!=null){
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        //适配左边列表
        adapter=new OtherAuctionOrderdetailsAdapter(context,orderInfoList);
        listView.setAdapter(adapter);
        //适配右边信息
        getOrderStatusInfoData(sn);

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

    public void showUI(){

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Utils.getScreenW(context);
        lp.height = Utils.getScrrenH(context);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);


        show();
    }

//普通订单逻辑
  /*  @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //Toast.makeText(context, "订单详情的对话框消失", Toast.LENGTH_SHORT).show();
        editor.putBoolean("isRefresh",true);
        editor.commit();
    }*/
/*@Override
public void onBackPressed() {
    super.onBackPressed();
    RxBus.get().post(ConStant.obString_select_commodity_type,"back");
}*/

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
                            }else if(infoData.getStatus()==Status.pendingShipment.ordinal()||infoData.getStatus()==12){
                                state.setText(context.getResources().getString(R.string.sendgoods));
                            }else if(infoData.getStatus()==Status.pendingReceive.ordinal()){
                                state.setText(context.getResources().getString(R.string.receivinggoods));
                            }else if(infoData.getStatus()==Status.completed.ordinal()){
                                state.setText(context.getResources().getString(R.string.completetransaction));
                            }else{
                                state.setText(context.getResources().getString(R.string.tradingclosed));
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
                            commodityprice.setText("¥"+Utils.getPrice(infoData.getPrice()));
                            discountprice.setText("-¥"+Utils.getPrice(infoData.getDiscountPrice()));
                            if(discountprice.getText().toString().equals("-¥0")||discountprice.getText().toString().equals("-¥0.00")){//无优惠价格
                                layout_discount.setVisibility(View.INVISIBLE);
                            }else{
                                layout_discount.setVisibility(View.VISIBLE);
                            }
                            count.setText(infoData.getQuantity()+"");
                            carriage.setText("¥"+Utils.getPrice(infoData.getFreight()));
                            orderprice.setText("¥"+Utils.getPrice(infoData.getAmount()));
                            if(infoData.getIsInvoice()==ConStant.INVOICE_NULL){//无发票 不显示
                                layout_invoice.setVisibility(View.GONE);
                            }else{//有发票
                                if(infoData.getInvoiceType().equals(context.getResources().getString(R.string.unit))){//公司  全显示
                                    layout_invoice.setVisibility(View.VISIBLE);
                                    layout_invoicetitle.setVisibility(View.VISIBLE);
                                    layout_taxno.setVisibility(View.VISIBLE);
                                    layout_invoicecontent.setVisibility(View.VISIBLE);
                                    invoicetitle.setText(context.getResources().getString(R.string.invoicetitle)+infoData.getInvoiceTitle());
                                    taxno.setText(context.getResources().getString(R.string.invoice_number)+infoData.getInvoiceId());
                                    invoicecontent.setText(context.getResources().getString(R.string.invoicecontent)+infoData.getInvoiceContent());
                                }else{//个人 不显示发票内容也无（发票号taxno）
                                    layout_invoice.setVisibility(View.VISIBLE);
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
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
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



                    }
                });
        addSubscription(subscription);
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

}
