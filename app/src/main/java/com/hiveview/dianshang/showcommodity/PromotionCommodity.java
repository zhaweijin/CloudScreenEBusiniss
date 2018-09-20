package com.hiveview.dianshang.showcommodity;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
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
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.ItemAdapter;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.commodity.CommodityRecored;
import com.hiveview.dianshang.entity.commodity.mergeData.MergeCommodityData;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.HiveGridLayoutManager;
import com.hiveview.dianshang.view.HiveRecyclerView;

import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carter on 4/24/17.
 */

public class PromotionCommodity extends BaseActivity implements OnItemKeyListener, OnItemViewSelectedListener {

    //凑单页面
    private String tag = "CommoditySpecial";


    @BindView(R.id.tv_recycler_view)
    HiveRecyclerView mTvRecyclerView;

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

    private ItemAdapter itemAdapter;
    private int pos;


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

    private String promotionSn="";


    public static void launch(Activity activity,String sn) {
        Intent intent = new Intent(activity, PromotionCommodity.class);
        intent.putExtra("sn",sn);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commodity_promotion);

        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(mTvRecyclerView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }
        itemSpace = mContext.getResources().getDimensionPixelSize(R.dimen.item_view_div_8);
        marign_buttom = mContext.getResources().getDimensionPixelSize(R.dimen.recycle_marign_buttom_30);

        promotionSn = getIntent().getStringExtra("sn");
        Utils.print(tag,"promotionSn=="+promotionSn);
        getPromotionCommodity();

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



    private void init(MergeCommodityData mergeCommodityData) {

        List<CommodityRecored> commodityRecoreds = mergeCommodityData.getData().getMegerGoodsVoPage().getRecords();
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
            mTvRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        }

        //初始化适配器
        itemAdapter = new ItemAdapter(mContext,commodityRecoreds,this,this,ConStant.PROMOTION_TO_INFO);

        //设置管理器
        manager = new HiveGridLayoutManager(mContext, 4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.supportsPredictiveItemAnimations();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(itemAdapter.isHeader(position)){
                    return manager.getSpanCount();
                }else {
                    return 1;
                }
            }
        });
        //初始化头
        View headview = LayoutInflater.from(mContext).inflate(R.layout.promotion_head, null);
        itemAdapter.setmHeaderView(headview);

        TextView promotion_type_name = (TextView)headview.findViewById(R.id.promotion_type_name);
        promotion_type_name.setText(Utils.promotionTypeTranform(this,mergeCommodityData.getData().getPromotionType()));

        TextView promotion_type = (TextView)headview.findViewById(R.id.promotion_type);
        promotion_type.setText(mergeCommodityData.getData().getPromotionTitle());



        //recyclerview 设置属性
        mTvRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int postion = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
                if (itemAdapter.isHeader(postion)) {
                    outRect.set(0, 0, 0, 0);
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && checkIsLastLine(postion) && pageIndex >= maxPage) {
                    outRect.set(itemSpace, itemSpace, itemSpace, marign_buttom);
                }  else {
                    outRect.set(itemSpace, itemSpace, itemSpace, 0);
                }
            }
        });
        mTvRecyclerView.setLayoutManager(manager);
        mTvRecyclerView.setAdapter(itemAdapter);

    }


    private boolean checkIsLastLine(int position){
        boolean isLastLine = false;
        position--;//减去头部位置计算
        int x = (itemAdapter.getItemCount()-1)%4;
        //Utils.print(tag,"x="+x+",position=="+position+",size="+itemAdapter.getItemCount());
        if(x==0){
            //处理最后四个
            if(position>itemAdapter.getItemCount()-1-1-4){
                isLastLine = true;
            }
        }else{
            //处理最后剩余的几个
            if(position>itemAdapter.getItemCount()-1-1-x){
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
                    Utils.print(tag, "is last page=="+itemAdapter.getItemCount());
                    mTvRecyclerView.smoothScrollToPosition(itemAdapter.getItemCount()-1);
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
        itemAdapter.setSelectItemId(position);
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
                getMorePromotionCommodity();
            }
        }
    }


    /**
     * 获取更多所有商品的信息
     */
    public void getMorePromotionCommodity(){
        Utils.print(tag,"getMorePromotionCommodity");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("promotionSn", promotionSn);
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetMergeCommodityData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MergeCommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag,"error="+e.getMessage());
                        load_more_status = false;
                    }

                    @Override
                    public void onNext(MergeCommodityData mergeCommodityData) {
                        Utils.print(tag,"status=="+mergeCommodityData.getErrorMessage());
                        if(mergeCommodityData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,mergeCommodityData.getErrorMessage());
                            return;
                        }

                        List<CommodityRecored> recored = mergeCommodityData.getData().getMegerGoodsVoPage().getRecords();
                        size = size + recored.size();
                        itemAdapter.addData(recored);
                        load_more_status = false;
                    }
                });
        addSubscription(s);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(itemAdapter!=null){
            itemAdapter.unRegisterObserver();
        }
        FeedbackTranslationAnimatorUtil.getInstance().recycle();
    }

    /**
     * 判断是否为最后一行
     * @return
     */
    private boolean isLastLine(){
        boolean isLastLine = false;
        int x = (itemAdapter.getItemCount()-1)%4;
        Utils.print(tag,"x="+x+",pos=="+pos+",size="+itemAdapter.getItemCount());
        if(x==0){
            //处理最后四个
            if(pos>itemAdapter.getItemCount()-1-1-4){
                isLastLine = true;
            }
        }else{
            //处理最后剩余的几个
            if(pos>itemAdapter.getItemCount()-1-1-x){
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


    /**
     * 获取所有商品的信息
     */
    public void getPromotionCommodity(){
        Utils.print(tag,"getPromotionCommodity");
        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("promotionSn", promotionSn);
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetMergeCommodityData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MergeCommodityData>() {
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
                    public void onNext(MergeCommodityData mergeCommodityData) {
                        Utils.print(tag,"all commodity status=="+mergeCommodityData.getErrorMessage()+",value="+mergeCommodityData.getReturnValue());
                        stopProgressDialog();
                        if(mergeCommodityData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,mergeCommodityData.getErrorMessage());
                            return;
                        }



                        List<CommodityRecored> recored = mergeCommodityData.getData().getMegerGoodsVoPage().getRecords();
                        /*for(int i=0;i<recored.size();i++){
                            Utils.print(tag,"name="+recored.get(i).getName());
                            Utils.print(tag,"icon url="+recored.get(i).getProductImages());
                        }*/

                        size = recored.size();
                        if(mergeCommodityData.getData().getMegerGoodsVoPage().getTotalCount()>0){
                            totalCount = mergeCommodityData.getData().getMegerGoodsVoPage().getTotalCount();
                            maxPage = totalCount/ConStant.PAGESIZE;
                            if(totalCount%ConStant.PAGESIZE!=0){
                                maxPage++;
                            }
                            Utils.print(tag,"totalcount="+totalCount+",maxpage="+maxPage);
                        }

                        if(mergeCommodityData.getData()!=null && mergeCommodityData.getData().getMegerGoodsVoPage().getRecords().size()>0)
                            init(mergeCommodityData);

                        initRequest = true;
                    }
                });
        addSubscription(s);
    }
}

