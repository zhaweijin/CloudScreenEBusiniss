package com.hiveview.dianshang.collection;

import android.animation.AnimatorSet;
import android.content.Context;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.CollectionItemAdapter;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.ConfirmDialog;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.collection.CollectionData;
import com.hiveview.dianshang.entity.collection.CollectionRecord;
import com.hiveview.dianshang.entity.token.TokenData;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.utils.DeviceInfo;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.HiveGridLayoutManager;
import com.hiveview.dianshang.view.HiveRecyclerView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by carter on 4/24/17.
 */

public class CollectionMain extends BaseFragment implements OnItemViewSelectedListener,OnItemKeyListener{


    private String tag ="collection";

    @BindView(R.id.tv_recycler_view)
    HiveRecyclerView mTvRecyclerView;

    /**
     * 无收藏记录的提示
     */
    @BindView(R.id.layout_tips)
    View layout_tips;
    /**
     * 无收藏记录的提示
     */
    @BindView(R.id.tips_text)
    TextView tips_text;

    private Observable<String> remove_observer;

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


    CollectionItemAdapter collectionItemAdapter;

    /**
     * 操作状态标志
     */
    private boolean op_status=false;

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

    private long time1;

    //清空确认对话框
    private ConfirmDialog confirmDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.collection;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(mTvRecyclerView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }
        RxBus.get().post(ConStant.obString_reset_nav_focus,1);  //每次恢复导航栏焦点

        itemSpace = mContext.getResources().getDimensionPixelSize(R.dimen.item_view_div_8);
        marign_buttom = mContext.getResources().getDimensionPixelSize(R.dimen.recycle_marign_buttom_30);

        String user = (String) SPUtils.get(mContext, ConStant.USERID, "");
        String token = (String) SPUtils.get(mContext, ConStant.USER_TOKEN, "");
        startProgressDialog();
        if (!user.equals("") && !token.equals("")) {
            Utils.print(tag, "set token");
            ConStant.userID = user;
            ConStant.Token = token;
            getAllFavoriteData();
        }else{
            getTokenData();
        }


        tips_text.setText(getResources().getString(R.string.collection_item_null_tips));
        initEvent();
    }


    public void initEvent() {
        remove_observer = RxBus.get().register(ConStant.obString_remove_collection_item, String.class);
        remove_observer.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Utils.print(tag,"remove goodsn=="+s);
                CollectionRecord record=null;
                for (int i = 0; i < collectionItemAdapter.getmDatas().size(); i++) {
                    if(collectionItemAdapter.getmDatas().get(i).getGoodsSn().equals(s)){
                        record = collectionItemAdapter.getmDatas().get(i);
                    }
                }
                if(record==null){
                    return;
                }
                updateAdapter(record);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(ConStant.obString_remove_collection_item,remove_observer);
        if(collectionItemAdapter!=null){
            collectionItemAdapter.unRegisterObserver();
        }
        Utils.print(tag,"onDestroy");
        FeedbackTranslationAnimatorUtil.getInstance().recycle();
    }

    /**
     * 获取所有收藏商品
     */
    public void getAllFavoriteData(){
        Utils.print(tag,"getAllFavoriteData");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }


        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetAllFavoriteData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                        stopProgressDialog();
                        handleNetworkException();

                    }

                    @Override
                    public void onNext(CollectionData collectionData) {
                        Utils.print(tag,"status=="+collectionData.getErrorMessage()+",returnValue="+collectionData.getReturnValue());

                        if(collectionData.getReturnValue()==-1){
                            stopProgressDialog();
                            ToastUtils.showToast(mContext,collectionData.getErrorMessage());
                            return;
                        }

                        if(collectionData.getReturnValue()==-2){   //token已经失效,再次请求
                            getTokenData();
                            return;
                        }

                        if(collectionData.getData()==null){
                            stopProgressDialog();
                            mTvRecyclerView.setVisibility(View.INVISIBLE);
                            layout_tips.setVisibility(View.VISIBLE);
                            return;
                        }
                        List<CollectionRecord> collectionRecord = collectionData.getData().getRecords();
                        /*for(int i=0;i<collectionRecord.size();i++){
                            Utils.print(tag,"s=="+collectionRecord.get(i).getGoodsName());
                        }*/

                        size = collectionData.getData().getRecords().size();
                        if(collectionData.getData().getRecords().size()>0){
                            totalCount = collectionData.getData().getTotalCount();
                            maxPage = totalCount/ConStant.PAGESIZE;
                            if(totalCount%ConStant.PAGESIZE!=0){
                                maxPage++;
                            }
                            Utils.print(tag,"totalcount="+totalCount+",maxpage="+maxPage);

                        }

                        init(collectionRecord);
                        stopProgressDialog();

                    }
                });
        addSubscription(s);
    }



    private void init(List<CollectionRecord> collectionRecords) {

        //设置动画
        DefaultItemAnimator animator = new DefaultItemAnimator();
        mTvRecyclerView.setItemAnimator(animator);
        mTvRecyclerView.getItemAnimator().setChangeDuration(0);
        ((SimpleItemAnimator)mTvRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTvRecyclerView.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, marign_buttom);
            mTvRecyclerView.setLayoutParams(layoutParams);
        }else{
            mTvRecyclerView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        }


        //初始化适配器
        collectionItemAdapter = new CollectionItemAdapter(mContext,collectionRecords,this,this);

        //设置管理器
        manager = new HiveGridLayoutManager(mContext, 4);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.supportsPredictiveItemAnimations();
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(collectionItemAdapter.isHeader(position)){
                    return manager.getSpanCount();
                }else {
                    return 1;
                }
            }
        });
        //初始化头
        View headview = LayoutInflater.from(mContext).inflate(R.layout.collection_head, null);
        collectionItemAdapter.setmHeaderView(headview);
        updateHead();


        //recyclerview 设置属性
        mTvRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int postion = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
                //Utils.print(tag,"addItemDecoration pos=="+postion);
                if (collectionItemAdapter.isHeader(postion)) {
                    outRect.set(0, 0, 0, 0);
                }else {
                    outRect.set(itemSpace, itemSpace, itemSpace, 0);
                }
            }
        });
        mTvRecyclerView.setLayoutManager(manager);
        mTvRecyclerView.setAdapter(collectionItemAdapter);
    }



    /**
     * 网络状态提示
     */
    private void handleNetworkException(){
        try{
            String error_tips = "";
            if(Utils.isConnected(mContext)){
                error_tips = mContext.getResources().getString(R.string.error_service_exception);
            }else{
                error_tips = mContext.getResources().getString(R.string.error_network_exception);
            }
            ToastUtils.showToast(mContext,error_tips);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 获取更多收藏商品
     */
    public void getMoreFavoriteData(){
        Utils.print(tag,"getMoreFavoriteData");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetAllFavoriteData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        handleNetworkException();
                        load_more_status = false;
                    }

                    @Override
                    public void onNext(CollectionData collectionData) {
                        Utils.print(tag,"status=="+collectionData.getErrorMessage());
                        if(collectionData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,collectionData.getErrorMessage());
                            return;
                        }

                        List<CollectionRecord> collectionRecord = collectionData.getData().getRecords();
                        /*for(int i=0;i<collectionRecord.size();i++){
                            Utils.print(tag,"s=="+collectionRecord.get(i).getGoodsName());
                        }*/

                        size = size + collectionData.getData().getRecords().size();
                        collectionItemAdapter.addData(collectionRecord);
                        load_more_status = false;
                    }
                });
        addSubscription(s);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    /**
     * 判断是否为最后一行
     * @return
     */
    private boolean isLastLine(){
        boolean isLastLine = false;
        int x = (collectionItemAdapter.getItemCount()-1)%4;
        Utils.print(tag,"x="+x+",pos=="+pos+",size="+collectionItemAdapter.getItemCount());
        if(x==0){
            //处理最后四个
            if(pos>collectionItemAdapter.getItemCount()-1-1-4){
                isLastLine = true;
            }
        }else{
            //处理最后剩余的几个
            if(pos>collectionItemAdapter.getItemCount()-1-1-x){
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
        Utils.print(tag,"OnItemViewSelectedListener......"+position);

        collectionItemAdapter.setSelectItemId(position);
        pos = position;

        RxBus.get().post(ConStant.obString_reset_nav_focus,0); //请求导航栏无法获取焦点

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
                getMoreFavoriteData();
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //Utils.print(tag, "is key");
        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (isLastLine()) {
                Utils.print(tag, "is last page");
                if (null != feedbackAnimator && !feedbackAnimator.isRunning()) {
                    Utils.print(tag, "start animation");
                    feedbackAnimator.start();
                }
                return true;
            }
        }else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
            if (isFirstLine() && !collectionItemAdapter.getEditMode()) {
                return true;
            }
        }else if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
            if (pos%4 == 0) {
                if(event.getAction()==KeyEvent.ACTION_DOWN){
                    RxBus.get().post(ConStant.obString_reset_nav_focus,1);  //恢复导航栏焦点
                }
                v.setNextFocusLeftId(R.id.layout_collection);
            }
        }else if(keyCode==KeyEvent.KEYCODE_MENU && event.getAction()==KeyEvent.ACTION_DOWN){
            if ((System.currentTimeMillis() - time1) < 300) {
                return true;
            }
            keycodeMenuFunction();
        }else if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            return keycodeBackFunction();
        }
        return false;
    }






    private void updateHead() {
        View itemView = collectionItemAdapter.getmHeaderView();
        LinearLayout layout_normal_mode = (LinearLayout) itemView.findViewById(R.id.layout_normal_mode);
        LinearLayout layout_edit_mode = (LinearLayout) itemView.findViewById(R.id.layout_edit_mode);
        Button all_delete = (Button)itemView.findViewById(R.id.all_delete);

        Utils.print(tag,"update head="+collectionItemAdapter.getEditMode());
        if (!collectionItemAdapter.getEditMode()) {
            layout_edit_mode.setVisibility(View.INVISIBLE);
            layout_normal_mode.setVisibility(View.VISIBLE);
            all_delete.setVisibility(View.INVISIBLE);
            all_delete.setFocusable(false);
        } else {
            layout_edit_mode.setVisibility(View.VISIBLE);
            layout_normal_mode.setVisibility(View.INVISIBLE);
            all_delete.setVisibility(View.VISIBLE);
            all_delete.setFocusable(true);
        }

        all_delete.setNextFocusLeftId(R.id.layout_collection);
        RxView.clicks(all_delete)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showClearAllDialog();
                    }
                });

        all_delete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    collectionItemAdapter.setSelectItemId(-1);
                    pos = -1;
                    all_delete.setTextColor(mContext.getResources().getColor(R.color.white));
                }else{
                    all_delete.setTextColor(mContext.getResources().getColor(R.color.black));
                }
            }
        });

        all_delete.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Utils.print(tag,"keycode==="+keyCode);
                if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                    return true;
                }else if(keyCode==KeyEvent.KEYCODE_MENU && event.getAction()==KeyEvent.ACTION_DOWN){
                    if ((System.currentTimeMillis() - time1) < 300) {
                        return true;
                    }
                    keycodeMenuFunction();
                }else if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
                    return keycodeBackFunction();
                }else if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT && event.getAction()==KeyEvent.ACTION_DOWN){
                    RxBus.get().post(ConStant.obString_reset_nav_focus,1);  //每次恢复导航栏焦点
                }
                return false;
            }
        });
    }

    /**
     * Menu功能键作用
     */
    private void keycodeMenuFunction(){

        time1 = System.currentTimeMillis();

        //Utils.print(tag,"size="+collectionItemAdapter.getItemCount());

        if(!collectionItemAdapter.getEditMode()){
            collectionItemAdapter.setEditMode(true);
            collectionItemAdapter.notifyItemRangeChanged(0,collectionItemAdapter.getItemCount());
        }else{
            collectionItemAdapter.setEditMode(false);
            collectionItemAdapter.notifyItemRangeChanged(0,collectionItemAdapter.getItemCount());
        }
        updateHead();
    }


    /**
     * 处理返回键功能
     * @return
     */
    private boolean keycodeBackFunction(){
        if(collectionItemAdapter.getEditMode()){
            collectionItemAdapter.setEditMode(false);
            updateHead();
            collectionItemAdapter.notifyItemRangeChanged(0,collectionItemAdapter.getItemCount());
            return true;
        }else{
            return false;
        }
    }


    /**
     * 删除收藏的商品
     */
    public void deleteFavoriteCommodity(CollectionRecord record){
        Utils.print(tag,"deleteFavoriteCommodity");

        if(op_status)
            return;
        op_status = true;
        //开启进度条
        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("goodsSn",record.getGoodsSn());
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
                        handleNetworkException();
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            stopProgressDialog();
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            op_status = false;
                            return;
                        }

                        updateAdapter(record);
                    }
                });
        addSubscription(s);
    }


    /**
     * 刷新收藏数据
     */
    private void updateAdapter(CollectionRecord record){
        int index = collectionItemAdapter.getmDatas().indexOf(record);
        Utils.print(tag,"index=="+index);
        collectionItemAdapter.getmDatas().remove(index);
        collectionItemAdapter.notifyItemRemoved(index+1);

        Utils.print(tag,"size="+collectionItemAdapter.getmDatas().size());
        if(collectionItemAdapter.getmDatas().size()==0){
            RxBus.get().post(ConStant.obString_reset_nav_focus,1);  //每次恢复导航栏焦点
            showNoCollection();
        }

        stopProgressDialog();
        op_status = false;
    }


    private void showNoCollection() {
        try {
            ((MainActivity) mContext).findViewById(R.id.layout_collection).requestFocus();
            mTvRecyclerView.setVisibility(View.INVISIBLE);
            layout_tips.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取收藏商品的个数
     * @return
     */
    public int getCollectionSize(){
        if(mTvRecyclerView!=null){
            return mTvRecyclerView.getChildCount();
        }
        return 0;
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

                        getAllFavoriteData();

                    }
                });
        addSubscription(s);
    }



    /**
     * 删除收藏的所有商品
     */
    public void deleteAllFavoriteCommodity() {
        Utils.print(tag, "deleteAllFavoriteCommodity");
        if(op_status)
            return;
        op_status = true;
        startProgressDialog();
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

        Subscription sb = RetrofitClient.getCommodityAPI()
                .httpDeleteAllFavorite(input, ConStant.getInstance(mContext).Token)
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
                        handleNetworkException();
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        stopProgressDialog();
                        if (statusData.getReturnValue() == -1){
                            op_status = false;
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        pos=0;
                        RxBus.get().post(ConStant.obString_reset_nav_focus,1);  //每次恢复导航栏焦点
                        showNoCollection();
                        op_status = false;
                    }
                });
        addSubscription(sb);
    }


    /**
     * 删除所有提示框
     */
    private void showClearAllDialog(){

        //检测网络状态
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        //正在交互
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            return;

        confirmDialog = new ConfirmDialog(mContext, new ConfirmDialog.ConfirmOnClickListener() {
            @Override
            public void onOk() {
                Utils.print(tag,"onOk");
                deleteAllFavoriteCommodity();
                confirmDialog.dismiss();
            }

            @Override
            public void onCancel() {
                Utils.print(tag,"onCancel");
                confirmDialog.dismiss();
            }

            @Override
            public void onDismiss() {
                Utils.print(tag,"onDismiss");
            }
        });
        confirmDialog.setTitle(mContext.getResources().getString(R.string.delete_all_favorite_tips));
        confirmDialog.setTitleSize(mContext.getResources().getDimensionPixelSize(R.dimen.sp_32));
        confirmDialog.setOkButton(mContext.getResources().getString(R.string.ok));
        confirmDialog.setCancelButton(mContext.getResources().getString(R.string.cancel));
        confirmDialog.showUI();
    }
}
