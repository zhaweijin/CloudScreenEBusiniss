package com.hiveview.dianshang.saleservice;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.AfterSaleAdapter;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.AfterdetailsDialog;
import com.hiveview.dianshang.entity.AfterSaleEntity;
import com.hiveview.dianshang.entity.AfterSaleEntityItem;
import com.hiveview.dianshang.entity.afterservice.AfterServiceData;
import com.hiveview.dianshang.entity.afterservice.AfterServiceRecord;
import com.hiveview.dianshang.entity.afterservice.exchange.Exchange;
import com.hiveview.dianshang.entity.afterservice.exchange.ExchangeItem;
import com.hiveview.dianshang.entity.afterservice.refund.Refund;
import com.hiveview.dianshang.entity.afterservice.refund.RefundItem;
import com.hiveview.dianshang.entity.commodity.CommodityAPI;
import com.hiveview.dianshang.entity.commodity.afterservice.phone.CommodityPhoneData;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
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
 * Created by carter on 4/10/17.
 */

public class AfterSaleServiceMain extends BaseFragment {
    LayoutInflater layoutInflater;


    @BindView(R.id.layout)
    LinearLayout layout;

    @BindView(R.id.listView)
    ListView listView;

    @BindView(R.id.layout_tips)
    View layout_tips;

    @BindView(R.id.tips_text)
    TextView tips_text;

    @BindView(R.id.totalphone)
    TextView totalphone;

    @BindView(R.id.totalphone2)
    TextView totalphone2;


    private String tag="AfterSaleServiceMain";
    private List<AfterSaleEntity> afterSaleEntityList;
    private List<AfterSaleEntity> afterSaleEntityListAll=new ArrayList<AfterSaleEntity>();
    private ReceiverAftersales receiverAftersales;
    private Boolean firstBoolean=true;
    private CustomProgressDialog mProgressDialog = null;
    public static RelativeLayout layout_order;
    private static RelativeLayout layout_domy_user_center;
    private static RelativeLayout layout_home;
    private static RelativeLayout layout_domy_shopping_cart;
    private static RelativeLayout layout_commodity;
    private static RelativeLayout layout_search;
    private static RelativeLayout layout_collection;
    private static RelativeLayout layout_after_sale_service;
    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;
    private int totalCount=0;
    private AfterSaleAdapter adapter;
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

    @Override
    protected int getLayoutId() {
        return R.layout.layout_aftersaleservicemain;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.print("test","onActivityCreated");
        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(listView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView.setOnKeyListener(onKeyListener);
        layout_after_sale_service.setOnKeyListener(onKeyListener);

        //适配售后电话
        getAfterServicePhone();

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(firstBoolean&&position==0)){
                    listView.requestFocus();
                    view.setBackground(mContext.getResources().getDrawable(R.drawable.order_selector));
                }else{
                    view.setBackground(mContext.getResources().getDrawable(R.drawable.shape1));
                }

              /*  for(int i=0;i<parent.getChildCount();i++){
                    if(i!=position){
                       // parent.getChildAt(i).setBackground(mContext.getResources().getDrawable(R.drawable.rectanglelist));
                    }
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        listView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //Toast.makeText(mContext, "焦点=="+hasFocus, Toast.LENGTH_SHORT).show();
                if(hasFocus){
                    firstBoolean=false;
                    setFocus(false);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("=======","===="+position);
                AfterdetailsDialog afterdetailsDialog=new AfterdetailsDialog(mContext,afterSaleEntityListAll.get(position));
                if(!afterdetailsDialog.isShowing()){
                    afterdetailsDialog.show();
                }
            }
        });
        //监听listView的滚动，获取总数量
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                totalCount=totalItemCount;

                if(visibleItemCount+firstVisibleItem==totalItemCount&&totalItemCount!=0&&listView.hasFocus()==true){
                    Log.i(tag, "===滑到底部");
                    pageIndex++;
                    if(pageIndex>maxPage)
                        return;
                    getAfterServiceListData();
                    Log.i(tag,"===再次请求数据！");
                }
            }
        });


        //适配列表
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter=new AfterSaleAdapter(mContext,new ArrayList<AfterSaleEntity>());
        listView.setAdapter(adapter);
        Log.i(tag,"===第一次请求列表数据");
        getAfterServiceListData();
        //注册广播
        receiverAftersales = new ReceiverAftersales ();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dianshang.damai.aftersales_refresh");
        mContext.registerReceiver(receiverAftersales, intentFilter);


        tips_text.setText(getResources().getString(R.string.afterservice_item_null_tips));
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(receiverAftersales);
    }

    /**
     * 获取售后列表数据
     */
    public void getAfterServiceListData(){
        Utils.print(tag,"getAfterServiceListData");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            layout_after_sale_service.setFocusable(true);
            layout_after_sale_service.requestFocus();
            return;
        }
        if(pageIndex==1){
            startProgressDialog();
        }

        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.userID);
            Log.i(tag,"=====pageIndex=="+pageIndex);
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetAfterServiceData(input,ConStant.Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AfterServiceData>() {
                    @Override
                    public void onCompleted() {
                        //适配售后列表页
                        if(afterSaleEntityList!=null){
                            afterSaleEntityListAll.addAll(afterSaleEntityList);
                           adapter.addAll(afterSaleEntityList);
                        }else{
                            if(pageIndex==1){
                                layout.setVisibility(View.INVISIBLE);
                                layout_tips.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(pageIndex==1){
                            stopProgressDialog();
                            layout_after_sale_service.setFocusable(true);
                            layout_after_sale_service.requestFocus();
                        }
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
                    public void onNext(AfterServiceData afterServiceData) {
                        if(pageIndex==1){
                            stopProgressDialog();
                        }
                        Utils.print(tag,"===status=="+afterServiceData.getErrorMessage());
                        if(afterServiceData.getReturnValue()==-1){
                            Toast.makeText(mContext, afterServiceData.getErrorMessage(), Toast.LENGTH_SHORT).show();
                            if(pageIndex==1){
                                layout_after_sale_service.setFocusable(true);
                                layout_after_sale_service.requestFocus();
                            }
                            return;
                        }
                        try {
                            List<AfterServiceRecord> records = afterServiceData.getData().getRecords();

                            //计算最大页数
                            if(isMaxPage){
                                if(afterServiceData.getData().getTotalCount()>0){
                                    int total=afterServiceData.getData().getTotalCount();
                                    maxPage = total/ConStant.PAGESIZE;
                                    if(total%ConStant.PAGESIZE!=0){
                                        maxPage++;
                                    }
                                    Log.i(tag,"===最大页数=="+maxPage);
                                }
                                isMaxPage=false;
                            }

                            afterSaleEntityList=new ArrayList<AfterSaleEntity>();
                            for(int i=0;i<records.size();i++){
                                Utils.print(tag,"===type="+records.get(i).getServiceType());
                                AfterSaleEntity afterSaleEntity=new AfterSaleEntity();
                                if(records.get(i).getServiceType()== AfterServiceStatus.exchange.ordinal()){//换商品
                                    Exchange exchange = records.get(i).getExchangeVo();
                                    afterSaleEntity.setAmount(exchange.getAmount());
                                    afterSaleEntity.setCreateTime(exchange.getCreateTime());
                                    afterSaleEntity.setOrderSn(exchange.getOrderSn());
                                    afterSaleEntity.setServiceSn(exchange.getServiceSn());
                                    afterSaleEntity.setQuantity(exchange.getQuantity());
                                    afterSaleEntity.setReason(exchange.getReason());
                                    afterSaleEntity.setServiceType(exchange.getServiceType());
                                    afterSaleEntity.setStatus(exchange.getStatus());
                                    List<AfterSaleEntityItem> itemList=new ArrayList<AfterSaleEntityItem>();
                                    for(int j=0;j<exchange.getExchangeItemList().size();j++){
                                        ExchangeItem item=exchange.getExchangeItemList().get(j);
                                        AfterSaleEntityItem itemEntity=new AfterSaleEntityItem();
                                        itemEntity.setGoodsName(item.getGoodsName());
                                        itemEntity.setGoodsTitle(item.getGoodsTitle());
                                        itemEntity.setGoodsSn(item.getGoodsSn());
                                        itemEntity.setGoodsSkuSn(item.getGoodsSkuSn());
                                        itemEntity.setPrice(item.getPrice());
                                        itemEntity.setQuantity(item.getQuantity());
                                        itemEntity.setSellerPhone(item.getSellerPhone());
                                        itemEntity.setThumbnail(item.getThumbnail());
                                        itemList.add(itemEntity);
                                    }
                                    afterSaleEntity.setItemList(itemList);


                                    afterSaleEntityList.add(afterSaleEntity);
                                    Utils.print(tag,"===reason="+exchange.getReason());
                                    List<ExchangeItem> exchangeItems = exchange.getExchangeItemList();
                                    Utils.print(tag,"===item size="+exchangeItems.size());
                                }else if(records.get(i).getServiceType()==AfterServiceStatus.refund.ordinal()){//退款
                                    Refund refund = records.get(i).getRefundVo();
                                    afterSaleEntity.setAmount(refund.getAmount());
                                    afterSaleEntity.setCreateTime(refund.getCreateTime());
                                    afterSaleEntity.setOrderSn(refund.getOrderSn());
                                    afterSaleEntity.setServiceSn(refund.getServiceSn());
                                    afterSaleEntity.setQuantity(refund.getQuantity());
                                    afterSaleEntity.setReason(refund.getReason());
                                    afterSaleEntity.setServiceType(refund.getServiceType());
                                    afterSaleEntity.setStatus(refund.getStatus());
                                    afterSaleEntity.setOrderType(refund.getOrderType());
                                    List<AfterSaleEntityItem> itemList=new ArrayList<AfterSaleEntityItem>();

                                    for(int j=0;j<refund.getRefundItemList().size();j++){
                                        RefundItem item=refund.getRefundItemList().get(j);
                                        AfterSaleEntityItem itemEntity=new AfterSaleEntityItem();
                                        itemEntity.setGoodsName(item.getGoodsName());
                                        itemEntity.setGoodsSn(item.getGoodsSn());
                                        itemEntity.setGoodsSkuSn(item.getGoodsSkuSn());
                                        itemEntity.setGoodsTitle(item.getGoodsTitle());
                                        itemEntity.setQuantity(item.getQuantity());
                                        itemEntity.setSellerPhone(item.getSellerPhone());
                                        itemEntity.setThumbnail(item.getThumbnail());
                                        itemEntity.setPrice(item.getPrice());
                                        itemList.add(itemEntity);
                                    }
                                    afterSaleEntity.setItemList(itemList);
                                    afterSaleEntityList.add(afterSaleEntity);
                                    Utils.print(tag,"===reason="+refund.getReason());
                                    List<RefundItem> refundItems = refund.getRefundItemList();
                                    Utils.print(tag,"===item size="+refundItems.size());
                                }
                            }
                            if(afterSaleEntityList!=null&&afterSaleEntityList.size()==0){
                                if(pageIndex==1){
                                    layout.setVisibility(View.INVISIBLE);
                                    layout_tips.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (Exception e) {
                            // TODO: handle exception
                        }


                    }
                });
        addSubscription(subscription);
    }

    public class ReceiverAftersales extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            //重新请求数据
            maxPage = 1;
            pageIndex = 1;
            isMaxPage=true;
            afterSaleEntityListAll=new ArrayList<AfterSaleEntity>();
            adapter=new AfterSaleAdapter(mContext,new ArrayList<AfterSaleEntity>());
            listView.setAdapter(adapter);
            Log.i(tag,"===第一次请求列表数据");
            getAfterServiceListData();
        }

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            layout_order = (RelativeLayout) mainActivity.findViewById(R.id.layout_order);
            layout_home=(RelativeLayout)mainActivity.findViewById(R.id.layout_home);
            layout_domy_user_center=(RelativeLayout)mainActivity.findViewById(R.id.layout_domy_user_center);
            layout_domy_shopping_cart=(RelativeLayout)mainActivity.findViewById(R.id.layout_domy_shopping_cart);
            layout_commodity=(RelativeLayout)mainActivity.findViewById(R.id.layout_commodity);
            layout_search=(RelativeLayout)mainActivity.findViewById(R.id.layout_search);
            layout_collection=(RelativeLayout)mainActivity.findViewById(R.id.layout_collection);
            layout_after_sale_service=(RelativeLayout)mainActivity.findViewById(R.id.layout_after_sale_service);
        }
    }


    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(v.getId()==R.id.listView && keyCode==KeyEvent.KEYCODE_DPAD_LEFT ) {
                   // Toast.makeText(mContext, "焦点在列表左键！", Toast.LENGTH_SHORT).show();
                    layout_after_sale_service.setFocusable(true);
                    v.setNextFocusLeftId(R.id.layout_after_sale_service);
                    listView.getSelectedView().setBackground(mContext.getResources().getDrawable(R.drawable.shape1));
                }else if(v.getId()==R.id.listView && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                    if (totalCount>3&&listView.getSelectedItemPosition() == totalCount-1) {
                        if (null != feedbackAnimator && !feedbackAnimator.isRunning()) {
                            Utils.print(tag,"start animation");
                            feedbackAnimator.start();
                        }

                    }
                }else if(v.getId()==R.id.layout_after_sale_service&& keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    if(listView!=null&&listView.getChildAt(0)!=null){
                        listView.getChildAt(0).setBackground(mContext.getResources().getDrawable(R.drawable.order_selector));
                    }
                    if(listView!=null&&listView.getSelectedView()!=null){
                        listView.getSelectedView().setBackground(mContext.getResources().getDrawable(R.drawable.order_selector));
                    }
                }
                if(v.getId()==R.id.layout_after_sale_service){
                    setFocus(true);
                }
            }
            return false;
        }
    };

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

    public static void setFocus(Boolean b){
        layout_home.setFocusable(b);
        layout_domy_user_center.setFocusable(b);
        layout_domy_shopping_cart.setFocusable(b);
        layout_commodity.setFocusable(b);
        layout_search.setFocusable(b);
        layout_collection.setFocusable(b);
        layout_order.setFocusable(b);
        layout_after_sale_service.setFocusable(b);

    }

    /**
     * 获取售后列表头部的电话
     */
    public void getAfterServicePhone(){
        Utils.print(tag,"getAfterServicePhone");

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetAfterServicePhone(ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityPhoneData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityPhoneData commodityPhoneData) {
                        Utils.print(tag,"status=="+commodityPhoneData.getErrorMessage()+",value="+commodityPhoneData.getReturnValue());
                        if(commodityPhoneData.getReturnValue()==-1)
                            return;

                        Utils.print(tag,"phone="+commodityPhoneData.getData().get(0).getPhone());
                        if(!commodityPhoneData.getData().get(0).getPhone().equals("")){
                            totalphone.setText(mContext.getResources().getString(R.string.aftersalephone)+commodityPhoneData.getData().get(0).getPhone());
                            totalphone2.setText(mContext.getResources().getString(R.string.aftersalephone)+commodityPhoneData.getData().get(0).getPhone());
                        }

                    }
                });
    }



}





