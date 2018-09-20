package com.hiveview.dianshang.search;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.ItemAdapter;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.commodity.CommodityData;
import com.hiveview.dianshang.entity.commodity.CommodityRecored;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.HiveGridLayoutManager;
import com.hiveview.dianshang.view.HiveRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 搜索结果页
 * Created by carter on 4/10/17.
 */

public class SearchResult extends BaseFragment implements OnItemKeyListener,OnItemViewSelectedListener {

    private String tag = "SearchResult";

    private ItemAdapter itemAdapter;

    @BindView(R.id.tv_recycler_view)
    HiveRecyclerView mTvRecyclerView;


    private Observable<String> observable;

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


    private int pos;


    private boolean initFinished = false;

    /**
     * 操作状态标志
     */
    private boolean op_status=false;

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


    //搜索头展示数据
    private String search_key_name;
    private int search_key_value=0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_result;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        itemSpace = mContext.getResources().getDimensionPixelSize(R.dimen.item_view_div_8);
        marign_buttom = mContext.getResources().getDimensionPixelSize(R.dimen.recycle_marign_buttom_30);

        Utils.print(tag,"key==="+ search_key_name);


        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(mTvRecyclerView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }


        initEvent();
        search_key_name = Utils.formatInvalidString(search_key_name);
        getSearchResultData();


        mTvRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Utils.print(tag, "view finished....");
                if (initFinished) {
                    initFinished = false;
                    Utils.print(tag, "view refresh finish");
                    if(mTvRecyclerView.getChildCount()>1){
                        mTvRecyclerView.getChildAt(1).requestFocus();
                    }
                }
            }
        });
    }




    public static final SearchResult newInstance(OpSearchKey opSearchKey)
    {
        SearchResult f = new SearchResult();
        Bundle bundle = new Bundle();
        bundle.putString("value",opSearchKey.getValue());
        f.setArguments(bundle);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        search_key_name = getArguments().getString("value");
    }

    private void initEvent(){
        observable = RxBus.get().register(ConStant.obString_search_recommend_back,String.class);
        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Utils.print(tag,"back====");
                RxBus.get().post(ConStant.obString_opt_search_natigation,new OpSearchKey(ConStant.SEARCH_HOME));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(itemAdapter!=null){
            itemAdapter.unRegisterObserver();
        }
        FeedbackTranslationAnimatorUtil.getInstance().recycle();
        RxBus.get().unregister(ConStant.obString_search_recommend_back,observable);
    }



    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Utils.print(tag, "is key");
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
            if (pos%4 == 0) {
                v.setNextFocusLeftId(R.id.layout_search);
            }
        }else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
            if (isFirstLine()) {
                return true;
            }
        }
        return false;
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
     * 判断是否为最后一页
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
     * 获取搜索商品结果
     * @param key
     */
    public void getSearchResultData(){
        Utils.print(tag,"getSearchResultData");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }
        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("keywords",search_key_name);
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetSearchResultData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        try{
                            stopProgressDialog();
                            String error_tips = "";
                            if(Utils.isConnected(mContext)){
                                error_tips = mContext.getResources().getString(R.string.error_op_service_exception);
                            }else{
                                error_tips = mContext.getResources().getString(R.string.error_network_exception);
                            }
                            ToastUtils.showToast(mContext,error_tips);
                            Utils.print(tag,"error="+e.getMessage());
                        }catch (Exception ee){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag,"status=="+commodityData.getErrorMessage()+",value="+commodityData.getReturnValue());
                        stopProgressDialog();
                        if(commodityData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,commodityData.getErrorMessage());
                            //因为需要显示顶部头提示信息
                            List<CommodityRecored> recoreds = new ArrayList<CommodityRecored>();
                            init(recoreds);
                            return;
                        }

                        /*for(int i=0;i<commodityData.getData().getRecords().size();i++){
                            Utils.print(tag,"s=="+commodityData.getData().getRecords().get(i).getName());
                        }*/

                        if(commodityData.getData().getRecords().size()>0){
                            totalCount = commodityData.getData().getTotalCount();
                            maxPage = totalCount/ConStant.PAGESIZE;
                            if(totalCount%ConStant.PAGESIZE!=0){
                                maxPage++;
                            }
                        }


                        Utils.print(tag,"number=="+commodityData.getData().getTotalCount());
                        search_key_value = commodityData.getData().getTotalCount();

                        init(commodityData.getData().getRecords());


                        size = commodityData.getData().getRecords().size();
                        initFinished = true;
                    }
                });

        addSubscription(s);
    }



    private void init(List<CommodityRecored> commodityRecoreds) {

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
        itemAdapter = new ItemAdapter(mContext,commodityRecoreds,this,this,ConStant.SEARCH_RESULT_TO_INFO);

        //加载头部数据
        itemAdapter.setSearchResultData(search_key_name,search_key_value);

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
        View headview = LayoutInflater.from(mContext).inflate(R.layout.search_result_head, null);
        itemAdapter.setmHeaderView(headview);

        //recyclerview 设置属性
        mTvRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int postion = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
                if (itemAdapter.isHeader(postion)) {
                    outRect.set(0, 0, 0, 0);
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && checkIsLastLine(postion) && pageIndex >= maxPage) {
                    outRect.set(itemSpace, itemSpace, itemSpace, marign_buttom);
                } else {
                    outRect.set(itemSpace, itemSpace, itemSpace, 0);
                }
            }
        });
        mTvRecyclerView.setLayoutManager(manager);
        mTvRecyclerView.setAdapter(itemAdapter);


        if(commodityRecoreds.size()==0){
            mTvRecyclerView.setFocusable(false);
        }

    }


    /**
     * 获取更多搜索商品结果
     * @param key
     */
    public void getSearchResultMoreData(){
        Utils.print(tag,"getSearchResultData");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("keywords",search_key_name);
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetSearchResultData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        op_status = false;
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag,"status=="+commodityData.getErrorMessage()+",value="+commodityData.getReturnValue());

                        if(commodityData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,commodityData.getErrorMessage());
                            return;
                        }

                        /*for(int i=0;i<commodityData.getData().getRecords().size();i++){
                            Utils.print(tag,"s=="+commodityData.getData().getRecords().get(i).getName());
                        }*/

                        itemAdapter.addData(commodityData.getData().getRecords());
                        size = size + commodityData.getData().getRecords().size();
                        op_status = false;
                    }
                });

        addSubscription(s);
    }


    @Override
    public void OnItemViewSelectedListener(int position) {
        itemAdapter.setSelectItemId(position);
        pos = position;
        Utils.print(tag,"p=="+position);

        if(isFirstLine()){
            mTvRecyclerView.smoothScrollToPosition(0);
        }

        if(position>ConStant.END_SIZE && position>=size-ConStant.END_SIZE){
            Utils.print(tag,"append data");
            if(!op_status){
                op_status = true;
                pageIndex++;
                if(pageIndex>maxPage)
                    return;
                getSearchResultMoreData();
            }
        }
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


    /**
     * 获取搜索结果商品的个数
     * @return
     */
    public int getSearchResultSize(){
        if(mTvRecyclerView!=null){
            return mTvRecyclerView.getChildCount();
        }
        return 0;
    }
}
