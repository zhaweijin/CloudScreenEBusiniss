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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.CategoryNavAdapter;
import com.hiveview.dianshang.adapter.ItemAdapter;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.category.commodity.LevelCategoryData;
import com.hiveview.dianshang.entity.category.commodity.LevelCategoryRecored;
import com.hiveview.dianshang.entity.commodity.CommodityData;
import com.hiveview.dianshang.entity.commodity.CommodityRecored;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.HiveGridLayoutManager;
import com.hiveview.dianshang.view.HiveRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carter on 4/24/17.
 */

public class CommodityCategory extends BaseActivity implements OnItemKeyListener, OnItemViewSelectedListener{


    private final int CATEGORY_PAGESIE = 160;
    /**
     * 商品二，三级分类
     */
    @BindView(R.id.tv_recycler_view)
    HiveRecyclerView mTvRecyclerView;


    @BindView(R.id.listview)
    ListView listView;


    /**
     * 左上角分类图标
     */
    @BindView(R.id.category_icon)
    SimpleDraweeView category_icon;

    /**
     * 左上角分类名称
     */
    @BindView(R.id.category_title)
    TextView category_title;

    /**
     * 三级分类向左箭头
     */
    @BindView(R.id.arrow_left)
    ImageView arrow_left;

    /**
     * 指示当前在那级列表分类
     */
    private int nav_pos = 1;

    /**
     * 二级分类标示
     */
    private final int NAV_FIRST = 1;
    /**
     * 三分分类标示
     */
    private final int NAV_SECOND = 2;
    private int nav_last_focus_pos = 0;
    private int nav_last_focus_top = 0;

    /**
     * 二级分类被选中的项,灰色背景
     */
    private int item_first_selected = 0;
    /**
     * 三分分类被选中的项,灰色背景
     */
    private int item_second_selected = 0;


    private String tag ="CommodityCagegory";

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


    private List<LevelCategoryRecored> firstNavList = new ArrayList<>();
    private List<LevelCategoryRecored> secondNavList = new ArrayList<>();

    /**
     * 记录当前浏览的推荐页码
     */
    public int currentPage =0;

    /**
     * 处理listview重新选择原来的位置
     */
    private boolean LISTVIEW_RESET_SELECTED = false;

    private Observable<String> observer;
    /**
     * 二级分类列表适配器
     */
    CategoryNavAdapter categoryFirstAdapter;
    /**
     * 三级分类列表适配器
     */
    CategoryNavAdapter categorySecondAdapter;

    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;


    ItemAdapter itemAdapter;
    private List<String> datas = new ArrayList<>();

    private String parentSn="";
    private String iconPath="";
    private String headImagePath="";
    private String categoryName="";

    private int pos;
    private int last_pos;

    /**
     * 操作状态标志
     */
    private boolean op_status=false;

    private int size = 0;

    /**
     * 分页数据加载标示
     */
    private boolean load_more_status = false;

    /**
     * recyclerview layout
     */
    private HiveGridLayoutManager manager;

    /**
     * recyclerview item 间距
     */
    private int itemSpace;

    private int marign_buttom;


    /**
     * 记录列表pos
     */
    private int aim_pos = 1;


    public static void launch(Activity activity, String iconPath, String categoryName, String sn, String headImage) {
        Intent intent = new Intent(activity, CommodityCategory.class);
        intent.putExtra("sn",sn);
        intent.putExtra("name",categoryName);
        intent.putExtra("icon_path",iconPath);
        intent.putExtra("head_image_path",headImage);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commodity_category);

        parentSn = getIntent().getStringExtra("sn");
        iconPath = getIntent().getStringExtra("icon_path");
        categoryName = getIntent().getStringExtra("name");
        headImagePath = getIntent().getStringExtra("head_image_path");

        Utils.print(tag,"iconpath="+iconPath);


        itemSpace = mContext.getResources().getDimensionPixelSize(R.dimen.category_item_view_div_8);
        marign_buttom = mContext.getResources().getDimensionPixelSize(R.dimen.recycle_marign_buttom_30);

        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(mTvRecyclerView, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }


        List<CommodityRecored> commodityRecoreds = new ArrayList<>();
        init(commodityRecoreds);

        initFirstCategoryNav();
        FrescoHelper.setImage(category_icon, Uri.parse(iconPath));
        category_title.setText(categoryName);
        setViewData();

        sendInfoStatistics();
    }


    private void init(List<CommodityRecored> commodityRecoreds) {

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
        itemAdapter = new ItemAdapter(mContext,commodityRecoreds,this,this,ConStant.CATEGORY_TO_INFO);

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
        View headview = LayoutInflater.from(mContext).inflate(R.layout.category_head, null);
        itemAdapter.setmHeaderView(headview);


        //recyclerview 设置属性
        mTvRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int postion = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
                if (itemAdapter.isHeader(postion)) {
                    outRect.set(0, 0, 0, 0);
                }else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && checkIsLastLine(postion) && pageIndex>=maxPage){
                    outRect.set(itemSpace, itemSpace, itemSpace, marign_buttom);
                } else{
                    outRect.set(itemSpace, itemSpace, itemSpace, 0);
                }
            }
        });
        mTvRecyclerView.setLayoutManager(manager);
        mTvRecyclerView.setAdapter(itemAdapter);

    }



    private void setAdapter(List<CommodityRecored> commodityRecoreds){
        itemAdapter = new ItemAdapter(mContext, commodityRecoreds,ConStant.CATEGORY_TO_INFO);

        //初始化头
        View headview = LayoutInflater.from(mContext).inflate(R.layout.category_head, null);
        itemAdapter.setmHeaderView(headview);

        itemAdapter.setCategory_img_url(headImagePath);
        mTvRecyclerView.setAdapter(itemAdapter);
    }


    /**
     * 初始化显示所有商品分类
     */
    private void setViewData(){
        currentPage=0;
        pageIndex=1;

        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }


        startProgressDialog();
        Utils.print(tag,"getCommodity");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            json.put("categorySn",parentSn);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetCommodityData(input,ConStant.getInstance(mContext).Token)
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
                            Utils.print(tag,"error="+e.getMessage());
                        }catch (Exception ee){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag,"status=="+commodityData.getReturnValue());
                        stopProgressDialog();

                        if(commodityData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,commodityData.getErrorMessage());
                            List<CommodityRecored> recored = new ArrayList<CommodityRecored>();

                            setAdapter(recored);
                            return;
                        }


                        List<CommodityRecored> recored = commodityData.getData().getRecords();
                        /*for(int i=0;i<recored.size();i++){
                            Utils.print(tag,"commodity name="+recored.get(i).getName());
                        }*/

                        if(commodityData.getData().getRecords().size()>0){
                            totalCount = commodityData.getData().getTotalCount();
                            maxPage = totalCount/ConStant.PAGESIZE;
                            if(totalCount%ConStant.PAGESIZE!=0){
                                maxPage++;
                            }
                            Utils.print(tag,"totalcount="+totalCount+",maxpage="+maxPage);
                        }

                        size = commodityData.getData().getRecords().size();

                        setAdapter(recored);
                    }
                });
        addSubscription(s);
    }

    /**
     * 分页显示更多数据
     */
    private void getMoreCommodity(){

        Utils.print(tag,"getMoreCommodity");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex",pageIndex);
            json.put("pageSize",ConStant.PAGESIZE);
            json.put("categorySn",parentSn);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetCommodityData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityData>() {
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
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag,"status=="+commodityData.getReturnValue());
                        if(commodityData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,commodityData.getErrorMessage());
                            return;
                        }


                        List<CommodityRecored> recored = commodityData.getData().getRecords();
                        /*for(int i=0;i<recored.size();i++){
                            Utils.print(tag,"commodity name="+recored.get(i).getName());
                        }*/

                        itemAdapter.addData(recored);
                        size = size + recored.size();
                        load_more_status = false;
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
     * 一级列表显示
     */
    private void updateFirstCategoryNav(){
        categoryFirstAdapter.setCurrentItemSelect(item_first_selected);
        listView.setAdapter(categoryFirstAdapter);

        listView.setSelection(item_first_selected==-1?0:item_first_selected);


        listView.setOnKeyListener(onKeyListener);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnItemSelectedListener(onItemSelectedListener);

        arrow_left.setVisibility(View.INVISIBLE);
        nav_pos = NAV_FIRST;

    }

    /**
     * 一级列表
     */
    private void initFirstCategoryNav(){

        Utils.print(tag,"get first LevelCategory");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex",1);
            json.put("pageSize",CATEGORY_PAGESIE);
            json.put("parentSn",parentSn);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetLevelData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LevelCategoryData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"11error="+e.getMessage());
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                    }

                    @Override
                    public void onNext(LevelCategoryData levelCategoryData) {
                        Utils.print(tag,"11status=="+levelCategoryData.getErrorMessage());
                        if(levelCategoryData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,levelCategoryData.getErrorMessage());
                            finish();
                            return;
                        }


                        List<LevelCategoryRecored> recored = levelCategoryData.getData().getRecords();
                        /*for(int i=0;i<recored.size();i++){
                            Utils.print(tag,"name="+recored.get(i).getName());
                            Utils.print(tag,"imgurl="+recored.get(i).getImgUrl());
                        }*/

                        nav_last_focus_pos = 0;
                        item_first_selected = 0;
                        firstNavList.clear();
                        //客户端添加全部分类
                        LevelCategoryRecored all = new LevelCategoryRecored();
                        all.setCategorySn(parentSn);
                        all.setName(mContext.getResources().getString(R.string.all_category));
                        all.setImgUrl("all_category");
                        firstNavList.add(all);
                        firstNavList.addAll(levelCategoryData.getData().getRecords());
                        categoryFirstAdapter = new CategoryNavAdapter(mContext,firstNavList,CategoryNavAdapter.ICON_TEXT);
                        categoryFirstAdapter.setCurrentItemSelect(item_first_selected);
                        listView.setAdapter(categoryFirstAdapter);

                        category_title.setText(categoryName);
                    }
                });
        addSubscription(s);


        listView.setOnKeyListener(onKeyListener);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnItemSelectedListener(onItemSelectedListener);

        arrow_left.setVisibility(View.INVISIBLE);
        nav_pos = NAV_FIRST;

        listView.requestFocus();

    }

    /**
     * 二级列表
     */
    private void initSecondCategoryNav(){


        Utils.print(tag,"initSecondCategoryNav=="+parentSn);
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex",1);
            json.put("pageSize",CATEGORY_PAGESIE);
            json.put("parentSn",parentSn);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetLevelData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LevelCategoryData>() {
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
                    }

                    @Override
                    public void onNext(LevelCategoryData levelCategoryData) {
                        Utils.print(tag,"2status=="+levelCategoryData.getErrorMessage());
                        if(levelCategoryData.getReturnValue()==-1){
                            setViewData();
                            ToastUtils.showToast(mContext,levelCategoryData.getErrorMessage());
                            return;
                        }


                        nav_last_focus_pos = 0;
                        item_second_selected=0;
                        secondNavList.clear();

                        //客户端添加全部分类
                        LevelCategoryRecored all = new LevelCategoryRecored();
                        all.setCategorySn(parentSn);
                        all.setName(mContext.getResources().getString(R.string.all_category));
                        all.setImgUrl("all_category");
                        secondNavList.add(all);
                        if(levelCategoryData.getData()!=null){
                            secondNavList.addAll(levelCategoryData.getData().getRecords());
                        }

                        /*for(int i=0;i<secondNavList.size();i++){
                            Utils.print(tag,"2name="+secondNavList.get(i).getName());
                            Utils.print(tag,"2imgurl="+secondNavList.get(i).getImgUrl());
                        }*/


                        categorySecondAdapter = new CategoryNavAdapter(mContext,secondNavList,CategoryNavAdapter.TEXT);
                        categorySecondAdapter.setCurrentItemSelect(item_second_selected);
                        listView.setAdapter(categorySecondAdapter);


                        listView.setOnKeyListener(onKeyListener);
                        listView.setOnItemClickListener(onItemClickListener);
                        listView.setOnItemSelectedListener(onItemSelectedListener);

                        arrow_left.setVisibility(View.VISIBLE);
                        nav_pos = NAV_SECOND;

                        category_title.setText(firstNavList.get(item_first_selected).getName());

                        setViewData();
                    }
                });
        addSubscription(s);

    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(nav_pos==NAV_FIRST){ //二级分类点击响应
                //全部分类商品展示
                Utils.print(tag,"show all ===="+item_first_selected);
                if(position==0){
                    Utils.print(tag,"show all commodity");
                    if (item_first_selected != 0) {
                        category_title.setText(categoryName);
                        parentSn = firstNavList.get(position).getCategorySn();
                        item_first_selected = position;
                        categoryFirstAdapter.setCurrentItemSelect(item_first_selected);
                        categoryFirstAdapter.notifyDataSetChanged();
                        setViewData();
                    }
                }else{
                    Utils.print(tag,"show all commodity22");
                    parentSn = firstNavList.get(position).getCategorySn();
                    item_first_selected = position;
                    categoryFirstAdapter.setCurrentItemSelect(item_first_selected);
                    categoryFirstAdapter.notifyDataSetChanged();
                    initSecondCategoryNav();
                }
            }else if(nav_pos==NAV_SECOND){ //三级分类点击响应
                if(position!=item_second_selected){
                    Utils.print(tag,"11111");
                    item_second_selected = position;
                    categorySecondAdapter.setCurrentItemSelect(item_second_selected);
                    categorySecondAdapter.notifyDataSetChanged();
                    parentSn = secondNavList.get(position).getCategorySn();
					Log.i("当前锁定的分类按钮项","item_second_selected=="+item_second_selected);
                    Log.i("","nav_last_focus_pos=="+nav_last_focus_pos);
                    Utils.print(tag,"nav_pos=="+nav_pos+",pos=="+position);//定位三级商品

                    setViewData();
                }
                //item_second_selected = position;
            }
        }
    };


    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    Utils.print(tag,"navpos=="+nav_pos);
                    if(nav_pos==NAV_SECOND) {
                        updateFirstCategoryNav();
                    }
                }else if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
                    //上移焦点拦截
                    if(nav_pos==NAV_FIRST){
                        if(nav_last_focus_pos==0){
                            return  true;
                        }
                    }else{
                        if(nav_last_focus_pos==0){
                            return  true;
                        }
                    }
                }else if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                    //下移焦点拦截
                    if(nav_pos==NAV_FIRST){
                        Utils.print(tag,"item="+nav_last_focus_pos+",size="+firstNavList.size());
                        if(nav_last_focus_pos==firstNavList.size()-1){
                            return  true;
                        }
                    }else{
                        if(nav_last_focus_pos==secondNavList.size()-1){
                            return  true;
                        }
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (itemAdapter.getItemCount() == 1) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Utils.print(tag, "onitemselected=" + position + ",nav_last_pos=" + nav_last_focus_pos);
            if (position == 0 && LISTVIEW_RESET_SELECTED) {
                LISTVIEW_RESET_SELECTED = false;
                nav_last_focus_pos = item_second_selected;
                Log.i("FOCUS", nav_last_focus_top + "");

                //listView.setSelectionFromTop(nav_last_focus_pos,nav_last_focus_top);
                return;
            }

            nav_last_focus_pos = position;
            nav_last_focus_top = (view == null) ? 0 : view.getTop();

            if (nav_pos == NAV_FIRST) {
                categoryFirstAdapter.setCurrentFocusItem(position,true);
            } else if (nav_pos == NAV_SECOND) {
                categorySecondAdapter.setCurrentFocusItem(position,true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(itemAdapter!=null){
            itemAdapter.unRegisterObserver();
        }
        FeedbackTranslationAnimatorUtil.getInstance().recycle();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && nav_pos==NAV_SECOND){
            updateFirstCategoryNav();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void OnItemViewSelectedListener(int position) {
        itemAdapter.setSelectItemId(position);
        pos = position;

        if (aim_pos == 1) {
            aim_pos = -1;
            if (nav_pos == NAV_FIRST) {
                categoryFirstAdapter.setCurrentFocusItem(listView.getSelectedItemPosition(), false);

            } else if (nav_pos == NAV_SECOND) {
                categorySecondAdapter.setCurrentFocusItem(listView.getSelectedItemPosition(), false);
            }
        }
        if (isFirstLine()) {
            mTvRecyclerView.smoothScrollToPosition(0);
        }


        if(position>ConStant.END_SIZE && position>=size-ConStant.END_SIZE){
            Utils.print(tag,"append data");
            if(!load_more_status){
                load_more_status = true;
                pageIndex++;
                if(pageIndex>maxPage)
                    return;
                getMoreCommodity();
            }
        }
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
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                Utils.print(tag, listView.getSelectedItemPosition() + "----1--");
                if (pos % 4 == 0) {
                    listView.requestFocus();
                    Utils.print(tag, listView.getSelectedItemPosition() + "----1--");
                    if (nav_pos == NAV_FIRST) {
                        categoryFirstAdapter.setCurrentFocusItem(listView.getSelectedItemPosition(), true);
                    } else if (nav_pos == NAV_SECOND) {
                        categorySecondAdapter.setCurrentFocusItem(listView.getSelectedItemPosition(), true);
                    }
                }
            }
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
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (pos % 4 == 0) {
                v.setNextFocusLeftId(R.id.layout_collection);
                aim_pos = 1;
            }
        }else if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
            if(isFirstLine()){
                return true;
            }

        }
        return false;
    }



    /**
     * 二级页面浏览器埋点发送
     */
    private void sendInfoStatistics(){
        HashMap<String,String> simpleMap=new HashMap<String,String>();

        //商品详情页浏览量埋点
        simpleMap.clear();
        String info = "";
        simpleMap.put("tabNo","");
        simpleMap.put("actionType","Ec3002");

        //一级分类标签id
        info = info + "firstTabId="+parentSn+"&";
        //一级分类名称
        info = info + "firstTabName="+categoryName;

        simpleMap.put("actionInfo",info);
        EBusinessApplication.getHSApi().addAction(simpleMap);

    }
}
