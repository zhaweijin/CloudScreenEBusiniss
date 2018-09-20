package com.hiveview.dianshang.showcommodity;

import android.animation.AnimatorSet;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.RootCategoryAdapter;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.category.commodity.OneLevelCategoryData;
import com.hiveview.dianshang.entity.category.commodity.OneLevelCategoryRecored;
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

public class CommodityMainCategory extends BaseFragment implements OnItemKeyListener, OnItemViewSelectedListener {


    @BindView(R.id.tv_recycler_view)
    HiveRecyclerView mTvRecyclerView;

    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;

    private String tag = "CommodityMainCategory";

    RootCategoryAdapter itemAdapter;

    /**
     * recyclerview layout
     */
    private HiveGridLayoutManager manager;

    /**
     * recyclerview item 间距
     */
    private int itemSpace;

    /**
     * 底部间距
     */
    private int marign_buttom;

    /**
     * item view位置
     */
    private int pos;
    @Override
    protected int getLayoutId() {
        return R.layout.commodity_category_main;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Utils.print(tag, "getOneLevelCategory");
        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(mTvRecyclerView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }
        itemSpace = mContext.getResources().getDimensionPixelSize(R.dimen.item_view_div_8);
        marign_buttom = mContext.getResources().getDimensionPixelSize(R.dimen.recycle_marign_buttom_30);

        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        startProgressDialog();
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", "1");
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetOneLevelData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OneLevelCategoryData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        try{
                            stopProgressDialog();
                            String error_tips = "";
                            if(Utils.isConnected(mContext)){
                                error_tips = mContext.getResources().getString(R.string.error_service_exception);
                            }else{
                                error_tips = mContext.getResources().getString(R.string.error_network_exception);
                            }
                            ToastUtils.showToast(mContext,error_tips);
                            Utils.print(tag, "error=" + e.getMessage());
                        }catch (Exception ee){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(OneLevelCategoryData oneLevelCategoryData) {
                        Utils.print(tag, "status==" + oneLevelCategoryData.getErrorMessage());
                        stopProgressDialog();
                        if(oneLevelCategoryData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,oneLevelCategoryData.getErrorMessage());
                            return;
                        }

                        if(oneLevelCategoryData.getData()==null){
                            ToastUtils.showToast(mContext,oneLevelCategoryData.getErrorMessage());
                            return;
                        }

                        //recored = oneLevelCategoryData.getData().getRecords();
                        /*for (int i = 0; i < recored.size(); i++) {
                            Utils.print(tag, "imgurl=" + recored.get(i).getImgUrl());
                        }*/


                        init(oneLevelCategoryData.getData().getRecords());
                    }
                });
        addSubscription(s);
    }


    private void init(List<OneLevelCategoryRecored> recored) {

        Utils.print(tag,"size=="+recored.size());
        //设置动画
        DefaultItemAnimator animator = new DefaultItemAnimator();
        mTvRecyclerView.setItemAnimator(animator);
        mTvRecyclerView.getItemAnimator().setChangeDuration(0);
        ((SimpleItemAnimator)mTvRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)mTvRecyclerView.getLayoutParams();
            layoutParams.setMargins(0,0,0,marign_buttom);
            mTvRecyclerView.setLayoutParams(layoutParams);
        }else {
            mTvRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        }

        //初始化适配器
        itemAdapter = new RootCategoryAdapter(mContext,recored,this,this);

        //设置管理器
        manager = new HiveGridLayoutManager(mContext, 3);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.supportsPredictiveItemAnimations();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });

        //recyclerview 设置属性
        mTvRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int postion = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
                Utils.print(tag,"pp="+postion);
                /*if(checkIsFirstLine(postion)){
                    Utils.print(tag,"2carter");
                    outRect.set(itemSpace, itemSpace+mContext.getResources().getDimensionPixelSize(R.dimen.gap), itemSpace, 0);
                    return;
                }

                if(checkIsLastLine(postion)){
                    outRect.set(itemSpace, itemSpace, itemSpace, marign_buttom);
                    return;
                }*/

                outRect.set(itemSpace, itemSpace, itemSpace, 0);
            }
        });
        mTvRecyclerView.setLayoutManager(manager);
        mTvRecyclerView.setAdapter(itemAdapter);
        Utils.print(tag,"222");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FeedbackTranslationAnimatorUtil.getInstance().recycle();
    }

    @Override
    public void OnItemViewSelectedListener(int position) {
        Utils.print(tag,"OnItemSelectedListener="+position);
        itemAdapter.setSelectItemId(position);
        pos = position;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (isLastLine()) {
                Utils.print(tag, "is last page");
                if (null != feedbackAnimator && !feedbackAnimator.isRunning()) {
                    Utils.print(tag, "start animation");
                    feedbackAnimator.start();
                }
                return true;
            }
        }else if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
            if (pos%3 == 0) {
                v.setNextFocusLeftId(R.id.layout_commodity);
            }
        }else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
            if (isFirstLine()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断是否为最后一页
     * @return
     */
    private boolean isLastLine(){
        boolean isLastLine = false;
        int x = itemAdapter.getItemCount()%3;
        Utils.print(tag,"x="+x+",pos=="+pos+",size="+itemAdapter.getItemCount());
        if(x==0){
            //处理最后三个
            if(pos>itemAdapter.getItemCount()-1-3){
                isLastLine = true;
            }
        }else{
            //处理最后剩余的几个
            if(pos>itemAdapter.getItemCount()-1-x){
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
        if(pos<3){
            isFirstLine = true;
        }
        return isFirstLine;
    }

    /**
     * 检测是否第一页
     * @param position
     * @return
     */
    private boolean checkIsFirstLine(int position){
        boolean isFirstLine = false;
        if(position<3){
            isFirstLine = true;
        }
        return isFirstLine;
    }


    private boolean checkIsLastLine(int position){
        boolean isLastLine = false;
        int x = (itemAdapter.getItemCount()-1)%3;
        //Utils.print(tag,"x="+x+",position=="+position+",size="+itemAdapter.getItemCount());
        if(x==0){
            //处理最后三个
            if(position>itemAdapter.getItemCount()-1-1-3){
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

    /**
     * 是否允许主页导航移动焦点到右边
     * @return
     */
    public boolean allowRequestFocus(){
        boolean allowFocus = false;
        if(mTvRecyclerView.getChildCount()>0){
            allowFocus = true;
        }
        return allowFocus;
    }
}
