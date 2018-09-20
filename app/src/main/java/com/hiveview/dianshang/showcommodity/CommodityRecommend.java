package com.hiveview.dianshang.showcommodity;

import android.animation.AnimatorSet;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.advertisement.Advertisement;
import com.hiveview.dianshang.entity.advertisement.AdvertisementData;
import com.hiveview.dianshang.entity.recommend.RecommendData;
import com.hiveview.dianshang.entity.recommend.RecommendItemData;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.ObservableScrollView;
import com.hiveview.dianshang.view.TypeFaceTextView;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carter on 4/24/17.
 */

public class CommodityRecommend extends BaseFragment {


    private String tag = "CommodityRecommend";
    /**
     * 记录当前浏览的推荐页码,用于显示唯一视频播放
     */
    public static int currentPage =0;

    /**
     * 当前请求数据的页数
     */
    private int pageIndex = 1;
    /**
     * 当前商品的总数量
     */
    private int totalCount = 0;

    /**
     * 最大请求的页数
     */
    private int maxPage = 1;

    @BindView(R.id.top_menu)
    RelativeLayout top_menu;


    @BindView(R.id.layout_notification)
    LinearLayout layout_notification;


    @BindView(R.id.scrollview)
    ObservableScrollView scrollview;

    @BindView(R.id.container)
    LinearLayout container;

    /**
     * 广告显示字条
     */
    @BindView(R.id.advertisement_message)
    TypeFaceTextView advertisement_message;

    private String message;

    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;

    public boolean finishHomeFocus = false;

    private boolean viewFinished = false;


    /**
     * 所有商品view
     */
    View allView;
    /**
     * 推荐位1的数据部分
     */
    private List<RecommendItemData> firstHome = new ArrayList<RecommendItemData>();

    /**
     * 推荐位2的数据部分
     */
    private List<RecommendItemData> secondHome = new ArrayList<RecommendItemData>();

    /**
     * 定义第一屏幕view,第二屏幕view
     */
    RecommendScroolItemView view0;
    RecommendScroolItemView view1;


    //test
    private long time1;
    private long time2;

    @Override
    protected int getLayoutId() {
        return R.layout.commodity_recommend;
    }

    public boolean isFinishHomeFocus() {
        return finishHomeFocus;
    }

    public void setFinishHomeFocus(boolean finishHomeFocus) {
        this.finishHomeFocus = finishHomeFocus;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //每次初始化
        currentPage=0;

        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(scrollview, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }
        Utils.print(tag,"create");
        RxBus.get().post(ConStant.obString_reset_nav_focus,1);  //恢复导航栏焦点

        top_menu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.print(tag,"focus=="+hasFocus);
                if(hasFocus){
                    if(view0!=null && view0.getFocusView()!=null){
                        view0.getFocusView().requestFocus();
                    }
                }
            }
        });

        advertisement_message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.print(tag,"222focus=="+hasFocus);
                if(hasFocus){
                    if(view0!=null && view0.getFocusView()!=null){
                        view0.getFocusView().requestFocus();
                    }
                }
            }
        });


        if(firstHome.size()==0 || secondHome.size()==0){ //第一次进入
            getHomeRecommendData();
        }else{
            scrollview.fullScroll(ScrollView.FOCUS_DOWN); //返回进入
            allView.requestFocus();
        }

        advertisement_message.setText(message);
        advertisement_message.setEllipsize(android.text.TextUtils.TruncateAt.MARQUEE);
        advertisement_message.setMarqueeRepeatLimit(-1);
        advertisement_message.setFocusable(true);
        advertisement_message.setFocusableInTouchMode(true);
        advertisement_message.setSelected(true);

        if(message==null || message.equals("")){
            layout_notification.setVisibility(View.GONE);
        }

        scrollview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Utils.print(tag, "view finished....");
                if (viewFinished) {
                    viewFinished = false;
                    Utils.print(tag, "view refresh finish");
                    //避免偶现一次滚动没有到达的现象
                    scrollview.fullScroll(ScrollView.FOCUS_UP);
                    scrollview.fullScroll(ScrollView.FOCUS_UP);
                    view0.requestPositionFocus(0);

                    RxBus.get().post(ConStant.obString_main_load_finish, MainActivity.NAV_HOME);

                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        Utils.print(tag,"onResume");

        if (message==null || message.equals("")) {
            loadAdvertisement();
        }

    }



    public static final CommodityRecommend newInstance(String message) {
        CommodityRecommend f = new CommodityRecommend();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        f.setArguments(bundle);
        return f;
    }

    public void setMessage(String message){
        this.message = message;
    }

    /**
     * 获取首页推荐2页数据
     */
    public void getHomeRecommendData(){
        Utils.print(tag,"getRecommendData");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }
        time1 = SystemClock.currentThreadTimeMillis();
        startProgressDialog();


        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetRecommendData(ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RecommendData>() {
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
                        Utils.print(tag,"1error="+e.getMessage());
                    }

                    @Override
                    public void onNext(RecommendData recommendData) {
                        Utils.print(tag,"status=="+recommendData.getErrorMessage());


                        if(recommendData.getReturnValue()==-1){
                            stopProgressDialog();
                            ToastUtils.showToast(mContext,recommendData.getErrorMessage());
                            return;
                        }

                        //商城上线推荐位不存在
                        if(recommendData.getRecommend()==null){
                            stopProgressDialog();
                            ToastUtils.showToast(mContext,recommendData.getErrorMessage());
                            return;
                        }

                        time2 = SystemClock.currentThreadTimeMillis();
                        Utils.print(tag,"time2-time1=="+(time2-time1));
                        List<RecommendItemData> recommendItemDatas =recommendData.getRecommend().getOperationMatrixItemList();

                        for(int i=0;i<recommendItemDatas.size();i++){
                            if(recommendItemDatas.get(i).getScreenNumber()==1){
                                firstHome.add(recommendItemDatas.get(i));
                            }else if(recommendItemDatas.get(i).getScreenNumber()==2){
                                secondHome.add(recommendItemDatas.get(i));
                            }
                        }

                        //第一页
                        view0 = new RecommendScroolItemView(mContext);
                        view0.init(mContext,0,firstHome);
                        view0.setTemplate(recommendData.getRecommend().getTemplateName(),recommendData.getRecommend().getTemplateSn());
                        container.addView(view0);

                        //第二页
                        view1 = new RecommendScroolItemView(mContext);
                        view1.init(mContext,1,secondHome);
                        view1.setTemplate(recommendData.getRecommend().getTemplateName(),recommendData.getRecommend().getTemplateSn());
                        container.addView(view1);


                        //加载全部商品图标
                        addAllCommodityView(recommendData.getRecommend().getAllGoodsImage());



                        viewFinished = true;
                        stopProgressDialog();
                    }
                });
        addSubscription(s);
    }





    private void addAllCommodityView(String iconUrl){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        allView = layoutInflater.inflate(R.layout.layout_all_commodity_button,null);

        Utils.print(tag,"iconuRL=="+iconUrl);
        SimpleDraweeView img_all_commodity = (SimpleDraweeView)allView.findViewById(R.id.img_all_commodity);
        FrescoHelper.setImage(img_all_commodity, Uri.parse(iconUrl));

        LinearLayout.LayoutParams layoutParams;
        int item_width = mContext.getResources().getDimensionPixelSize(R.dimen.recoment_1040_view);
        int item_height = mContext.getResources().getDimensionPixelSize(R.dimen.recoment_167_view);
        layoutParams = new LinearLayout.LayoutParams(item_width,item_height);
        container.addView(allView,layoutParams);

        final ImageView focusView = (ImageView)allView.findViewById(R.id.focus_view);

        allView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusView.setVisibility(View.VISIBLE);
                    handScaleView(v,true);
                }else{
                    focusView.setVisibility(View.INVISIBLE);
                    handScaleView(v,false);
                }
            }
        });

        allView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.print(tag,"all commodity show");
                RxBus.get().post(ConStant.obString_opt_natigation,ConStant.OP_ALL);
            }
        });


        allView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                        if (null != feedbackAnimator && !feedbackAnimator.isRunning()) {
                            Utils.print(tag, "start animation");
                            feedbackAnimator.start();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }



    private void handScaleView(View v, boolean isScale) {

        float bigScale = 1.02f;

        ViewPropertyAnimator animator = v.animate();
        animator.setDuration(ConStant.SCALE_DURATION);
        if (isScale) {
            animator.scaleX(bigScale).scaleY(bigScale);
        } else {
            animator.scaleX(1.0f).scaleY(1.0f);
        }

        animator.start();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        FeedbackTranslationAnimatorUtil.getInstance().recycle();
    }



    /**
     * 获取首页广告信息
     */
    public void  loadAdvertisement(){
        Utils.print(tag,"getAdvertisement");

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetAdvertisement(ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AdvertisementData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        try{
                            Utils.print(tag,"error="+e.getMessage());
                            String message = "";
                            advertisement_message.setText(message);
                        }catch (Exception ee){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(AdvertisementData advertisementData) {
                        Utils.print(tag,"ad status=="+advertisementData.getErrorMessage()+",value="+advertisementData.getReturnValue());

                        if(advertisementData.getReturnValue()==-2){   //token已经失效,再次请求
                            return;
                        }

                        String message = "";
                        if(advertisementData.getReturnValue()==-1){
                        }else{
                            List<Advertisement> advertisements = advertisementData.getData();
                            for(int i=0;i<advertisements.size();i++){
                                Utils.print(tag,"s=="+advertisements.get(i).getDescription());
                                if(i==advertisements.size()-1){
                                    message = message+advertisements.get(i).getDescription();
                                }else{
                                    message = message+advertisements.get(i).getDescription()+"      ";
                                }
                            }
                        }
                        advertisement_message.setText(message);
                    }
                });
        addSubscription(s);
    }

}
