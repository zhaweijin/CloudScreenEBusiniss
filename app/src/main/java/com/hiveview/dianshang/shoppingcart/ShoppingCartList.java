package com.hiveview.dianshang.shoppingcart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.google.gson.Gson;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.ShoppingOrderAdapter;
import com.hiveview.dianshang.auction.AcutionInfomation;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.ConfirmDialog;
import com.hiveview.dianshang.dialog.InvoiceDialog;
import com.hiveview.dianshang.entity.acution.cancelorder.CancelOrderData;
import com.hiveview.dianshang.entity.acution.unpay.order.DomyAuctionOrderVo;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderUpdateData;
import com.hiveview.dianshang.entity.address.UserAddress;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.entity.createorder.CommitOrderStatusData;
import com.hiveview.dianshang.entity.netty.invoice.DomyInvoiceTcpMsgBodyVo;
import com.hiveview.dianshang.entity.netty.invoice.DomyInvoiceTcpMsgVo;
import com.hiveview.dianshang.entity.shoppingcart.ActivityInfoData;
import com.hiveview.dianshang.entity.shoppingcart.DirectShoppingCartData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartRecord;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfo;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfoData;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfoGift;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfoType;
import com.hiveview.dianshang.entity.shoppingcart.post.ShoppingPostCartInfo;
import com.hiveview.dianshang.entity.shoppingcart.post.ShoppingPostOrderInfo;
import com.hiveview.dianshang.home.InstallService;
import com.hiveview.dianshang.home.PaymentPage;
import com.hiveview.dianshang.usercenter.UserAdderssSelect;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by carter on 7/3/17.
 */

public class ShoppingCartList extends BaseActivity implements OnItemViewSelectedListener{

    private String tag = "ShoppingCartList";


    private LayoutInflater mFactory = null;
    

    /**
     * 左边购物车商品列表
     */
    @BindView(R.id.list)
    RecyclerView recyclerView;

    /**
     * 直接单项支付，一项数据
     */
    @BindView(R.id.item_focus)
    RelativeLayout item_focus;

    /**
     * 购物车商品列表适配器
     */
    ShoppingOrderAdapter shoppingOrderAdapter;

    @BindView(R.id.commodity_icon)
    SimpleDraweeView commodity_icon;

    @BindView(R.id.commodity_name)
    TextView commodity_name;

    @BindView(R.id.commodity_type)
    TextView commodity_type;

    @BindView(R.id.commodity_number)
    TextView commodity_quantity;

    @BindView(R.id.commodity_price)
    TextView commodity_price;

    @BindView(R.id.price_type)
    TextView price_type;

    @BindView(R.id.express_unit)
    TextView express_unit;

    //########################################

    /**
     * 地址及操作容器
     */
    @BindView(R.id.layout_address)
    LinearLayout layout_address;

    /**
     * 添加操作按钮容器
     */
    @BindView(R.id.layout_contain)
    LinearLayout layout_contain;

    /**
     * 商品总价
     */
    @BindView(R.id.commodity_total_price)
    TextView commodity_total_price;

    /**
     * 优惠价格
     */
    @BindView(R.id.commodity_discount)
    TextView commodity_discount;


    /**
     * 商品总数量
     */
    @BindView(R.id.commodity_total_number)
    TextView commodity_total_number;

    /**
     * 商品总运费
     */
    @BindView(R.id.commodity_express_price)
    TextView commodity_express_price;


    /**
     * 商品快递标签
     */
    @BindView(R.id.commodity_express_title)
    TextView commodity_express_title;


    /**
     * 订单总价
     */
    @BindView(R.id.commodity_order_total_price)
    TextView commodity_order_total_price;

    /**
     * 单个商品直接支付项
     */
    @BindView(R.id.commodity_item)
    LinearLayout commodity_item;

    /**
     * 用于展示单个商品直接支付
     */
    @BindView(R.id.layout_item)
    LinearLayout layout_item;

    /**
     * 订单号展示区域
     */
    @BindView(R.id.layout_order_sn)
    LinearLayout layout_order_sn;

    /**
     * 提交订单操作按键
     */
    RelativeLayout layout_user_center;
    TextView user_center_text;
    //提交订单
    RelativeLayout layout_submit_order;
    TextView submit_order_text_time;
    TextView submit_order_text;
    //取消订单
    RelativeLayout layout_cancel_order;
    TextView cancel_order_text;

    //发票
    RelativeLayout layout_invoice;
    ImageView invoice_status;
    TextView invoice_type;


    TextView address=null;
    TextView transaction_close_status;


    @BindView(R.id.layout_gift)
    LinearLayout layout_gift;

    @BindView(R.id.layout_full_gift)
    LinearLayout layout_full_gift;

    @BindView(R.id.full_gift_title_name)
    TextView full_gift_title_name;

    @BindView(R.id.layout_discount)
    RelativeLayout layout_discount;


    private DirectShoppingCartData directPaymentData;
    //当前页面类型:直接支付,购物车,购物车支付
    private int typeUI_ID = 1;

    private Observable<OpShoppingCart> observable;

    //地址
    private int receiveId=-1;
    //发票,总价等信息
    private ShoppingCartInfo info;
    //发票类型
    private int invoiceType = ConStant.INVOICE_NULL;
    //发票填写的信息
    private String[] invoice_info_value;

    //地址信息
    private String[] address_info_value;

    //清空确认对话框
    private ConfirmDialog confirmDialog;


    private String address_info="";

    //购物车列表数据
    private List<ShoppingCartRecord> shoppingCartRecords = new ArrayList<>();
    private List<ActivityInfoData> shoppingCartGiftRecords = new ArrayList<>();


    /**
     * 服务器交互标志位
     */
    private boolean op_status = false;

    /**
     * 提示的错误信息
     */
    private String errMessage;

    /**
     * 用于记录待支付的所有商品ID
     */
    private List<Long> itemIdList = new ArrayList<>();

    /**
     * 发票对话框
     */
    private InvoiceDialog invoiceDialog;
    //
    private CommodityCountDownTimer timer;
    private long TIME = 25 * 1000L;
    private final long INTERVAL = 1000L;

    /**
     * 买赠商品列表
     */
    private List<ShoppingCartInfoGift> buyGiftList;
    /**
     * 满赠商品列表
     */
    private List<ShoppingCartInfoGift> fullGiftList;


    /**
     * 拍卖订单sn
     */
    private String orderSn;

    /**
     * 拍卖数据
     */
    private DomyAuctionOrderVo acutionData;

    /**
     * 直接购买，待支付入口
     * @param activity
     * @param type
     * @param directShoppingCartData
     */
    public static void launch(Activity activity,int type,DirectShoppingCartData directShoppingCartData) {
        Intent intent = new Intent(activity, ShoppingCartList.class);
        intent.putExtra("type",type);
        intent.putExtra("direct_pay",directShoppingCartData);
        activity.startActivity(intent);
    }


    /**
     * 拍卖待支付订单入口
     * @param activity
     * @param type
     */
    public static void launch(Context context,int type,DomyAuctionOrderVo acutionData) {
        Intent intent = new Intent(context, ShoppingCartList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("type",type);
        intent.putExtra("data",acutionData);
        context.startActivity(intent);
    }



    /**
     * 购物车进入待支付订单入口
     * @param activity
     * @param type
     * @param list
     */
    public static void launch(Activity activity,int type,ArrayList<ShoppingCartRecord> list) {
        Intent intent = new Intent(activity, ShoppingCartList.class);
        intent.putExtra("type",type);
        intent.putParcelableArrayListExtra("list",list);
        activity.startActivity(intent);
    }
    /**
     * 订单详情入口
     */
    public static void launch(Activity activity,int type,ArrayList<ShoppingCartRecord> list,ArrayList<ActivityInfoData> activityInfoDataList) {
        Intent intent = new Intent(activity, ShoppingCartList.class);
        intent.putExtra("type",type);
        intent.putParcelableArrayListExtra("list",list);
        intent.putParcelableArrayListExtra("fullGiftList",activityInfoDataList);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart_list_layout);

        typeUI_ID = getIntent().getIntExtra("type",1);
        if(typeUI_ID==ConStant.DIRECT_PAYMENT){
            directPaymentData = getIntent().getParcelableExtra("direct_pay");
            info = directPaymentData.getShoppingCartInfo();
            receiveId = directPaymentData.getReceiveId();
            address_info = directPaymentData.getAddress_info();
        }else if(typeUI_ID==ConStant.SHOPPING_CART){
            shoppingCartRecords = getIntent().getParcelableArrayListExtra("list");
            shoppingCartGiftRecords = getIntent().getParcelableArrayListExtra("fullGiftList");

            /*if(shoppingCartGiftRecords!=null){
                for (int i = 0; i < shoppingCartGiftRecords.size(); i++) {
                    Utils.print(tag,"name=="+shoppingCartGiftRecords.get(i).getGiftName());
                }
            }*/

            for (int i = 0; i < shoppingCartRecords.size() ; i++) {
                itemIdList.add(shoppingCartRecords.get(i).getCartItemId());
            }
        }else if(typeUI_ID==ConStant.ACUTION_PAYMENT){
            acutionData = getIntent().getParcelableExtra("data");
            Utils.print(tag,"sn=="+acutionData.getOrderSn());
            orderSn = acutionData.getOrderSn();
        }

        regeditReceiver();

        //******test
        /*itemIdList.add((long)25086);
        itemIdList.add((long)25087);*/
        //*******

        mFactory = LayoutInflater.from(mContext);
        Utils.print(tag,"id==="+typeUI_ID);
        handleMainView();
        initEvent();
        //showUI();
    }

    private void handleMainView() {
        if (typeUI_ID == ConStant.DIRECT_PAYMENT) { //单个商品的直接支付
            layout_item.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);

            //普通订单
            changToSubmitOrder();
            initItemCommodityView();
            showDirectCommodityPrice();

            if (receiveId == -1) {
                ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.set_default_address_tip));
            }

            Utils.print(tag,"checkHasDirectBuyGift="+checkHasDirectBuyGift());
            if (checkHasDirectBuyGift()) {
                addBuyGiftListView(layout_gift);
            }
            if (checkHasDirectFullGift()) {
                addFullGiftListView(layout_full_gift);
            }

            checkHasDiscount();

        } else if (typeUI_ID == ConStant.SHOPPING_CART) { //购物车
            layout_item.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            changToSubmitOrder();  //更新结算按钮为提交订单
            Utils.print(tag, "typeID=" + typeUI_ID);
            //listView.setFocusable(false);

            initRecyclerView();

            getUserDefaultAddress();
            //购物车总价,总数量,通过购物车列表客户端计算
        } else if (typeUI_ID == ConStant.ACUTION_PAYMENT) {
            layout_item.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            //拍卖订单
            changToSubmitOrderOrCancel();
            if(acutionData!=null){
                transaction_close_status.setVisibility(View.VISIBLE);
                transaction_close_status.setText(mContext.getResources().getString(R.string.gopay));

                initAcutionItemCommodityView();
                showAcutionCommodityPrice();
                checkOrderType();

                long consumeTime = acutionData.getTimeStamp()-acutionData.getCreateTime();
                long totalTime = acutionData.getExpireTime()-acutionData.getCreateTime();
                TIME = totalTime-consumeTime;
                Utils.print(tag,"TIME=="+TIME);
                startTimer();

                address_info = acutionData.getConsignee()+"     "+acutionData.getPhone()+"\n"+acutionData.getAddress();

              
                if(acutionData.getAddress().equals("-") || acutionData.getPhone().equals("-") ||
                        acutionData.getConsignee().equals("-")) {
                    address_info="";
                }
                setAddressInfo(address_info);

                if(acutionData.getIsInvoice()!=0){
                    invoice_status.setVisibility(View.VISIBLE);
                }else{
                    invoice_status.setVisibility(View.GONE);
                }

                initFocusItem();
            }

            layout_discount.setVisibility(View.INVISIBLE);
        }
    }


    private void initRecyclerView(){

        recyclerView.setHasFixedSize(true);

        /**
         * 设置纵向滚动，也可以设置横向滚动
         */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this); //listview 垂直方向
        recyclerView.setLayoutManager(layoutManager);

        /**
         * 增加分割线
         */
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, LinearLayoutManager.HORIZONTAL, 1, ContextCompat.getColor(this, R.color.shopping_cart_order_item_div)));

        Utils.print(tag,"size="+shoppingCartRecords.size());
        shoppingOrderAdapter = new ShoppingOrderAdapter(this,shoppingCartRecords,shoppingCartGiftRecords,this);
        recyclerView.setAdapter(shoppingOrderAdapter);

        //shoppingOrderAdapter.handTestEvent();

    }

    /**
     * 显示直接支付各项价格
     */
    private void showDirectCommodityPrice(){
        if(info==null){
            return;
        }
        Utils.print(tag,"total number="+info.getTotalNumber());
        commodity_total_price.setText(Utils.getPrice(info.getTotalPrice()));
        commodity_order_total_price.setText(Utils.getPrice(info.getTotalOrderPrice()));
        commodity_total_number.setText(""+info.getTotalNumber());
        commodity_express_price.setText(Utils.getPrice(info.getTotaldeliveryPrice()));
        commodity_discount.setText("-¥"+Utils.getPrice(info.getDiscountPrice()));
        if(info.getDiscountPrice()==0){
            layout_discount.setVisibility(View.INVISIBLE);
        }else{
            layout_discount.setVisibility(View.VISIBLE);
        }

        Utils.print(tag,"222=="+address_info);
        setAddressInfo(address_info);
    }



    /**
     * 显示拍卖待支付各项价格
     */
    private void showAcutionCommodityPrice(){
        if(acutionData==null){
            return;
        }

        commodity_total_price.setText(Utils.getPrice(acutionData.getPrice()));
        commodity_order_total_price.setText(Utils.getPrice(acutionData.getPrice()+acutionData.getFreight()));
        commodity_total_number.setText(""+acutionData.getQuantity());
        commodity_express_price.setText(Utils.getPrice(acutionData.getFreight()));


        Utils.print(tag,"222=="+address_info);
        setAddressInfo(address_info);
    }


    /**
     * 加载单个商品支付页面的数据
     */
    private void initItemCommodityView(){
        FrescoHelper.setImage(commodity_icon, Uri.parse(directPaymentData.getSkuInfo().getImgUrl()),new ResizeOptions(150,150));
        commodity_name.setText(directPaymentData.getGoodName());
        commodity_type.setText(directPaymentData.getSkuInfo().getSpecItemValues());
        commodity_quantity.setText(mContext.getResources().getString(R.string.quantity)+ directPaymentData.getQuantity()+"");
        commodity_price.setText("¥"+Utils.getPrice(directPaymentData.getSkuInfo().getDiscountPrice()));


        price_type.setVisibility(View.INVISIBLE);
    }


    /**
     * 加载拍卖单个商品待支付页面的数据
     */
    private void initAcutionItemCommodityView(){
        if(acutionData==null){
            Utils.print(tag,"nulllllll");
        }else {
            Utils.print(tag,"..."+acutionData.getThumbnail());
        }

        FrescoHelper.setImage(commodity_icon, Uri.parse(acutionData.getThumbnail()),new ResizeOptions(150,150));
        commodity_name.setText(acutionData.getGoodsName());
        commodity_type.setText(acutionData.getSpecifications());
        commodity_quantity.setText(mContext.getResources().getString(R.string.quantity)+ acutionData.getQuantity()+"");
        commodity_price.setText("¥"+Utils.getPrice(acutionData.getPrice()));

        price_type.setVisibility(View.INVISIBLE);
    }


    private void initEvent(){
        observable = RxBus.get().register(ConStant.obString_modify_shopping_cart, OpShoppingCart.class);
        observable.subscribe(new Action1<OpShoppingCart>() {
            @Override
            public void call(OpShoppingCart op) {
                Utils.print(tag, "op=" + op.getKey());
                switch (op.getKey()){
                    case OpShoppingCart.MODIFY_INVOICE:
                        if(isAcutionOrder()) {
                            invoice_info_value = op.getValue().split("#");
                            invoiceType = op.getInvoiceType();
                            getUpdateAcutionInvoiceData(orderSn, op);
                        }
                        else{
                            invoice_info_value = op.getValue().split("#");
                            invoiceType = op.getInvoiceType();
                            if(invoiceType!=ConStant.INVOICE_NULL){
                                invoice_status.setVisibility(View.VISIBLE);
                            }else{
                                invoice_status.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case OpShoppingCart.MODIFY_ADDRESS:
                        address_info_value = op.getValue().split("#");
                        Utils.print(tag,"id=="+Integer.parseInt(address_info_value[0]));
                        receiveId = Integer.parseInt(address_info_value[0]);

                        if(isAcutionOrder()){
                            getUpdateAcutionReceiveData(orderSn,receiveId);
                        }else{
                            if(address_info_value.length>1){
                                Utils.print(tag,"address=="+address_info_value[1]);
                                address_info = address_info_value[1];
                            }else{
                                address_info="";
                            }
                            setAddressInfo(address_info);
                            Utils.print(tag,"set finish=="+typeUI_ID);
                            if(typeUI_ID==ConStant.DIRECT_PAYMENT){
                                getShoppingItemInfoData();
                            }else if(typeUI_ID==ConStant.SHOPPING_CART){
                                getShoppingCartInfoData();
                            }
                        }
                        break;
                }
            }
        });


    }


    /*public void showUI(){

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width =  Utils.getScreenW(mContext);
        lp.height = Utils.getScrrenH(mContext);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
    }*/


    /**
     * 普通商品：切换到提交订单,个人中心UI操作
     */
    private void changToSubmitOrder(){
        layout_address.removeAllViews();
        View layout_shopping_cart_address = mFactory.inflate(R.layout.layout_shopping_cart_address, null);
        layout_address.addView(layout_shopping_cart_address);

        address = (TextView)layout_shopping_cart_address.findViewById(R.id.address);

        layout_contain.removeAllViews();
        View layout_submit_order_button = mFactory.inflate(R.layout.layout_submit_order_button, null);
        layout_contain.addView(layout_submit_order_button);

        layout_user_center = (RelativeLayout)layout_submit_order_button.findViewById(R.id.layout_user_center);
        user_center_text = (TextView)layout_submit_order_button.findViewById(R.id.user_center_text);
        layout_submit_order = (RelativeLayout)layout_submit_order_button.findViewById(R.id.layout_submit_order);
        submit_order_text = (TextView)layout_submit_order_button.findViewById(R.id.submit_order_text);
        layout_invoice = (RelativeLayout)layout_submit_order_button.findViewById(R.id.layout_invoice);
        invoice_status =(ImageView)layout_submit_order_button.findViewById(R.id.invoice_status);
        invoice_type = (TextView)layout_submit_order_button.findViewById(R.id.invoice_type);


        layout_user_center.setOnClickListener(onClickListener);
        layout_submit_order.setOnClickListener(onClickListener);
        layout_invoice.setOnClickListener(onClickListener);

        layout_user_center.setOnFocusChangeListener(onTextFocus);
        layout_submit_order.setOnFocusChangeListener(onTextFocus);
        layout_invoice.setOnFocusChangeListener(onTextFocus);


        layout_submit_order.requestFocus();


    }


    /**
     * 拍卖商品：切换到提交订单,个人中心UI操作
     */
    private void changToSubmitOrderOrCancel(){
        layout_address.removeAllViews();
        View layout_shopping_cart_address = mFactory.inflate(R.layout.layout_shopping_cart_address, null);
        layout_address.addView(layout_shopping_cart_address);

        address = (TextView)layout_shopping_cart_address.findViewById(R.id.address);
        transaction_close_status = (TextView)layout_shopping_cart_address.findViewById(R.id.transaction_close_status);

        layout_contain.removeAllViews();
        View layout_submit_order_button = mFactory.inflate(R.layout.layout_acution_submit_order_button, null);
        layout_contain.addView(layout_submit_order_button);

        layout_user_center = (RelativeLayout)layout_submit_order_button.findViewById(R.id.layout_user_center);
        user_center_text = (TextView)layout_submit_order_button.findViewById(R.id.user_center_text);
        layout_submit_order = (RelativeLayout)layout_submit_order_button.findViewById(R.id.layout_submit_order);
        layout_cancel_order = (RelativeLayout)layout_submit_order_button.findViewById(R.id.layout_cancel_order);
        submit_order_text_time = (TextView)layout_submit_order_button.findViewById(R.id.submit_order_text_time);
        submit_order_text = (TextView)layout_submit_order_button.findViewById(R.id.submit_order_text);
        layout_invoice = (RelativeLayout)layout_submit_order_button.findViewById(R.id.layout_invoice);
        invoice_status =(ImageView)layout_submit_order_button.findViewById(R.id.invoice_status);
        invoice_type = (TextView)layout_submit_order_button.findViewById(R.id.invoice_type);
        cancel_order_text = (TextView) layout_submit_order_button.findViewById(R.id.cancel_order_text);

        layout_user_center.setOnClickListener(onClickListener);
        layout_submit_order.setOnClickListener(onClickListener);
        layout_invoice.setOnClickListener(onClickListener);
        layout_cancel_order.setOnClickListener(onClickListener);

        layout_user_center.setOnFocusChangeListener(onTextFocus);
        layout_submit_order.setOnFocusChangeListener(onTextFocus);
        layout_invoice.setOnFocusChangeListener(onTextFocus);
        layout_cancel_order.setOnFocusChangeListener(onTextFocus);


        layout_submit_order.requestFocus();


    }


    View.OnFocusChangeListener onTextFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.layout_user_center:
                    if(hasFocus){
                        user_center_text.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    }else{
                        user_center_text.setTextColor(mContext.getResources().getColor(android.R.color.black));
                    }
                    break;
                case R.id.layout_submit_order:
                    if(hasFocus){
                        submit_order_text.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    }else{
                        submit_order_text.setTextColor(mContext.getResources().getColor(android.R.color.black));
                    }
                    break;
                case R.id.layout_invoice:
                    if(hasFocus){
                        invoice_type.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    }else{
                        invoice_type.setTextColor(mContext.getResources().getColor(android.R.color.black));
                    }
                    break;
                case R.id.layout_cancel_order:
                    if(hasFocus){
                        cancel_order_text.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    }else{
                        cancel_order_text.setTextColor(mContext.getResources().getColor(android.R.color.black));
                    }
                    break;
            }
        }
    };



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RxBus.get().post(ConStant.obString_select_commodity_type,"back");
    }


    /**
     * 按钮事件操作
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //正在交互
            if(mProgressDialog!=null && mProgressDialog.isShowing())
                return;
            switch (v.getId())
            {
                case R.id.layout_user_center:   //用户中心
                    Utils.print(tag,"go user center select address");
                    if(address!=null&&!address.getText().toString().equals("")){
                        UserAdderssSelect.launch((ShoppingCartList)mContext,receiveId,true);
                    }else{
                        UserAdderssSelect.launch((ShoppingCartList)mContext,receiveId,false);
                    }
                    break;
                case R.id.layout_submit_order:  //提交订单
                    orderOperater();
                    break;
                case R.id.layout_cancel_order:  //取消订单
                    Utils.print(tag,"cancle order");
                    getCancelAcutionOrder(orderSn);
                    sendAcutionCancelOrderStatistics();
                    break;
                case R.id.layout_invoice:   //发票编辑
                    invoiceOperate();
                    break;
            }
        }
    };

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
        invoiceDialog = new InvoiceDialog(mContext,invoiceKey);
        invoiceDialog.showUI();
    }


    /**
     * 订单操作
     */
    private void orderOperater(){
        Utils.print(tag,"sumbit order");
        if(address.getText().toString().equals("")){
            ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.error_address_null));
            return;
        }

        //检测网络状态
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        //正在交互
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+op_status);
        if(op_status)
            return;
        op_status = true;

        if(typeUI_ID==ConStant.DIRECT_PAYMENT){
            commitEncryptItemOrderData();
        }else if(typeUI_ID==ConStant.SHOPPING_CART){
            commitEncryptShoppingCartOrderData();
        }else if(typeUI_ID==ConStant.ACUTION_PAYMENT){
            //进入支付页面
            PaymentPage.launch((ShoppingCartList)mContext,acutionData.getOrderSn(),acutionData.getAmount(),acutionData.getGoodsName());
            finish();
        }
    }


    /**
     * 获取默认地址
     */
    public void getUserDefaultAddress(){
        Utils.print(tag,"getUserAddressList");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("pageIndex","1");
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }
        Subscription s = RetrofitClient.getAddressAPI()
                .httpGetUserAddressListData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserAddress>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String error_tips = "";
                        if (Utils.isConnected(mContext)) {
                            error_tips = mContext.getResources().getString(R.string.error_op_service_exception);
                        } else {
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext, error_tips);
                        Utils.print(tag,"error>>>>"+e.getMessage());
                    }

                    @Override
                    public void onNext(UserAddress userAddress) {

                        if(userAddress.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,userAddress.getErrorMessage());
                            return;
                        }

                        Utils.print(tag,"address get data=="+userAddress.getErrorMessage());
                        Utils.print(tag,"total count="+userAddress.getData().getTotalCount());
                        List<UserData> userDatas = userAddress.getData().getRecords();

                        for(int i=0;i<userDatas.size();i++){
                            Utils.print(tag,"isdefault=="+userDatas.get(i).getDefault());
                            if(userDatas.get(i).getDefault()){
                                String receiveName = userDatas.get(i).getConsignee();
                                String receivePhone = userDatas.get(i).getPhone();

                                String detailAddress = userDatas.get(i).getAddressProvince()+userDatas.get(i).getAddressTown()+userDatas.get(i).getAddressDetail();

                                address_info = receiveName+"     "+receivePhone+"\n"+detailAddress;

                                setAddressInfo(address_info);
                                receiveId = userDatas.get(i).getId();
                                Utils.print(tag,"address="+userDatas.get(i).getAddressDetail());
                                Utils.print(tag,"address id="+userDatas.get(i).getId());
                                break;
                            }
                        }

                        Utils.print(tag,"11=="+typeUI_ID);
                        if(typeUI_ID==ConStant.DIRECT_PAYMENT){
                            getShoppingItemInfoData();
                        }else if(typeUI_ID==ConStant.SHOPPING_CART){
                            //获取订单总价,运费等信息
                            getShoppingCartInfoData();  //购物车订
                        }
                    }
                });
        addSubscription(s);
    }

    /**
     * 设置快递地址
     * @param addressInfo
     */
    private void setAddressInfo(String addressInfo){
        if(address!=null){
            if(Utils.getStringLength(addressInfo)>ConStant.SHOP_ADDRESS_SHOW_LENGTH){
                address.setText(Utils.getShowKeyString(address_info,ConStant.SHOP_ADDRESS_SHOW_LENGTH));
            }else{
                address.setText(address_info);
            }
        }
    }


    /**
     * 获取直接支付总价等详情信息
     */
    public void getShoppingItemInfoData(){
        Utils.print(tag,"getShoppingItemInfoData");
        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("goodsSkuSn",directPaymentData.getSkuInfo().getGoodskuSn());
            json.put("quantity",directPaymentData.getQuantity());
            json.put("receiveId",receiveId);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetShoppingItemInfoData(ConStant.APP_VERSION,input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartInfoData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag,"22error="+e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartInfoData shoppingCartInfoData) {
                        Utils.print(tag,"shop status=="+shoppingCartInfoData.getErrorMessage()+",value="+shoppingCartInfoData.getReturnValue());
                        stopProgressDialog();
                        if(shoppingCartInfoData.getReturnValue()==-1){
                            errMessage = shoppingCartInfoData.getErrorMessage();
                            ToastUtils.showToast(mContext,shoppingCartInfoData.getErrorMessage());
                            commodity_express_price.setText("");
                            commodity_discount.setText("");
                            layout_discount.setVisibility(View.INVISIBLE);
                            return;
                        }

                        info = shoppingCartInfoData.getData();
                        Utils.print(tag,"total number="+info.getTotalNumber());
                        commodity_total_price.setText(Utils.getPrice(info.getTotalPrice()));
                        commodity_order_total_price.setText(Utils.getPrice(info.getTotalOrderPrice()));
                        commodity_total_number.setText(""+info.getTotalNumber());
                        commodity_express_price.setText(Utils.getPrice(info.getTotaldeliveryPrice()));
                        commodity_discount.setText("-¥"+Utils.getPrice(info.getDiscountPrice()));
                        if(info.getDiscountPrice()==0){
                            layout_discount.setVisibility(View.INVISIBLE);
                        }else {
                            layout_discount.setVisibility(View.VISIBLE);
                        }

                        setAddressInfo(address_info);
                    }
                });
        addSubscription(s);
    }



    /**
     * 获取购物车订单总价等详情信息
     */
    public void getShoppingCartInfoData(){
        Utils.print(tag,"getShoppingCartInfoData");
        startProgressDialog();
        String input="";
        try{
            Gson json = new Gson();
            ShoppingPostCartInfo postCartInfo = new ShoppingPostCartInfo();
            postCartInfo.setUserid(ConStant.getInstance(mContext).userID);
            postCartInfo.setReceiveId(receiveId);
            postCartInfo.setItemIdList(itemIdList);
            input = json.toJson(postCartInfo);

            input = input.replace("{","%7B").replace("}","%7D");
            input = input.replace("[","%5B").replace("]","%5D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetShoppingcartInfoData(ConStant.APP_VERSION,input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartInfoData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartInfoData shoppingCartInfoData) {
                        Utils.print(tag,"status=="+shoppingCartInfoData.getErrorMessage()+",returnvalue="+shoppingCartInfoData.getReturnValue());
                        stopProgressDialog();
                        if(shoppingCartInfoData.getReturnValue()==-1){
                            address_info="";
                            ToastUtils.showToast(mContext,shoppingCartInfoData.getErrorMessage());
                            setAddressInfo(address_info);
                            return;
                        }

                        info = shoppingCartInfoData.getData();

                        Utils.print(tag,"total price="+info.getTotalOrderPrice());
                        commodity_total_price.setText(Utils.getPrice(info.getTotalPrice()));
                        commodity_order_total_price.setText(Utils.getPrice(info.getTotalOrderPrice()));
                        commodity_total_number.setText(""+info.getTotalNumber());
                        commodity_discount.setText("-¥"+Utils.getPrice(info.getDiscountPrice()));
                        commodity_express_price.setText(Utils.getPrice(info.getTotaldeliveryPrice()));
                        commodity_express_price.setVisibility(View.VISIBLE);
                        commodity_express_title.setVisibility(View.VISIBLE);
                        express_unit.setVisibility(View.VISIBLE);

                        if(info.getDiscountPrice()==0){
                            layout_discount.setVisibility(View.INVISIBLE);
                        }else{
                            layout_discount.setVisibility(View.VISIBLE);
                        }


                        setAddressInfo(address_info);

                        if (info.getOfflineIdList().size()!=0) {
                            for (int i=0;i<shoppingCartRecords.size();i++){
                                for (int j=0;j<info.getOfflineIdList().size();j++){
                                    if(info.getOfflineIdList().get(j).equals(shoppingCartRecords.get(i).getCartId()+"")){
                                        shoppingCartRecords.get(i).setMarketStatus(2);
                                    }
                                }
                            }
                            shoppingOrderAdapter = new ShoppingOrderAdapter(mContext,shoppingCartRecords,shoppingCartGiftRecords, ShoppingCartList.this);
                            recyclerView.setAdapter(shoppingOrderAdapter);
                        }
                    }
                });
        addSubscription(s);
    }




    /**
     * 商品直接进入支付订单
     */
    public void commitEncryptItemOrderData(){
        Utils.print(tag,"commitEncryptItemOrderData");

        if(info==null){
            op_status=false;
            String error_tips = "";
            if(Utils.isConnected(mContext)){
                error_tips = errMessage;
            }else{
                error_tips = mContext.getResources().getString(R.string.error_network_exception);
            }
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        startProgressDialog();
        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("quantity",info.getTotalNumber());  //商品购买数量
            json.put("goodsSkuSn",directPaymentData.getGoodskuSn()); //商品选择类型SN
            json.put("receiveId",receiveId); //邮寄地址ID
            if(invoiceType==ConStant.INVOICE_NULL){
                json.put("isInvoice",false);
            }else if(invoiceType==ConStant.INVOICE_PERSON){
                json.put("isInvoice",true);
                json.put("invoiceType",invoice_info_value[0]); //发票抬头
            }else if(invoiceType==ConStant.INVOICE_COMPANY){
                json.put("isInvoice",true); //是否需要发票
                json.put("invoiceType", invoice_info_value[0]); //发票抬头
                json.put("invoiceTitle",Utils.formatInvalidString(invoice_info_value[1])); //发票标题
                json.put("invoiceContent",invoice_info_value[3]); //发票用途
                json.put("invoiceId",invoice_info_value[2]); //纳税人编号
            }

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sigh="+sign);
            ///*********
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpCommitEncryptItemOrderData(ConStant.APP_VERSION,input,sign,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommitOrderStatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CommitOrderStatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();
                        op_status=false;
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        Utils.print(tag,"1ordersn="+statusData.getData().getOrderSn());
                        Utils.print(tag,"1price="+statusData.getData().getAmount());

                        //进入支付页面
                        PaymentPage.launch((ShoppingCartList)mContext,statusData.getData().getOrderSn(),statusData.getData().getAmount(),directPaymentData.getSkuInfo().getGoodsName());
                        //关闭当前窗口
                        RxBus.get().post(ConStant.obString_select_commodity_type,"back");
                        finish();

                    }
                });
        addSubscription(s);
    }



    /**
     * 购物车直接进入支付订单(加密接口)
     */
    public void commitEncryptShoppingCartOrderData(){
        Utils.print(tag,"commitEncryptShoppingCartOrderData");
        startProgressDialog();
        String input="";
        String sign="";
        try{
            Gson gson = new Gson();
            //用户提交服务器实体类
            ShoppingPostOrderInfo postOrderInfo = new ShoppingPostOrderInfo();
            //map 用户计算md5
            org.json.JSONObject json = new org.json.JSONObject();

            //userid
            postOrderInfo.setUserid(ConStant.getInstance(mContext).userID);
            json.put("userid",ConStant.getInstance(mContext).userID);

            //cardSn
            postOrderInfo.setCartSn(shoppingCartRecords.get(0).getCartSn());//购物车sn
            json.put("cartSn",shoppingCartRecords.get(0).getCartSn()); //购物车sn

            //receiveId
            postOrderInfo.setReceiveId(receiveId);
            json.put("receiveId",receiveId);

            if(invoiceType==ConStant.INVOICE_NULL){
                postOrderInfo.setInvoice(false);
                json.put("isInvoice",false);
            }else if(invoiceType==ConStant.INVOICE_PERSON){
                postOrderInfo.setInvoice(true);
                postOrderInfo.setInvoiceType(invoice_info_value[0]);//发票抬头

                json.put("isInvoice",true);
                json.put("invoiceType",invoice_info_value[0]); //发票抬头
            }else if(invoiceType==ConStant.INVOICE_COMPANY){
                postOrderInfo.setInvoice(true);//是否需要发票
                postOrderInfo.setInvoiceType(invoice_info_value[0]);//发票抬头
                postOrderInfo.setInvoiceTitle(Utils.formatInvalidString(invoice_info_value[1])); //发票标题
                postOrderInfo.setInvoiceContent(invoice_info_value[3]); //发票用途
                postOrderInfo.setInvoiceId(invoice_info_value[2]); //纳税人编号

                json.put("isInvoice",true); //是否需要发票
                json.put("invoiceType", invoice_info_value[0]); //发票抬头
                json.put("invoiceTitle", Utils.formatInvalidString(invoice_info_value[1])); //发票标题
                json.put("invoiceContent",invoice_info_value[3]); //发票用途
                json.put("invoiceId",invoice_info_value[2]); //纳税人编号
            }
            postOrderInfo.setItemIdList(itemIdList);
            if(itemIdList.size()>0){
                json.put("itemIdList",itemIdList);
            }


            /*org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("cartSn",shoppingCartRecords.get(0).getCartSn()); //购物车sn
            json.put("receiveId",receiveId);
            if(invoiceType==ConStant.INVOICE_NULL){
                json.put("isInvoice",false);
            }else if(invoiceType==ConStant.INVOICE_PERSON){
                json.put("isInvoice",true);
                json.put("invoiceType",invoice_info_value[0]); //发票抬头
            }else if(invoiceType==ConStant.INVOICE_COMPANY){
                json.put("isInvoice",true); //是否需要发票
                json.put("invoiceType", invoice_info_value[0]); //发票抬头
                json.put("invoiceTitle", Utils.formatInvalidString(invoice_info_value[1])); //发票标题
                json.put("invoiceContent",invoice_info_value[3]); //发票用途
                json.put("invoiceId",invoice_info_value[2]); //纳税人编号
            }
            if(itemIdList.size()>0)
               json.put("itemIdList",itemIdList);*/

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sigh="+sign);
            ///*********

            input = gson.toJson(postOrderInfo);
            input = input.replace("{","%7B").replace("}","%7D");
            input = input.replace("[","%5B").replace("]","%5D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpCommitEncryptShoppingcartOrderData(ConStant.APP_VERSION,input,sign,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommitOrderStatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CommitOrderStatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();
                        op_status=false;
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            finish();
                            return;
                        }

                        Utils.print(tag,"ordersn="+statusData.getData().getOrderSn());
                        Utils.print(tag,"price="+statusData.getData().getAmount());

                        //进入支付页面
                        String payName = "";
                        for(int i=0;i<shoppingCartRecords.size();i++){
                            if(i==shoppingCartRecords.size()-1){
                                payName = payName + shoppingCartRecords.get(i).getGoodsName();
                            }else{
                                payName = payName + shoppingCartRecords.get(i).getGoodsName()+"-";
                            }
                        }
                        if(payName.length()>100){
                            payName = payName.substring(0,100);
                        }
                        Utils.print(tag,"payname="+payName);
                        PaymentPage.launch((ShoppingCartList)mContext,statusData.getData().getOrderSn(),statusData.getData().getAmount(),payName);
                        finish();
                    }
                });
        addSubscription(s);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
        Utils.print(tag,"onDestroy");
        unregisterReceiver(nettyReceiver);
        RxBus.get().unregister(ConStant.obString_modify_shopping_cart,observable);
    }


    /**
     * 是否有买赠
     * @return
     */
    private boolean checkHasDirectBuyGift(){
        boolean result = false;
        if(directPaymentData.getShoppingCartInfo()!=null){
            List<ShoppingCartInfoType> infoType = directPaymentData.getShoppingCartInfo().getGiftData();
            if(infoType!=null){
                for (int i = 0; i < infoType.size(); i++) {
                    String type = infoType.get(i).getPromotionType();
                    if(type.equals(ConStant.BUY_GIFTS)){
                        buyGiftList = infoType.get(i).getGiftList();
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 是否有满赠
     * @return
     */
    private boolean checkHasDirectFullGift(){
        boolean result = false;
        if(directPaymentData.getShoppingCartInfo()!=null){
            List<ShoppingCartInfoType> infoType = directPaymentData.getShoppingCartInfo().getGiftData();
            if(infoType!=null){
                for (int i = 0; i < infoType.size(); i++) {
                    String type = infoType.get(i).getPromotionType();
                    if(type.equals(ConStant.FULL_GIFTS)){
                        fullGiftList = infoType.get(i).getGiftList();
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }


    /**
     * 添加每个商品的买赠商品
     * @param layout
     */
    private void addBuyGiftListView(LinearLayout layout){

        if(buyGiftList==null)
            return;


        for (int i = 0; i < buyGiftList.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_cart_buy_gift_item,null);
            TextView name = (TextView)view.findViewById(R.id.buy_gift_goods_name);
            TextView number = (TextView)view.findViewById(R.id.buy_gift_goods_number);
            name.setText(buyGiftList.get(i).getGiftName());
            number.setText("X"+buyGiftList.get(i).getGiftNum());

            if(i==0){
                view.findViewById(R.id.buy_gift_title).setVisibility(View.VISIBLE);
            }else{
                view.findViewById(R.id.buy_gift_title).setVisibility(View.INVISIBLE);
            }
            layout.addView(view);
        }


    }



    /**
     * 添加满赠商品
     * @param layout
     */
    private void addFullGiftListView(LinearLayout layout){

        if(fullGiftList==null)
            return;

        full_gift_title_name.setVisibility(View.VISIBLE);

        for (int i = 0; i < fullGiftList.size(); i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.full_gift_item,null);
            TextView name = (TextView)view.findViewById(R.id.full_gift_goods_name);
            TextView number = (TextView)view.findViewById(R.id.full_gift_goods_number);
            name.setText(fullGiftList.get(i).getGiftName());
            number.setText("X"+fullGiftList.get(i).getGiftNum());
            layout.addView(view);
        }


    }


    /**
     * 查看订单类型
     */
    private void checkOrderType(){
        //如果是拍卖订单，则显示订单号
        //layout_order_sn
        if(acutionData==null)
            return;
        TextView textView = new TextView(this);
        textView.setText("订单号:"+acutionData.getOrderSn());
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.getPaint().setFakeBoldText(true);
        textView.setTextSize(25);
        layout_order_sn.addView(textView);
    }

    /**
     * 检测订单是否有优惠，右侧的优惠-0.00
     */
    private void checkHasDiscount(){
        if(directPaymentData.getShoppingCartInfo()!=null && directPaymentData.getShoppingCartInfo().getDiscountPrice()==0){
            layout_discount.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 检测是否拍卖订单类型
     * @return
     */
    private boolean isAcutionOrder(){
        return typeUI_ID==ConStant.ACUTION_PAYMENT?true:false;
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
            submit_order_text_time.setText(String.format("(%02d:%02d)", minute,second));

        }

        @Override
        public void onFinish() {
            submit_order_text_time.setText("(00:00)");
            layout_contain.removeAllViews();
            transaction_close_status.setVisibility(View.VISIBLE);
            transaction_close_status.setText(mContext.getString(R.string.transaction_close));
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


    @Override
    protected void onResume() {
        super.onResume();
        Utils.print(tag,"onResume");
        //检测是否启动安装服务app
        if(!Utils.isServiceWorked(this, ConStant.Install_ServiceName)){
            //启动安装服务
            Intent intent = new Intent(mContext,InstallService.class);
            intent.putExtra("status", InstallService.ENTER);
            startService(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.print(tag,"onPause");
        if(!Utils.isServiceWorked(this, ConStant.Install_ServiceName)){
            Intent intent = new Intent(this,InstallService.class);
            intent.putExtra("status",InstallService.EXIT);
            startService(intent);
        }
    }



    BroadcastReceiver nettyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msgJson = intent.getStringExtra("msgJson");
            Utils.print(tag, "netty>>>" + msgJson);

            Gson gson = new Gson();
            DomyInvoiceTcpMsgBodyVo invoiceTcpMsgBodyVo = gson.fromJson(msgJson, DomyInvoiceTcpMsgBodyVo.class);
            DomyInvoiceTcpMsgVo invoiceInfo = invoiceTcpMsgBodyVo.getInfo().get(0);
            if(invoiceInfo.getActionType()==ConStant.ACTION_TYPE_INVOICE && invoiceInfo.getContentId().equals(ConStant.NETTY_OP_SUCESS)){
                Utils.print(tag,"edit invoice");
                String[] invoice_type = context.getResources().getStringArray(R.array.invoice_type);

                OpShoppingCart opShoppingCart = new OpShoppingCart();
                opShoppingCart.setKey(OpShoppingCart.MODIFY_INVOICE);
                if(invoiceInfo.getInvoiceType()==ConStant.INVOICE_TYPE_NULL){
                    opShoppingCart.setValue(invoice_type[0]);
                    opShoppingCart.setInvoiceType(ConStant.INVOICE_NULL);
                }else if(invoiceInfo.getInvoiceType()==ConStant.INVOICE_TYPE_PERSON) {
                    opShoppingCart.setValue(invoice_type[1]);
                    opShoppingCart.setInvoiceType(ConStant.INVOICE_PERSON);
                }else{
                    opShoppingCart.setValue(invoice_type[2]+
                            "#"+invoiceInfo.getInvoiceTitle() +
                            "#"+ invoiceInfo.getInvoiceId() +
                            "#"+context.getResources().getString(R.string.invoice_head_info_data));
                    opShoppingCart.setInvoiceType(ConStant.INVOICE_COMPANY);
                }

                RxBus.get().post(ConStant.obString_modify_shopping_cart,opShoppingCart);
                if(invoiceDialog!=null){
                    invoiceDialog.dismiss();
                }
            }
        }
    };

    private void regeditReceiver() {
        Utils.print(tag,"regeditReceiver netty");
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConStant.ACTION_NETTRY_RECEIVER);
        registerReceiver(nettyReceiver, mFilter);
    }


    @Override
    public void OnItemViewSelectedListener(int position) {
        Utils.print(tag,"positon==="+position+",count="+recyclerView.getAdapter().getItemCount());
        if(position==recyclerView.getAdapter().getItemCount()-1){
            recyclerView.scrollToPosition(position);
        }
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
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
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
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderUpdateData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        op_status=false;
                        stopProgressDialog();
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData()!=null){
                            Utils.print(tag,"orderSn=="+statusData.getData().getOrderSn());
                        }


                        if(invoiceType!=ConStant.INVOICE_NULL){
                            invoice_status.setVisibility(View.VISIBLE);
                        }else{
                            invoice_status.setVisibility(View.GONE);
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
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********

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
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderUpdateData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        op_status=false;
                        stopProgressDialog();
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData()!=null){
                            Utils.print(tag,"orderSn=="+statusData.getData().getOrderSn());
                        }

                        if(address_info_value.length>1){
                            Utils.print(tag,"address=="+address_info_value[1]);
                            address_info = address_info_value[1];
                        }else{
                            address_info="";
                        }
                        setAddressInfo(address_info);
                    }
                });

    }




    /**
     * 拍卖的商品参加竞拍
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
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
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
                        String error_tips = "";
                        stopProgressDialog();
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        op_status = false;
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CancelOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();

                        if(statusData.getReturnValue()==-1){
                            op_status=false;
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }
                        Utils.print(tag,"sn=="+statusData.getData().getOrderSn());
                        transaction_close_status.setVisibility(View.VISIBLE);
                        transaction_close_status.setText(mContext.getString(R.string.transaction_close));
                        layout_contain.removeAllViews();
                        cancelTimer();
                        op_status=false;
                    }
                });
    }




    private void initFocusItem(){
        item_focus.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    commodity_name.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    commodity_type.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    commodity_quantity.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    commodity_price.setTextColor(mContext.getResources().getColor(android.R.color.white));
                }else{
                    commodity_name.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                    commodity_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                    commodity_quantity.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                    commodity_price.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                }
            }
        });

        item_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAcutionOrder()){
                    Utils.print(tag,"sn...="+acutionData.getGoodSn());
                    AcutionInfomation.launch(ShoppingCartList.this,acutionData.getGoodSn());
                }
            }
        });
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
