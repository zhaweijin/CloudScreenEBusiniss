package com.hiveview.dianshang.showcommodity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.SelectCommodityType;
import com.hiveview.dianshang.dialog.TipsDialog;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.commodity.detail.CommodityDetail;
import com.hiveview.dianshang.entity.commodity.detail.CommodityDetailData;
import com.hiveview.dianshang.entity.token.TokenData;
import com.hiveview.dianshang.utils.DeviceInfo;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 商品详情显示
 */
public class CommodityInfomation extends BaseActivity implements ViewPager.OnPageChangeListener{

    @BindView(R.id.view_pager)
    ViewPager pager;

    @BindView(R.id.layout_shopping)
    LinearLayout layout_shopping;

    @BindView(R.id.layout_collection)
    LinearLayout layout_collection;


    @BindView(R.id.layout_promotion)
    LinearLayout layout_promotion;

    @BindView(R.id.arrow_left)
    ImageView arrow_left;

    @BindView(R.id.arrow_right)
    ImageView arrow_right;

    @BindView(R.id.commodity_item_information)
    LinearLayout commodity_item_information;

    /**
     * 商品图标
     */
    @BindView(R.id.commodity_icon)
    SimpleDraweeView commodity_icon;

    /**
     * 商品名称
     */
    @BindView(R.id.commodity_name)
    TextView commodity_name;

    /**
     * 商品标题
     */
    @BindView(R.id.commodity_title)
    TextView commodity_title;

    /**
     * 商品收藏状态
     */
    @BindView(R.id.commodity_collecton_status)
    ImageView commodity_collecton_status;

    /**
     * 商品促销价格
     */
    @BindView(R.id.commodity_new_price)
    TextView commodity_new_price;

    /**
     * 商品促销价格小数点后面显示
     */
    @BindView(R.id.commodity_new_price_dot)
    TextView commodity_new_price_dot;

    /**
     * 商品原价
     */
    @BindView(R.id.commodity_old_price)
    TextView commodity_old_price;

    /**
     * 商品支持的信誉
     */
    @BindView(R.id.commodity_support)
    TextView commodity_support;

    /**
     * 倒计时背景条
     */
    @BindView(R.id.layout_count_time)
    RelativeLayout layout_count_time;

    /**
     * 倒计时提示
     */
    @BindView(R.id.count_down_time)
    TextView count_down_time;

    /**
     * 按ok键提示信息
     */
    @BindView(R.id.tips_ok)
    TextView tips_ok;

    /**
     * 商品的物流价格
     */
    @BindView(R.id.commodity_express_price)
    TextView commodity_express_price;

    @BindView(R.id.layout_sale_promotion)
    LinearLayout layout_sale_promotion;

    /**
     * 商品促销二维码展示区域
     */
    @BindView(R.id.promotion_qrcode)
    SimpleDraweeView promotion_qrcode;

    /**
     * 商品收藏文字
     */
    @BindView(R.id.collection_item_text)
    TextView collection_item_text;

    private CommodityDetail commodityDetail;

    private Observable<String> observer;

    private String tag = "CommodityInfomation";
    private int pos=0;
    private List<String> list = new ArrayList<>();

    /**
     * 服务器交互标志位
     */
    private boolean op_status = false;


    private final int BUY=1;
    private final int COLLECTION=2;
    private final int CANCEL_COLLECTION=3;

    private String goodsSn="";
    private boolean isFarovite = false;

    private CommodityCountDownTimer timer;

    private long TIME = 0;//60*26*60 * 1000L;
    private final long INTERVAL = 1000L;

    /**
     * 倒计时策略
     */
    private Subscription sc;

    /**
     * 记录从那个页面进入详情的,是搜索,首页,收藏还是订单等等
     */
    private int mFromType;

    /**
     * 下线提示对话框
     */
    private TipsDialog tipsDialog;

    private boolean hasQr = false; //是否显示二维码
    private boolean hasCountime = false; //是否显示倒计时

    public static void launch(Activity activity,String sn,int type) {
        Intent intent = new Intent(activity, CommodityInfomation.class);
        intent.putExtra("sn",sn);
        intent.putExtra("type",type);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_information);
        pager = (ViewPager)findViewById(R.id.view_pager);
        goodsSn = getIntent().getStringExtra("sn");
        mFromType = getIntent().getIntExtra("type",-1);
        Utils.print(tag,"mFromType=="+mFromType+",goodsSn="+goodsSn);
        //goodsSn = "427984796640088064";


        //颜色不够深度
        //commodity_item_information.setBackgroundResource(R.drawable.information_item_bg);
        layout_collection.setVisibility(View.INVISIBLE);
        commodity_collecton_status.setVisibility(View.INVISIBLE);
        layout_shopping.requestFocus();


        String user = (String) SPUtils.get(mContext, ConStant.USERID, "");
        String token = (String) SPUtils.get(mContext, ConStant.USER_TOKEN, "");
        startProgressDialog();
        if (!user.equals("") && !token.equals("")) {
            Utils.print(tag, "set token");
            ConStant.userID = user;
            ConStant.Token = token;
            getCommodityDetail();
        }else{
            getTokenData();
        }

        initEvent();

    }



    /**
     * 操作收藏相关
     */
    private void operateCollection(){
        if(null==commodityDetail || (mProgressDialog!=null && mProgressDialog.isShowing()))
            return;

        //检测网络状态
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }
        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+op_status);
        if(op_status)
            return;
        op_status = true;
        //开启进度条
        startProgressDialog();
        if(isFarovite){
            deleteFavoriteCommodity();
            sendOnclickStatistics(CANCEL_COLLECTION);
        }else{
            addFavoriteCommodity();
            sendOnclickStatistics(COLLECTION);
        }
        Utils.print(tag,"collection");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Utils.print(tag,"onResume");
        layout_shopping.requestFocus();
        showInfoUI(true);
    }


    class MyPagerAdapter extends PagerAdapter {

        List<String> comlist=new ArrayList<>();
        List<SimpleDraweeView> imageViews = new ArrayList<>();

        public MyPagerAdapter(List<String> list){
            if(list.size()>1){
                comlist.add(list.get(list.size()-1));
                comlist.addAll(list);
                comlist.add(list.get(0));
                Utils.print(tag,"size====="+comlist.size());
                for(int i=0;i<comlist.size();i++){
                    imageViews.add(new SimpleDraweeView(CommodityInfomation.this));
                }
            }else{
                comlist.addAll(list);
                Utils.print(tag,"size====="+comlist.size());
                for(int i=0;i<comlist.size();i++){
                    imageViews.add(new SimpleDraweeView(CommodityInfomation.this));
                }
            }

        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return comlist.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;    //这行代码很重要，它用于判断你当前要显示的页面
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Utils.print(tag,"instantiateItem");
            SimpleDraweeView imageView = imageViews.get(position);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);


            container.addView(imageView);
            //load data
            Utils.print(tag,"url="+comlist.get(position));
            FrescoHelper.setImage(imageView,Uri.parse(comlist.get(position)),new ResizeOptions(Utils.getScreenW(mContext),Utils.getScrrenH(mContext)));
            return imageView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Utils.print(tag,"destroyItem");
            SimpleDraweeView imageView = imageViews.get(position);
            container.removeView(imageView);
        }

    }


    /**
     * 处理左右方向操作,循环切换图片显示
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {


        Utils.print(tag,"item visible="+checkCommidityItemUIVisible());
        if(!checkCommidityItemUIVisible() && (event.getKeyCode()==KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode()==KeyEvent.KEYCODE_BACK)
                && (commodity_item_information.getVisibility()==View.INVISIBLE)?true:false){
            showInfoUI(true);
            return true;
        }

        if(checkCommidityItemUIVisible()){
            Utils.print(tag,"other key");
            resetTimer();
        }




        if(event.getAction()==KeyEvent.ACTION_DOWN){
            if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){
                if(list.size()<=1){
                    return true;
                }
                pos++;
                Utils.print(tag,"right--"+pos);
                if(pos<=list.size()){
                    Utils.print(tag,"set pos");
                    pager.setCurrentItem(pos);
                }else if ( pos > list.size()) { //末位之后，跳转到首位（1）
                    pager.setCurrentItem(1,false); //false:不显示跳转过程的动画
                    pos = 1;
                }
            }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){
                if(list.size()<=1){
                    return true;
                }
                pos--;
                Utils.print(tag,"left--"+pos);
                if ( pos < 1) { //首位之前，跳转到末尾（N）
                    pos = list.size();
                    pager.setCurrentItem(pos,false);
                }else if(pos<=list.size()) {
                    pager.setCurrentItem(pos);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Utils.print(tag,"pos==="+position+",size="+list.size());
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }





    public void initEvent() {
        observer = RxBus.get().register(ConStant.obString_select_commodity_type, String.class);
        observer.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Utils.print(tag, "op=" + s);
                showInfoUI(true);
                layout_shopping.requestFocus();
            }
        });

        pager.setOnPageChangeListener(this);


        RxView.clicks(layout_collection)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        operateCollection();
                    }
                });


        RxView.clicks(layout_shopping)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        goShopping();
                    }
                });

        RxView.clicks(layout_promotion)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        PromotionInformation.launch(CommodityInfomation.this, goodsSn,0);
                    }
                });
    }


    /**
     * 购买操作
     */
    private void goShopping(){
        if(null==commodityDetail || (mProgressDialog!=null && mProgressDialog.isShowing()))
            return;

        //检测网络状态
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }


        sendOnclickStatistics(BUY);
        SelectCommodityType selectCommodityType = new SelectCommodityType(mContext,R.style.SelectTypeCustomDialog,SelectCommodityType.SELECT_COMMODITY);
        selectCommodityType.showUI(goodsSn);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.print(tag,"destroy");
        cancelTimer();
        if(sc!=null){
            sc.unsubscribe();
        }

        RxBus.get().unregister(ConStant.obString_select_commodity_type,observer);
        //RxBus.get().unregister(ConStant.obString_select_commodity_type,observer);
        //统一处理收藏页面是否更新
        try{
            if(commodityDetail!=null && !commodityDetail.getFavorite().equals(isFarovite)){
                Utils.print(tag,"change favorite");
                RxBus.get().post(ConStant.obString_remove_collection_item,goodsSn);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取商品详细信息
     */
    public void getCommodityDetail(){
        Utils.print(tag,"getCommodityDetail");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            finish();
            return;
        }


        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("goodsSn",goodsSn);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetCommodityDetailData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityDetailData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"444error="+e.getMessage());
                        stopProgressDialog();
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        finish();
                    }

                    @Override
                    public void onNext(CommodityDetailData commodityDetailData) {
                        Utils.print(tag,"status=="+commodityDetailData.getErrorMessage());

                        if(commodityDetailData.getReturnValue()==-1){
                            stopProgressDialog();
                            ToastUtils.showToast(mContext,commodityDetailData.getErrorMessage());
                            finish();
                            return;
                        }

                        if(commodityDetailData.getReturnValue()==-2){   //token已经失效,再次请求
                            getTokenData();
                            return;
                        }


                        /**
                         * 已下架提示
                         */
                        if(commodityDetailData.getData().getMarketStatus()==ConStant.OFF_LINE){
                            stopProgressDialog();
                            /*ToastUtils.showToast(mContext,getResources().getString(R.string.commodity_off_line));
                            finish();*/
                            showOffLineTips();
                            return;
                        }

                        list = commodityDetailData.getData().getDetailUrl();
                        for(int i=0;i<list.size();i++){
                            Utils.print(tag,"image url="+list.get(i));
                        }
                        Utils.print(tag,"sn="+commodityDetailData.getData().getGoodsSn());

                        if(list.size()<=1){
                            arrow_right.setVisibility(View.INVISIBLE);
                            arrow_left.setVisibility(View.INVISIBLE);
                        }

                        MyPagerAdapter adapter = new MyPagerAdapter(list);
                        pager.setAdapter(adapter);

                        //设置同步后台顺序
                        if(list.size()>1){
                            pager.setCurrentItem(1);
                            pos=1;
                        }

                        try{
                            initCommodityInfoData(commodityDetailData.getData());

                            //处理促销信息展示
                            if(commodityDetailData.getData().isPromotion()){
                                handlePromotionView(commodityDetailData.getData().getPromotionName());
                                layout_promotion.setVisibility(View.VISIBLE);
                            }

                            if (hasCountime) {
                                handCountDownTimer(commodityDetailData.getData().getEndTime());
                            }
                            showInfoUI(true);

                            sendInfoStatistics();
                            stopProgressDialog();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
        addSubscription(s);
    }


    private void initCommodityInfoData(CommodityDetail commodityDetail){

        this.commodityDetail = commodityDetail;
        commodity_collecton_status.setVisibility(View.VISIBLE);
        layout_collection.setVisibility(View.VISIBLE);

        FrescoHelper.setImage(commodity_icon,Uri.parse(commodityDetail.getProductImages()),new ResizeOptions(300,300));

        commodity_name.setText(commodityDetail.getName());
        commodity_title.setText(commodityDetail.getProductTitle());

        String price = Utils.getPrice(commodityDetail.getPrice());
        if(!price.contains(".")){
            commodity_new_price_dot.setVisibility(View.INVISIBLE);
            commodity_new_price.setText(getResources().getString(R.string.jiner)+"¥"+price);
        }else{
            commodity_new_price_dot.setVisibility(View.VISIBLE);
            commodity_new_price.setText(getResources().getString(R.string.jiner)+"¥"+price.substring(0,price.indexOf(".")));
            commodity_new_price_dot.setText(price.substring(price.indexOf("."),price.length()));
        }

        commodity_old_price.setText("¥"+Utils.getPrice(commodityDetail.getMarketPrice()));
        //设置商品价格横线
        commodity_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);


        if(commodityDetail.isSupportFlag()){
            commodity_support.setVisibility(View.VISIBLE);
        }else{
            commodity_support.setVisibility(View.INVISIBLE);
        }
        commodity_support.setText(commodityDetail.getSupport().get(0));
        commodity_express_price.setText(mContext.getResources().getString(R.string.express_title)+"¥"+Utils.getPrice(commodityDetail.getDeliveryPrice()));

        isFarovite = commodityDetail.getFavorite();
        if(isFarovite){
            collection_item_text.setText(getResources().getString(R.string.remove_collection));
            commodity_collecton_status.setVisibility(View.VISIBLE);
        }else{
            collection_item_text.setText(getResources().getString(R.string.collection));
            commodity_collecton_status.setVisibility(View.INVISIBLE);
        }


        if(commodityDetail.getEndTime()!=null && commodityDetail.getEndTime()!=0){
            hasCountime = true;
            count_down_time.setVisibility(View.VISIBLE);
        }
        if(commodityDetail.getQrCode()!=null && !commodityDetail.getQrCode().equals("")){
            hasQr = true;
            FrescoHelper.setImage(promotion_qrcode,Uri.parse(commodityDetail.getQrCode()));
        }



        Utils.print(tag,"is favorite="+isFarovite);
    }



    /**
     * 添加收藏的商品
     */
    public void addFavoriteCommodity(){
        Utils.print(tag,"addFavoriteCommodity");

        String input="";
        try{
            Map<String,String> json = new HashMap<>();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("goodsSn",goodsSn);
            input = new Gson().toJson(json);
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpaddFavoriteCommodity(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        op_status = false;
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
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();
                        op_status = false;
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }


                        isFarovite = true;
                        collection_item_text.setText(getResources().getString(R.string.remove_collection));
                        commodity_collecton_status.setVisibility(View.VISIBLE);

                    }
                });
        addSubscription(s);
    }


    /**
     * 删除收藏的商品
     */
    public void deleteFavoriteCommodity(){
        Utils.print(tag,"deleteFavoriteCommodity");

        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("goodsSn",goodsSn);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpdeleteFavoriteCommodity(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        op_status = false;
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
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();
                        op_status = false;
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        isFarovite = false;
                        collection_item_text.setText(getResources().getString(R.string.collection));
                        commodity_collecton_status.setVisibility(View.INVISIBLE);

                    }
                });
        addSubscription(s);
    }



    /**
     * 按钮点击埋点统计,收藏 取消收藏 购买
     * @param type
     */
    private void sendOnclickStatistics(int type){
        HashMap<String,String> simpleMap=new HashMap<String,String>();

        simpleMap.put("tabNo","");
        simpleMap.put("actionType","Ec4002");
        String info = "";
        //商品ID
        info = info + "goodsId="+commodityDetail.getGoodsSn()+"&";
        //商品SN
        info = info + "goodsName="+commodityDetail.getName()+"&";
        info = info + "button="+type;
        simpleMap.put("actionInfo",info);
        EBusinessApplication.getHSApi().addAction(simpleMap);
    }


    /**
     * 商品浏览器埋点发送
     */
    private void sendInfoStatistics(){
        HashMap<String,String> simpleMap=new HashMap<String,String>();

        //商品详情页浏览量埋点
        simpleMap.clear();
        String info = "";
        simpleMap.put("tabNo","");
        simpleMap.put("actionType","Ec4001");

        //商品ID
        info = info + "goodsId="+commodityDetail.getGoodsSn()+"&";
        //商品SN
        info = info + "goodsName="+commodityDetail.getName();
        String[] categorysns = commodityDetail.getCategoryTreePath().split(",");
        String[] categoryNames = commodityDetail.getCategoryTreeName().split(",");

        //一级
        if(categoryNames.length>0 && categorysns.length>0){
            info = info + "firstTabId="+categorysns[0] +"&";
            info = info + "firstTabName="+categoryNames[0] +"&";
        }

        //二级
        if(categoryNames.length>1 && categorysns.length>1){
            info = info + "secondTabId="+categorysns[1] +"&";
            info = info + "secondTabName="+categoryNames[1] +"&";
        }

        //三级
        if(categoryNames.length>2 && categorysns.length>2){
            info = info + "thirdTabId="+categorysns[2] +"&";
            info = info + "thirdTabName="+categoryNames[2] +"&";
        }

        //图文商品
        info = info + "goodsType=1"+"&";
        //商户信息
        info = info + "supplierId="+commodityDetail.getSellerId()+"&";
        info = info + "supplierName="+commodityDetail.getSellerName()+"&";
        //来源
        info = info + "source="+mFromType;

        simpleMap.put("actionInfo",info);
        EBusinessApplication.getHSApi().addAction(simpleMap);

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
                            finish();
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
                            finish();
                            return;
                        }


                        String user = tokenData.getData().getUserid();
                        String token = tokenData.getData().getToken();
                        SPUtils.putApply(mContext, ConStant.USERID,tokenData.getData().getUserid());
                        SPUtils.putApply(mContext,ConStant.USER_TOKEN,tokenData.getData().getToken());


                        ConStant.userID = user;
                        ConStant.Token = token;

                        getCommodityDetail();

                    }
                });
        addSubscription(s);
    }


    /**
     * 显示促销信息
     */
    private void handlePromotionView(List<String> list){
        if(list==null)
            return;
        for (int i = 0; i < list.size(); i++) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.information_sale_promotion_item,null);
            TextView promotion_text_item = (TextView)itemView.findViewById(R.id.sale_promotion_text);
            promotion_text_item.setText(list.get(i));
            layout_sale_promotion.addView(itemView);
            if(i>=1){
                break;
            }
        }
    }


    /**
     * 处理促销二维码展示
     */
    private void handlePromotionQrcode(String qrUrl){
        if(qrUrl==null || qrUrl.equals("")){
            promotion_qrcode.setVisibility(View.INVISIBLE);
        }else{
            promotion_qrcode.setVisibility(View.VISIBLE);
            FrescoHelper.setImage(promotion_qrcode,Uri.parse(qrUrl));
        }
    }

    /**
     * 处理倒计时展示
     */
    private void handCountDownTimer(long time){
        TIME = time;
        Utils.print(tag,"handCountDownTimer");
        layout_count_time.setVisibility(View.VISIBLE);
        count_down_time.setVisibility(View.VISIBLE);
        startTimer();
    }


    public class CommodityCountDownTimer extends CountDownTimer {
        public CommodityCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long time = millisUntilFinished / 1000;

            long day = 0;
            long hour=0;
            long minute=0;
            long second=0;

            if(time>3600*24){
                day = time/(3600*24);
                hour = (time-day*24*3600) / 3600;
                minute = (time-day*24*3600- hour * 3600) / 60;
                second = time-day*24*3600 - hour * 3600 - minute * 60;
                count_down_time.setText(String.format("距离结束还剩: %02d天 %02d:%02d:%02d", day,hour,minute,second));
            }else{
                hour = time / 3600;
                minute = (time- hour * 3600) / 60;
                second = time - hour * 3600 - minute * 60;
                count_down_time.setText(String.format("距离结束还剩: %02d:%02d:%02d", hour,minute,second));
            }
        }

        @Override
        public void onFinish() {
            count_down_time.setText("倒计时结束  00:00:00");
            count_down_time.setText("");
            cancelTimer();
            hasCountime = false;
            count_down_time.setVisibility(View.INVISIBLE);
            if(checkCommidityItemUIVisible())
               layout_count_time.setVisibility(View.INVISIBLE);
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
     * 重置定时器
     */
    private void resetTimer(){
        if(sc!=null){
            sc.unsubscribe();
        }
        startTickShowUI();
    }


    /**
     * 除商品大图外UI展示的处理
     * @param isShow
     */
    private void showInfoUI(boolean isShow){
        if(sc!=null){
            sc.unsubscribe();
        }

        Utils.print(tag,"showInfoUI="+isShow+",hasQR="+hasQr+",hasC="+hasCountime);

        //处理二维码显示，隐藏
        if(hasQr){
            if(isShow){
                viewAlphaVisible(promotion_qrcode);
            }else{
                viewAlphaInvisible(promotion_qrcode);
            }
        }
        //处理倒计时条的显示，隐藏
        if(hasCountime){
            if(isShow){
                tips_ok.setVisibility(View.INVISIBLE);
                viewAlphaVisible(count_down_time);
            }else{
                viewAlphaInvisible(count_down_time);
            }
        }else{
            if(isShow){
                layout_count_time.setVisibility(View.INVISIBLE);
            }
        }
        //处理底部详情数据的显示，隐藏
        if(isShow){
            viewAlphaVisible(commodity_item_information);
            layout_shopping.requestFocus();
        }else{
            viewAlphaInvisible(commodity_item_information);
        }

        if(isShow)
           startTickShowUI();
    }


    /**
     * view 动画渐变显示
     * @param view
     */
    private void viewAlphaInvisible(View view){
        Utils.print(tag,"viewAlphaInvisible");
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
        view.startAnimation(alphaAnimation);//开始动画
        alphaAnimation.setFillAfter(true);//动画结束后保持状态
        alphaAnimation.setDuration(1000);//动画持续时间，单位为毫秒

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout_count_time.setVisibility(View.VISIBLE);
                tips_ok.setVisibility(View.VISIBLE);
                commodity_item_information.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        alphaAnimation.start();
    }


    /**
     * view 动画渐变隐藏
     * @param view
     */
    private void viewAlphaVisible(View view) {
        Utils.print(tag, "viewAlphaVisible");
        view.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
        view.startAnimation(alphaAnimation);//开始动画
        alphaAnimation.setFillAfter(true);//动画结束后保持状态
        alphaAnimation.setDuration(1000);//动画持续时间，单位为毫秒

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }




        /**
         * 检测底部的商品细节是否正在显示
         * @return
         */
    private boolean checkCommidityItemUIVisible(){
        Utils.print(tag,"commodity_item_information alpha ="+commodity_item_information.getAlpha());
        if(commodity_item_information.getVisibility()==View.VISIBLE){
            return true;
        }
        return false;
    }


    /**
     * 处理界面８s没有任何操作的情况下，隐藏部分ui的展示
     */
    private void startTickShowUI(){

        Observable<Long> observable = Observable.timer(8, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread());
        sc = observable.subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long number) {
                         showInfoUI(false);
                    }
                });
    }


    /**
     * 提示已经下线
     */
    private void showOffLineTips(){
        tipsDialog = new TipsDialog(this, new TipsDialog.ConfirmOnClickListener() {
            @Override
            public void onOk() {
                if(tipsDialog!=null)
                    tipsDialog.dismiss();
                finish();
            }


            @Override
            public void onDismiss() {

            }
        });
        tipsDialog.setTitle(mContext.getResources().getString(R.string.off_line_tips));
        tipsDialog.setOkButton(mContext.getResources().getString(R.string.ok));
        tipsDialog.showUI();

    }
}
