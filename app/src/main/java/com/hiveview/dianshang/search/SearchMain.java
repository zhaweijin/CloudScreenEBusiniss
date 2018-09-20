package com.hiveview.dianshang.search;

import android.animation.AnimatorSet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.SearchMainAdapter;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.address.qrdata.QrData;
import com.hiveview.dianshang.entity.commodity.CommodityData;
import com.hiveview.dianshang.entity.commodity.CommodityRecored;
import com.hiveview.dianshang.entity.netty.search.DomySearchTcpMsgBodyVo;
import com.hiveview.dianshang.entity.netty.search.DomySearchTcpMsgVo;
import com.hiveview.dianshang.entity.search.key.SearchKeyData;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.QrcodeUtil;
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
 * 搜索主页
 * Created by carter on 4/10/17.
 */

public class SearchMain extends BaseFragment implements OnItemKeyListener, OnItemViewSelectedListener {


    private String tag = "SearchMain";
    private SearchMainAdapter searchMainAdapter;

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

    private boolean defaultRequestFocus = false;

    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;

    @BindView(R.id.tv_recycler_view)
    HiveRecyclerView mTvRecyclerView;

    /**
     * 历史关键字
     */
    private List<String> keydatas = new ArrayList<>();


    private int pos;

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

    /**
     * recyclerview item 向底间隔
     */
    private int marign_buttom;


    private boolean initRequest = false;


    public static final SearchMain newInstance(boolean focus) {
        SearchMain f = new SearchMain();
        Bundle bundle = new Bundle();
        bundle.putBoolean("focus", focus);
        f.setArguments(bundle);
        return f;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.search;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultRequestFocus = getArguments().getBoolean("focus");
        regeditReceiver();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(mTvRecyclerView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }

        itemSpace = mContext.getResources().getDimensionPixelSize(R.dimen.item_view_div_8);
        marign_buttom = mContext.getResources().getDimensionPixelSize(R.dimen.recycle_marign_buttom_30);
        initEvent();

        getSearchHotKeyData();

        mTvRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Utils.print(tag,"view finished....");
                if(mTvRecyclerView==null)
                    return;
                if(initRequest){
                    Utils.print(tag,"init request focus");
                    initRequest = false;
                    Utils.print(tag,"isFocus=="+defaultRequestFocus);
                    if(defaultRequestFocus){
                        getSearchKey().requestFocus();
                    }
                }
            }
        });
    }

    /**
     * 处理接收的消息
     */
    private void initEvent(){
        observable = RxBus.get().register(ConStant.obString_search_recommend_back,String.class);
        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Utils.print(tag,"back====");
                RxBus.get().post(ConStant.obString_opt_search_natigation,new OpSearchKey(ConStant.SEARCH_EXIT));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(searchMainAdapter!=null){
            searchMainAdapter.unRegisterObserver();
        }

        mContext.unregisterReceiver(nettyReceiver);

        FeedbackTranslationAnimatorUtil.getInstance().recycle();
        RxBus.get().unregister(ConStant.obString_search_recommend_back,observable);
    }


    /**
     * 处理焦点
     * @param v
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Utils.print(tag, "is key=");
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
        }else if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
            if(isFirstLine()){
                Utils.print(tag,"smooth top");
                mTvRecyclerView.smoothScrollToPosition(0);
            }
        }
        return false;
    }


    /**
     * 获取搜索历史关键词
     */
    public void getSearchHotKeyData(){
        Utils.print(tag,"getSearchKeyData");
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
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetSearchKeyData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<SearchKeyData>() {
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
                            Utils.print(tag,tag+"keydata"+e.getMessage());
                        }catch (Exception ee){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(SearchKeyData searchKeyData) {
                        Utils.print(tag,"status=="+searchKeyData.getErrorMessage());
                        getSearchRecommendData();
                        if(searchKeyData.getReturnValue()==-1)
                            return ;
                        for(int i=0;i<searchKeyData.getData().size();i++){
                            Utils.print(tag,"s1=="+searchKeyData.getData().get(i));
                        }

                        keydatas = new ArrayList<String>();
                        if(searchKeyData.getData().size()>8){
                            for(int i=0;i<8;i++){
                                keydatas.add(searchKeyData.getData().get(i));
                            }
                        }else{
                            keydatas.addAll(searchKeyData.getData());
                        }
                        Utils.print(tag,"get keydata ok");
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
        searchMainAdapter = new SearchMainAdapter(mContext,commodityRecoreds,this,this,ConStant.SEARCH_RECOMMEND_TO_INFO);

        //set data
        searchMainAdapter.setSearchKeyData(keydatas);
        searchMainAdapter.setmTvRecyclerView(mTvRecyclerView);

        //设置管理器
        manager = new HiveGridLayoutManager(mContext, 4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.supportsPredictiveItemAnimations();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(searchMainAdapter.isHeader(position)){
                    return manager.getSpanCount();
                }else {
                    return 1;
                }
            }
        });
        //初始化头
        View headview = LayoutInflater.from(mContext).inflate(R.layout.search_recomment_main, null);
        searchMainAdapter.setmHeaderView(headview);

        SimpleDraweeView qrImg = (SimpleDraweeView)searchMainAdapter.getmHeaderView().findViewById(R.id.search_qrcode);
        getSearchQr(qrImg);

        //recyclerview 设置属性
        mTvRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int postion = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
                if (searchMainAdapter.isHeader(postion)) {
                    outRect.set(0, 0, 0, 0);
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && checkIsLastLine(postion) && pageIndex >= maxPage) {
                    outRect.set(itemSpace, itemSpace, itemSpace, marign_buttom);
                }else {
                    outRect.set(itemSpace, itemSpace, itemSpace, itemSpace);
                }
            }
        });
        mTvRecyclerView.setLayoutManager(manager);
        mTvRecyclerView.setAdapter(searchMainAdapter);

        initRequest = true;

    }


    private boolean checkIsLastLine(int position){
        boolean isLastLine = false;
        position--;//减去头部位置计算
        int x = (searchMainAdapter.getItemCount()-1)%4;
        //Utils.print(tag,"x="+x+",position=="+position+",size="+itemAdapter.getItemCount());
        if(x==0){
            //处理最后四个
            if(position>searchMainAdapter.getItemCount()-1-1-4){
                isLastLine = true;
            }
        }else{
            //处理最后剩余的几个
            if(position>searchMainAdapter.getItemCount()-1-1-x){
                isLastLine = true;
            }
        }
        return isLastLine;
    }


    /**
     * 获取搜索推荐
     */
    public void getSearchRecommendData(){
        Utils.print(tag,"getSearchRecommendData");

        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetSearchRecommendData(input,ConStant.getInstance(mContext).Token)
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
                                error_tips = mContext.getResources().getString(R.string.error_service_exception);
                            }else{
                                error_tips = mContext.getResources().getString(R.string.error_network_exception);
                            }
                            ToastUtils.showToast(mContext,error_tips);
                            Utils.print(tag,tag+"error="+e.getMessage());
                        }catch (Exception ee){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag,"2status=="+commodityData.getErrorMessage());
                        stopProgressDialog();
                        List<CommodityRecored> list = new ArrayList<CommodityRecored>();
                        if(commodityData.getReturnValue()==-1){
                            init(list);
                            return;
                        }

                        /*for(int i=0;i<commodityData.getData().getRecords().size();i++){
                            Utils.print(tag,"s2=="+commodityData.getData().getRecords().get(i).getName());
                        }*/
                        /**
                         * 处理没有推荐数据的时候显示搜索条
                         */
                        if(commodityData.getData()!=null){
                            list = commodityData.getData().getRecords();
                        }
                        if(list.size()>0){
                            totalCount = commodityData.getData().getTotalCount();
                            maxPage = totalCount/ConStant.PAGESIZE;
                            if(totalCount%ConStant.PAGESIZE!=0){
                                maxPage++;
                            }
                            Utils.print(tag,"totalcount="+totalCount+",maxpage="+maxPage);
                        }

                        size = commodityData.getData().getRecords().size();
                        Utils.print(tag,"1111="+keydatas.size());

                        try{
                            init(list);
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });

        addSubscription(s);
    }


    /**
     * 获取搜索推荐
     */
    public void getSearchMoreRecommendData(){
        Utils.print(tag,"getSearchMoreRecommendData");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetSearchRecommendData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        try{
                            op_status = false;
                            String error_tips = "";
                            if(Utils.isConnected(mContext)){
                                error_tips = mContext.getResources().getString(R.string.error_service_exception);
                            }else{
                                error_tips = mContext.getResources().getString(R.string.error_network_exception);
                            }
                            ToastUtils.showToast(mContext,error_tips);
                            Utils.print(tag,"2error="+e.getMessage());
                        }catch (Exception ee){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag,"2status=="+commodityData.getErrorMessage());

                        if(commodityData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,commodityData.getErrorMessage());
                            return;
                        }

                        List<CommodityRecored> list = new ArrayList<CommodityRecored>();
                        if(commodityData.getData()!=null){
                            list = commodityData.getData().getRecords();
                        }

                        searchMainAdapter.addData(list);
                        size = size + list.size();
                        op_status = false;
                    }
                });

        addSubscription(s);
    }

    /**
     * 判断是否为最后一页
     * @return
     */
    private boolean isLastLine(){
        boolean isLastLine = false;
        int x = (searchMainAdapter.getItemCount()-1)%4;
        Utils.print(tag,"x="+x+",pos=="+pos+",size="+searchMainAdapter.getItemCount());
        if(x==0){
            //处理最后四个
            if(pos>searchMainAdapter.getItemCount()-1-1-4){
                isLastLine = true;
            }
        }else{
            //处理最后剩余的几个
            if(pos>searchMainAdapter.getItemCount()-1-1-x){
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


    @Override
    public void OnItemViewSelectedListener(int position) {
        Utils.print(tag,"pos==="+position);
        searchMainAdapter.setSelectItemId(position);
        pos = position;


        if(position>ConStant.END_SIZE && position>=size-ConStant.END_SIZE){
            Utils.print(tag,"append data");
            if(!op_status){
                op_status = true;
                pageIndex++;
                if(pageIndex>maxPage)
                    return;
                getSearchMoreRecommendData();
            }
        }
    }


    public View getSearchKey(){
        if(searchMainAdapter!=null && searchMainAdapter.getmHeaderView()!=null){
            return searchMainAdapter.getmHeaderView().findViewById(R.id.search_key);
        }
        return null;
    }


    public HiveRecyclerView getmTvRecyclerView(){
        return mTvRecyclerView;
    }

    public EditText getSearchKeyView(){
        return (EditText)searchMainAdapter.getmHeaderView().findViewById(R.id.search_key);
    }


    BroadcastReceiver nettyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msgJson = intent.getStringExtra("msgJson");
            Utils.print(tag, "netty>>>" + msgJson);
            Gson gson = new Gson();
            DomySearchTcpMsgBodyVo domyTcpMsgBodyVo = gson.fromJson(msgJson, DomySearchTcpMsgBodyVo.class);
            DomySearchTcpMsgVo info = domyTcpMsgBodyVo.getInfo().get(0);
            if(info.getActionType()==ConStant.ACTION_TYPE_SEARCH && info.getContentId().equals(ConStant.NETTY_OP_SUCESS)){
                Utils.print(tag,"qr search edit");
                Utils.print(tag,"key="+domyTcpMsgBodyVo.getInfo().get(0).getSearch());
                OpSearchKey opSearchKey = new OpSearchKey();
                opSearchKey.setKey(ConStant.SEARCH_RESULT);

                opSearchKey.setValue(Utils.formatInvalidString(domyTcpMsgBodyVo.getInfo().get(0).getSearch().trim()));
                RxBus.get().post(ConStant.obString_opt_search_natigation,opSearchKey);
            }
        }
    };

    private void regeditReceiver() {
        Utils.print(tag,"regeditReceiver netty");
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConStant.ACTION_NETTRY_RECEIVER);
        mContext.registerReceiver(nettyReceiver, mFilter);
    }



    /**
     * 获取搜索二维码地址
     */
    public void getSearchQr(final SimpleDraweeView qrImg) {
        Utils.print(tag, "getSearchQr");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetSearchQr(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<QrData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(QrData qrData) {
                        Utils.print(tag, "status==" + qrData.getErrorMessage() + ",value=" + qrData.getReturnValue());
                        if (qrData.getReturnValue() == -1)
                            return;

                        Utils.print(tag, "qr==" + qrData.getData().getQrData());
                        Utils.print(tag, "qr==" + qrData.getData().getTimeStamp());

                        qrImg.setBackground(new QrcodeUtil().generateQRCode(mContext,qrData.getData().getQrData()));
                        ((TextView)searchMainAdapter.getmHeaderView().findViewById(R.id.qr_text)).setVisibility(View.VISIBLE);
                    }
                });
    }

}
