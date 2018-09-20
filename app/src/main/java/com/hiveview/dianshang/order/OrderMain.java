package com.hiveview.dianshang.order;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.OrderAdapter;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.OrderdetailsDialog;
import com.hiveview.dianshang.entity.acution.unpay.order.DomyAuctionOrderVo;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderData;
import com.hiveview.dianshang.entity.commodity.CommodityAPI;
import com.hiveview.dianshang.entity.order.OrderData;
import com.hiveview.dianshang.entity.order.OrderRecord;
import com.hiveview.dianshang.entity.token.TokenData;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.utils.DeviceInfo;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gavin on 2017/5/3.
 */

public class OrderMain extends BaseFragment implements View.OnClickListener{
    LayoutInflater layoutInflater;
    @BindView(R.id.orderListView)
    ListView orderListView;

    @BindView(R.id.layout_tips)
    View layout_tips;

    @BindView(R.id.tips_text)
    TextView tips_text;

    @BindView(R.id.layout)
    LinearLayout layout;



    private OrderAdapter adapter;
    private ReceiverOrderMain receiverOrderMain;
    public static int currentPosition = -1;
    private String tag="OrderMain";
    private List<OrderRecord> orderInfoList=new ArrayList<OrderRecord>();
    private Boolean firstBoolean=true;
    private int currentSelection=-1;

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;
    public static RelativeLayout layout_order;
    private static RelativeLayout layout_domy_user_center;
    private static RelativeLayout layout_home;
    private static RelativeLayout layout_acution;
    private static RelativeLayout layout_domy_shopping_cart;
    private static RelativeLayout layout_commodity;
    private static RelativeLayout layout_search;
    private static RelativeLayout layout_collection;
    private static RelativeLayout layout_after_sale_service;
    private CustomProgressDialog mProgressDialog = null;
    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;
    private int totalCount=0;
   // private Boolean isHas=false;
    /**
     * 最大请求的页数
     */
    private int maxPage = 1;
    /**
     * 当前请求数据的页数
     */
    private int pageIndex = 1;
    /**
     * 为true时，计算一下最大页数，false时无需计算
     */
    private boolean isMaxPage=true;
    //获取是否刷新界面的标志
    private SharedPreferences preferences;
    //为解决订单详情进入商品页后，重刷数据
    private boolean firstRefresh=true;
    private SharedPreferences.Editor editor;
    private boolean isLocation=true;




    //处理筛选订单条件的变量
    private int fromMain=0;
    private PopupWindow popupWindow;
    //记录订单筛选条件默认为1，其他依次类推
    private int orderType=ConStant.ORDER_TYPE_ALL;//全部订单类型
    private int orderStatus=ConStant.ORDER_STATUS_ALL;//全部订单状态
    Button total_type;
    Button buy_type;
    Button auction_type;
    Button total_status;
    Button pay_status;
    Button sendgoods_status;
    Button receivegoods_status;
    Button complete_status;
    Button down_status;
    private DomyAuctionOrderVo auctionOrderBean;
    //用于拍卖未支付自动交易关闭需刷新列表数据是，是否显示进度条
    private boolean isProgressBar=true;
    /*
    * 区分订单类型
    * */
    //拍卖未支付
    public static int ORDER_TYPE_AUCTON_NO=0;
    //拍卖其他状态
    public static int ORDER_TYPE_AUCTON_OTHER=1;
    //普通订单
    public static int ORDER_TYPE_COMMON_YN=2;





    @Override
    protected int getLayoutId() {
        return R.layout.layout_order;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.print("test","onActivityCreated");
        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(orderListView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }

        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferences = mContext.getSharedPreferences("isRefreshOrder",
                mContext.MODE_WORLD_READABLE);
        editor = preferences.edit();


        if(layout_order!=null){
            layout_order.setOnKeyListener(onKeyListener);
        }
        orderListView.setOnKeyListener(onKeyListener);


        //点击ListView
        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition=position;//当前选择的节目item
                //isHasLogistics表示是否显示物流按钮，true是显示，false是不显示。
                Boolean isHasLogistics=true;
                List<OrderRecord> list=adapter.getList();
                if(list.get(currentPosition).getStatus()==Status.pendingPayment.ordinal()||
                        list.get(currentPosition).getStatus()==Status.close.ordinal()||
                        list.get(currentPosition).getStatus()==Status.processing.ordinal()||
                        list.get(currentPosition).getStatus()==Status.refund_seller.ordinal()||
                        list.get(currentPosition).getStatus()==Status.refund_hiveview.ordinal()||
                        list.get(currentPosition).getStatus()==Status.refunded.ordinal() ||
                        list.get(currentPosition).getStatus()==Status.unpay_close.ordinal()){
                    isHasLogistics=false;
                }



                //普通订单详情页
                int status=ConStant.ORDER_STATUS_UNPAY;
                if(list.get(currentPosition).getStatus()==Status.pendingPayment.ordinal()){
                    //待付款
                    status=ConStant.ORDER_STATUS_UNPAY;
                }else if(list.get(currentPosition).getStatus()==Status.pendingShipment.ordinal()||list.get(currentPosition).getStatus()==12) {
                    //待发货
                    status=ConStant.ORDER_STATUS_UNDELIVERY;
                }else if(list.get(currentPosition).getStatus()==Status.pendingReceive.ordinal()) {
                    //待收货
                    status=ConStant.ORDER_STATUS_UNTACK_DELIVERY;
                }else if(list.get(currentPosition).getStatus()==Status.completed.ordinal()) {
                    //交易完成
                    status=ConStant.ORDER_STATUS_TRANSACTION_SUCESS;
                }else{//交易关闭
                    status=ConStant.ORDER_STATUS_TRANSACTION_CLOSE;
                }

                if(list.get(currentPosition).getOrderType()==ConStant.ORDER_TYPE_COMMOM){//普通订单
                    Log.i("===333===","==普通订单");
                    DomyAuctionOrderVo auctionOrderBeanOther=null;
                    OrderdetailsDialog orderdetailsDialog = new OrderdetailsDialog(mContext,list.get(currentPosition).getOrderSn(),isHasLogistics,status,list.get(currentPosition),ORDER_TYPE_COMMON_YN,list.get(currentPosition).getOrderInfoList(),auctionOrderBeanOther);
                    if(!orderdetailsDialog.isShowing()){
                        orderdetailsDialog.showUI();
                    }
                }else if(list.get(currentPosition).getOrderType()==ConStant.ORDER_TYPE_AUCTON){//拍卖订单
                    if(status==ConStant.ORDER_STATUS_UNPAY){//拍卖未支付
                        Log.i("===333===","==拍卖未支付");
                        getAcutionUnpayData(list,status);

                    }else{//拍卖其他状态
                        Log.i("===333===","==拍卖其他状态");
                        DomyAuctionOrderVo auctionOrderBeanOther=null;
                        OrderdetailsDialog orderdetailsDialog = new OrderdetailsDialog(mContext,list.get(currentPosition).getOrderSn(),isHasLogistics,status,list.get(currentPosition),ORDER_TYPE_AUCTON_OTHER,list.get(currentPosition).getOrderInfoList(),auctionOrderBeanOther);
                        if(!orderdetailsDialog.isShowing()){
                            orderdetailsDialog.showUI();
                        }
                    }
                }




            }
        });

        orderListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               //Toast.makeText(mContext, "焦点=="+hasFocus, Toast.LENGTH_SHORT).show();
                if(hasFocus){
                    if(orderListView.getSelectedView()!=null){
                        orderListView.getSelectedView().setBackground(mContext.getResources().getDrawable(R.drawable.order_selector));

                    }

                }
            }
        });
        //监听滚动底部
        orderListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                totalCount=totalItemCount;

                if(visibleItemCount+firstVisibleItem==totalItemCount&&totalItemCount!=0&&orderListView.hasFocus()==true){
                    Log.i(tag, "===滑到底部");
                    pageIndex++;
                    if(pageIndex>maxPage){
                        pageIndex--;
                        return;
                    }

                    orderInfoList.clear();
                    getOrderListData();
                    Log.i(tag,"===再次请求数据！");
                }

            }
        });

        orderListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isLocation&&position!=0){
                    orderListView.post(new Runnable() {
                        @Override
                        public void run() {
                            orderListView.clearFocus();
                            orderListView.setSelection(0);
                            firstBoolean=true;
                            isLocation=false;
                        }
                    });
                    return;
                }
                currentSelection=position;
                if(!layout_domy_shopping_cart.hasFocus()&&!layout_domy_user_center.hasFocus()&&!layout_collection.hasFocus() &&!layout_after_sale_service.hasFocus()
                        &&!layout_search.hasFocus() && !layout_commodity.hasFocus()){
                    if(firstBoolean&&position==0){
                        if(layout_order!=null){
                            orderListView.clearFocus();
                            layout_order.setFocusable(true);
                            layout_order.requestFocus();
                        }
                        view.setBackground(mContext.getResources().getDrawable(R.drawable.shape1));
                        firstBoolean=false;
                        isLocation=false;
                    }else{
                        view.setBackground(mContext.getResources().getDrawable(R.drawable.order_selector));
                        if(!layout_domy_shopping_cart.hasFocus()){
                            orderListView.requestFocus();
                        }

                    }
                }
                //当此选中的item的子控件需要获得焦点时
                parent.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            }
        });

        //注册广播
        receiverOrderMain = new ReceiverOrderMain ();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dianshang.damai.message_restore");
        mContext.registerReceiver(receiverOrderMain, intentFilter);

        tips_text.setText(getResources().getString(R.string.order_item_null_tips));

    }

    @Override
    public void onResume() {
        super.onResume();
        //进入订单详情页里面的列表，由于进入新的activity，所以重新请求了列表的数据，防止发生，做个标识
       // private boolean isRefresh
       // Toast.makeText(mContext, "订单列表重现！", Toast.LENGTH_SHORT).show();
        //适配订单列表
        //请求订单列表数据，适配listView

       boolean b=preferences.getBoolean("isRefresh",true);
        if(b||firstRefresh){
            orderListView.clearFocus();
            isMaxPage=true;
            maxPage = 1;
            pageIndex = 1;
            orderInfoList=new ArrayList<OrderRecord>();
            orderListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            adapter=new OrderAdapter(mContext,new ArrayList<OrderRecord>(),orderListView);
            orderListView.setAdapter(adapter);
            Log.i("===222===","===第一次请求列表数据=="+currentPosition);
            String user = (String) SPUtils.get(mContext, ConStant.USERID, "");
            String token = (String) SPUtils.get(mContext, ConStant.USER_TOKEN, "");
            if(pageIndex==1&&isProgressBar){
                startProgressDialog();
            }
            if (!user.equals("") && !token.equals("")) {
                Utils.print(tag, "set token");
                ConStant.userID = user;
                ConStant.Token = token;
                getOrderListData();
            }else{
                getTokenData();
            }

            firstRefresh=false;
        }
        editor.putBoolean("isRefresh",true);
        editor.commit();
        if(!Utils.isConnected(mContext)){
            layout_order.setFocusable(true);
            layout_order.requestFocus();

        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            layout_order = (RelativeLayout) mainActivity.findViewById(R.id.layout_order);
            layout_home=(RelativeLayout)mainActivity.findViewById(R.id.layout_home);
            layout_acution=(RelativeLayout)mainActivity.findViewById(R.id.layout_acution);
            layout_domy_user_center=(RelativeLayout)mainActivity.findViewById(R.id.layout_domy_user_center);
            layout_domy_shopping_cart=(RelativeLayout)mainActivity.findViewById(R.id.layout_domy_shopping_cart);
            layout_commodity=(RelativeLayout)mainActivity.findViewById(R.id.layout_commodity);
            layout_search=(RelativeLayout)mainActivity.findViewById(R.id.layout_search);
            layout_collection=(RelativeLayout)mainActivity.findViewById(R.id.layout_collection);
            layout_after_sale_service=(RelativeLayout)mainActivity.findViewById(R.id.layout_after_sale_service);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(receiverOrderMain);
    }

    public class ReceiverOrderMain extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean restoreBoolean = intent.getBooleanExtra("restore",true);
            if(!restoreBoolean){//左键焦点回落
                if(orderListView.getSelectedView()!=null){
                    if ((!orderListView.getSelectedView().hasFocus())){
                        // Toast.makeText(mContext, "焦点回落到订单！", Toast.LENGTH_SHORT).show();
                        layout_order.setFocusable(true);
                        layout_order.requestFocus();
                        currentPosition=-1;
                    }
                }

            }

            //为了处理拍卖未支付订单，取消订单后，返回订单列表也需要刷新数据
            Boolean orderrefresh = intent.getBooleanExtra("orderrefresh",true);
            if(orderrefresh){
                //定位第一个
                currentPosition=0;
                //刷新数据时，不显示进度条
                isProgressBar=false;
                onResume();
            }


        }

    }



    /**
     * 获取订单列表数据
     */
    public void getOrderListData(){
        Utils.print(tag,"getOrderData");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            layout_order.setFocusable(true);
            layout_order.requestFocus();
            if(pageIndex==1){
                stopProgressDialog();
            }
            return;
        }

        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            Log.i(tag,"=====pageIndex=="+pageIndex);
            json.put("pageIndex",pageIndex);
            if(orderStatus!=ConStant.ORDER_STATUS_ALL){
                json.put("orderStatus",orderStatus);// 不传递表示查询所有的
                Log.i("===333===","orderStatus"+orderStatus);
            }
            json.put("orderType",orderType);
            Log.i("===333===","orderType"+orderType);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetOrderListData(ConStant.APP_VERSION,input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderData>() {
                    @Override
                    public void onCompleted() {
                        if(pageIndex==1){//清除适配器之前的数据
                            adapter.clearAll();
                        }
                        if(orderInfoList!=null){
                            if(pageIndex==1&&orderInfoList.size()!=0){
                                layout.setVisibility(View.VISIBLE);
                                layout_tips.setVisibility(View.GONE);
                            }
                            adapter.addAll(orderInfoList);
                        }else{
                            if(pageIndex==1){
                                layout.setVisibility(View.INVISIBLE);
                                layout_tips.setVisibility(View.VISIBLE);
                                layout_order.setFocusable(true);
                                layout_order.requestFocus();
                            }

                        }

                        if(currentPosition!=-1){//非首次进入该页，即返回状态的处理
                            if(adapter!=null&&adapter.getList()!=null&&adapter.getList().size()!=0){//进行定位处理
                                orderListView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        orderListView.setSelection(currentPosition);
                                    }
                                });
                            }

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(pageIndex==1){
                            stopProgressDialog();
                            layout_order.setFocusable(true);
                            layout_order.requestFocus();
                        }
                        //列表刷新数据显示进度条的标志初始化
                        isProgressBar=true;
                        Utils.print(tag,"===error="+e.getMessage());
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                    }

                    @Override
                    public void onNext(OrderData orderData) {
                        Utils.print(tag,"===status=="+orderData.getErrorMessage());
                        //列表刷新数据显示进度条的标志初始化
                        isProgressBar=true;
                        if(orderData.getReturnValue()==-1){//网络异常
                            if(pageIndex==1){
                                stopProgressDialog();
                            }
                            ToastUtils.showToast(mContext,orderData.getErrorMessage());
                            if(pageIndex==1){
                                layout_order.setFocusable(true);
                                layout_order.requestFocus();
                            }
                            return;
                        }

                        if(orderData.getReturnValue()==-2){   //token已经失效,再次请求
                            getTokenData();
                            return;
                        }

                        if(pageIndex==1){
                            stopProgressDialog();
                        }
                        try {
                            //计算最大页数
                            if(isMaxPage){
                                if(orderData.getData().getTotalCount()>0){
                                    int total=orderData.getData().getTotalCount();
                                    maxPage = total/ConStant.PAGESIZE;
                                    if(total%ConStant.PAGESIZE!=0){
                                        maxPage++;
                                    }
                                    Log.i(tag,"===最大页数=="+maxPage);
                                }
                                isMaxPage=false;
                            }

                            List<OrderRecord> orderRecords = orderData.getData().getRecords();
                            orderInfoList=orderRecords;
                            if(orderInfoList!=null&&orderInfoList.size()==0&&pageIndex==1){
                                layout.setVisibility(View.INVISIBLE);
                                layout_tips.setVisibility(View.VISIBLE);
                                layout_order.setFocusable(true);
                                layout_order.requestFocus();
                            }
                            for (int i=0;i<orderRecords.size();i++){
                                Utils.print(tag,"===time="+orderRecords.get(i).getCreateTime()+",orderSn="+orderRecords.get(i).getOrderSn());
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                            if(pageIndex==1){
                                layout.setVisibility(View.INVISIBLE);
                                layout_tips.setVisibility(View.VISIBLE);
                                layout_order.setFocusable(true);
                                layout_order.requestFocus();
                            }

                        }



                    }
                });
        addSubscription(subscription);
    }

    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(v.getId()==R.id.orderListView && keyCode==KeyEvent.KEYCODE_DPAD_UP) {
                   firstBoolean=false;
                    orderListView.requestFocus();
                    setFocus(false);


                }else if(v.getId()==R.id.orderListView && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                    orderListView.requestFocus();
                    setFocus(false);
                    if (totalCount>3&&orderListView.getSelectedItemPosition() == totalCount-1) {
                        if (null != feedbackAnimator && !feedbackAnimator.isRunning()) {
                            Utils.print(tag,"start animation");
                            feedbackAnimator.start();
                        }

                    }


                }else if(v.getId()==R.id.orderListView && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                   setFocus(false);
				   orderListView.getSelectedView().setBackground(mContext.getResources().getDrawable(R.drawable.shape1));
                }else if(v.getId()==R.id.orderListView && keyCode==KeyEvent.KEYCODE_MENU){
                   // Toast.makeText(mContext, "点击菜单键！！！", Toast.LENGTH_SHORT).show();
                    fromMain= OrderAdapter.Location.RIGHT.ordinal();
                    initPopupWindow();

                }
                if(v.getId()==R.id.layout_order &&keyCode==KeyEvent.KEYCODE_DPAD_CENTER){
                    firstBoolean=true;
                    currentPosition=-1;
                    //初始化订单类型（全部），订单状态（全部）
                    orderType=ConStant.ORDER_TYPE_ALL;
                    orderStatus=ConStant.ORDER_STATUS_ALL;
                   // Toast.makeText(mContext, "点击订单按钮！！！", Toast.LENGTH_SHORT).show();
                }
                if(v.getId()==R.id.layout_order){
                    setFocus(true);
                }
            }
            return false;
        }
    };

    public static void setFocus(Boolean b){
        layout_home.setFocusable(b);
        layout_acution.setFocusable(b);
        layout_domy_user_center.setFocusable(b);
        layout_domy_shopping_cart.setFocusable(b);
        layout_commodity.setFocusable(b);
        layout_search.setFocusable(b);
        layout_collection.setFocusable(b);
        layout_order.setFocusable(b);
        layout_after_sale_service.setFocusable(b);

    }

    public void startProgressDialog() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new CustomProgressDialog(mContext);
            }
            Utils.print(tag, "startProgressDialog");
            //mProgressDialog.setMessage(msg);
            mProgressDialog.setCancelable(true);
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



    /**
     * 获取token
     */
    public void getTokenData(){
        Utils.print(tag,"getTokenData");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("mac", DeviceInfo.getInstance(mContext).getMac());
            json.put("sn",DeviceInfo.getInstance(mContext).getSn());
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetToken(input)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TokenData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        try{
                            String error_tips = "";
                            stopProgressDialog();
                            if(Utils.isConnected(mContext)){
                                error_tips = mContext.getResources().getString(R.string.error_service_exception);
                            }else{
                                error_tips = mContext.getResources().getString(R.string.error_network_exception);
                            }
                            ToastUtils.showToast(mContext,error_tips);
                            Utils.print(tag,"error="+e.getMessage());
                        }catch (Exception ee){
                            ee.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(TokenData tokenData) {
                        Utils.print(tag,"token status=="+tokenData.getErrorMessage());
                        if(tokenData.getReturnValue()==-1){
                            stopProgressDialog();
                            ToastUtils.showToast(mContext,tokenData.getErrorMessage());
                            return;
                        }


                        String user = tokenData.getData().getUserid();
                        String token = tokenData.getData().getToken();
                        SPUtils.putApply(mContext, ConStant.USERID,tokenData.getData().getUserid());
                        SPUtils.putApply(mContext,ConStant.USER_TOKEN,tokenData.getData().getToken());


                        ConStant.userID = user;
                        ConStant.Token = token;

                        getOrderListData();

                    }
                });
        addSubscription(s);
    }

    protected void initPopupWindow(){
        View popupWindowView = LayoutInflater.from(mContext).inflate(R.layout.layout_orderselect, null);
        //内容，高度，宽度
        if(OrderAdapter.Location.BOTTOM.ordinal() == fromMain){
            popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        }else{
            popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT, true);
        }
        //动画效果
        if(OrderAdapter.Location.RIGHT.ordinal() == fromMain){
            popupWindow.setAnimationStyle(R.style.AnimationRightFade);
        }
        //菜单背景色
        //ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselectbackgroud));
        //宽度
        //popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
        //高度
        //popupWindow.setHeight(LayoutParams.FILL_PARENT);
        //显示位置
        if(OrderAdapter.Location.RIGHT.ordinal() == fromMain){
            popupWindow.showAtLocation(LayoutInflater.from(mContext).inflate(R.layout.layout_order, null), Gravity.RIGHT, 0, mContext.getResources().getDimensionPixelSize(R.dimen.length_500));
        }
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());

        //事件的处理
        total_type=(Button)popupWindowView.findViewById(R.id.total_type);
        buy_type=(Button)popupWindowView.findViewById(R.id.buy_type);
        auction_type=(Button)popupWindowView.findViewById(R.id.auction_type);
        total_status=(Button)popupWindowView.findViewById(R.id.total_status);
        pay_status=(Button)popupWindowView.findViewById(R.id.pay_status);
        sendgoods_status=(Button)popupWindowView.findViewById(R.id.sendgoods_status);
        receivegoods_status=(Button)popupWindowView.findViewById(R.id.receivegoods_status);
        complete_status=(Button)popupWindowView.findViewById(R.id.complete_status);
        down_status=(Button)popupWindowView.findViewById(R.id.down_status);


        total_type.setOnClickListener(this);
        buy_type.setOnClickListener(this);
        auction_type.setOnClickListener(this);


        total_status.setOnClickListener(this);
        pay_status.setOnClickListener(this);
        sendgoods_status.setOnClickListener(this);
        receivegoods_status.setOnClickListener(this);
        complete_status.setOnClickListener(this);
        down_status.setOnClickListener(this);

        /*
        * 记忆原来的订单类型和状态
        * */
        //订单类型的定位
        if(orderType==ConStant.ORDER_TYPE_ALL){//全部订单
            total_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
            total_type.requestFocus();
        }else if(orderType==ConStant.ORDER_TYPE_COMMOM){//普通订单
            buy_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
            buy_type.requestFocus();
        }else if(orderType==ConStant.ORDER_TYPE_AUCTON){//拍卖订单
            auction_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
            auction_type.requestFocus();
        }
        //订单状态的定位
        if(orderStatus==ConStant.ORDER_STATUS_ALL){//订单全部状态
            total_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
        }else if(orderStatus==ConStant.ORDER_STATUS_UNPAY){//待支付
            pay_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
        }else if(orderStatus==ConStant.ORDER_STATUS_UNDELIVERY){//待发货
            sendgoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
        }else if(orderStatus==ConStant.ORDER_STATUS_UNTACK_DELIVERY){//待收货
            receivegoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
        }else if(orderStatus==ConStant.ORDER_STATUS_TRANSACTION_SUCESS){//交易完成
            complete_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
        }else if(orderStatus==ConStant.ORDER_STATUS_TRANSACTION_CLOSE){//交易关闭
            down_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
        }

    }

    @Override
    public void onClick(View v) {
        //orderselect_focus
        switch (v.getId()) {
            case R.id.total_type:
                if(orderType!=ConStant.ORDER_TYPE_ALL){
                   // Toast.makeText(mContext, "点击全部！！！", Toast.LENGTH_SHORT).show();
                    orderType=ConStant.ORDER_TYPE_ALL;
                    total_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
                    buy_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    auction_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    currentPosition=0;
                    setFocus(false);
                    getChangedData();
                }
                break;
            case R.id.buy_type:
                if(orderType!=ConStant.ORDER_TYPE_COMMOM){
                    //Toast.makeText(mContext, "点击优购！！！", Toast.LENGTH_SHORT).show();
                    orderType=ConStant.ORDER_TYPE_COMMOM;
                    buy_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
                    total_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    auction_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    currentPosition=0;
                    setFocus(false);
                    getChangedData();
                }

                break;
            case R.id.auction_type:
                if(orderType!=ConStant.ORDER_TYPE_AUCTON){
                   // Toast.makeText(mContext, "点击拍卖！！！", Toast.LENGTH_SHORT).show();
                    orderType=ConStant.ORDER_TYPE_AUCTON;
                    auction_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
                    total_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    buy_type.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    currentPosition=0;
                    setFocus(false);
                    getChangedData();
                }
                break;
            case R.id.total_status:
                if(orderStatus!=ConStant.ORDER_STATUS_ALL){
                  //  Toast.makeText(mContext, "点击全部状态！！！", Toast.LENGTH_SHORT).show();
                    orderStatus=ConStant.ORDER_STATUS_ALL;
                    total_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
                    pay_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    sendgoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    receivegoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    complete_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    down_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    currentPosition=0;
                    setFocus(false);
                    getChangedData();
                }
                break;
            case R.id.pay_status:
                if(orderStatus!=ConStant.ORDER_STATUS_UNPAY){
                   // Toast.makeText(mContext, "点击待付状态！！！", Toast.LENGTH_SHORT).show();
                    orderStatus=ConStant.ORDER_STATUS_UNPAY;
                    pay_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
                    total_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    sendgoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    receivegoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    complete_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    down_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    currentPosition=0;
                    setFocus(false);
                    getChangedData();
                }
                break;
            case R.id.sendgoods_status:
                if(orderStatus!=ConStant.ORDER_STATUS_UNDELIVERY){
                   // Toast.makeText(mContext, "点击待发状态！！！", Toast.LENGTH_SHORT).show();
                    orderStatus=ConStant.ORDER_STATUS_UNDELIVERY;
                    sendgoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
                    total_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    pay_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    receivegoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    complete_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    down_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    currentPosition=0;
                    setFocus(false);
                    getChangedData();
                }
                break;
            case R.id.receivegoods_status:
                if(orderStatus!=ConStant.ORDER_STATUS_UNTACK_DELIVERY){
                   // Toast.makeText(mContext, "点击待收状态！！！", Toast.LENGTH_SHORT).show();
                    orderStatus=ConStant.ORDER_STATUS_UNTACK_DELIVERY;
                    receivegoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
                    total_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    pay_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    sendgoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    complete_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    down_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    currentPosition=0;
                    setFocus(false);
                    getChangedData();
                }
                break;
            case R.id.complete_status:
                if(orderStatus!=ConStant.ORDER_STATUS_TRANSACTION_SUCESS){
                   // Toast.makeText(mContext, "点击完成状态！！！", Toast.LENGTH_SHORT).show();
                    orderStatus=ConStant.ORDER_STATUS_TRANSACTION_SUCESS;
                    complete_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
                    total_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    pay_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    sendgoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    receivegoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    down_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    currentPosition=0;
                    setFocus(false);
                    getChangedData();
                }
                break;
            case R.id.down_status:
                if(orderStatus!=ConStant.ORDER_STATUS_TRANSACTION_CLOSE){
                 //   Toast.makeText(mContext, "点击关闭状态！！！", Toast.LENGTH_SHORT).show();
                    orderStatus=ConStant.ORDER_STATUS_TRANSACTION_CLOSE;
                    down_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selected_icon));
                    total_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    pay_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    sendgoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    receivegoods_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    complete_status.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.orderselect_focus));
                    currentPosition=0;
                    setFocus(false);
                    getChangedData();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     */
    class popupDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            //backgroundAlpha(1f);
        }

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

        /**
         * 退款取消申请
         */
        refund_cancel,
        /**
         * 换货取消申请
         */
        exchange_cancel,
        /**
         * 处理中（待发货－商户确认中）
         */
        processing_shipment_seller,
        /**
         * 非支付交易关闭
         */
        unpay_close,

    }
    /*
    * 订单筛选数据适配
    * */
    public void getChangedData(){
        orderListView.clearFocus();
        isMaxPage=true;
        maxPage = 1;
        pageIndex = 1;
        orderInfoList=new ArrayList<OrderRecord>();
       /* orderListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter=new OrderAdapter(mContext,new ArrayList<OrderRecord>(),orderListView);
        orderListView.setAdapter(adapter);*/
        Log.i("===333===","===筛选请求列表数据==orderType=="+orderType+"==orderStatus=="+orderStatus+"=="+currentPosition);
        /* String user = (String) SPUtils.get(mContext, ConStant.USERID, "");
        String token = (String) SPUtils.get(mContext, ConStant.USER_TOKEN, "");*/
        if(pageIndex==1){
            startProgressDialog();
        }
       /* if (!user.equals("") && !token.equals("")) {
            Utils.print(tag, "set token");
            ConStant.userID = user;
            ConStant.Token = token;*/
            getOrderListData();
     /*   }else{
            getTokenData();
        }*/
    }
    /**
     * 获取拍卖未支付的订单信息
     */
    public void getAcutionUnpayData(List<OrderRecord> list,int statusch){
        Utils.print(tag,"getAcutionUnpayData");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
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
                .httpGetAcutionUnpayData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UnpayOrderData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"===error=1139"+e.getMessage());
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                    }

                    @Override
                    public void onNext(UnpayOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            Utils.print(tag,"===1154");
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        try{
                        if(statusData.getData()!=null){
                            Utils.print(tag,"===1160");
                            auctionOrderBean=statusData.getData();
                            if(auctionOrderBean.isHaveUnpayOrder()){//有拍卖未支付的数据
                                Utils.print(tag,"===1163");
                                //list.get(currentPosition).getOrderSn()
                                Utils.print(tag,"===list.get(currentPosition).getOrderSn()=="+list.get(currentPosition).getOrderSn());
                                Utils.print(tag,"===list.get(currentPosition).getOrderInfoList()=="+list.get(currentPosition).getOrderInfoList());
                                Utils.print(tag,"===list.get(currentPosition).getAmount()=="+list.get(currentPosition).getAmount());
                                OrderdetailsDialog orderdetailsDialog = new OrderdetailsDialog(mContext,list.get(currentPosition).getOrderSn(),false,statusch,list.get(currentPosition),ORDER_TYPE_AUCTON_NO,list.get(currentPosition).getOrderInfoList(),auctionOrderBean);
                                if(!orderdetailsDialog.isShowing()){
                                    orderdetailsDialog.showUI();
                                }
                            }else{//无拍卖未支付的数据，需刷新数据
                                //定位第一个
                                Utils.print(tag,"===1171");
                                currentPosition=0;
                                onResume();
                            }

                        }else{
                            Utils.print(tag,"===1177");
                            ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.error_service_exception));
                        }
                        }catch (Exception e){
                            Utils.print(tag,"===e.getMessage()=="+e.getMessage());
                            e.printStackTrace();

                        }



                    }
                });

    }

}
