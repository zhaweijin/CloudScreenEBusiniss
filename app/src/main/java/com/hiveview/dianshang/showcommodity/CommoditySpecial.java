package com.hiveview.dianshang.showcommodity;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.SpecialAdapter;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.special.SpecialData;
import com.hiveview.dianshang.entity.special.SpecialRecored;
import com.hiveview.dianshang.entity.token.TokenData;
import com.hiveview.dianshang.utils.DeviceInfo;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.HiveGridLayoutManager;
import com.hiveview.dianshang.view.HiveRecyclerView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carter on 4/24/17.
 */

public class CommoditySpecial extends BaseActivity implements OnItemKeyListener, OnItemViewSelectedListener {


    private String tag = "CommoditySpecial";


    @BindView(R.id.tv_recycler_view)
    HiveRecyclerView mTvRecyclerView;

    @BindView(R.id.special_advertisment)
    SimpleDraweeView special_advertisment;


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

    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;


    /**
     * 初始化请求第一个view 获取焦点
     */
    private boolean initRequest = false;

    private SpecialAdapter specialAdapter;
    private int pos;


    private String specialName="";
    private String specialSn;

    /**
     * 分页数据加载标示
     */
    private boolean load_more_status = false;

    private int size = 0;

    /**
     * recyclerview layout
     */
    private HiveGridLayoutManager manager;

    /**
     * recyclerview item 间距
     */
    private int itemSpace;


    private int marign_buttom;

    private int lastKey;


    public static void launch(Activity activity, String specialName,String specialSn) {
        Intent intent = new Intent(activity, CommoditySpecial.class);
        intent.putExtra("specName",specialName);
        intent.putExtra("specSn",specialSn);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commodity_special);

        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(mTvRecyclerView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }
        itemSpace = mContext.getResources().getDimensionPixelSize(R.dimen.item_view_div_8);
        marign_buttom = mContext.getResources().getDimensionPixelSize(R.dimen.recycle_marign_buttom_30);

        try{
            specialName = getIntent().getStringExtra("specName");
        }catch (Exception e){
            e.printStackTrace();
        }

        specialSn = getIntent().getStringExtra("specSn");

        Utils.print(tag,"sn="+specialSn+",name="+specialName);
        String user = (String) SPUtils.get(mContext, ConStant.USERID, "");
        String token = (String) SPUtils.get(mContext, ConStant.USER_TOKEN, "");
        startProgressDialog();
        if (!user.equals("") && !token.equals("")) {
            Utils.print(tag, "set token");
            ConStant.userID = user;
            ConStant.Token = token;
            getSpecialList();
        }else{
            getTokenData();
        }


        sendStatistics();


        mTvRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Utils.print(tag,"view finished....");
                if(initRequest){
                    Utils.print(tag,"init request focus");
                    initRequest = false;
                    if(mTvRecyclerView.getChildCount()>1){
                        mTvRecyclerView.getChildAt(1).requestFocus();
                    }
                }
            }
        });


        mTvRecyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Utils.print(tag,"mTvRecyclerView focus==="+hasFocus);
                if(hasFocus && mTvRecyclerView.getChildCount()>1){
                    int visiblePos = 0;
                    if(lastKey==KeyEvent.KEYCODE_DPAD_DOWN){
                        visiblePos = (pos)%4+2*4;
                        Utils.print(tag,"visiblePos="+visiblePos);
                        resetFocus(visiblePos);
                    }else if(lastKey==KeyEvent.KEYCODE_DPAD_UP){
                        visiblePos = (pos)%4;
                        Utils.print(tag,"visiblePos="+visiblePos);
                        resetFocus(visiblePos);
                    }
                }
            }
        });
    }


    /**
     * recyclerview item 重新请求焦点
     * @param visiblePos
     */
    private void resetFocus(int visiblePos){
        if(mTvRecyclerView.getChildAt(visiblePos)==null){
            Utils.print(tag,"null");
            return;
        }

        if(!mTvRecyclerView.getChildAt(visiblePos).hasFocus()){
            Utils.print(tag,"reset focus.................");
            mTvRecyclerView.getChildAt(visiblePos).requestFocus();
        }
    }



    private void init(List<SpecialRecored> specialRecoreds) {

        //设置动画
        DefaultItemAnimator animator = new DefaultItemAnimator();
        mTvRecyclerView.setItemAnimator(animator);
        mTvRecyclerView.getItemAnimator().setChangeDuration(0);
        ((SimpleItemAnimator)mTvRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mTvRecyclerView.getLayoutParams();
            layoutParams.setMargins(0,0,0,marign_buttom);
            mTvRecyclerView.setLayoutParams(layoutParams);
        }else {
            /*Utils.print(tag,"set......");
            mTvRecyclerView.getItemAnimator().setChangeDuration(0);
            ((SimpleItemAnimator)mTvRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);*/
            mTvRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        }

        //初始化适配器
        specialAdapter = new SpecialAdapter(mContext,specialRecoreds,this,this,ConStant.SPECIAL_TO_INFO);

        //设置管理器
        manager = new HiveGridLayoutManager(mContext, 4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.supportsPredictiveItemAnimations();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(specialAdapter.isHeader(position)){
                    return manager.getSpanCount();
                }else {
                    return 1;
                }
            }
        });
        //初始化头
        View headview = LayoutInflater.from(mContext).inflate(R.layout.special_head, null);
        specialAdapter.setmHeaderView(headview);


        //recyclerview 设置属性
        mTvRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int postion = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
                if (specialAdapter.isHeader(postion)) {
                    outRect.set(0, 0, 0, 0);
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && checkIsLastLine(postion) && pageIndex >= maxPage) {
                    outRect.set(itemSpace, itemSpace, itemSpace, marign_buttom);
                }  else {
                    outRect.set(itemSpace, itemSpace, itemSpace, 0);
                }
            }
        });
        mTvRecyclerView.setLayoutManager(manager);
        mTvRecyclerView.setAdapter(specialAdapter);

    }


    private boolean checkIsLastLine(int position){
        boolean isLastLine = false;
        position--;//减去头部位置计算
        int x = (specialAdapter.getItemCount()-1)%4;
        //Utils.print(tag,"x="+x+",position=="+position+",size="+itemAdapter.getItemCount());
        if(x==0){
            //处理最后四个
            if(position>specialAdapter.getItemCount()-1-1-4){
                isLastLine = true;
            }
        }else{
            //处理最后剩余的几个
            if(position>specialAdapter.getItemCount()-1-1-x){
                isLastLine = true;
            }
        }
        return isLastLine;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        lastKey = keyCode;
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (isLastLine()) {
                    Utils.print(tag, "is last page=="+specialAdapter.getItemCount());
                    mTvRecyclerView.smoothScrollToPosition(specialAdapter.getItemCount()-1);
                    if (null != feedbackAnimator && !feedbackAnimator.isRunning()) {
                        Utils.print(tag, "start animation");
                        feedbackAnimator.start();
                    }
                    return true;
                }
            }else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                if (isFirstLine()) {
                    return true;
                }
            }
        }
        return false;
    }






    @Override
    public void OnItemViewSelectedListener(int position) {
        specialAdapter.setSelectItemId(position);
        Utils.print(tag,"pos==="+position);
        pos = position;

        if(isFirstLine()){
            mTvRecyclerView.smoothScrollToPosition(0);
        }

        if(position>ConStant.END_SIZE && position>=size-ConStant.END_SIZE){
            Utils.print(tag,"append data");
            if(!load_more_status){
                load_more_status = true;
                pageIndex++;
                if(pageIndex>maxPage)
                    return;
                getMoreSpecialList();
            }
        }
    }

    /**
     * 获取专题列表
     */
    public void getSpecialList(){
        Utils.print(tag,"getSpecialList");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("specSn",specialSn); //专题号
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }
        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetSpecialData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SpecialData>() {
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
                    public void onNext(SpecialData specialData) {
                        Utils.print(tag,"status=="+specialData.getErrorMessage()+",value="+specialData.getReturnValue());

                        if(specialData.getReturnValue()==-1){
                            stopProgressDialog();
                            ToastUtils.showToast(mContext,specialData.getErrorMessage());
                            RxBus.get().post(ConStant.obString_opt_natigation,ConStant.OP_HOME);
                            return;
                        }

                        if(specialData.getReturnValue()==-2){   //token已经失效,再次请求
                            getTokenData();
                            return;
                        }

                        for(int i=0;i<specialData.getData().getRecords().size();i++){
                            Utils.print(tag,"name="+specialData.getData().getRecords().get(i).getGoodsName());
                        }

                        if(specialData.getData().getTotalCount()>0){
                            totalCount = specialData.getData().getTotalCount();
                            maxPage = totalCount/ConStant.PAGESIZE;
                            if(totalCount%ConStant.PAGESIZE!=0){
                                maxPage++;
                            }
                            Utils.print(tag,"totalcount="+totalCount+",maxpage="+maxPage);
                        }


                        FrescoHelper.setImage(special_advertisment, Uri.parse(specialData.getData().getRecords().get(0).getSpecialImg()),new ResizeOptions(Utils.getScreenW(mContext),mContext.getResources().getDimensionPixelSize(R.dimen.special_advertisment_height_500)));
                        init(specialData.getData().getRecords());
                        size = specialData.getData().getRecords().size();
                        initRequest = true;

                        stopProgressDialog();

                    }
                });
        addSubscription(s);
    }



    /**
     * 获取更多专题列表
     */
    public void getMoreSpecialList(){
        Utils.print(tag,"getSpecialList");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("specSn",specialSn); //专题号
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }
        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetSpecialData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SpecialData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        load_more_status = false;
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
                    public void onNext(SpecialData specialData) {
                        Utils.print(tag,"status=="+specialData.getErrorMessage()+",value="+specialData.getReturnValue());
                        if(specialData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,specialData.getErrorMessage());
                            return;
                        }


                        specialAdapter.addData(specialData.getData().getRecords());
                        size = size + specialData.getData().getRecords().size();
                        load_more_status = false;
                    }
                });
        addSubscription(s);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(specialAdapter!=null){
            specialAdapter.unRegisterObserver();
        }
        FeedbackTranslationAnimatorUtil.getInstance().recycle();
    }

    /**
     * 判断是否为最后一行
     * @return
     */
    private boolean isLastLine(){
        boolean isLastLine = false;
        int x = (specialAdapter.getItemCount()-1)%4;
        Utils.print(tag,"x="+x+",pos=="+pos+",size="+specialAdapter.getItemCount());
        if(x==0){
            //处理最后四个
            if(pos>specialAdapter.getItemCount()-1-1-4){
                isLastLine = true;
            }
        }else{
            //处理最后剩余的几个
            if(pos>specialAdapter.getItemCount()-1-1-x){
                isLastLine = true;
            }
        }
        return isLastLine;
    }


    /**
     * 判断是否为第一行
     * @return
     */
    private boolean isFirstLine(){
        boolean isFirstLine = false;
        if(pos<4){
            isFirstLine = true;
        }
        return isFirstLine;
    }

    private void sendStatistics(){
        HashMap<String,String> simpleMap=new HashMap<String,String>();

        String info = "";
        //专题浏览量埋点
        simpleMap.put("tabNo","");
        simpleMap.put("actionType","Ec2001");
        info = info + "subjectId="+specialSn+"&";
        info = info + "subjectName="+specialName;
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

                        getSpecialList();

                    }
                });
        addSubscription(s);
    }
}

