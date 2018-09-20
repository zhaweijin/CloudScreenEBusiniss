
package com.hiveview.dianshang.auction;

import android.animation.AnimatorSet;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.AcutionItemAdapter;
import com.hiveview.dianshang.auction.database.AcutionBean;
import com.hiveview.dianshang.auction.database.AcutionDao;
import com.hiveview.dianshang.base.AcutionListener;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.base.OnLongKeyUpListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.UnpayAcutionDialog;
import com.hiveview.dianshang.entity.InputSignBean;
import com.hiveview.dianshang.entity.acution.addprice.AddPriceData;
import com.hiveview.dianshang.entity.acution.common.AcutionItemData;
import com.hiveview.dianshang.entity.acution.info.AcutionInfo;
import com.hiveview.dianshang.entity.acution.listdata.AcutionCommodityData;
import com.hiveview.dianshang.entity.acution.time.ServerTimeData;
import com.hiveview.dianshang.entity.acution.unpay.order.DomyAuctionOrderVo;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderData;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.shoppingcart.ShoppingCartList;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.HiveRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hiveview.dianshang.utils.httputil.RetrofitClient.getAcutionLauAPI;
import static com.hiveview.dianshang.utils.httputil.RetrofitClient.getCommodityAPI;


/**
 * Created by carter on 4/24/17.
 */

public class AcutionCommodity extends BaseFragment implements OnItemKeyListener, OnItemViewSelectedListener,AcutionListener,AcutionItemAdapter.ItemClick,OnLongKeyUpListener{


    private String tag = "AcutionCommodity";
    /*****竞价逻辑**********************
     *
     * 1. 客户端出价，把最后一次出价的商品sn,价格保存到文件
     * 2. 客户端第二次出价，判断最后一次价格的商品，服务器价格
     * （1）如果能获取服务器价格（没有下线），那么允许客户端继续出价
     * （2）如果无法获取服务器价格（下线了），那么判断服务器是否有未支付的订单，如果有禁止竞价，如果没有允许继续出价
     *
     *
     */

    /**
     * 拍卖商品列表
     */
    @BindView(R.id.tv_recycler_view)
    HiveRecyclerView mTvRecyclerView;


    //layout right
    @BindView(R.id.layout_info)
    LinearLayout layout_info;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.layout_left_focus)
    ImageView layout_left_focus;

    /**
     * 按确认键进入详情提示信息
     */
    @BindView(R.id.tips11)
    TextView tips11;

    @BindView(R.id.tip33)
    TextView tip33;

    /**
     * 无拍卖记录的提示
     */
    @BindView(R.id.layout_tips)
    View layout_tips;
    /**
     * 无拍卖记录的提示
     */
    @BindView(R.id.tips_text)
    TextView tips_text;

    @BindView(R.id.layout_right_tips)
    RelativeLayout layout_right_tips;

    /**
     * 商品名称
     */
    @BindView(R.id.name)
    TextView name;

    /**
     * 商品规格信息
     */
    @BindView(R.id.types)
    LinearLayout types;

    /**
     * 当前竞拍价
     */
    @BindView(R.id.new_price)
    TextView new_price;

    /**
     * 原价
     */
    @BindView(R.id.old_price)
    TextView old_price;

    /**
     * 加价幅度
     */
    @BindView(R.id.add_price)
    TextView add_price;

    /**
     * 快递价格
     */
    @BindView(R.id.freight_price)
    TextView freight_price;

    /**
     * 详情描述
     */
    @BindView(R.id.description)
    TextView description;

    /**
     * 详情图顶部
     */
    @BindView(R.id.top_description_icon)
    SimpleDraweeView top_description_icon;

    /**
     * 动态添加详情图
     */
    @BindView(R.id.layout_description_icon)
    LinearLayout layout_description_icon;

    /**
     * 右侧整体滚动layout
     */
    @BindView(R.id.layout_right_global)
    LinearLayout layout_right_global;

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

    private int update_commodity_size=0;

    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;

    private long test_time;




    /**
     * 获取最新数据
     */
    private Subscription top_sc;

    /**
     * 获取单个商品
     */
    private Subscription loop_data_sc;

    /**
     * 进度条订阅
     */
    private Subscription loop_progress_sc;

    private Subscription getmore_sc;

    /**
     * 是否正在初始化
     */
    private boolean initing=false;

    private String oldFocusGoodsSn="";

    /**
     * 向上面请求最新数据的标示
     */
    private boolean load_top_status=false;

    /**
     * 获取未支付数据
     */
    private Subscription unpay_sc;

    /**
     * 加价获取数据
     */
    private Subscription add_price_sc;

    /**
     * 竞价
     */
    private Subscription bid_sc;

    /**
     * 适配器
     */
    private AcutionItemAdapter itemAdapter;

    /**
     * 列表顶部提示view
     */
    private View headview;


    private boolean recycleViewHasFocus = false;

    /**
     * 当前item位置
     */
    private int pos;

    /**
     * 检测焦点是否在拍卖页面
     */
    private boolean focusIsAcution=false;



    private long time1;

    /**
     * 分页数据加载标示
     */
    private boolean load_more_status = false;


    /**
     * recyclerview layout
     */
    private GridLayoutManager manager;

    /**
     * recyclerview item 间距
     */
    private int itemSpace;

    private int marign_buttom;

    private Subscription sc_global;

    private UnpayAcutionDialog unpayAcutionDialog;
    private DomyAuctionOrderVo acutionData;

    /**
     * 操作状态标志
     */
    private boolean op_status=false;


    /**
     * 初始化，以及不需要滚动的时候，请求焦点，标志
     */
    private boolean initRequest = false;


    private boolean scrollRequest =false;

    /**
     * 已经获取到了未支付订单的数据，为了再次获取时做判断依据
     */
    private boolean unpayDataGetFinished = false;

    /**
     * 服务器与客户端时间差
     */
    private long TIME_DIV=0;


    private long resetTime=0;

    private final int NOTIFI_ADAPTER = 0x22;
    private final int HANDLE_END_STATUS = 0x33;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                /*case NOTIFI_ADAPTER:
                    if(itemAdapter!=null){
                        itemAdapter.notifyGlobalUpdate();
                        //Utils.print(tag,"recycle focus="+recycleViewIsFocus());
                        if(recycleViewHasFocus && !recycleViewIsFocus()){
                            Utils.print(tag,"recycle focus@@");
                            resetFocus();
                        }
                    }
                    break;*/
                case HANDLE_END_STATUS:
                    handleJoinEnd();
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.acution_commodity;
    }


    //列表数据获取完成后，再检测未支付订单接口
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(mTvRecyclerView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }

        RxBus.get().post(ConStant.obString_reset_nav_focus,1);  //恢复导航栏焦点

        tips_text.setText(getResources().getString(R.string.acution_commotity_null));
        Utils.print(tag,"start");

        List<AcutionBean> list = AcutionDao.getInstance(mContext).querySn("sssss");
        Utils.print(tag,"list size----"+list.size());

        itemSpace = mContext.getResources().getDimensionPixelSize(R.dimen.item_view_div_8);
        marign_buttom = mContext.getResources().getDimensionPixelSize(R.dimen.recycle_marign_buttom_30);

        layout_info.setVisibility(View.INVISIBLE);


        mTvRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Utils.print(tag,"view finished....");
                if(mTvRecyclerView==null)
                    return;
                if(initRequest){
                    Utils.print(tag,"init request focus pos=="+pos+",childsize="+mTvRecyclerView.getChildCount());
                    initRequest = false;

                    int requestPos = 0;
                    for (int i = 0; i < itemAdapter.getListDatas().size()-1; i++) {
                        if(oldFocusGoodsSn.equals(itemAdapter.getListDatas().get(i).getAuctionStatusVo().getGoodsSn())){
                            Utils.print(tag,"scroll position="+i);
                            requestPos=i;
                            break;
                        }
                    }
                    if(requestPos<4){
                        View view = mTvRecyclerView.getChildAt(requestPos+1);
                        if(view!=null){
                            view.requestFocus();
                        }
                    }else{
                        Utils.print(tag,"scroll to "+requestPos);
                        mTvRecyclerView.scrollToPosition(requestPos+1);
                        scrollRequest=true;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                View view = mTvRecyclerView.getChildAt(3);
                                if(view!=null){
                                    Utils.print(tag,"last=requestFocus===");
                                    view.requestFocus();
                                }
                            }
                        },200);
                    }


                    /*if(mTvRecyclerView.getChildAt(1)!=null){
                        mTvRecyclerView.getChildAt(1).requestFocus();
                        if(unpayAcutionDialog!=null && unpayAcutionDialog.isShowing()){
                            unpayAcutionDialog.dismiss();
                        }
                    }*/
                }
            }
        });


        /*mTvRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Utils.print(tag, "status==" + newState + ",s=" + scrollRequest);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && scrollRequest) {
                    scrollRequest = false;

                    mTvRecyclerView.scrollToPosition(itemAdapter.getItemCount()-1);
                    scrollRequest=true;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            View view = mTvRecyclerView.getChildAt(3);
                            if(view!=null){
                                Utils.print(tag,"last=requestFocus===");
                                view.requestFocus();
                            }
                        }
                    },200);

                   *//* View view = mTvRecyclerView.getChildAt(3);
                    if(view!=null){
                        Utils.print(tag,"last=requestFocus===");
                        view.requestFocus();
                    }*//*
                }
            }
        });*/

        handleEventAcutionInformation();
        getAcutionListData();


    }


    /**
     * 刷新列表数据后，持续判断焦点的位置，如果那个有焦点就请求
     */
    private void loopRequestFocus(){
        Utils.print(tag,"recycle view isFocus="+recycleViewIsFocus());
        resetTime = System.currentTimeMillis();
        initRequest=true;
    }




    /**
     * recycleview 是否落焦点
     * @return
     */
    private boolean recycleViewIsFocus(){
        if(mTvRecyclerView==null)
            return false;
        boolean focus =false;
        for (int i = 0; i < mTvRecyclerView.getChildCount(); i++) {
            if(mTvRecyclerView.getChildAt(i)!=null && mTvRecyclerView.getChildAt(i).isFocused()){
                focus = true;
                break;
            }
        }
        return focus;
    }

    @Override
    public void onResume() {
        super.onResume();
        layout_description_icon.removeAllViews();

        Utils.print(tag,"onResume");
        //从其他页面返回
        loopGetAllCommodity();
        loopGetData();
//        loopProgress();
    }



    /**
     * 初始化数据
     * @param acutionItemDatas
     */
    private void init(List<AcutionItemData> acutionItemDatas) {

        Utils.print(tag,"real size="+acutionItemDatas.size());
        mTvRecyclerView.setItemAnimator(null);
        //初始化适配器

        itemAdapter = new AcutionItemAdapter(mContext,acutionItemDatas,this,this,this,this,ConStant.HOME_TO_INFO);
        itemAdapter.setHasStableIds(true);
        itemAdapter.setOnLongKeyUpListener(this);

        //设置管理器
        manager = new GridLayoutManager(mContext, 1);
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
        headview = LayoutInflater.from(mContext).inflate(R.layout.acution_head, null);
        headview.setVisibility(View.INVISIBLE);
        itemAdapter.setmHeaderView(headview);


        //recyclerview 设置属性
        mTvRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int postion = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
                if (itemAdapter.isHeader(postion)) {
                    outRect.set(0, 0, 0, 0);
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && (postion==itemAdapter.getItemCount()-1) && pageIndex >= maxPage) {
                    outRect.set(0, itemSpace, 0, marign_buttom);
                } else {
                    outRect.set(0, itemSpace, 0, 0);
                }
            }
        });
        mTvRecyclerView.setLayoutManager(manager);
        mTvRecyclerView.setAdapter(itemAdapter);
        Utils.print(tag,"item adapder size="+itemAdapter.getItemCount());


        if(acutionItemDatas.size()>0){
            switchInformationData();
        }
    }


    /**
     * 刷新数据
     */
    private void setItemAdapter(List<AcutionItemData> acutionItemDatas){
        //初始化适配器
        Utils.print(tag,"setItemAdapter");
        itemAdapter = new AcutionItemAdapter(mContext,acutionItemDatas,this,this,this,this,ConStant.HOME_TO_INFO);
        itemAdapter.setHasStableIds(true);
        itemAdapter.setOnLongKeyUpListener(this);
        //初始化头
        headview = LayoutInflater.from(mContext).inflate(R.layout.acution_head, null);
        headview.setVisibility(View.INVISIBLE);
        itemAdapter.setmHeaderView(headview);
        mTvRecyclerView.setAdapter(itemAdapter);
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            return true;
        }

        Utils.print(tag, "is key=="+event.getAction()+"keycode="+keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

            if (pos==itemAdapter.getItemCount()-1) {
                Utils.print(tag, "is last page");
                if (null != feedbackAnimator && !feedbackAnimator.isRunning()) {
                    Utils.print(tag, "start animation");
                    feedbackAnimator.start();
                }
                return true;
            }
        }else if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
            Utils.print(tag,"left.......");
            v.setNextFocusLeftId(R.id.layout_acution);
            RxBus.get().post(ConStant.obString_reset_nav_focus,1);  //恢复导航栏焦点
        }else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
            if (pos==0) {
                return true;
            }
        }else if(keyCode==KeyEvent.KEYCODE_DPAD_CENTER && event.getAction()==KeyEvent.ACTION_DOWN){
            //getAcutionTopData(true,pos);
        }

        if(event.getAction()== KeyEvent.ACTION_DOWN && (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT || keyCode==KeyEvent.KEYCODE_DPAD_LEFT)){
            Utils.print(tag,"right.......");
            headview.setVisibility(View.INVISIBLE);
            recycleViewHasFocus=false;
        }

        return false;
    }





    /**
     * 获取拍卖商品列表
     */
    public void getAcutionListData(){
        if(layout_info==null)
            return;
        Utils.print(tag,"getAcutionListData");
        Utils.print(tag,"recycleViewIsFocus="+recycleViewIsFocus()+",layout_info focus="+layout_info.isFocused());
        if(recycleViewIsFocus() || layout_info.isFocused()){
            focusIsAcution = true;
        }


        //检测网络状态
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        startProgressDialog();

        initing = true;
        Subscription sc = getCommodityAPI()
                .httpGetAcutionServerTime(Utils.getAcutionUnpayInput(mContext).getInput(),Utils.getAcutionUnpayInput(mContext).getSign())
                .flatMap(new Func1<ServerTimeData, Observable<AcutionCommodityData>>() {
                    @Override
                    public Observable<AcutionCommodityData> call(ServerTimeData serverTimeData) {
                        if(serverTimeData.getReturnValue()!=-1){
                            long local = new Date(System.currentTimeMillis()).getTime();
                            long server = serverTimeData.getData().getTimeStamp();
                            Utils.print(tag,"local time=="+Utils.getDateToString(local)+",server="+Utils.getDateToString(server));
                            TIME_DIV = local-server;
                            if(TIME_DIV!=0){
                                TIME_DIV = TIME_DIV/1000;
                            }
                        }
                        Utils.print(tag,"time div=="+TIME_DIV);

                        String input="";
                        String sign="";
                        try{
                            JSONObject json = new JSONObject();
                            json.put("pageIndex", pageIndex);
                            json.put("pageSize", ConStant.ACUTION_PAGE_SIZE);
                            input = json.toString();
                            input = input.replace("{", "%7B").replace("}", "%7D");
                            Utils.print(tag, "input=" + input);


                            String domyshop_order_key = ConStant.domyshop_order_key;
                            String domyshop_value = "";
                            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
                            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
                            domyshop_value = domyshop_value.replace(" ","");
                            Utils.print(tag,""+domyshop_value);
                            sign = Utils.getMD5(domyshop_value);
                            Utils.print(tag,"sign="+sign);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        return getAcutionLauAPI().httpGetAcutionListData(input,sign);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Utils.print(tag,"get server time error");
                    }
                })
                .map(new Func1<AcutionCommodityData, AcutionCommodityData>() {
                    @Override
                    public AcutionCommodityData call(AcutionCommodityData commodityData) {
                        calculatePageSize(commodityData);
                        return handAcutionStatusData(commodityData);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AcutionCommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(!unpayDataGetFinished){
                            getAcutionUnpayData();
                        }
                        //初始化
                        Utils.print(tag,"getAcutionListData error="+e.getMessage());
                        stopProgressDialog();
                        String error_tips = "";
                        if (Utils.isConnected(mContext)) {
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        } else {
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext, error_tips);
                    }

                    @Override
                    public void onNext(AcutionCommodityData commodityData) {
                        Utils.print(tag,"getAcutionListData status=="+commodityData.getErrorMessage()+",value="+commodityData.getReturnValue());
                        if(!unpayDataGetFinished){
                            getAcutionUnpayData();
                        }

                        stopProgressDialog();
                        if(commodityData.getReturnValue()==-1){
                            //初始化
                            showNoAcutionCommotity();
                            return;
                        }

                        if(commodityData.getData().getTotalCount()<=0 || commodityData.getData().getRecords().size()==0){
                            Utils.print(tag,"recored.null");
                            //初始化
                            showNoAcutionCommotity();
                            return;
                        }
                         try {
                             //第一次与第二次区别，第二次需要获取焦点，第一次不用
                             if(itemAdapter==null){
                                 init(commodityData.getData().getRecords());
                             }else {
                                 setItemAdapter(commodityData.getData().getRecords());
                                 loopRequestFocus();
                             }
                         } catch (Exception e) {
                             e.printStackTrace();
                         }


                    }
                });
        addSubscription(sc);
    }

    /**
     * 每次与服务器交互，存在total size变化的情况，因此总的分页数也需要变化
     */
    private void calculatePageSize(AcutionCommodityData commodityData){
        if(commodityData.getData().getTotalCount()>0){
            totalCount = commodityData.getData().getTotalCount();
            maxPage = totalCount/ConStant.ACUTION_PAGE_SIZE;
            if(totalCount%ConStant.ACUTION_PAGE_SIZE!=0){
                maxPage++;
            }
            Utils.print(tag,"totalcount="+totalCount+",maxpage="+maxPage);
        }



        if(itemAdapter!=null && itemAdapter.getListDatas().size()<=ConStant.ACUTION_PAGE_SIZE && commodityData.getData().getTotalCount()>ConStant.ACUTION_PAGE_SIZE){
            //Log.v(tag,"getmore");
            //getMoreCommodity();
        }
    }


    /**
     * 处理拍卖状态角标数据
     * @param commodityData
     * @return
     */
    private AcutionCommodityData handAcutionStatusData(AcutionCommodityData commodityData){
        if (commodityData.getReturnValue() != -1 && commodityData.getData().getTotalCount()>0) {
            List<AcutionItemData> itemDatas = commodityData.getData().getRecords();
            for (int i = 0; i < itemDatas.size(); i++) {
                String sn = itemDatas.get(i).getAuctionStatusVo().getAuctionSn();
                if (AcutionDao.getInstance(mContext).querySn(sn).size() > 0) {
                    //已经参拍
                    itemDatas.get(i).setJoinAcution(true);
                }
                //Utils.print(tag,"replace acutionSN=="+itemDatas.get(i).getAuctionVo().getAuctionSn()+",name="+itemDatas.get(i).getAuctionVo().getGoodsName());
            }
        }
        return commodityData;
    }


    /**
     * 获取更多所有商品的信息
     */
    public void getMoreCommodity(){
        Utils.print(tag,"getMoreCommodity");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        pageIndex++;
        if(pageIndex>maxPage){
            pageIndex=1;
        }

        if(getmore_sc!=null && getmore_sc.isUnsubscribed()){
            getmore_sc.unsubscribe();
        }

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", pageIndex);
            json.put("pageSize", ConStant.ACUTION_PAGE_SIZE);
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

        getmore_sc = getAcutionLauAPI()
                .httpGetAcutionListData(input,sign)
                .map(new Func1<AcutionCommodityData, Integer>() {
                    @Override
                    public Integer call(AcutionCommodityData commodityData) {
                        Utils.print(tag,"get more status=="+commodityData.getReturnValue()+",message="+commodityData.getErrorMessage());
                        if (commodityData.getReturnValue() != -1 && commodityData.getData().getTotalCount()>0) {
                            List<AcutionItemData> itemDatas = commodityData.getData().getRecords();
                            for (int i = 0; i < itemDatas.size(); i++) {
                                String sn = itemDatas.get(i).getAuctionStatusVo().getAuctionSn();
                                if (AcutionDao.getInstance(mContext).querySn(sn).size() > 0) {
                                    //已经参拍
                                    itemDatas.get(i).setJoinAcution(true);
                                }
                                //Log.v(tag, "more data name=" + itemDatas.get(i).getAuctionVo().getGoodsName()+",price==="+itemDatas.get(i).getAuctionStatusVo().getCurrentPrice());
                            }
                        }

                        if(commodityData.getReturnValue()==-1){
                            return -1;
                        }

                        calculatePageSize(commodityData);
                        addToTailMoreData(commodityData.getData().getRecords());
                        return 0;
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
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
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        load_more_status=false;
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(Integer status) {
                        load_more_status=false;
                        if(status>=0){
                            Utils.print(tag,"notifyGlobalUpdate");
                            itemAdapter.notifyGlobalUpdate();
                        }

                        if(itemAdapter!=null && itemAdapter.getListDatas().size()<=0){
                            showNoAcutionCommotity();
                        }
                    }
                });
    }


    /**
     * 添加服务器最新的商品到列表尾部
     * @param commodityData
     */
    private void addToTailMoreData(List<AcutionItemData> recored){
        if(recored==null) {
            return;
        }
        List<AcutionItemData> mDatas = itemAdapter.getListDatas();
        if(mDatas.size()<=0){
            pos=-1;
            itemAdapter.addAllData(recored);
            return;
        }
        //替换数据
        for (int i = 0; i < recored.size(); i++) {
//          Utils.print(tag,"i==="+i);
            //Log.v(tag, "top data name=" + recored.get(i).getAuctionVo().getGoodsName()+",price==="+recored.get(i).getAuctionStatusVo().getCurrentPrice());
            for (int j = 0; j < mDatas.size(); j++) {
//              Utils.print(tag,"j=="+j);
                if (recored.get(i).getAuctionStatusVo().getGoodsSn().equals(mDatas.get(j).getAuctionStatusVo().getGoodsSn())) {
                    recored.get(i).setProgressValue(mDatas.get(j).getProgressValue());
                    itemAdapter.replaceData(j, recored.get(i));
                    Utils.print(tag, "replace name=" + recored.get(i).getAuctionVo().getGoodsName() + ",pos=" + j);
                    break;
                }
                if (j == mDatas.size() - 1) {
                    itemAdapter.addData(recored.get(i));
                    Utils.print(tag, "add name=" + recored.get(i).getAuctionVo().getGoodsName());
                    break;
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(itemAdapter!=null){
            itemAdapter.unRegisterObserver();
        }

        FeedbackTranslationAnimatorUtil.getInstance().recycle();
    }



    @Override
    public void OnItemViewSelectedListener(int position) {
        headview.setVisibility(View.VISIBLE);
        recycleViewHasFocus=true;

        Utils.print(tag,"OnItemSelectedListener="+position);
        RxBus.get().post(ConStant.obString_reset_nav_focus,0);  //恢复导航栏焦点
        itemAdapter.setSelectItemId(position);

        boolean arrowUp = false;
        if(pos>position){
            arrowUp = true;
        }

        pos = position;
        switchInformationData();
        Utils.print(tag,"arrowUp=="+arrowUp);

        if(position==0){
            mTvRecyclerView.smoothScrollToPosition(0);
            Utils.print(tag,"---------"+(System.currentTimeMillis()-resetTime));
            if(arrowUp){
                getAcutionTopData(true,position);
            }
        }

        if(position>=itemAdapter.getItemCount()-ConStant.END_ACUTION_SIZE){
            Utils.print(tag,"append data load_more_status="+load_more_status);
            if(!load_more_status){
                load_more_status = true;

                getMoreCommodity();
            }
        }
    }


    private void handleEventAcutionInformation(){
        layout_info.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode==KeyEvent.KEYCODE_DPAD_UP && scrollView.getScrollY()==0){
                    return true;
                }

                if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT && event.getAction()==KeyEvent.ACTION_DOWN){
                    Utils.print(tag,"go left");
                    int visiblePos = pos-manager.findFirstVisibleItemPosition()+1;
                    Utils.print(tag,"visiblePos="+visiblePos+",first="+manager.findFirstVisibleItemPosition()+",last="+manager.findLastVisibleItemPosition());
                    View view = mTvRecyclerView.getChildAt(visiblePos);
                    if(view!=null){
                        Utils.print(tag,"request focus.....");
                        view.requestFocus();
                    }
                    headview.setVisibility(View.VISIBLE);
                }

                View childView = scrollView.getChildAt(0);
                if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN && (childView.getMeasuredHeight() <= (scrollView.getScrollY() + scrollView.getHeight()))){
                    return true;
                }
                return false;
            }
        });

        layout_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String sn = (String) layout_info.getTag();
                 if(sn!=null ){
                     AcutionInfomation.launch(getActivity(),sn,TIME_DIV);
                 }
            }
        });


        layout_info.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(sc_global!=null && sc_global.isUnsubscribed()){
                    return;
                }
                if(hasFocus){
                    layout_right_tips.setVisibility(View.VISIBLE);
                    layout_left_focus.setVisibility(View.VISIBLE);
                }else{
                    layout_right_tips.setVisibility(View.INVISIBLE);
                    layout_left_focus.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    /**
     * 循环获取数据
     */
    private void loopGetData(){
        Utils.print(tag,"loopGetData");
        if(sc_global!=null && !sc_global.isUnsubscribed())
            sc_global.unsubscribe();

        Observable<Long> ob = Observable.interval(ConStant.ACUTION_LOOP_TIME, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
        sc_global = ob.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                Utils.print(tag,".....time.....");
                loopGetAllCommodity();
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        Utils.print(tag,"onStop");
        unSubcription();
        if(itemAdapter!=null){
            itemAdapter.release();
        }
    }

    private void unSubcription(){
        /*if(sc_loop!=null && !sc_loop.isUnsubscribed()){
            Utils.print(tag,"sc loop stop");
            sc_loop.unsubscribe();
        }*/

        //全局
        if(sc_global!=null && !sc_global.isUnsubscribed()){
            Utils.print(tag,"sc_global stop");
            sc_global.unsubscribe();
        }

        //单个商品数据
        if(loop_data_sc!=null && !loop_data_sc.isUnsubscribed()){
            loop_data_sc.unsubscribe();
        }

        //获取单个商品
        if(top_sc!=null && !top_sc.isUnsubscribed()){
            top_sc.unsubscribe();
        }

        //获取未支付信息
        if(unpay_sc!=null && !unpay_sc.isUnsubscribed()){
            unpay_sc.unsubscribe();
        }

        //加价
        if(add_price_sc!=null && !add_price_sc.isUnsubscribed()){
            add_price_sc.unsubscribe();
        }

        //竞价
        if(bid_sc!=null && !bid_sc.isUnsubscribed()){
            bid_sc.unsubscribe();
        }

        //进度条监听
        if(loop_progress_sc!=null && !loop_progress_sc.isUnsubscribed()){
            Utils.print(tag,"loop_progress_sc stop");
            loop_progress_sc.unsubscribe();
        }

        //获取更多数据
        if(getmore_sc!=null && !getmore_sc.isUnsubscribed()){
            getmore_sc.unsubscribe();
        }
    }



    /**
     * 循环请求服务器接口数据
     */
    /*public void loopGetAllCommodity(){
        test_time = System.currentTimeMillis();
        Utils.print(tag,"loopGetAllCommodity start time="+ System.currentTimeMillis());
        Utils.print(tag,"recycleViewIsFocus="+recycleViewIsFocus()+",layout_info focus="+layout_info.isFocused());
        if(recycleViewIsFocus() || layout_info.isFocused()){
            focusIsAcution = true;
        }
        //检测网络状态
        if(!Utils.isConnected(mContext)){
            return;
        }

        int index=0;
        if(pos==0){
            index=1;
        }else{
            index = pos/ConStant.ACUTION_PAGE_SIZE + 1;
        }

        if(sc_loop!=null && !sc_loop.isUnsubscribed())
            sc_loop.unsubscribe();

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", index);
            json.put("pageSize", ConStant.ACUTION_PAGE_SIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            /*//********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Utils.print(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Utils.print(tag,"sign="+sign);
            /*//********
        }catch (Exception e){
            e.printStackTrace();
        }


        sc_loop = getAcutionLauAPI()
                .httpGetAcutionListData(input,sign)
                .map(new Func1<AcutionCommodityData, AcutionCommodityData>() {
                    @Override
                    public AcutionCommodityData call(AcutionCommodityData commodityData) {
                        Utils.print(tag,"loop commodity status=="+commodityData.getErrorMessage()+",value="+commodityData.getReturnValue()+",end time="+System.currentTimeMillis());
                        calculatePageSize(commodityData);

                        if (commodityData.getReturnValue() != -1 && commodityData.getData().getTotalCount()>0) {
                            List<AcutionItemData> itemDatas = commodityData.getData().getRecords();
                            for (int i = 0; i < itemDatas.size(); i++) {
                                String sn = itemDatas.get(i).getAuctionStatusVo().getAuctionSn();
                                if (AcutionDao.getInstance(mContext).querySn(sn).size() > 0) {
                                    //已经参拍
                                    itemDatas.get(i).setJoinAcution(true);
                                }
                            }
                        }

                        if(commodityData.getReturnValue()==-1){
                            return commodityData;
                        }

                        List<AcutionItemData> recored = commodityData.getData().getRecords();
                        if(recored!=null){
                            List<AcutionItemData> mDatas = itemAdapter.getListDatas();
                            //替换数据
                            for (int i = 0; i < recored.size(); i++) {
//                                Utils.print(tag,"i==="+i);
                                if(mDatas.size()==0){
                                    itemAdapter.addData(recored.get(i));
                                    Utils.print(tag,"add name="+recored.get(i).getAuctionVo().getGoodsName());
                                    continue;
                                }
                                for (int j = 0; j < mDatas.size(); j++) {
//                                    Utils.print(tag,"j=="+j);
                                    if(recored.get(i).getAuctionStatusVo().getGoodsSn().equals(mDatas.get(j).getAuctionStatusVo().getGoodsSn())){
                                        itemAdapter.replaceData(j,recored.get(i));
                                        Utils.print(tag,"replace name="+recored.get(i).getAuctionVo().getGoodsName()+",pos="+j);
                                        break;
                                    }
                                    if(j==mDatas.size()-1){
                                        itemAdapter.addData(recored.get(i));
                                        Utils.print(tag,"add name="+recored.get(i).getAuctionVo().getGoodsName());
                                        break;
                                    }
                                }
                            }
                        }
                        return commodityData;
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AcutionCommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"loop error="+e.getMessage());
                    }

                    @Override
                    public void onNext(AcutionCommodityData commodityData) {
                        if(commodityData.getReturnValue()==-1){
                            return;
                        }

                        if(commodityData.getData().getTotalCount()>0){
                            Utils.print(tag,"loopGetAllCommodity notifyGlobalUpdate");
                            layout_tips.setVisibility(View.INVISIBLE);
                            mTvRecyclerView.setVisibility(View.VISIBLE);
                            layout_right_global.setVisibility(View.VISIBLE);
                            itemAdapter.notifyGlobalUpdate();
                            initInformationData();
                        }else{
                            showNoAcutionCommotity();
                        }
                        Utils.print(tag,"update time=="+(System.currentTimeMillis()-test_time));
                    }
                });
        addSubscription(sc_loop);

    }*/



    public void loopGetAllCommodity(){
        test_time = System.currentTimeMillis();
        if(layout_info==null){
            return;
        }

        Utils.print(tag,"loopGetAllCommodity start time="+ System.currentTimeMillis());
        Utils.print(tag,"recycleViewIsFocus="+recycleViewIsFocus()+",layout_info focus="+layout_info.isFocused());
        if(recycleViewIsFocus() || layout_info.isFocused()){
            focusIsAcution = true;
        }
        //检测网络状态
        if(!Utils.isConnected(mContext)){
            return;
        }

        if(itemAdapter==null){
            return;
        }


        int index = pos; //从0开始
        int first = index-5>=0?index-5:0;
        int last = index+5>itemAdapter.getListDatas().size()-1?itemAdapter.getListDatas().size()-1:index+5;

        Utils.print(tag,"frist="+first+",last="+last); //最多获取11个数据
        update_commodity_size=0;

        if(itemAdapter!=null){
            itemAdapter.closeInactivationTimer(first,last);
        }

        for (int i = first; i <=last; i++) {
            AcutionItemData data = itemAdapter.getItemData(i);
            if(data!=null){
                String acutionSn = data.getAuctionVo().getAuctionSn();
                Utils.print(tag,"getAcutionData name==="+data.getAuctionVo().getGoodsName());
                update_commodity_size++;
                getAcutionData(acutionSn);
            }
        }
    }

    

    public InputSignBean getAcutionInput(String acutionSn){

        InputSignBean bean = new InputSignBean();
        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("auctionSn", acutionSn);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            //Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            //Utils.print(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            //Utils.print(tag,"sign="+sign);
            ///*********

            bean.setInput(input);
            bean.setSign(sign);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bean;
    }


    public void getAcutionData(String acutionSn){
        Utils.print(tag,"getAcutionInfoData sn==="+acutionSn);
        if (!Utils.isConnected(mContext)) {
            return;
        }

        if(loop_data_sc!=null && loop_data_sc.isUnsubscribed()){
            loop_data_sc.unsubscribe();
        }

        loop_data_sc = getAcutionLauAPI()
                .httpGetAcutionInfoData(getAcutionInput(acutionSn).getInput(),getAcutionInput(acutionSn).getSign())
                .map(new Func1<AcutionInfo, Integer>() {
                    @Override
                    public Integer call(AcutionInfo acutionInfo) {
                        Utils.print(tag, "loop commodity status==" + acutionInfo.getErrorMessage() + ",value=" + acutionInfo.getReturnValue() + ",end time=" + System.currentTimeMillis());

                        if (acutionInfo.getReturnValue() != -1) {
                            String sn = acutionInfo.getData().getAuctionStatusVo().getAuctionSn();
                            if (AcutionDao.getInstance(mContext).querySn(sn).size() > 0) {
                                //已经参拍
                                acutionInfo.getData().setJoinAcution(true);
                            }
                            Utils.print(tag, "update data name=" + acutionInfo.getData().getAuctionVo().getGoodsName()+",price==="+acutionInfo.getData().getAuctionStatusVo().getCurrentPrice());
                            //Log.v(tag, "update data name=" + acutionInfo.getData().getAuctionVo().getGoodsName()+",price==="+acutionInfo.getData().getAuctionStatusVo().getCurrentPrice());
                            itemAdapter.setItemData(acutionInfo.getData());
                            update_commodity_size--;
                            if(update_commodity_size==0){
                                Utils.print(tag,"no commodity offline.....");
                                warnLessCommodity();
                            }
                        }else{
                            itemAdapter.resetAcutionTime(acutionSn);
                            mHandler.removeMessages(HANDLE_END_STATUS);
                            mHandler.sendEmptyMessage(HANDLE_END_STATUS);
                        }
                        return 0;
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Utils.print(tag,"getAcutionData error");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        itemAdapter.notifyGlobalUpdate();
                        Utils.print(tag,"replace getAcutionData error="+e.getMessage());
                    }

                    @Override
                    public void onNext(Integer statusData) {
                        if (statusData != null) {
                            itemAdapter.notifyGlobalUpdate();
                        }
                        //更新价格数据
                        String sn = (String) layout_info.getTag();
                        if(sn!=null && sn.equals(acutionSn)){
                            switchInfoPrice();
                        }
                    }
                });
    }



    /**
     * 显示无拍卖商品
     */
    private void showNoAcutionCommotity(){
        Utils.print(tag,"show not commotity");
        if(itemAdapter==null){
            List<AcutionItemData> list = new ArrayList<AcutionItemData>();
            init(list);
        }


        RxBus.get().post(ConStant.obString_reset_nav_focus,1);  //每次恢复导航栏焦点
        //仅仅前一步焦点在拍卖页面才请求复位焦点，如果焦点在其他位置，则不复位
        if(focusIsAcution){
            Utils.print(tag,"request layout acution focus");
            ((MainActivity)mContext).findViewById(R.id.layout_acution).requestFocus();
        }


        layout_tips.setVisibility(View.VISIBLE);


        mTvRecyclerView.setVisibility(View.INVISIBLE);
        layout_right_global.setVisibility(View.INVISIBLE);
    }



    /**
     * 设置拍卖商品规格
     */
    private void initTypeView(ViewGroup root, String[] names) {
        root.removeAllViews();
        for (int i = 0; i < names.length; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.acution_commitity_type_description, null);
            TextView tv = (TextView) view.findViewById(R.id.type);
            TextView div_icon = (TextView) view.findViewById(R.id.div_icon);
            tv.setText(names[i]);
            if (i == names.length - 1) {
                div_icon.setVisibility(View.GONE);
            }

            if (i != 0) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
                lp.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.length_6), 0, 0, 0);
            }
            root.addView(view);
        }

    }


    /**
     * 更新详情当前价格
     */
    private void switchInfoPrice(){
        if(sc_global!=null && sc_global.isUnsubscribed()){
            return;
        }

        AcutionItemData itemData = itemAdapter.getItemData(pos);
        if(itemData==null){
            return;
        }
        new_price.setText("¥"+Utils.getPrice(itemData.getAuctionStatusVo().getCurrentPrice()));
    }



    /**
     *展示右侧的信息
     */
    private void switchInformationData(){
        if(sc_global!=null && sc_global.isUnsubscribed()){
            return;
        }

        layout_info.setVisibility(View.VISIBLE);
        AcutionItemData itemData = itemAdapter.getItemData(pos);
        if(itemData==null){
            return;
        }

        layout_info.setTag(itemData.getAuctionStatusVo().getAuctionSn());
        name.setText(itemData.getAuctionVo().getGoodsName());


        initTypeView(types,itemData.getAuctionVo().getGoodsSpec().split(","));

        new_price.setText("¥"+Utils.getPrice(itemData.getAuctionStatusVo().getCurrentPrice()));
        old_price.setText("¥"+Utils.getPrice(itemData.getAuctionVo().getOrigPrice()));

        add_price.setText("¥"+Utils.getPrice(itemData.getAuctionVo().getAddQuota()));
        freight_price.setText("¥"+Utils.getPrice(itemData.getAuctionVo().getFreight()));

        //设置商品价格横线
        old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        description.setText(itemData.getAuctionVo().getDescription());

        //详情图填充
        top_description_icon.setBackgroundResource(R.drawable.commodity_big_icon);

        if(itemData.getAuctionVo().getDetailUrl()!=null && itemData.getAuctionVo().getDetailUrl().size()>0){
            FrescoHelper.setImage(top_description_icon, Uri.parse(itemData.getAuctionVo().getDetailUrl().get(0)),new ResizeOptions(mContext.getResources().getDimensionPixelSize(R.dimen.acution_item_icon_width_150),mContext.getResources().getDimensionPixelSize(R.dimen.acution_item_height_200)));
        }

        layout_description_icon.removeAllViews();
        if(itemData.getAuctionVo().getDetailUrl()!=null && itemData.getAuctionVo().getDetailUrl().size()<=1){
            return;
        }

        if(itemData.getAuctionVo().getDetailUrl()!=null) {
            for (int i = 1; i < itemData.getAuctionVo().getDetailUrl().size(); i++) {
                SimpleDraweeView icon = new SimpleDraweeView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,mContext.getResources().getDimensionPixelSize(R.dimen.length_200));
                icon.setLayoutParams(lp);


                icon.setBackgroundResource(R.drawable.commodity_big_icon);

                FrescoHelper.setImage(icon, Uri.parse(itemData.getAuctionVo().getDetailUrl().get(i)),new ResizeOptions(mContext.getResources().getDimensionPixelSize(R.dimen.acution_item_icon_width_150),mContext.getResources().getDimensionPixelSize(R.dimen.acution_item_height_200)));
                layout_description_icon.addView(icon);
            }
        }
    }


    @Override
    public void OnItemAcutionListener(boolean status) {
        Utils.print(tag,"progress end listener");
        if(itemAdapter!=null){
            loopGetAllCommodity();
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
                        Utils.print(tag,"getAcutionUnpayData error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderData statusData) {
                        Utils.print(tag,"getAcutionUnpayData status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        unpayDataGetFinished = true;
                        if(statusData.getReturnValue()==-1){
                            return;
                        }

                        acutionData = statusData.getData();
                        Utils.print(tag,"isHaveUnpayOrder=="+statusData.getData().isHaveUnpayOrder());
                        if(acutionData.isHaveUnpayOrder()){
                            if(unpayAcutionDialog!=null && unpayAcutionDialog.isShowing()){
                                ShoppingCartList.launch(mContext,ConStant.ACUTION_PAYMENT,acutionData);
                                unpayAcutionDialog.dismiss();
                                return;
                            }
                            showUnpayDialog();
                        }
                    }
                });
    }




    /**
     * 获取拍卖未支付的订单信息
     */
    public void checkEqAcutionSnUnpayData(String acutionSn,double currentPrice){
        Utils.print(tag,"checkUnpayData");

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
                        Utils.print(tag,"1error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        if(statusData.getReturnValue()==-1){
                            return;
                        }

                        acutionData = statusData.getData();
                        Utils.print(tag,"isHaveUnpayOrder=="+statusData.getData().isHaveUnpayOrder());
                        if(acutionData.isHaveUnpayOrder()){
                            if(unpayAcutionDialog!=null && unpayAcutionDialog.isShowing()){
                                ShoppingCartList.launch(getActivity(),ConStant.ACUTION_PAYMENT,acutionData);
                                unpayAcutionDialog.dismiss();
                                return;
                            }
                            showUnpayDialog();
                            unpayDataGetFinished = true;
                        }else{
                            acutionAddPriceData(acutionSn,Utils.getPrice(currentPrice));
                        }
                    }
                });
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


    /**
     * 显示未支付提示对话框
     */
    private void showUnpayDialog(){
            if(unpayAcutionDialog==null){
                unpayAcutionDialog = new UnpayAcutionDialog(mContext,confirmOnClickListener);
            }
            if(!unpayAcutionDialog.isShowing()){
                unpayAcutionDialog.showUI();
            }
    }



    /**
     * 拍卖的商品参加竞拍
     */
    public void acutionAddPriceData(String auctionSn,String price){
        Utils.print(tag,"getAcutionAddPriceData>>>>>>>>>");

        //检测网络状态
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        if(op_status)
            return;
        op_status = true;


        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("auctionSn",auctionSn); //订单号
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
                .map(new Func1<AddPriceData, AddPriceData>() {
                    @Override
                    public AddPriceData call(AddPriceData statusData) {
                        if(statusData.getData().getRetNum()==1){
                            Utils.print(tag,"add price sucess loop get commodity");
                            loopGetAllCommodity();

                            //保存最新的sn,价格
                            SPUtils.putApply(mContext,ConStant.ACUTION_TOP_PRICE,String.valueOf(statusData.getData().getCurPrice()));
                            SPUtils.putApply(mContext,ConStant.ACUTION_TOP_SN,statusData.getData().getAuctionSn());

                            //添加出价记录到数据库
                            if(AcutionDao.getInstance(mContext).querySn(statusData.getData().getAuctionSn()).size()<=0){
                                AcutionBean bean = new AcutionBean();
                                bean.setSn(statusData.getData().getAuctionSn());
                                AcutionDao.getInstance(mContext).insert(bean);
                                Utils.print(tag,"save to database...");
                            }
                        }
                        return statusData;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AddPriceData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        op_status = false;
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
                        stopProgressDialog();
                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            op_status = false;
                            return;
                        }
                        if(statusData.getData().getRetNum()!=1){
                            Utils.print(tag,"add price failed");
                            ToastUtils.showToast(mContext,statusData.getData().getRetMsg());
                        }else{
                            ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.acution_add_price_sucess));
                        }
                        op_status = false;
                    }
                });

    }


    /**
     * 获取拍卖商品价格
     */
    public void bidAcutionPrice(String acutionSn,double currentPrice){
        Utils.print(tag,"bidAcutionPrice>>>>>>>>");
        if (!Utils.isConnected(mContext)) {
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext, error_tips);
            return;
        }

        String topAcutionSn = (String) SPUtils.get(mContext,ConStant.ACUTION_TOP_SN,"");
        if(topAcutionSn.equals("")){
            Utils.print(tag,"local no top acution sn");
            //允许继续出价
            acutionAddPriceData(acutionSn, Utils.getPrice(currentPrice));
            return;
        }

        //保存的最高价sn与当前sn一致
        if(topAcutionSn.equals(acutionSn)){
            checkEqAcutionSnUnpayData(acutionSn,currentPrice);
            return;
        }


        startProgressDialog();
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

                        Utils.print(tag, "server price=" + acutionInfo.getData().getAuctionStatusVo().getCurrentPrice());
                        if (acutionInfo.getData().getAuctionStatusVo().getCurrentPrice() > Double.parseDouble(topPrice)) {
                            //服务器的价格比本地价格更高，允许继续竞拍
                            unpayOrderData.setHasTopPrice(false);
                            Utils.print(tag, "allow");
                            return Observable.just(unpayOrderData);
                        } else {
                            //服务器的价格没有本地价格更高，禁止继续竞拍
                            unpayOrderData.setHasTopPrice(true);
                            Utils.print(tag, "forbid");
                            return Observable.just(unpayOrderData);
                        }

                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Utils.print(tag,"get price failed");
                        stopProgressDialog();
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
                        stopProgressDialog();
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
                        Utils.print(tag,"status=="+statusData.getData().isHaveUnpayOrder()+",hastopprice="+statusData.isHasTopPrice());
                        if(statusData.isHasTopPrice()){
                            stopProgressDialog();
                            ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.acution_has_top_price));
                        }else{
                            if(statusData.getData().isHaveUnpayOrder()){
                                stopProgressDialog();
                                showUnpayDialog();
                            }else{
                                acutionAddPriceData(acutionSn, Utils.getPrice(currentPrice));
                            }
                        }
                    }
                });
    }


    /**
     * 获取拍卖商品的个数
     * @return
     */
    public int getAcutionSize(){
        if(mTvRecyclerView!=null){
            return mTvRecyclerView.getChildCount();
        }
        return 0;
    }


    @Override
    public void onWaring() {
    }

    @Override
    public void onItemClick(int pos) {
        Utils.print(tag,"onitemclick");
        AcutionItemData data = itemAdapter.getItemData(pos);
        if(data==null)
            return;
        bidAcutionPrice(data.getAuctionVo().getAuctionSn(),data.getAuctionStatusVo().getCurrentPrice());
    }





    /**
     * 获取拍卖最新商品
     * @param isToTop  添加商品到頂部，或者到底部
     */
    public void getAcutionTopData(boolean isToTop,int currentPos){
        Utils.print(tag,"replace getAcutionTopData");
        //检测网络状态
        if(!Utils.isConnected(mContext)){
            return;
        }

        if(load_top_status){
            return;
        }
        load_top_status=true;

        if(isToTop){
            oldFocusGoodsSn = itemAdapter.getListDatas().get(0).getAuctionStatusVo().getGoodsSn();
        }

        String input="";
        String sign="";
        try{
            JSONObject json = new JSONObject();
            json.put("pageIndex", 1);
            json.put("pageSize", ConStant.ACUTION_PAGE_SIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);


            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Utils.print(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Utils.print(tag,"sign="+sign);

        }catch (Exception e){
            e.printStackTrace();
        }

        if(top_sc!=null && top_sc.isUnsubscribed()){
            top_sc.unsubscribe();
        }

        top_sc = getAcutionLauAPI()
                .httpGetAcutionListData(input,sign)
                .map(new Func1<AcutionCommodityData, Integer>() {
                    @Override
                    public Integer call(AcutionCommodityData commodityData) {
                        calculatePageSize(commodityData);
                        commodityData = handAcutionStatusData(commodityData);
                        Utils.print(tag,"replace getAcutionTopData status=="+commodityData.getErrorMessage()+",value="+commodityData.getReturnValue());
                        if(isToTop){  //顶部插入
                            addToTopMoreData(commodityData.getData().getRecords());
                        }else{  //插入尾部
                            /*int returnValue = replaceServerNewData(currentPos,recored);
                            if(returnValue==-1){
                                checkTailOffLine();
                                againReplaceListData(currentPos);
                            }*/
                            addToTailMoreData(commodityData.getData().getRecords());
                        }
                        return 0;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"getAcutionTopData error="+e.getMessage());
                        load_top_status=false;
                    }

                    @Override
                    public void onNext(Integer returnData) {
                        Utils.print(tag, "getAcutionTopData notifyGlobalUpdate");
                        itemAdapter.notifyGlobalUpdate();
                        if (isToTop) {
                            loopRequestFocus();
                        }

                        if(pos==-1){
                            initRequest=true;
                        }
                        load_top_status = false;
                    }
                });
    }


    /**
     * 检测列表尾部是否存在下线商品
     */
    private void checkTailOffLine(){
        Utils.print(tag,"replace checkTailOffLine");
        test_time = System.currentTimeMillis();
        ArrayList<AcutionItemData> mDatas = new ArrayList<>();
        mDatas.addAll(itemAdapter.getListDatas());
        for (int t = mDatas.size() - 1; t >= 0; t--) {
            Utils.print(tag, "replace i=" + t);
            AcutionItemData tmp = mDatas.get(t);
            if (tmp.getAuctionScheduleVo().getAuctionTime() == 0) {
                Utils.print(tag, "replace remove=" +itemAdapter.getListDatas().get(t).getAuctionVo().getGoodsName());
                itemAdapter.getListDatas().remove(t);
                itemAdapter.notifyItemRemoved(t+1);
                itemAdapter.notifyGlobalUpdate();
            } else {
                break; //遇到沒有下架的，直接退出
            }
        }
        mDatas.clear();
    }


    /**
     * 用本地列表的尾部填充，竞拍结束状态的拍卖商品
     * @param currentPos
     */
    private void againReplaceListData(int currentPos){
        //必须放在主线程，因为需要刷新UI
        int index = currentPos; //从0开始
        int first = index-5>=0?index-5:0;
        int last = index+5>itemAdapter.getListDatas().size()-1?itemAdapter.getListDatas().size()-1:index+5;
        List<AcutionItemData> mDatas = new ArrayList<>();
        mDatas.addAll(itemAdapter.getListDatas());
        Utils.print(tag,"replace mDatas size=="+mDatas.size());
        if(mDatas.size()<=0){
            return;
        }
        int useID = mDatas.size()-1;
        Utils.print(tag,"again replace frist="+first+",last="+last+",user buttom data"); //最多获取11个数据
        for (int i = first; i <=last; i++) {
            AcutionItemData data = itemAdapter.getItemData(i);
            if(data!=null && data.getAuctionScheduleVo().getAuctionTime()==0){
                //查找可用的数据
                AcutionItemData itemData = mDatas.get(useID);
                itemAdapter.replaceData(i, itemData);
                itemAdapter.getListDatas().remove(useID);
                itemAdapter.notifyItemRemoved(useID+1);
                itemAdapter.notifyGlobalUpdate();
                useID--;
                Utils.print(tag, "again replace name=" + data.getAuctionVo().getGoodsName()+",user data=" + itemData.getAuctionVo().getGoodsName());
            }
        }
        mDatas.clear();
        Utils.print(tag,"replace div time==="+(System.currentTimeMillis()-test_time));
    }


    /**
     * 用服务器最新上线的商品填充，竞拍结束位置的拍卖商品
     * @param currentPos
     */
    private int replaceServerNewData(int currentPos,List<AcutionItemData> recored){
        //初步替换，把获取的数据填充到竞拍结束的位置，如果没有新的替换，只能用列表最后一个填充，竞拍结束位置的商品
        int index = currentPos; //从0开始
        int first = index-5>=0?index-5:0;
        int last = index+5<itemAdapter.getListDatas().size()-1?itemAdapter.getListDatas().size()-1:index+5;
        Utils.print(tag,"replace frist="+first+",last="+last); //最多获取11个数据
        int endSize =0; //统计有多少个商品竞拍结束
        for (int i = first; i <=last; i++) {
            AcutionItemData data = itemAdapter.getItemData(i);
            if(data!=null && data.getAuctionScheduleVo().getAuctionTime()==0){
                Utils.print(tag,"replace end name=="+data.getAuctionVo().getGoodsName());
                endSize++;
            }
        }
        Utils.print(tag,"replace endSize=="+endSize);
        int replaceSize=0;//统计竞拍结束的商品，已经替换了多少

        //test
        /*Utils.print(tag,"replace new size="+recored.size());
          for (int i = 0; i < recored.size(); i++) {
          Utils.print(tag,i+" replace new data="+recored.get(i).getAuctionVo().getGoodsName());
        }*/
        Utils.print(tag,"replace new size="+recored.size());


        for (int j = 0; j < recored.size(); j++) {
//            Utils.print(tag,"replace data exist="+itemAdapter.checkDataExistList(recored.get(j))+",local list size="+itemAdapter.getListDatas().size());
            if(!itemAdapter.checkDataExistList(recored.get(j))){
                for (int i = first; i <=last; i++) {
                    AcutionItemData data = itemAdapter.getItemData(i);
                    if(data!=null && data.getAuctionScheduleVo().getAuctionTime()==0){
                        itemAdapter.replaceData(i, recored.get(j));
                        replaceSize++;
                        Utils.print(tag, "replace name=" + data.getAuctionVo().getGoodsName()+",user data=" + recored.get(j).getAuctionVo().getGoodsName());
                        break;
                    }
                }
            }
        }
        Utils.print(tag,"replaceSize=="+replaceSize);
        if(replaceSize==endSize){
            Utils.print(tag,"replace status==0");
            return 0; //填充完成
        }else {
            Utils.print(tag,"replace status==-1");
            return -1;//填完不完成，待继续用最后商品填充
        }
    }

    /**
     * 往上滚动，获取服务器最新上线商品，然后插入到顶部
     * @param recored
     * @return
     */
    private void addToTopMoreData(List<AcutionItemData> recored) {
        if (recored == null) {
            return;
        }
        List<AcutionItemData> mDatas = itemAdapter.getListDatas();
        //替换数据
        for (int i = 0; i < recored.size(); i++) {
//          Utils.print(tag,"i==="+i);
            //Log.v(tag, "top data name=" + recored.get(i).getAuctionVo().getGoodsName()+",price==="+recored.get(i).getAuctionStatusVo().getCurrentPrice());
            for (int j = 0; j < mDatas.size(); j++) {
//              Utils.print(tag,"j=="+j);
                if (recored.get(i).getAuctionStatusVo().getGoodsSn().equals(mDatas.get(j).getAuctionStatusVo().getGoodsSn())) {
                    recored.get(i).setProgressValue(mDatas.get(j).getProgressValue());
                    itemAdapter.replaceData(j, recored.get(i));
                    Utils.print(tag, "replace name=" + recored.get(i).getAuctionVo().getGoodsName() + ",pos=" + j);
                    break;
                }
                if (j == mDatas.size() - 1) {
                    itemAdapter.addDataToTop(recored.get(i));
                    Utils.print(tag, "add top name=" + recored.get(i).getAuctionVo().getGoodsName());
                    break;
                }
            }
        }
    }

    /**
     * 处理竞拍结束的商品替换逻辑
     */
    private void handleJoinEnd() {
        try {
            boolean isRequestFocus=false;
            if(pos>=itemAdapter.getListDatas().size()-4){
                isRequestFocus=true;
            }

            int resetFocusPos = pos-manager.findFirstVisibleItemPosition()+1;
            checkTailOffLine();
            againReplaceListData(pos);
            Utils.print(tag, "pos=="+pos+",replace request focus===" + resetFocusPos+",is request="+isRequestFocus);

            //处理焦点位置在最后面的情况
            ResetFocusRunable resetFocusRunable = new ResetFocusRunable();
            mHandler.removeCallbacks(resetFocusRunable);
            resetFocusRunable.setRequest(isRequestFocus);
            resetFocusRunable.setRequestPos(resetFocusPos);
            mHandler.postDelayed(resetFocusRunable, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ResetFocusRunable implements Runnable{

        private int requestPos;
        private boolean isRequest;

        public void setRequest(boolean request) {
            isRequest = request;
        }

        public void setRequestPos(int requestPos) {
            this.requestPos = requestPos;
        }

        @Override
        public void run() {
             try {
                 if(isRequest){
                     resetFocus(requestPos);
                 }


                 switchInformationData();
                 getAcutionTopData(false,pos);

                 warnLessCommodity();
             } catch (Exception e) {
                 e.printStackTrace();
             }
        }
    }

    /**
     * 警戒商品很少的情况，再次从服务器获取商品
     */
    private void warnLessCommodity(){
        if(itemAdapter!=null && itemAdapter.getListDatas().size()<ConStant.ACUTION_PAGE_SIZE){
            getMoreCommodity();
        }
    }

    /**
     * 复位焦点
     */
    private void resetFocus(int pos) {
         try {
             Utils.print(tag,"resetFocus.....");
             if(mTvRecyclerView==null)
                 return;
             if (itemAdapter.getListDatas().size() > 3) {
                 Utils.print(tag, "replace request111...");
                 View view = mTvRecyclerView.getChildAt(pos);
                 if (view != null) {
                     Utils.print(tag, "replace request...");
                     view.requestFocus();
                 }else {  //异常情况，焦点落在第一个
                     defaultFocusRequest();
                 }
             } else {
                 int childID = itemAdapter.getListDatas().size() - 1;
                 if (childID < 0) {
                     childID = 0;
                 }
                 View view = mTvRecyclerView.getChildAt(childID + 1);
                 if (view != null) {
                     view.requestFocus();
                     Utils.print(tag, "replace request...");
                 }else {
                     defaultFocusRequest();
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
    }


    /**
     * 当找不到落焦点的位置的时候，把焦点落在当前页第一个商品上面
     */
    private void defaultFocusRequest(){
        View view = mTvRecyclerView.getChildAt(1);
        if (view != null) {
            Utils.print(tag, "default request...1");
            view.requestFocus();
        }
    }


    private void loopProgress(){
        Utils.print(tag,"loopProgress");
        Observable<Long> ob = Observable.interval(200, TimeUnit.MILLISECONDS).observeOn(Schedulers.io());
        loop_progress_sc = ob.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {

                //Log.v(tag,".....progress update.....time="+System.currentTimeMillis());
                time1 = System.currentTimeMillis();
                if(itemAdapter==null){
                    return;
                }
                List<AcutionItemData> itemDatas = itemAdapter.getListDatas();
                Date date = new Date(System.currentTimeMillis());
                for (int i = 0; i < itemDatas.size(); i++) {
                    AcutionItemData entity = itemDatas.get(i);
                    long localtime= date.getTime();
                    long durationTime = entity.getAuctionScheduleVo().getDurationTime();
                    long consumeTime = (localtime-entity.getAuctionScheduleVo().getAuctionTime());
                    long currentTime = durationTime*entity.getAuctionScheduleVo().getTimes()*1000-consumeTime+TIME_DIV;

                    //Log.v(tag,"name="+entity.getAuctionVo().getGoodsName()+",durationTime==="+durationTime+",currentTime="+currentTime);

                    float time = currentTime / 1000f;
                    //把基数都放大10倍
                    time = time*10;
                    durationTime = durationTime*10;
                    if(time<0){
                        continue;
                    }
                    if(time%durationTime==0){
                        //执行完一个轮次了
                        entity.setProgressValue((int)durationTime);
                    }else {
                        entity.setProgressValue((int)(time%durationTime));
                    }
                    //Log.v(tag,"value=="+entity.getProgressValue());
                }
                //Log.v(tag,"progress update div=="+(System.currentTimeMillis()-time1));
                mHandler.removeMessages(NOTIFI_ADAPTER);
                mHandler.sendEmptyMessage(NOTIFI_ADAPTER);
            }
        });
    }


    @Override
    public void onLongItemKeyUp() {
        Utils.print(tag,"onLongItemKeyUp");
        if(recycleViewHasFocus && !recycleViewIsFocus()){
            Utils.print(tag,"recycle focus@@");
            int resetFocusPos = pos-manager.findFirstVisibleItemPosition()+1;
            resetFocus(resetFocusPos);
        }
    }
}

