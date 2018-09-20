package com.hiveview.dianshang.auction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.auction.database.AcutionBean;
import com.hiveview.dianshang.auction.database.AcutionDao;
import com.hiveview.dianshang.base.AcutionListener;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.TipsDialog;
import com.hiveview.dianshang.dialog.UnpayAcutionDialog;
import com.hiveview.dianshang.entity.InputSignBean;
import com.hiveview.dianshang.entity.acution.addprice.AddPriceData;
import com.hiveview.dianshang.entity.acution.detail.DetailData;
import com.hiveview.dianshang.entity.acution.info.AcutionInfo;
import com.hiveview.dianshang.entity.acution.unpay.order.DomyAuctionOrderVo;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderData;
import com.hiveview.dianshang.entity.commodity.detail.CommodityDetail;
import com.hiveview.dianshang.shoppingcart.ShoppingCartList;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hiveview.dianshang.R.id.add;
import static com.hiveview.dianshang.R.id.count_down_time;
import static com.hiveview.dianshang.R.id.wrap_content;
import static com.hiveview.dianshang.utils.httputil.RetrofitClient.getAcutionLauAPI;
import static com.hiveview.dianshang.utils.httputil.RetrofitClient.getCommodityAPI;

/**
 * 商品详情显示
 */
public class AcutionInfomation extends BaseActivity implements AcutionListener {

    //只允许第一次没有获取到任何数据的情况下，提示商品已经下架的对话框，其他情况都只是展示，竞拍结束的状态

    /**
     * 左侧切换大图的指示图标
     */
    @BindView(R.id.arrow_left)
    ImageView arrow_left;

    /**
     * 右侧切换大图的指示图标
     */
    @BindView(R.id.arrow_right)
    ImageView arrow_right;

    /**
     * 全屏四周闪烁的view
     */
    @BindView(R.id.warning_img)
    ImageView warning_img;

    /**
     * 右侧，进度条上方的圆形图标状态
     */
    @BindView(R.id.layout_status)
    RelativeLayout layout_status;

    /**
     * 倒计时
     */
    @BindView(R.id.time)
    TextView time;

    /**
     * 倒计时秒
     */
    @BindView(R.id.second)
    TextView second;

    /**
     * 当前竞拍价标题
     */
    @BindView(R.id.acution_title)
    TextView acution_title;
    /**
     * 价格
     */
    @BindView(R.id.acution_title_price)
    TextView acution_title_price;


    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @BindView(R.id.layout_acution)
    LinearLayout layout_acution;

    @BindView(R.id.tx_top_right_tips)
    TextView tx_top_right_tips;

    @BindView(R.id.layout_tips)
    RelativeLayout layout_tips;

    @BindView(R.id.gallery)
    Gallery gallery;

    /**
     * 当前竞价
     */
    @BindView(R.id.rl_right_price)
    RelativeLayout rl_right_price;


    private Subscription loop_global_sc;

    /**
     * 获取未支付数据
     */
    private Subscription unpay_sc;


    /**
     * 商品sn
     */
    private String acutionSn;

    private Observable<String> observer;

    private String tag = "AcutionInfomation";

    private List<String> list = new ArrayList<>();

    private AcutionProgress acutionProgress;

    private InputSignBean inputSignBean;

    private double currentPrice=0;

    /**
     * 之前的拍卖激活时间
     */
    private long oldAcutionTime;

    /**
     * 同步后台获取的数据
     */
    private AcutionInfo entityData;


    /**
     * 服务器交互标志位,竞价
     */
    private boolean op_bid_status = false;

    /**
     * 竞价后获取商品信息
     */
    private Subscription bid_after_get_sc;

    /**
     * 是否拍卖结束
     */
    private boolean acution_end_status=false;

    /**
     * 轮寻
     */
    private Subscription sc_loop;
    /**
     * 加价
     */
    private Subscription add_price_sc;

    /**
     * 竞价
     */
    private Subscription bid_sc;
    /**
     * 用于多个拍卖轮次的动画结束标志
     */
    private boolean loop_animal_runing=false;

    /**
     * 拍卖未支付提示框
     */
    private UnpayAcutionDialog unpayAcutionDialog;
    private DomyAuctionOrderVo acutionData;


    /**
     * 下线提示对话框
     */
    private TipsDialog tipsDialog;

    /**
     * 与服务器时间差
     */
    private long divTime=0;

    /**
     * 商品sn
     */
    private String goodSn;

    /**
     * 拉起拍卖详情入口
     * @param activity
     * @param sn  拍卖sn
     */
    public static void launch(Activity activity,String goodSn) {
        Intent intent = new Intent(activity, AcutionInfomation.class);
        intent.putExtra("goodSn",goodSn);
        activity.startActivity(intent);
    }


    public static void launch(Activity activity,String acutionSn,long divTime) {
        Intent intent = new Intent(activity, AcutionInfomation.class);
        intent.putExtra("acutionSn",acutionSn);
        intent.putExtra("divtime",divTime);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auction_commodity_information);

        acutionSn = getIntent().getStringExtra("acutionSn");
        goodSn = getIntent().getStringExtra("goodSn");
        divTime = getIntent().getLongExtra("divtime",0l);
        //sn = "426113707060236288";
        inputSignBean = getAcutionInput();
        Utils.print(tag,"sn=="+acutionSn);
        Utils.print(tag,"goodSn=="+goodSn);
        Utils.print(tag,"divTime=="+divTime);

        if(goodSn!=null){
            //拍卖结束生产订单后，只要求查看详情图
            layout_tips.setVisibility(View.INVISIBLE);
            layout_acution.setVisibility(View.INVISIBLE);
            getAcutionDetail(goodSn);
            return;
        }
        getAcutionInfoData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //rl_right_price.removeAllViews();
        loopGetData();
    }

    /**
     * 显示拍卖状态
     */
    private void showAcutionStatus(boolean isInit){
        layout_acution.setVisibility(View.VISIBLE);
        if(acution_end_status){
            //拍卖结束
            acutionEndView();
        }else{
            if(entityData.getData().getAuctionStatusVo().getUserid()==Long.parseLong(ConStant.getInstance(mContext).userID)){
                //出价最高
                joinTopAcutionView(isInit);
            }else {
                if(AcutionDao.getInstance(mContext).querySn(acutionSn).size()>0){
                    //已参拍
                    joinAcutionView(isInit);
                }else{
                    //我要拍
                    readyAcutionView(isInit);
                }
            }
        }
    }




    /**
     * 处理左右方向操作,循环切换图片显示
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if(event.getKeyCode()==KeyEvent.KEYCODE_MENU){
//                viewAlphaVisible(warning_img);
                //readyAcutionView(3);
//                warning_img.clearAnimation();
//                loop_animal_runing=false;
            }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_CENTER){
                if(!acution_end_status && entityData!=null){
                    bidAcutionPrice(acutionSn);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }



    /**
     * 显示未支付提示对话框
     */
    private void showUnpayDialog(){
        if (unpayAcutionDialog == null) {
            unpayAcutionDialog = new UnpayAcutionDialog(mContext, confirmOnClickListener);
        }
        if (!unpayAcutionDialog.isShowing()) {
            unpayAcutionDialog.showUI();
        }
    }

    UnpayAcutionDialog.ConfirmOnClickListener confirmOnClickListener = new UnpayAcutionDialog.ConfirmOnClickListener() {
        @Override
        public void onOk() {
            getAcutionUnpayData();
        }

        @Override
        public void onDismiss() {
            unpayAcutionDialog.dismiss();
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.print(tag, "destroy");
        warning_img.clearAnimation();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(acutionProgress!=null){
            acutionProgress.release();
        }
        unUnsubscrition();
    }

    private void unUnsubscrition(){
        //轮循获取数据
        if(sc_loop!=null && !sc_loop.isUnsubscribed()){
            Utils.print(tag,"sc loop stop");
            sc_loop.unsubscribe();
        }
        //全局定时器
        if(loop_global_sc!=null && !loop_global_sc.isUnsubscribed()){
            Utils.print(tag,"sc_global stop");
            loop_global_sc.unsubscribe();
        }
        //加价订阅器
        if(add_price_sc!=null && !add_price_sc.isUnsubscribed()){
            add_price_sc.unsubscribe();
        }
        //未支付数据
        if(unpay_sc!=null && !unpay_sc.isUnsubscribed()){
            unpay_sc.unsubscribe();
        }
        //竞价
        if(bid_sc!=null && !bid_sc.isUnsubscribed()){
            bid_sc.unsubscribe();
        }

        //竞价后获取数据
        if(bid_after_get_sc!=null && !bid_after_get_sc.isUnsubscribed()){
            bid_after_get_sc.unsubscribe();
        }
    }

    /**
     * 获取拍卖商品信息
     */
    public void getAcutionInfoData(){
        Utils.print(tag,"getAcutionInfoData sn==="+acutionSn);
        if (!Utils.isConnected(mContext)) {
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext, error_tips);
            finish();
            return;
        }
        startProgressDialog();
        Subscription s = getAcutionLauAPI()
                .httpGetAcutionInfoData(inputSignBean.getInput(),inputSignBean.getSign())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AcutionInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        String error_tips = "";
                        if (Utils.isConnected(mContext)) {
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        } else {
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext, error_tips);
                        finish();
                    }

                    @Override
                    public void onNext(AcutionInfo statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();
                        if (statusData.getReturnValue() == -1) {
                            showOffLineTips();
                            //ToastUtils.showToast(mContext, statusData.getErrorMessage());
                            //finish();
                            return;
                        }

                        entityData = statusData;
                        oldAcutionTime=entityData.getData().getAuctionScheduleVo().getAuctionTime();
                        currentPrice = entityData.getData().getAuctionStatusVo().getCurrentPrice();
                        showAcutionStatus(true);

                        if(acutionProgress!=null){
                            acutionProgress.release();
                        }

                        acutionProgress = new AcutionProgress(mContext);
                        acutionProgress.setDivTime(divTime);
                        acutionProgress.setAcutionListener(AcutionInfomation.this);
                        acutionProgress.setProgressBar(progressbar);
                        acutionProgress.setDurationTime(entityData.getData().getAuctionScheduleVo().getDurationTime());
                        acutionProgress.setTimes(entityData.getData().getAuctionScheduleVo().getTimes());
                        acutionProgress.setTimeStamp(entityData.getData().getAuctionScheduleVo().getAuctionTime());
                        acutionProgress.startTimer();

                        if(statusData.getData().getAuctionVo().getDetailUrl()==null){
                            return;
                        }
                        list = statusData.getData().getAuctionVo().getDetailUrl();
                        for (int i = 0; i < list.size(); i++) {
                            Utils.print(tag, "image url=" + list.get(i));
                        }
                        Utils.print(tag, "sn=" + statusData.getData().getAuctionVo().getAuctionSn());

                        if (list.size() <= 1) {
                            arrow_right.setVisibility(View.INVISIBLE);
                            arrow_left.setVisibility(View.INVISIBLE);
                        }


                        setAdapterGallery();
                    }
                });
        addSubscription(s);
    }



    /**
     * view 动画渐变隐藏
     * @param view
     */
    private void viewAlphaVisible(View view){
        if(!loop_animal_runing){
            return;
        }

        Utils.print(tag,"viewAlphaVisible");
        view.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
        view.startAnimation(alphaAnimation);//开始动画
        alphaAnimation.setFillAfter(true);//动画结束后保持状态
        alphaAnimation.setDuration(2000);//动画持续时间，单位为毫秒

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewAlphaInvisible(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    /**
     * view 动画渐变显示
     * @param view
     */
    private void viewAlphaInvisible(View view){
        if(!loop_animal_runing){
            return;
        }
        Utils.print(tag,"viewAlphaInvisible");
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);//初始化操作，参数传入0和1，即由透明度0变化到透明度为1
        view.startAnimation(alphaAnimation);//开始动画
        alphaAnimation.setFillAfter(true);//动画结束后保持状态
        alphaAnimation.setDuration(2000);//动画持续时间，单位为毫秒

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                viewAlphaVisible(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        alphaAnimation.start();
    }




    /**
     * 我要拍
     * @param oldprice
     */
    private void readyAcutionView(boolean isInit){
        Utils.print(tag,"readyAcutionView");
        acution_title_price.setVisibility(View.GONE);
        layout_status.setBackgroundResource(R.drawable.acution_info_ready_status);
        time.setVisibility(View.GONE);
        second.setVisibility(View.GONE);

        tx_top_right_tips.setVisibility(View.VISIBLE);

        if(isInit){
            //最初添加价格
            View view = LayoutInflater.from(this).inflate(R.layout.acution_info_price_view,null);
            TextView p = (TextView) view.findViewById(R.id.acution_price);
            p.setText(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
            rl_right_price.addView(view);

            if(p.getText().length()>2){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_acution.getLayoutParams();
                layoutParams.width = (int)mContext.getResources().getDimension(R.dimen.length_280) + (int)(p.getText().length()*mContext.getResources().getDimension(R.dimen.length_53sp_unit));
                layout_acution.setLayoutParams(layoutParams);
            }
        }else {
            final View v = LayoutInflater.from(this).inflate(R.layout.acution_info_price_view,null);
            TextView price1 = (TextView) v.findViewById(R.id.acution_price);
            price1.setText(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
            rl_right_price.addView(v);

            TextView currentViewPrice = (TextView) v.findViewById(R.id.acution_price);
            Utils.print(tag,"current="+currentViewPrice.getText().length());
            if(currentViewPrice.getText().length()>2){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_acution.getLayoutParams();
                layoutParams.width = (int)mContext.getResources().getDimension(R.dimen.length_280) + (int)(currentViewPrice.getText().length()*mContext.getResources().getDimension(R.dimen.length_53sp_unit));
                layout_acution.setLayoutParams(layoutParams);
            }

            rl_right_price.getChildAt(0).setAnimation(AnimationUtils.loadAnimation(this,R.anim.push_up_out));
            rl_right_price.getChildAt(0).getAnimation().setAnimationListener(updatePriceListener);
            rl_right_price.getChildAt(1).setAnimation(AnimationUtils.loadAnimation(this,R.anim.push_down_in));
        }
    }


    /**
     * 参拍
     * @param oldprice
     */
    private void joinAcutionView(boolean isInit){
        Utils.print(tag,"joinAcutionView");
        acution_title_price.setVisibility(View.GONE);
        layout_status.setBackgroundResource(R.drawable.acution_info_join_status);
        time.setVisibility(View.GONE);
        second.setVisibility(View.GONE);

        tx_top_right_tips.setVisibility(View.VISIBLE);

        if(isInit){
            //最初添加价格
            View view = LayoutInflater.from(this).inflate(R.layout.acution_info_price_view,null);
            TextView p = (TextView) view.findViewById(R.id.acution_price);
            p.setText(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
            rl_right_price.addView(view);

            if(p.getText().length()>2){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_acution.getLayoutParams();
                layoutParams.width = (int)mContext.getResources().getDimension(R.dimen.length_280) + (int)(p.getText().length()*mContext.getResources().getDimension(R.dimen.length_53sp_unit));
                layout_acution.setLayoutParams(layoutParams);
            }
        }else {
            final View v = LayoutInflater.from(this).inflate(R.layout.acution_info_price_view,null);
            TextView price1 = (TextView) v.findViewById(R.id.acution_price);
            price1.setText(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
            rl_right_price.addView(v);

            TextView currentViewPrice = (TextView) v.findViewById(R.id.acution_price);
            Utils.print(tag,"current="+currentViewPrice.getText().length());
            if(currentViewPrice.getText().length()>2){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_acution.getLayoutParams();
                layoutParams.width = (int)mContext.getResources().getDimension(R.dimen.length_280) + (int)(currentViewPrice.getText().length()*mContext.getResources().getDimension(R.dimen.length_53sp_unit));
                layout_acution.setLayoutParams(layoutParams);
            }

            rl_right_price.getChildAt(0).setAnimation(AnimationUtils.loadAnimation(this,R.anim.push_up_out));
            rl_right_price.getChildAt(0).getAnimation().setAnimationListener(updatePriceListener);
            rl_right_price.getChildAt(1).setAnimation(AnimationUtils.loadAnimation(this,R.anim.push_down_in));
        }
    }


    /**
     * 出价最高
     * @param oldprice
     */
    private void joinTopAcutionView(boolean isInit){
        Utils.print(tag,"joinTopAcutionView");
        acution_title_price.setVisibility(View.GONE);
        layout_status.setBackgroundResource(R.drawable.acution_info_top1_status);
        time.setVisibility(View.GONE);
        second.setVisibility(View.GONE);

        tx_top_right_tips.setVisibility(View.VISIBLE);
        Utils.print("ani","ani text size="+rl_right_price.getChildCount());

        if(isInit){
            //最初添加价格
            View view = LayoutInflater.from(this).inflate(R.layout.acution_info_price_view,null);
            TextView p = (TextView) view.findViewById(R.id.acution_price);
            p.setText(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
            rl_right_price.addView(view);

            if(p.getText().length()>2){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_acution.getLayoutParams();
                layoutParams.width = (int)mContext.getResources().getDimension(R.dimen.length_280) + (int)(p.getText().length()*mContext.getResources().getDimension(R.dimen.length_53sp_unit));
                layout_acution.setLayoutParams(layoutParams);
            }
        }else {
            final View v = LayoutInflater.from(this).inflate(R.layout.acution_info_price_view,null);
            TextView price1 = (TextView) v.findViewById(R.id.acution_price);
            price1.setText(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
            rl_right_price.addView(v);

            TextView currentViewPrice = (TextView) v.findViewById(R.id.acution_price);
            Utils.print(tag,"current="+currentViewPrice.getText().length());
            if(currentViewPrice.getText().length()>2){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_acution.getLayoutParams();
                layoutParams.width = (int)mContext.getResources().getDimension(R.dimen.length_280) + (int)(currentViewPrice.getText().length()*mContext.getResources().getDimension(R.dimen.length_53sp_unit));
                layout_acution.setLayoutParams(layoutParams);
            }

            rl_right_price.getChildAt(0).setAnimation(AnimationUtils.loadAnimation(this,R.anim.push_up_out));
//            rl_right_price.getChildAt(0).getAnimation().setAnimationListener(updatePriceListener);
            rl_right_price.getChildAt(1).setAnimation(AnimationUtils.loadAnimation(this,R.anim.push_down_in));


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    View topView = rl_right_price.getChildAt(0);
                    if(topView!=null){
                        Utils.print("ani",".....remove.....");
                        rl_right_price.removeView(topView);
                    }
                }
            },300);
        }
    }


    /**
     * 结束拍卖(拍卖结束后，判断没有库存的情况下，展示这个状态)
     * @param oldprice
     */
    private void acutionEndView(){
        Utils.print(tag,"AcutionEndView");
        progressbar.setVisibility(View.INVISIBLE);
        layout_tips.setVisibility(View.INVISIBLE);
        acution_title.setVisibility(View.GONE);
        acution_title_price.setVisibility(View.GONE);
        //acution_title_price.setText(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
        layout_status.setBackgroundResource(R.drawable.acution_time_bg);
        time.setText(mContext.getResources().getString(R.string.acution_end));
        time.setTextSize(mContext.getResources().getDimension(R.dimen.sp_24));
        time.setVisibility(View.VISIBLE);
        second.setVisibility(View.GONE);


        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_acution.getLayoutParams();
        layoutParams.width = (int)mContext.getResources().getDimension(R.dimen.length_280);
        layout_acution.setLayoutParams(layoutParams);


        rl_right_price.removeAllViews();

        final View v = LayoutInflater.from(this).inflate(R.layout.acution_info_transaction_status_view,null);
        TextView tx = (TextView) v.findViewById(R.id.transaction_status);
        tx.setText(mContext.getResources().getString(R.string.acution_finish));

        tx.setTextColor(Color.parseColor("#7B7B7B"));
        rl_right_price.addView(v);
    }



    Animation.AnimationListener updatePriceListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            View topView = rl_right_price.getChildAt(0);
            if(topView!=null){
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.print("ani","remove....");
                        rl_right_price.removeView(topView);
                    }
                });
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };



    /**
     * 循环获取数据
     */
    private void loopGetData(){
        Observable<Long> ob = Observable.interval(ConStant.ACUTION_LOOP_TIME, TimeUnit.SECONDS);
        loop_global_sc = ob.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                Utils.print(tag,".....time.....");
                loopGetCommodity();
            }
        });
    }

    /**
     * 循环请求服务器接口数据
     */
    public void loopGetCommodity(){
        Utils.print(tag,"loopGetCommodity start time="+ System.currentTimeMillis());
        if(!Utils.isConnected(mContext)){
            return;
        }
        if(sc_loop!=null && !sc_loop.isUnsubscribed())
            sc_loop.unsubscribe();

        sc_loop = getAcutionLauAPI()
                .httpGetAcutionInfoData(inputSignBean.getInput(),inputSignBean.getSign())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AcutionInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        if(sc_loop!=null && !sc_loop.isUnsubscribed()){
                            Utils.print(tag,"sc loop stop");
                            sc_loop.unsubscribe();
                        }
                        Utils.print(tag,"loop error="+e.getMessage());
                        closeTwinkleAnimation();
                    }

                    @Override
                    public void onNext(AcutionInfo statusData) {
                        Utils.print(tag,"loop commodity status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue()+",end time="+System.currentTimeMillis());
                        stopProgressDialog();
                        if(statusData.getReturnValue()==-1){
                            closeTwinkleAnimation();
                            if(entityData!=null){
                                acution_end_status = true;
                            }
                            showAcutionStatus(false);
                            if(loop_global_sc!=null && !loop_global_sc.isUnsubscribed()){
                                Utils.print(tag,"sc_global stop");
                                loop_global_sc.unsubscribe();
                            }
                            return;
                        }


                        entityData = statusData;
                        //时间不一致，说明是新的轮次开始了，因此把闪烁复位
                        if(oldAcutionTime!=entityData.getData().getAuctionScheduleVo().getAuctionTime()){
                            oldAcutionTime = entityData.getData().getAuctionScheduleVo().getAuctionTime();
                            closeTwinkleAnimation();
                        }
                        Utils.print(tag,"currentPrice=="+currentPrice+",server price="+entityData.getData().getAuctionStatusVo().getCurrentPrice());
                        if(currentPrice!=entityData.getData().getAuctionStatusVo().getCurrentPrice()){
                            Utils.print(tag,"showAcutionStatus ");
                            showAcutionStatus(false);
                        }
                        currentPrice=entityData.getData().getAuctionStatusVo().getCurrentPrice();

                        if(acutionProgress!=null){
                            acutionProgress.release();
                        }
                        if(statusData.getReturnValue()==0){
                            acutionProgress = new AcutionProgress(mContext);
                            acutionProgress.setDivTime(divTime);
                            acutionProgress.setAcutionListener(AcutionInfomation.this);
                            acutionProgress.setProgressBar(progressbar);
                            acutionProgress.setDurationTime(entityData.getData().getAuctionScheduleVo().getDurationTime());
                            acutionProgress.setTimes(entityData.getData().getAuctionScheduleVo().getTimes());
                            acutionProgress.setTimeStamp(entityData.getData().getAuctionScheduleVo().getAuctionTime());
                            acutionProgress.startTimer();
                        }
                    }
                });
    }


    /**
     * 竞价后获取商品数据
     */
    public void bidAfterGetCommodity(){
        Utils.print(tag,"bidAfterGetCommodity start time="+ System.currentTimeMillis());
        if(!Utils.isConnected(mContext)){
            bidEnd();
            return;
        }

        if(bid_after_get_sc!=null && bid_after_get_sc.isUnsubscribed()){
            bid_after_get_sc.unsubscribe();
        }


        bid_after_get_sc = getAcutionLauAPI()
                .httpGetAcutionInfoData(inputSignBean.getInput(),inputSignBean.getSign())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AcutionInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        bidEnd();
                        Utils.print(tag,"bidAfterGetCommodity error="+e.getMessage());
                    }

                    @Override
                    public void onNext(AcutionInfo statusData) {
                        Utils.print(tag,"bidAfterGetCommodity status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue()+",end time="+System.currentTimeMillis());
                        bidEnd();
                        if(statusData.getReturnValue()==-1){
//                            showOffLineTips();
                            if(entityData!=null){
                                acution_end_status = true;
                            }
                        }

                        entityData = statusData;
                        Utils.print(tag,"currentPrice=="+currentPrice+",server price="+entityData.getData().getAuctionStatusVo().getCurrentPrice());
                        if(currentPrice!=entityData.getData().getAuctionStatusVo().getCurrentPrice()){
                            Utils.print(tag,"showAcutionStatus ");
                            showAcutionStatus(false);
                        }
                        currentPrice=entityData.getData().getAuctionStatusVo().getCurrentPrice();

                        //时间不一致，说明是新的轮次开始了，因此把闪烁复位
                        if(oldAcutionTime!=entityData.getData().getAuctionScheduleVo().getAuctionTime()){
                            oldAcutionTime = entityData.getData().getAuctionScheduleVo().getAuctionTime();
                            closeTwinkleAnimation();
                        }

                        if(acutionProgress!=null){
                            acutionProgress.release();
                        }

                        if(statusData.getReturnValue()==0){
                            acutionProgress = new AcutionProgress(mContext);
                            acutionProgress.setDivTime(divTime);
                            acutionProgress.setAcutionListener(AcutionInfomation.this);
                            acutionProgress.setProgressBar(progressbar);
                            acutionProgress.setDurationTime(entityData.getData().getAuctionScheduleVo().getDurationTime());
                            acutionProgress.setTimes(entityData.getData().getAuctionScheduleVo().getTimes());
                            acutionProgress.setTimeStamp(entityData.getData().getAuctionScheduleVo().getAuctionTime());
                            acutionProgress.startTimer();
                        }

                    }
                });
    }

    /**
     * 竞价结果标志
     */
    private void bidEnd(){
        op_bid_status=false;
        stopProgressDialog();
    }

    public InputSignBean getAcutionInput(){

        InputSignBean bean = new InputSignBean();
        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("auctionSn", acutionSn);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Utils.print(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Utils.print(tag,"sign="+sign);
            ///*********

            bean.setInput(input);
            bean.setSign(sign);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 拍卖的商品参加竞拍
     */
    public void acutionAddPriceData(String price){
        Utils.print(tag,"acutionAddPriceData");
        //检测网络状态
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            bidEnd();
            return;
        }

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("auctionSn",acutionSn); //订单号
            json.put("currentPrice", price); //当前价格
            json.put("userid",ConStant.getInstance(mContext).userID);// 用户ID
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Utils.print(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Utils.print(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }

        if(add_price_sc!=null && add_price_sc.isUnsubscribed()){
            add_price_sc.unsubscribe();
        }

        add_price_sc = getCommodityAPI()
                .httpGetAcutionAddPriceData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AddPriceData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        bidEnd();
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_acution_network_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag,"1error="+e.getMessage());
                    }

                    @Override
                    public void onNext(AddPriceData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        if(statusData.getReturnValue()==-1){
                            bidEnd();
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData().getRetNum()==1){
                            Utils.print(tag,"add price sucess");
                            //保存最新的sn,价格
                            SPUtils.putApply(mContext,ConStant.ACUTION_TOP_PRICE,String.valueOf(statusData.getData().getCurPrice()));
                            SPUtils.putApply(mContext,ConStant.ACUTION_TOP_SN,statusData.getData().getAuctionSn());

                            closeTwinkleAnimation();
                            bidAfterGetCommodity();
                            ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.acution_add_price_sucess));
                            //添加出价记录到数据库
                            if(AcutionDao.getInstance(mContext).querySn(statusData.getData().getAuctionSn()).size()<=0){
                                AcutionBean bean = new AcutionBean();
                                bean.setSn(statusData.getData().getAuctionSn());
                                AcutionDao.getInstance(mContext).insert(bean);
                                Utils.print(tag,"save to database...");
                            }
                        }else{
                            Utils.print(tag,"add price failed");
                            ToastUtils.showToast(mContext,statusData.getData().getRetMsg());
                            bidEnd();
                        }
                    }
                });
    }



    private void closeTwinkleAnimation(){
        Utils.print(tag,"closeTwinkleAnimation");
        warning_img.setVisibility(View.GONE);
        warning_img.clearAnimation();
        loop_animal_runing=false;
        if(acutionProgress!=null){
            acutionProgress.setWaring(false);
        }
    }

    /**
     * 获取拍卖未支付的订单信息
     */
    public void getAcutionUnpayData(){
        Utils.print(tag,"getAcutionUnpayData");
        if(unpay_sc!=null && unpay_sc.isUnsubscribed()){
            unpay_sc.unsubscribe();
        }

        unpay_sc = getCommodityAPI()
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
                       /* if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);*/
                        Utils.print(tag,"1error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            //ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        acutionData = statusData.getData();
                        Utils.print(tag,"isHaveUnpayOrder=="+statusData.getData().isHaveUnpayOrder());
                        if(acutionData.isHaveUnpayOrder()){
                            if(unpayAcutionDialog!=null && unpayAcutionDialog.isShowing()){
                                unpayAcutionDialog.dismiss();
                            }
                            ShoppingCartList.launch(AcutionInfomation.this,ConStant.ACUTION_PAYMENT,acutionData);
                        }
                    }
                });
    }


    @Override
    public void OnItemAcutionListener(boolean status) {
        loopGetCommodity();
    }



    /**
     * 获取拍卖商品价格
     */
    public void bidAcutionPrice(String acutionSn){
        Utils.print(tag,"bidAcutionPrice");
        if(op_bid_status)
            return;
        op_bid_status = true;

        startProgressDialog();
        Utils.print(tag,"bidAcutionPrice1");
        String topAcutionSn = (String) SPUtils.get(mContext,ConStant.ACUTION_TOP_SN,"");
        if(topAcutionSn.equals("")){
            Utils.print(tag,"local no top acution sn");
            //允许继续出价
            acutionAddPriceData(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
            return;
        }

        //保存的最高价sn与当前sn一致
        if(topAcutionSn.equals(acutionSn)){
            checkEqAcutionSnUnpayData();
            return;
        }

        if (!Utils.isConnected(mContext)) {
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext, error_tips);
            bidEnd();
            return;
        }

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("auctionSn", topAcutionSn);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Utils.print(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Utils.print(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }

        if(bid_sc!=null && bid_sc.isUnsubscribed()){
            bid_sc.unsubscribe();
        }

        bid_sc = getAcutionLauAPI()
                .httpGetAcutionInfoData(input,sign)
                .flatMap(new Func1<AcutionInfo, Observable<UnpayOrderData>>() {
                    @Override
                    public Observable<UnpayOrderData> call(AcutionInfo acutionInfo) {
                        Utils.print(tag,"return value==="+acutionInfo.getReturnValue());
                        if(acutionInfo.getReturnValue()==-1){
                            //已经下架的情况，需要判断当前用户是否已经生成订单
                            Utils.print(tag,"httpGetAcutionUnpayData ");
                            return getCommodityAPI()
                                    .httpGetAcutionUnpayData(Utils.getAcutionUnpayInput(mContext).getInput(),Utils.getAcutionUnpayInput(mContext).getSign());
                        }


                        //没有下架的情况
                        String topPrice = (String) SPUtils.get(mContext, ConStant.ACUTION_TOP_PRICE, "0");
                        Utils.print(tag, "local top price=" + topPrice);

                        UnpayOrderData unpayOrderData = new UnpayOrderData();
                        DomyAuctionOrderVo domyAuctionOrderVo = new DomyAuctionOrderVo();
                        domyAuctionOrderVo.setHaveUnpayOrder(false);
                        unpayOrderData.setData(domyAuctionOrderVo);

                        if (acutionInfo.getData().getAuctionStatusVo().getCurrentPrice() > Double.parseDouble(topPrice)) {
                            //服务器的价格比本地价格更高，允许继续竞拍
                            unpayOrderData.setHasTopPrice(false);
                            return Observable.just(unpayOrderData);
                        } else {
                            //服务器的价格没有本地价格更高，禁止继续竞拍
                            unpayOrderData.setHasTopPrice(true);
                            return Observable.just(unpayOrderData);
                        }
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Utils.print(tag,"get price failed");
                        bidEnd();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UnpayOrderData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        bidEnd();
                        String error_tips = "";
                        if (Utils.isConnected(mContext)) {
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        } else {
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext, error_tips);
                    }

                    @Override
                    public void onNext(UnpayOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getData().isHaveUnpayOrder());
                        if(statusData.isHasTopPrice()){
                            bidEnd();
                            ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.acution_has_top_price));
                        }else{
                            if(statusData.getData().isHaveUnpayOrder()){
                                bidEnd();
                                showUnpayDialog();
                            }else{
                                acutionAddPriceData(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
                            }
                        }
                    }
                });
    }




    /**
     * 获取拍卖未支付的订单信息
     */
    public void checkEqAcutionSnUnpayData(){
        Utils.print(tag,"checkUnpayData");
        unpay_sc = getCommodityAPI()
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
                        bidEnd();
                        Utils.print(tag,"1error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        if(statusData.getReturnValue()==-1){
                            bidEnd();
                            return;
                        }

                        acutionData = statusData.getData();
                        Utils.print(tag,"isHaveUnpayOrder=="+statusData.getData().isHaveUnpayOrder());
                        if(acutionData.isHaveUnpayOrder()){
                            showUnpayDialog();
                            bidEnd();
                        }else{
                            acutionAddPriceData(Utils.getPrice(entityData.getData().getAuctionStatusVo().getCurrentPrice()));
                        }
                    }
                });
    }


    /**
     * 提示已经下线
     */
    private void showOffLineTips(){
        if(tipsDialog!=null && tipsDialog.isShowing()){
            return;
        }
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

    @Override
    public void onWaring() {
        if(warning_img.getVisibility()==View.GONE){
            loop_animal_runing=true;
            Utils.print(tag,"onWaring");
            viewAlphaVisible(warning_img);
        }
    }



    class GalleryAdapter extends BaseAdapter {
        private Context context;
        private List<String> list;

        private int totalSize=0;

        public GalleryAdapter(Context context, List<String> list) {
            super();
            this.context = context;
            this.list = list;
            if(list.size()<=1){
                totalSize=1;
            }else{
                totalSize=Integer.MAX_VALUE;
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return totalSize;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position%list.size());
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.infomation_image_item, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            ViewHolder vh = (ViewHolder) convertView.getTag();
            FrescoHelper.setImage(vh.image, Uri.parse(list.get(position%list.size())), new ResizeOptions(Utils.getScreenW(mContext), Utils.getScrrenH(mContext)));
            return convertView;
        }

        public class ViewHolder {
            private SimpleDraweeView image;
            public ViewHolder(View viewItem) {
                image = (SimpleDraweeView) viewItem.findViewById(R.id.image);
            }
        }
    }



    /**
     * 获取拍卖详情大图
     */
    public void getAcutionDetail(String goodSn){
        Utils.print(tag,"getAcutionDetail");

        if (!Utils.isConnected(mContext)) {
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext, error_tips);
            finish();
            return;
        }
        startProgressDialog();


        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("goodsSn",goodSn);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Utils.print(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Utils.print(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }


        Subscription s= RetrofitClient.getCommodityAPI()
                .httpGetAcutionDetail(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DetailData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                        stopProgressDialog();
                    }

                    @Override
                    public void onNext(DetailData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        stopProgressDialog();
                        if(statusData.getReturnValue()==-1){
                            showOffLineTips();
                            return;
                        }

                        /*for (int i = 0; i < statusData.getData().getDetailUrl().size(); i++) {
                            Utils.print(tag,"url="+statusData.getData().getDetailUrl().get(i));
                        }*/
                        list = statusData.getData().getDetailUrl();

                        if (statusData.getData().getDetailUrl().size() <= 1) {
                            arrow_right.setVisibility(View.INVISIBLE);
                            arrow_left.setVisibility(View.INVISIBLE);
                        }

                        setAdapterGallery();
                    }
                });
        addSubscription(s);
    }

    private void setAdapterGallery(){
        GalleryAdapter galleryAdapter = new GalleryAdapter(mContext,list);
        gallery.setAdapter(galleryAdapter);

        Utils.print(tag,">>>>"+list.size());
        if(list.size()>1){
            for (int i = 0; i < list.size(); i++) {
                if((Integer.MAX_VALUE/2+i)%list.size()==0){
                    gallery.setSelection(Integer.MAX_VALUE/2+i);
                    break;
                }
            }
        }
    }

}
