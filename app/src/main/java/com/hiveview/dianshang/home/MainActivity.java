package com.hiveview.dianshang.home;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.auction.AcutionService;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.collection.CollectionMain;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.address.UserAddress;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.entity.shoppingcart.GroupData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartRecord;
import com.hiveview.dianshang.order.OrderMain;
import com.hiveview.dianshang.saleservice.AfterSaleServiceMain;
import com.hiveview.dianshang.search.OpSearchKey;
import com.hiveview.dianshang.search.SearchMain;
import com.hiveview.dianshang.search.SearchResult;
import com.hiveview.dianshang.shoppingcart.ShoppingCartGrid;
import com.hiveview.dianshang.auction.AcutionCommodity;
import com.hiveview.dianshang.showcommodity.AllCommodity;
import com.hiveview.dianshang.showcommodity.CommodityMainCategory;
import com.hiveview.dianshang.showcommodity.CommodityRecommend;
import com.hiveview.dianshang.usercenter.AddressManager;
import com.hiveview.dianshang.usercenter.EditAddress;
import com.hiveview.dianshang.usercenter.UserCenterManager;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.NetworkUtil;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.SetAddressPopWindow;
import com.hiveview.dianshang.view.SetAddressWindowInterface;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static java.lang.Thread.sleep;


/**
 * 商品主页
 */
public class MainActivity extends BaseActivity {

    private Observable<Integer> observer;
    private Observable<OpSearchKey> ob_search;
    private Observable<String> shopping_cart_observer;
    private Observable<Integer> observer_nav_focus;
    private Observable<Integer> observer_reset_nav_focus;


    private String tag = "Main";

    private FragmentManager fragmentManager;

    /**
     * 首页推荐导航
     */
    @BindView(R.id.layout_home)
    RelativeLayout layout_home;

    //图标+文字
    @BindView(R.id.home_selected_layout)
    LinearLayout home_selected_layout;

    //首页文字
    @BindView(R.id.home_text)
    TextView home_text;

    //非扩展图标
    @BindView(R.id.image_home)
    ImageView image_home;

    //扩展图标
    @BindView(R.id.home_icon)
    ImageView home_icon;



    /**
     * 拍卖导航
     */
    @BindView(R.id.layout_acution)
    RelativeLayout layout_acution;

    //图标+文字
    @BindView(R.id.commodity_acution_layout)
    LinearLayout commodity_acution_layout;

    //拍卖文字
    @BindView(R.id.acution_text)
    TextView acution_text;

    //非扩展图标
    @BindView(R.id.image_acution)
    ImageView image_acution;

    //扩展图标
    @BindView(R.id.acution_icon)
    ImageView acution_icon;



    /**
     * 商品分类导航
     */
    @BindView(R.id.layout_commodity)
    RelativeLayout layout_commodity;

    //图标+文字
    @BindView(R.id.commodity_selected_layout)
    LinearLayout commodity_selected_layout;

    //分类文字
    @BindView(R.id.commodity_text)
    TextView commodity_text;

    //非扩展图标
    @BindView(R.id.image_commodity)
    ImageView image_commodity;

    //扩展图标
    @BindView(R.id.commodity_icon)
    ImageView commodity_icon;


    /**
     * 搜索导航
     */
    @BindView(R.id.layout_search)
    RelativeLayout layout_search;

    //图标+文字
    @BindView(R.id.commodity_search_layout)
    LinearLayout commodity_search_layout;


    //搜索文字
    @BindView(R.id.search_text)
    TextView search_text;

    //非扩展图标
    @BindView(R.id.image_search)
    ImageView image_search;

    //扩展图标
    @BindView(R.id.search_icon)
    ImageView search_icon;



    /**
     * 收藏导航
     */
    @BindView(R.id.layout_collection)
    RelativeLayout layout_collection;

    //图标+文字
    @BindView(R.id.commodity_collection_layout)
    LinearLayout commodity_collection_layout;

    //收藏文字
    @BindView(R.id.collection_text)
    TextView collection_text;

    //非扩展图标
    @BindView(R.id.image_collection)
    ImageView image_collection;

    //扩展图标
    @BindView(R.id.collection_icon)
    ImageView collection_icon;





    /**
     * 我的订单导航
     */
    @BindView(R.id.layout_order)
    RelativeLayout layout_order;

    //图标+文字
    @BindView(R.id.commodity_order_layout)
    LinearLayout commodity_order_layout;

    //订单文字
    @BindView(R.id.order_text)
    TextView order_text;

    //非扩展图标
    @BindView(R.id.image_order)
    ImageView image_order;

    //扩展图标
    @BindView(R.id.order_icon)
    ImageView order_icon;



    /**
     * 售后服务导航
     */
    @BindView(R.id.layout_after_sale_service)
    RelativeLayout layout_after_sale_service;


    //图标+文字
    @BindView(R.id.commodity_sale_service_layout)
    LinearLayout commodity_sale_service_layout;

    //售后文字
    @BindView(R.id.sale_service_text)
    TextView sale_service_text;

    //非扩展图标
    @BindView(R.id.image_after_sale_service)
    ImageView image_after_sale_service;


    //扩展图标
    @BindView(R.id.sale_service_icon)
    ImageView sale_service_icon;


    /**
     * 导航栏
     */
    @BindView(R.id.main_navbar)
    LinearLayout nav_contain;

    /**
     * 网格购物车入口
     */
    @BindView(R.id.layout_domy_shopping_cart)
    RelativeLayout layout_domy_shopping_cart;


    /**
     * 用户中心导航
     */
    @BindView(R.id.layout_domy_user_center)
    RelativeLayout layout_domy_user_center;


    @BindView(R.id.layout_no_focus_layer)
    FrameLayout layout_no_focus_layer;

    /**
     * 购物车快捷方式文本字体显示
     */

    TextView number;
    TextView titleText;

    /**
     * 购物车空的情况
     */
    TextView model;


    RelativeLayout.LayoutParams layoutParams;


    private int NAV_POS = 1;
    public static final int NAV_HOME = 1;
    public static final int NAV_COMMODITY = 2;
    public static final int NAV_SEARCH = 3;
    public static final int NAV_COLLECTION = 4;
    public static final int NAV_ORDER = 5;
    public static final int NAV_SALE_SERVICE = 6;
    public static final int NAV_USERR_CENTER = 7;
    public static final int NAV_SPECIAL = 8;
    public static final int NAV_ALL_COMMODITY = 9;
    public static final int NAV_SEARCH_RESULT = 10;
    public static final int NAV_ACUTION = 11;
    public static final int NAV_ACUTION_SERVER=7;//用于服务器埋点定义
    private boolean firstBoolean=true;
    private SetAddressPopWindow setAddressWindow;
    private double dialogAlpha = 0.45;


    private SearchMain searchMain;
    private SearchResult searchResult;
    private boolean userDatasBoolean=false;
    private boolean searchIsRequestFocus = false;
    private Fragment currentFragment;
    private EditAddress editAddress;
    private AllCommodity allCommodity;
    private AcutionCommodity acutionCommodity;
    private UserCenterManager userCenterManager;
    private AddressManager addressManager;
    private CollectionMain collection;
    private OrderMain orderMain;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ReceiverAfter receiverAfter;

    private CommodityRecommend commodityRecommend; //商品推荐位
    private CommodityMainCategory commodityMainCategory;

    private List<ShoppingCartRecord> records = new ArrayList<>();

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    private String message;

    /**
     * 定义导航栏拉伸，或者压缩状态
     */
    private enum NAV_FOCUS_STATUS{
        NORMAL,
        EXPAND
    }
    //当前导航栏状态
    private int mNavFocusStatus;

    public static void launch(Context context,String adMessage) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("adMessage",adMessage);
        context.startActivity(intent);
    }


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout_domy_shopping_cart.setNextFocusDownId(R.id.layout_domy_shopping_cart);
        fragmentManager = getSupportFragmentManager();


        message = getIntent().getStringExtra("adMessage");
        initEvent();

        //init nav
        mNavFocusStatus = NAV_FOCUS_STATUS.NORMAL.ordinal();
        home_selected_layout.setVisibility(View.INVISIBLE);
        commodity_acution_layout.setVisibility(View.INVISIBLE);
        commodity_selected_layout.setVisibility(View.INVISIBLE);
        commodity_collection_layout.setVisibility(View.INVISIBLE);
        commodity_search_layout.setVisibility(View.INVISIBLE);
        commodity_order_layout.setVisibility(View.INVISIBLE);
        commodity_sale_service_layout.setVisibility(View.INVISIBLE);

        image_home.setVisibility(View.VISIBLE);
        image_acution.setVisibility(View.VISIBLE);
        image_commodity.setVisibility(View.VISIBLE);
        image_collection.setVisibility(View.VISIBLE);
        image_search.setVisibility(View.VISIBLE);
        image_order.setVisibility(View.VISIBLE);
        image_after_sale_service.setVisibility(View.VISIBLE);






        /**
         * 提供launcher跳转到相应界面
         */
        String action = getIntent().getAction();
        if(action!=null){
            if(action.equals(ConStant.ACTION_ORDER)){
                selectFragment(ConStant.OP_ORDER);
                NAV_POS=NAV_ORDER;
                layout_order.requestFocus();
            }else if(action.equals(ConStant.ACTION_FAVORITE)){
                selectFragment(ConStant.OP_COLLECTION);
                NAV_POS=NAV_COLLECTION;
                layout_collection.requestFocus();
            }else if(action.equals(ConStant.ACTION_USER_CENTER)){
                selectFragment(ConStant.OP_USER_CENTER);
                NAV_POS=NAV_USERR_CENTER;
                layout_domy_user_center.requestFocus();
            }else if(action.equals(ConStant.ACTION_ACUTION)){
                selectFragment(ConStant.OP_ACUTION);
                NAV_POS=NAV_ACUTION;
                layout_acution.requestFocus();
            }else{
                selectFragment(ConStant.OP_HOME);
            }
        }else{
            selectFragment(ConStant.OP_HOME);
        }



        handleNavViewFocus();
 

        preferences = this.getSharedPreferences("location",
                this.MODE_WORLD_READABLE);
        editor = preferences.edit();
        /*InvoiceDialog invoiceDialog = new InvoiceDialog(mContext);
        invoiceDialog.showUI();*/

        //ShoppingCartList.launch((MainActivity)mContext,ConStant.SHOPPING_CART);


        /*SelectCommodityType selectCommodityType = new SelectCommodityType(mContext,R.style.SelectTypeCustomDialog,SelectCommodityType.SELECT_COMMODITY);
        selectCommodityType.showUI("401889913554866176");*/

        //TestActivity.launch(this);
        //LivePlay.launch(this,"");
        //AcutionInfomation.launch(this);
        receiverAfter=new ReceiverAfter();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dianshang.damai.message_changed");
        mContext.registerReceiver(receiverAfter, intentFilter);
        //联网后3次刷新购物车状态，避免不能加载
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(tag,"............start...........");
                int i=0;
                while(!NetworkUtil.checkNetConnect(mContext)||i<2){
                    try {
                        sleep(1000);
                        if (NetworkUtil.checkNetConnect(mContext)){
                            Log.i(tag,"............true...........");
                            getShoppingCartCommodity();
                            i++;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(tag,"............false...........");


                }
            }
        }).start();

    }

    /**
     * 切换不同的页面
     * @param i
     */
    private void selectFragment(Integer i) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (i){
            case ConStant.OP_EDIT_ADDRESS:
                editAddress = new EditAddress();
                currentFragment=editAddress;
                transaction.replace(R.id.layout_contain, editAddress);
                break;
            case ConStant.OP_SHOW_ADDRESS_MANAGER:
                addressManager = new AddressManager();
                currentFragment=addressManager;
                transaction.replace(R.id.layout_contain, addressManager);
                break;
            case ConStant.OP_SEARCH_MAIN:
                searchMain = SearchMain.newInstance(searchIsRequestFocus);
                currentFragment = searchMain;
                transaction.replace(R.id.layout_contain, searchMain);
                break;
            case ConStant.OP_SEARCH_MAIN_RESULT:
                break;
            case ConStant.OP_COMMODITY_CAGEGORY:
                commodityMainCategory = new CommodityMainCategory();
                transaction.replace(R.id.layout_contain, commodityMainCategory);
                currentFragment = commodityMainCategory;
                break;
            case ConStant.OP_ORDER:
                orderMain=new OrderMain();
                transaction.replace(R.id.layout_contain, orderMain);
                currentFragment=orderMain;
                break;
            case ConStant.OP_HOME:
                if(commodityRecommend==null){
                    commodityRecommend = new CommodityRecommend();
                    commodityRecommend.setMessage(message);
                }
                transaction.replace(R.id.layout_contain, commodityRecommend);
                currentFragment = commodityRecommend;
                break;
            case ConStant.OP_USER_CENTER:
                userCenterManager=new UserCenterManager();
                currentFragment=userCenterManager;
                transaction.replace(R.id.layout_contain, userCenterManager);
                break;
            case ConStant.OP_COLLECTION:
                collection = new CollectionMain();
                transaction.replace(R.id.layout_contain, collection);
                currentFragment=collection;
                break;
            case ConStant.OP_AFTER_SALE_SERVICE:
                AfterSaleServiceMain afterSaleServiceMain=new AfterSaleServiceMain();
                transaction.replace(R.id.layout_contain, afterSaleServiceMain);
                currentFragment=afterSaleServiceMain;
                break;
            case ConStant.OP_ALL:
                layout_home.requestFocus();
                allCommodity = new AllCommodity();
                transaction.replace(R.id.layout_contain, allCommodity);
                currentFragment=allCommodity;
                NAV_POS = NAV_ALL_COMMODITY;
                break;
            case ConStant.OP_ACUTION:
                layout_acution.requestFocus();
                acutionCommodity = new AcutionCommodity();
                transaction.replace(R.id.layout_contain, acutionCommodity);
                currentFragment=acutionCommodity;
                NAV_POS = NAV_ACUTION;
                break;

        }


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            transaction.commitAllowingStateLoss();
        }else{
            transaction.commit();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        //刷新购物车状态
        getShoppingCartCommodity();

        Utils.print(tag,"onResume");
        //检测是否启动安装服务app
        if(!Utils.isServiceWorked(this, ConStant.Install_ServiceName)){
            //启动安装服务
            Intent intent = new Intent(mContext,InstallService.class);
            intent.putExtra("status",InstallService.ENTER);
            startService(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.print(tag,"onPause");
        if(!Utils.isServiceWorked(this, ConStant.Install_ServiceName)){
            Intent intent = new Intent(this,InstallService.class);
            intent.putExtra("status",InstallService.EXIT);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(ConStant.obString_opt_natigation,observer);
        RxBus.get().unregister(ConStant.obString_opt_search_natigation,ob_search);
        RxBus.get().unregister(ConStant.obString_main_load_finish,observer_nav_focus);
        RxBus.get().unregister(ConStant.obString_reset_nav_focus,observer_reset_nav_focus);
        this.unregisterReceiver(receiverAfter);

        //退出清空缓存
        ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();

        Intent intent = new Intent(this,InstallService.class);
        intent.putExtra("status",InstallService.EXIT);
        startService(intent);
        //关闭所有的activity
        activityManagerApplication.finishAllActivity();
    }



    public void initEvent(){
        observer = RxBus.get().register(ConStant.obString_opt_natigation,Integer.class);
        observer.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                Utils.print(tag,"op="+i);
                selectFragment(i);
            }
        });

        ob_search = RxBus.get().register(ConStant.obString_opt_search_natigation,OpSearchKey.class);
        ob_search.subscribe(new Action1<OpSearchKey>() {
            @Override
            public void call(OpSearchKey opSearchKey) {
                if(opSearchKey.getKey().equals(ConStant.SEARCH_HOME)){
                    searchIsRequestFocus = true;
                    layout_search.requestFocus();
                    selectFragment(ConStant.OP_SEARCH_MAIN);
                }else if(opSearchKey.getKey().equals(ConStant.SEARCH_RESULT)){
                    layout_search.requestFocus();
                    searchMain = null;
                    //Utils.print(tag,"value>>>"+opSearchKey.getValue()+",key="+opSearchKey.getKey());
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    searchResult = SearchResult.newInstance(opSearchKey);//new SearchResult(opSearchKey);
                    currentFragment = searchResult;
                    transaction.replace(R.id.layout_contain, searchResult);
                    transaction.commit();
                    NAV_POS = NAV_SEARCH_RESULT;
                }else if(opSearchKey.getKey().equals(ConStant.SEARCH_EXIT)){
                    if ((System.currentTimeMillis() - exitTime) > 3000) {
                        //弹出提示，可以有多种方式
                        Utils.print(tag,">>>>>>");
                        ToastUtils.showToast(mContext,getResources().getString(R.string.exit_tips));
                        exitTime = System.currentTimeMillis();
                    }else{
                        finish();
                    }
                }
            }
        });



        observer_nav_focus = RxBus.get().register(ConStant.obString_main_load_finish,Integer.class);
        observer_nav_focus.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                switch (i){
                    case NAV_HOME:
                        Utils.print(tag,"home request focus");
                        //主页数据加载完成
                        //判断是否显示提示添加用户地址
                        if (firstBoolean) {
                            Log.i("=========","===检测有无数据，显示地址对话框！");
                            getUserAddressList();
                        }
                        break;
                    case NAV_COLLECTION:
                        //收藏没有数据，重新请求焦点
                        Utils.print(tag,"collection request");
                        layout_collection.requestFocus();
                        break;
                }
            }
        });


        observer_reset_nav_focus = RxBus.get().register(ConStant.obString_reset_nav_focus,Integer.class);
        observer_reset_nav_focus.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Utils.print(tag,"observer_reset_nav_focus="+integer);
                if(integer==0){
                    setNavFocus(false);
                }else{
                    setNavFocus(true);
                }
            }
        });



    }






    /**
     * 处理导航焦点
     */
    private void handleNavViewFocus(){
        layout_home.setOnFocusChangeListener(onNavFocusChangeListener);
        layout_acution.setOnFocusChangeListener(onNavFocusChangeListener);
        layout_commodity.setOnFocusChangeListener(onNavFocusChangeListener);
        layout_order.setOnFocusChangeListener(onNavFocusChangeListener);
        layout_search.setOnFocusChangeListener(onNavFocusChangeListener);
        layout_collection.setOnFocusChangeListener(onNavFocusChangeListener);
        layout_after_sale_service.setOnFocusChangeListener(onNavFocusChangeListener);
        layout_domy_shopping_cart.setOnFocusChangeListener(onFocusChangeListener);
        layout_domy_user_center.setOnFocusChangeListener(onFocusChangeListener);

        layout_home.setOnClickListener(onClickListener);
        layout_acution.setOnClickListener(onClickListener);
        layout_commodity.setOnClickListener(onClickListener);
        layout_order.setOnClickListener(onClickListener);
        layout_search.setOnClickListener(onClickListener);
        layout_collection.setOnClickListener(onClickListener);
        layout_after_sale_service.setOnClickListener(onClickListener);
        layout_domy_shopping_cart.setOnClickListener(onClickListener);
        layout_domy_user_center.setOnClickListener(onClickListener);

        layout_home.setOnKeyListener(onKeyListener);
        layout_acution.setOnKeyListener(onKeyListener);
        layout_commodity.setOnKeyListener(onKeyListener);
        layout_order.setOnKeyListener(onKeyListener);
        layout_search.setOnKeyListener(onKeyListener);
        layout_collection.setOnKeyListener(onKeyListener);
        layout_after_sale_service.setOnKeyListener(onKeyListener);
        layout_domy_user_center.setOnKeyListener(onKeyListener);
        layout_domy_shopping_cart.setOnKeyListener(onKeyListener);

        resetNavSelect();
    }

    private void showExpandNav(){
        Utils.print(tag,"mNavFocusStatus="+mNavFocusStatus);
        home_selected_layout.setVisibility(View.VISIBLE);
        commodity_acution_layout.setVisibility(View.VISIBLE);
        commodity_selected_layout.setVisibility(View.VISIBLE);
        commodity_collection_layout.setVisibility(View.VISIBLE);
        commodity_search_layout.setVisibility(View.VISIBLE);
        commodity_order_layout.setVisibility(View.VISIBLE);
        commodity_sale_service_layout.setVisibility(View.VISIBLE);

        image_home.setVisibility(View.INVISIBLE);
        image_acution.setVisibility(View.INVISIBLE);
        image_commodity.setVisibility(View.INVISIBLE);
        image_collection.setVisibility(View.INVISIBLE);
        image_search.setVisibility(View.INVISIBLE);
        image_order.setVisibility(View.INVISIBLE);
        image_after_sale_service.setVisibility(View.INVISIBLE);


        int oldwidth = mContext.getResources().getDimensionPixelSize(R.dimen.main_nav_width_93);
        ValueAnimator anim = ValueAnimator.ofFloat(oldwidth, oldwidth*2);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                Log.d("TAG", "cuurent value is " + currentValue);

                layoutParams = (RelativeLayout.LayoutParams) nav_contain.getLayoutParams();
                layoutParams.width = (int)currentValue;
                nav_contain.setLayoutParams(layoutParams);

            }
        });


        Utils.print(tag,"expand");
        Utils.print(tag,"focus="+navHasFocus());
        if(mNavFocusStatus==NAV_FOCUS_STATUS.NORMAL.ordinal() && navHasFocus()){
            Utils.print(tag,"expand start");
            anim.start();
            mNavFocusStatus = NAV_FOCUS_STATUS.EXPAND.ordinal();
        }


        if(NAV_POS==NAV_HOME){
            home_icon.setBackgroundResource(R.drawable.home_red);
            home_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_red_color));
        }else if(NAV_POS==NAV_COMMODITY){
            commodity_icon.setBackgroundResource(R.drawable.category_red);
            commodity_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_red_color));
        }else if(NAV_POS==NAV_COLLECTION){
            collection_icon.setBackgroundResource(R.drawable.collection_red);
            collection_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_red_color));
        }else if(NAV_POS==NAV_SEARCH){
            search_icon.setBackgroundResource(R.drawable.search_red);
            search_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_red_color));
        }else if(NAV_POS==NAV_ORDER){
            order_icon.setBackgroundResource(R.drawable.order_red);
            order_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_red_color));
        }else if(NAV_POS==NAV_SALE_SERVICE){
            sale_service_icon.setBackgroundResource(R.drawable.sale_service_red);
            sale_service_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_red_color));
        }else if(NAV_POS==NAV_ACUTION){
            acution_icon.setBackgroundResource(R.drawable.acution_red);
            acution_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_red_color));
        }
    }


    private void showNormalNav(){


        Utils.print(tag,"mNavFocusStatus="+mNavFocusStatus);

        home_selected_layout.setVisibility(View.INVISIBLE);
        commodity_acution_layout.setVisibility(View.INVISIBLE);
        commodity_selected_layout.setVisibility(View.INVISIBLE);
        commodity_collection_layout.setVisibility(View.INVISIBLE);
        commodity_search_layout.setVisibility(View.INVISIBLE);
        commodity_order_layout.setVisibility(View.INVISIBLE);
        commodity_sale_service_layout.setVisibility(View.INVISIBLE);

        image_home.setVisibility(View.VISIBLE);
        image_acution.setVisibility(View.VISIBLE);
        image_commodity.setVisibility(View.VISIBLE);
        image_collection.setVisibility(View.VISIBLE);
        image_search.setVisibility(View.VISIBLE);
        image_order.setVisibility(View.VISIBLE);
        image_after_sale_service.setVisibility(View.VISIBLE);



        int oldwidth = mContext.getResources().getDimensionPixelSize(R.dimen.main_nav_width_93);
        ValueAnimator anim = ValueAnimator.ofFloat(oldwidth*2, oldwidth);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                Log.d("TAG", "cuurent value is " + currentValue);

                layoutParams = (RelativeLayout.LayoutParams) nav_contain.getLayoutParams();
                layoutParams.width = (int)currentValue;
                nav_contain.setLayoutParams(layoutParams);

            }
        });



        Utils.print(tag,"normal");
        Utils.print(tag,"focus="+navHasFocus());
        if(mNavFocusStatus==NAV_FOCUS_STATUS.EXPAND.ordinal() && !navHasFocus()){
            Utils.print(tag,"normal start");
            anim.start();
            mNavFocusStatus = NAV_FOCUS_STATUS.NORMAL.ordinal();
        }

    }



    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Utils.print(tag,"onFocusChangeListener ID="+v.getId()+",hasFocus="+hasFocus);
            if(hasFocus){
                //layout_no_focus_layer.setVisibility(View.VISIBLE);
                v.setBackgroundColor(getResources().getColor(R.color.home_focus_icon_color));
                if(v.getId()==R.id.layout_domy_shopping_cart && records!=null){
                   /* number.setTextColor(mContext.getResources().getColor(R.color.white));
                    titleText.setTextColor(mContext.getResources().getColor(R.color.white));
                    number.getPaint().setFakeBoldText(true);
                    titleText.getPaint().setFakeBoldText(true);*/
                }else if (v.getId()==R.id.layout_domy_shopping_cart && records==null){
                    model.setTextColor(mContext.getResources().getColor(R.color.white));
                    model.getPaint().setFakeBoldText(true);
                }
            }else {
                //layout_no_focus_layer.setVisibility(View.INVISIBLE);
                v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                if(v.getId()==R.id.layout_domy_shopping_cart) {
                    v.setBackgroundResource(R.drawable.home_shopping_cart_bg);
                    if (records!=null && records.size() > 0) {
                        /*number.setTextColor(mContext.getResources().getColor(R.color.white));
                        titleText.setTextColor(mContext.getResources().getColor(R.color.white));
                        number.getPaint().setFakeBoldText(true);
                        titleText.getPaint().setFakeBoldText(true);*/
                    }else if(records==null){
                        model.setTextColor(mContext.getResources().getColor(R.color.white));
                        model.getPaint().setFakeBoldText(true);
                    }
                }
            }
        }
    };



    View.OnFocusChangeListener onNavFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Utils.print(tag,"onFocusChangeListener ID="+v.getId()+",hasFocus="+hasFocus);
            if(hasFocus){
                showExpandNav();
                v.setBackgroundColor(getResources().getColor(R.color.home_focus_icon_color));
                //layout_no_focus_layer.setVisibility(View.VISIBLE);
                switch (v.getId()){
                    case R.id.layout_home:
                        home_icon.setBackgroundResource(R.drawable.home_white);
                        home_text.setTextColor(mContext.getResources().getColor(R.color.white));
                        break;
                    case R.id.layout_commodity:
                        commodity_icon.setBackgroundResource(R.drawable.category_white);
                        commodity_text.setTextColor(mContext.getResources().getColor(R.color.white));
                        //勿删以下代码，解决焦点从个人中心页面移到导航时，焦点框与焦点不一致的问题！！！
                        //UserCenterManager
                        if(currentFragment.equals(userCenterManager)){
                            home_icon.setBackgroundResource(R.drawable.home_gray);
                            home_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        }

                        break;
                    case R.id.layout_search:
                        search_icon.setBackgroundResource(R.drawable.search_white);
                        search_text.setTextColor(mContext.getResources().getColor(R.color.white));
                        //勿删以下代码，解决焦点从个人中心页面移到导航时，焦点框与焦点不一致的问题！！！
                        if(currentFragment.equals(userCenterManager)){
                            home_icon.setBackgroundResource(R.drawable.home_gray);
                            home_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        }
                        break;
                    case R.id.layout_collection:
                        collection_icon.setBackgroundResource(R.drawable.collection_white);
                        collection_text.setTextColor(mContext.getResources().getColor(R.color.white));
                        //勿删以下代码，解决焦点从个人中心页面移到导航时，焦点框与焦点不一致的问题！！！
                        if(currentFragment.equals(userCenterManager)){
                            home_icon.setBackgroundResource(R.drawable.home_gray);
                            home_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        }
                        break;
                    case R.id.layout_order:
                        order_icon.setBackgroundResource(R.drawable.order_white);
                        order_text.setTextColor(mContext.getResources().getColor(R.color.white));
                        //勿删以下代码，解决焦点从个人中心页面移到导航时，焦点框与焦点不一致的问题！！！
                        if(currentFragment.equals(userCenterManager)){
                            home_icon.setBackgroundResource(R.drawable.home_gray);
                            home_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        }
                        break;
                    case R.id.layout_after_sale_service:
                        sale_service_icon.setBackgroundResource(R.drawable.sale_service_white);
                        sale_service_text.setTextColor(mContext.getResources().getColor(R.color.white));
                        //勿删以下代码，解决焦点从个人中心页面移到导航时，焦点框与焦点不一致的问题！！！
                        if(currentFragment.equals(userCenterManager)){
                            home_icon.setBackgroundResource(R.drawable.home_gray);
                            home_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        }
                        break;
                    case R.id.layout_acution:
                        acution_icon.setBackgroundResource(R.drawable.acution_white);
                        acution_text.setTextColor(mContext.getResources().getColor(R.color.white));
                        //勿删以下代码，解决焦点从个人中心页面移到导航时，焦点框与焦点不一致的问题！！！
                        if(currentFragment.equals(userCenterManager)){
                            home_icon.setBackgroundResource(R.drawable.home_gray);
                            home_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        }
                        break;
                }

            }else {
                showNormalNav();
                v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                //layout_no_focus_layer.setVisibility(View.INVISIBLE);

                switch (v.getId()){
                    case R.id.layout_home:
                        home_icon.setBackgroundResource(R.drawable.home_gray);
                        home_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        break;
                    case R.id.layout_commodity:
                        commodity_icon.setBackgroundResource(R.drawable.category_gray);
                        commodity_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        break;
                    case R.id.layout_search:
                        search_icon.setBackgroundResource(R.drawable.search_gray);
                        search_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        break;
                    case R.id.layout_collection:
                        collection_icon.setBackgroundResource(R.drawable.collection_gray);
                        collection_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        break;
                    case R.id.layout_order:
                        order_icon.setBackgroundResource(R.drawable.order_gray);
                        order_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        break;
                    case R.id.layout_after_sale_service:
                        sale_service_icon.setBackgroundResource(R.drawable.sale_service_gray);
                        sale_service_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        break;
                    case R.id.layout_acution:
                        acution_icon.setBackgroundResource(R.drawable.acution_gray);
                        acution_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
                        break;
                }
            }
        }
    };


    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(!Utils.isConnected(mContext)){
                String error_tips = mContext.getResources().getString(R.string.error_network_exception);
                ToastUtils.showToast(mContext,error_tips);
                return;
            }
            switch (v.getId()){
                case R.id.layout_home:
                    Utils.print(tag,"home="+NAV_POS);
                    if(NAV_POS==NAV_HOME)
                        return;
                    Utils.print(tag,"home2");
                    selectFragment(ConStant.OP_HOME);
                    sendNavStatistics(NAV_HOME);
                    break;
                case R.id.layout_commodity:
                    Utils.print(tag,"commodity");
                    if(NAV_POS==NAV_COMMODITY)
                        return;
                    Utils.print(tag,"commodity2");
                    selectFragment(ConStant.OP_COMMODITY_CAGEGORY);
                    sendNavStatistics(NAV_COMMODITY);
                    break;
                case R.id.layout_search:
                    if(NAV_POS==NAV_SEARCH)
                        return;
                    Utils.print(tag,"commodity");
                    searchIsRequestFocus = false;
                    selectFragment(ConStant.OP_SEARCH_MAIN);
                    sendNavStatistics(NAV_SEARCH);
                    break;
                case R.id.layout_collection:
                    if(NAV_POS==NAV_COLLECTION)
                        return;
                    Utils.print(tag,"collection");
                    selectFragment(ConStant.OP_COLLECTION);
                    sendNavStatistics(NAV_COLLECTION);
                    break;
                case R.id.layout_order:
                    if(NAV_POS==NAV_ORDER)
                        return;
                    Utils.print(tag,"order");
                    selectFragment(ConStant.OP_ORDER);
                    sendNavStatistics(NAV_ORDER);
                    break;
                case R.id.layout_domy_shopping_cart:
                    Utils.print(tag,"shopping cart");
                    if(!Utils.isConnected(mContext)){
                        String error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        ToastUtils.showToast(mContext,error_tips);
                        return;
                    }
                    if(records!=null && records.size()>0){
                        ShoppingCartGrid.launch((MainActivity)mContext);
                    }else{
                        ToastUtils.showToast(mContext,mContext.getString(R.string.shopping_cart_null_tips));
                    }
                    break;
                case R.id.layout_after_sale_service:
                    if(NAV_POS==NAV_SALE_SERVICE)
                        return;
                    selectFragment(ConStant.OP_AFTER_SALE_SERVICE);
                    sendNavStatistics(NAV_SALE_SERVICE);
                    break;
                case R.id.layout_domy_user_center:
                    //layout_domy_user_center.requestFocus();
                   // Toast.makeText(mContext, "点击个人中心写入0", Toast.LENGTH_SHORT).show();
                    editor.putInt("pos",0);
                    editor.commit();
                    selectFragment(ConStant.OP_USER_CENTER);
                    break;
                case R.id.layout_acution:
                    Utils.print(tag,"acution");
                    if(NAV_POS==NAV_ACUTION)
                        return;
                    Utils.print(tag,"acution2");
                    selectFragment(ConStant.OP_ACUTION);
                    sendNavStatistics(NAV_ACUTION_SERVER);
                    break;
            }


            switch (v.getId()) {
                case R.id.layout_home:
                    NAV_POS = NAV_HOME;
                    break;
                case R.id.layout_commodity:
                    NAV_POS = NAV_COMMODITY;
                    break;
                case R.id.layout_collection:
                    NAV_POS = NAV_COLLECTION;
                    break;
                case R.id.layout_order:
                    NAV_POS = NAV_ORDER;
                    break;
                case R.id.layout_search:
                    NAV_POS = NAV_SEARCH;
                    break;
                case R.id.layout_after_sale_service:
                    NAV_POS = NAV_SALE_SERVICE;
                    break;
                case R.id.layout_domy_user_center:
                    NAV_POS = NAV_USERR_CENTER;
                    break;
            }


            resetNavSelect();
        }
    };


    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //Utils.print(tag,"onkey==="+keyCode);
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(v.getId()==R.id.layout_home && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_home);
                    layout_domy_user_center.setNextFocusRightId(R.id.layout_home);
                }else if(v.getId()==R.id.layout_commodity && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_commodity);
                    layout_domy_user_center.setNextFocusRightId(R.id.layout_commodity);
                }else if(v.getId()==R.id.layout_search && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_search);
                    layout_domy_user_center.setNextFocusRightId(R.id.layout_commodity);
                }else if(v.getId()==R.id.layout_collection && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_collection);
                    layout_domy_user_center.setNextFocusRightId(R.id.layout_commodity);
                }else if(v.getId()==R.id.layout_order && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_order);
                    layout_domy_user_center.setNextFocusRightId(R.id.layout_commodity);
                }else if(v.getId()==R.id.layout_after_sale_service && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_after_sale_service);
                    layout_domy_user_center.setNextFocusRightId(R.id.layout_commodity);
                }else if(v.getId()==R.id.layout_search && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    //Utils.print(tag,"111=="+ searchMain+"##"+searchMain.getmTvRecyclerView()+",##"+searchMain.getSearchKey());
                    //因为仅搜索主页才需要控制焦点变化，能看到搜索条的时候，向右需要切换到搜索条，不能看到的时候走正常焦点
                    if(currentFragment.equals(searchMain)){
                        //焦点走向搜索条
                        if(searchMain!=null && searchMain.getmTvRecyclerView()!=null && searchMain.getSearchKey()!=null && Utils.isVisibleLocal(searchMain.getSearchKey())){
                            layout_search.setNextFocusRightId(R.id.search_key);
                            return false;
                        }
                        //正常焦点流程
                        if(searchMain!=null && searchMain.getmTvRecyclerView()!=null && searchMain.getSearchKey()!=null){
                            layout_search.setNextFocusRightId(R.id.scrollview);
                            return false;
                        }
                        //页面没有加载完成的时候
                        return true;
                    }else if(currentFragment.equals(searchResult)){
                        if(searchResult==null || searchResult.getSearchResultSize()<=1){
                            Utils.print(tag,"121212");
                            return true;
                        }
                        Utils.print(tag,"size="+searchResult.getSearchResultSize());
                    }
                }else if(v.getId()==R.id.layout_collection && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    if(currentFragment.equals(collection)){
                        if(collection==null || collection.getCollectionSize()<=1){
                            Utils.print(tag,"232323");
                            return true;
                        }
                        Utils.print(tag,"size="+collection.getCollectionSize());
                    }
                }else if(v.getId()==R.id.layout_domy_user_center&&keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    if(NAV_POS==NAV_HOME){//主页
                        layout_domy_user_center.setNextFocusRightId(R.id.layout_home);
                    }else if(NAV_POS==NAV_COMMODITY){//商品分类
                        layout_domy_user_center.setNextFocusRightId(R.id.layout_commodity);
                    }else if(NAV_POS==NAV_COLLECTION){//收藏
                        layout_domy_user_center.setNextFocusRightId(R.id.layout_collection);
                    }else if(NAV_POS==NAV_SEARCH){//搜索
                        layout_domy_user_center.setNextFocusRightId(R.id.layout_search);
                    }else if(NAV_POS==NAV_ORDER){//订单
                        layout_domy_user_center.setNextFocusRightId(R.id.layout_order);
                    }else if(NAV_POS==NAV_SALE_SERVICE){//售后
                        layout_domy_user_center.setNextFocusRightId(R.id.layout_after_sale_service);
                    }
                }else if(v.getId()==R.id.layout_domy_shopping_cart&&keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    if(NAV_POS==NAV_HOME){//主页
                        layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_home);
                    }else if(NAV_POS==NAV_COMMODITY){//商品分类
                        layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_commodity);
                    }else if(NAV_POS==NAV_COLLECTION){//收藏
                        layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_collection);
                    }else if(NAV_POS==NAV_SEARCH){//搜索
                        layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_search);
                    }else if(NAV_POS==NAV_ORDER){//订单
                        layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_order);
                    }else if(NAV_POS==NAV_SALE_SERVICE){//售后
                        layout_domy_shopping_cart.setNextFocusRightId(R.id.layout_after_sale_service);
                    }
                }else if(v.getId()==R.id.layout_home && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    /*if(commodityRecommend!=null && !commodityRecommend.allowRequestFocus()){
                        return true;
                    }*/
                }else if(v.getId()==R.id.layout_commodity && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    if(commodityMainCategory!=null && (currentFragment instanceof CommodityMainCategory) && !commodityMainCategory.allowRequestFocus()){
                        return true;
                    }
                }
                //检查拍卖是否有数据
                if(navHasFocus() && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT && currentFragment.equals(acutionCommodity)){
                    if(acutionCommodity==null || acutionCommodity.getAcutionSize()<=1){
                        Utils.print(tag,"no acution");
                        return true;
                    }
                    Utils.print(tag,"size="+acutionCommodity.getAcutionSize());
                }
            }
            return false;
        }
    };






    private void showShoppingCartNullLayout(){
        //检测购物车
        Utils.print(tag,"showShoppingCartNullLayout");
        layout_domy_shopping_cart.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.home_shopping_cart_null, null);


        model = (TextView)view.findViewById(R.id.model);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout_domy_shopping_cart.addView(view,layoutParams);

    }


    private void showShoppingCartLayout(int totalSize){

        Utils.print(tag,"showShoppingCartLayout");
        layout_domy_shopping_cart.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.home_shopping_cart, null);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout_domy_shopping_cart.addView(view,layoutParams);

        LinearLayout.LayoutParams lp;
        Utils.print(tag,"records size=="+records.size());
        for(int i=0;i<totalSize;i++){
            SimpleDraweeView imageView = new SimpleDraweeView(this);
            imageView.setTag("shopping_item_"+i);
            imageView.setBackgroundResource(R.drawable.commodity_small_icon);
            FrescoHelper.setImage(imageView, Uri.parse(records.get(i).getSquareUrl()),new ResizeOptions(64,75));
            lp = new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.home_shopping_cart_item_width_100),
                    mContext.getResources().getDimensionPixelSize(R.dimen.home_shopping_cart_item_height_100));
            lp.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.home_shopping_cart_item_margin_top_0),mContext.getResources().getDimensionPixelSize(R.dimen.home_shopping_cart_item_margin_top_8),0,0);
            view.addView(imageView,lp);
            if(i>=2)  //最大允许放三个购物商品
                break;
        }

        titleText= new TextView(this);
        titleText.setText(mContext.getResources().getString(R.string.home_shopping_cart_text));
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        titleText.setTextColor(mContext.getResources().getColor(R.color.white));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX,mContext.getResources().getDimension(R.dimen.sp_16));
        titleText.getPaint().setFakeBoldText(true);
        lp.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.home_shopping_cart_item_margin_top3_0),mContext.getResources().getDimensionPixelSize(R.dimen.home_shopping_cart_text_margin_top_25),0,0);
        view.addView(titleText,lp);
        number = new TextView(this);
        String value = mContext.getResources().getString(R.string.home_shopping_cart_number,totalSize);
        number.setText(value);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        number.setTextColor(mContext.getResources().getColor(R.color.white));
        number.setTextSize(TypedValue.COMPLEX_UNIT_PX,mContext.getResources().getDimension(R.dimen.sp_24));
        number.getPaint().setFakeBoldText(true);
        lp.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.home_shopping_cart_item_margin_top4_0),mContext.getResources().getDimensionPixelSize(R.dimen.home_shopping_cart_text_margin_top_3),0,0);
        view.addView(number,lp);


        number.setTextColor(mContext.getResources().getColor(R.color.white));
        titleText.setTextColor(mContext.getResources().getColor(R.color.white));
        number.getPaint().setFakeBoldText(true);
        titleText.getPaint().setFakeBoldText(true);
    }

    private void resetNavSelect(){
        Utils.print(tag,"select="+NAV_POS);
        //reset normal
        image_home.setBackgroundResource(R.drawable.home_gray);
        image_acution.setBackgroundResource(R.drawable.acution_gray);
        image_commodity.setBackgroundResource(R.drawable.category_gray);
        image_search.setBackgroundResource(R.drawable.search_gray);
        image_collection.setBackgroundResource(R.drawable.collection_gray);
        image_order.setBackgroundResource(R.drawable.order_gray);
        image_after_sale_service.setBackgroundResource(R.drawable.sale_service_gray);


        if(NAV_POS==NAV_HOME){
            image_home.setBackgroundResource(R.drawable.home_red);
        }else if(NAV_POS==NAV_COMMODITY){
            image_commodity.setBackgroundResource(R.drawable.category_red);
        }else if(NAV_POS==NAV_COLLECTION){
            image_collection.setBackgroundResource(R.drawable.collection_red);
        }else if(NAV_POS==NAV_SEARCH){
            image_search.setBackgroundResource(R.drawable.search_red);
        }else if(NAV_POS==NAV_ORDER){
            image_order.setBackgroundResource(R.drawable.order_red);
        }else if(NAV_POS==NAV_SALE_SERVICE){
            image_after_sale_service.setBackgroundResource(R.drawable.sale_service_red);
        }else if(NAV_POS==NAV_ACUTION){
            image_acution.setBackgroundResource(R.drawable.acution_red);
        }



        //reset expand
        home_icon.setBackgroundResource(R.drawable.home_gray);
        acution_icon.setBackgroundResource(R.drawable.acution_gray);
        commodity_icon.setBackgroundResource(R.drawable.category_gray);
        search_icon.setBackgroundResource(R.drawable.search_gray);
        collection_icon.setBackgroundResource(R.drawable.collection_gray);
        order_icon.setBackgroundResource(R.drawable.order_gray);
        sale_service_icon.setBackgroundResource(R.drawable.sale_service_gray);


        home_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
        acution_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
        commodity_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
        search_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
        collection_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
        order_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));
        sale_service_text.setTextColor(mContext.getResources().getColor(R.color.main_nav_gray_color));


        if(NAV_POS==NAV_HOME){
            home_icon.setBackgroundResource(R.drawable.home_white);
            home_text.setTextColor(mContext.getResources().getColor(R.color.white));
        }else if(NAV_POS==NAV_COMMODITY){
            commodity_icon.setBackgroundResource(R.drawable.category_white);
            commodity_text.setTextColor(mContext.getResources().getColor(R.color.white));
        }else if(NAV_POS==NAV_COLLECTION){
            collection_icon.setBackgroundResource(R.drawable.collection_white);
            collection_text.setTextColor(mContext.getResources().getColor(R.color.white));
        }else if(NAV_POS==NAV_SEARCH){
            search_icon.setBackgroundResource(R.drawable.search_white);
            search_text.setTextColor(mContext.getResources().getColor(R.color.white));
        }else if(NAV_POS==NAV_ORDER){
            order_icon.setBackgroundResource(R.drawable.order_white);
            order_text.setTextColor(mContext.getResources().getColor(R.color.white));
        }else if(NAV_POS==NAV_SALE_SERVICE){
            sale_service_icon.setBackgroundResource(R.drawable.sale_service_white);
            sale_service_text.setTextColor(mContext.getResources().getColor(R.color.white));
        }else if(NAV_POS==NAV_ACUTION){
            acution_icon.setBackgroundResource(R.drawable.acution_white);
            acution_text.setTextColor(mContext.getResources().getColor(R.color.white));
        }


        if(NAV_POS!=NAV_HOME){
           commodityRecommend=null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Utils.print(tag,"keycode-------"+keyCode);
        if(currentFragment instanceof CollectionMain){
            boolean returnValue = ((CollectionMain) currentFragment).onKeyDown(keyCode,event);
            if(returnValue)
                return true;
        }


        if(keyCode == KeyEvent.KEYCODE_MENU){
            Utils.print(tag,"focus pos>>>"+NAV_POS);

        }else if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if(currentFragment!=null && currentFragment.equals(searchResult)) {
                searchIsRequestFocus = true;
                layout_search.requestFocus();
                selectFragment(ConStant.OP_SEARCH_MAIN);
                return true;
            }

            if(currentFragment!=null && currentFragment.equals(allCommodity)){
                selectFragment(ConStant.OP_HOME);
                return true;
            }

            if(editAddress!=null&&currentFragment!=null){
                if(currentFragment.equals(editAddress)){
                    selectFragment(ConStant.OP_USER_CENTER);
                    if(userCenterManager!=null){
                        currentFragment=userCenterManager;
                    }else{
                        if(addressManager!=null){
                            currentFragment=addressManager;
                        }
                    }

                    return true;
                }
            }

          /*  if(currentFragment!=null&&currentFragment.equals(orderMain)){
                Intent mIntent = new Intent("com.dianshang.damai.message_restore");
                //发送广播
                mIntent.putExtra("restore",true);
                this.sendBroadcast(mIntent);
                return true;
            }*/

            if ((System.currentTimeMillis() - exitTime) > 3000) {
                //弹出提示，可以有多种方式
                ToastUtils.showToast(this,getResources().getString(R.string.exit_tips));
                exitTime = System.currentTimeMillis();
                return true;
            }

            /*//直接退出
            Utils.print(tag,"exit");
            Intent mIntent = new Intent(ConStant.REMOTE_ACTION);
            sendBroadcast(mIntent);*/

        }else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!navHasFocus()) {//焦点在订单按钮或者其他各个Fragment里
                if (currentFragment != null && currentFragment.equals(orderMain) && (!layout_order.hasFocus())) {
                    Intent mIntent = new Intent("com.dianshang.damai.message_restore");
                    //发送广播
                    mIntent.putExtra("restore", false);
                    mIntent.putExtra("orderrefresh", false);
                    sendBroadcast(mIntent);

                    Intent acuintent = new Intent(mContext,AcutionService.class);
                    acuintent.putExtra("status",InstallService.EXIT);
                    startService(acuintent);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    //添加地址的弹出框
    public void showSetAddressWindow(){

        setAddressWindow=new SetAddressPopWindow(this, this.getResources().getString( R.string.settip),
                new SetAddressWindowInterface() {

                    @Override
                    public void onSkip() {
                        // TODO Auto-generated method stub
                        //Toast.makeText(MainActivity.this, "点击跳过！", Toast.LENGTH_LONG).show();
                        setAddressWindow.dismiss();
                        firstBoolean=false;
                    }

                    @Override
                    public void onSetting() {
                        // TODO Auto-generated method stub
                        //Toast.makeText(MainActivity.this, "点击设置！", Toast.LENGTH_LONG).show();
                        setAddressWindow.dismiss();
                        firstBoolean=false;
                        editor.putInt("pos",0);
                        editor.commit();
                        NAV_POS = NAV_USERR_CENTER;
                       // Toast.makeText(mContext, "点击设置写入0", Toast.LENGTH_SHORT).show();
                        selectFragment(ConStant.OP_SHOW_ADDRESS_MANAGER);
                        image_home.setBackgroundResource(R.drawable.home_gray);
                    }
                });
        setAddressWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                backgroundAlpha((float) 1.0);
            }
        });
        backgroundAlpha((float) dialogAlpha);
        setAddressWindow.showWindow();
        firstBoolean=false;


        //showNormalNav();
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 获取用户地址列表
     */
    public void getUserAddressList(){
        Utils.print(tag,"getUserAddressList");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("pageIndex","1");
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetUserAddressListData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserAddress>() {
                    @Override
                    public void onCompleted() {
                        if(!userDatasBoolean){//无用户数据时显示
                            Log.i("=========","===显示地址对话框！");
                            //Toast.makeText(mContext, "显示地址对话框eancheng！", Toast.LENGTH_SHORT).show();
                            if(setAddressWindow!=null){
                                if(!setAddressWindow.isShowing()) {
                                   // Toast.makeText(mContext, "显示地址对话框！", Toast.LENGTH_SHORT).show();
                                    showSetAddressWindow();
                                    firstBoolean = false;
                               }
                            }else{
                                showSetAddressWindow();
                                firstBoolean = false;
                            }

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"===error>>>>"+e.getMessage());
                    }

                    @Override
                    public void onNext(UserAddress userAddress) {
                        if(userAddress.getReturnValue()==-1)
                            return;
                        List<UserData> userDatas = userAddress.getData().getRecords();
                        if(userDatas.size()==0){
                            userDatasBoolean=false;
                        }else{
                            userDatasBoolean=true;
                        }
                    }
                });
        addSubscription(subscription);
    }


    /**
     * 获取购物车商品列表
     */
    public void getShoppingCartCommodity(){
        Utils.print(tag,"getShoppingCartCommodity");
        records.clear();
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
                .httpGetShoppingCartData(ConStant.APP_VERSION,input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"main error="+e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartData shoppingCartData) {
                        try{
                            Utils.print(tag,"main status=="+shoppingCartData.getErrorMessage()+",value="+shoppingCartData.getReturnValue());
                            if(shoppingCartData.getReturnValue()==-1){
                                showShoppingCartNullLayout();
                                return;
                            }


                            int total = 0;
                            List<GroupData> groupDatas = shoppingCartData.getData();
                            if(groupDatas!=null){
                                for (int i = 0; i < groupDatas.size(); i++) {
                                    for (int j = 0; j < groupDatas.get(i).getSkuList().size(); j++) {
                                        total = total + 1;
                                        records.add(groupDatas.get(i).getSkuList().get(j));
                                    }
                                }
                            }


                            Utils.print(tag,"shop record size="+total);
                            /*for(int i=0;i<records.size();i++){
                                Utils.print(tag,"name=="+records.get(i).getGoodsName());
                                Utils.print(tag,"quantity=="+records.get(i).getBuyNum());
                                Utils.print(tag,"carid=="+records.get(i).getCartId());
                            }*/
                            if(total>0){
                                showShoppingCartLayout(total);
                            }else{
                                showShoppingCartNullLayout();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });
        addSubscription(s);
    }


    /**
     * 发送点击首页 分类 搜索 收藏等导航的埋点统计到后台
     */
    private void sendNavStatistics(int pos){
        HashMap<String,String> simpleMap=new HashMap<String,String>();

        simpleMap.put("tabNo","");
        simpleMap.put("actionType","Ec1002");
        simpleMap.put("actionInfo","button="+pos);
        EBusinessApplication.getHSApi().addAction(simpleMap);
    }

    public class ReceiverAfter extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
           // Toast.makeText(mContext,"接收到信号sss！",Toast.LENGTH_LONG).show();
            if(NAV_POS==NAV_SALE_SERVICE)
                return;
            selectFragment(ConStant.OP_AFTER_SALE_SERVICE);
            NAV_POS = NAV_SALE_SERVICE;
            resetNavSelect();
        }

    }


    /**
     * 更新阴影层的宽度，随着焦点的移动
     */
    private void updateLayerWidth(){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)layout_no_focus_layer.getLayoutParams();
        if(layout_commodity.isFocused() || layout_search.isFocused() || layout_acution.isFocused() ||
                layout_home.isFocused() || layout_collection.isFocused() ||
                layout_order.isFocused() || layout_after_sale_service.isFocused()){
            layoutParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.main_nav_width_93)*2,0,0,0);
        }else{
            layoutParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.main_nav_width_93),0,0,0);
        }
        layout_no_focus_layer.setLayoutParams(layoutParams);
    }



    /**
     * 检测导航栏是是否获取到焦点
     * @return
     */
    private boolean navHasFocus(){
        boolean hasFocus = false;
        if (layout_home.isFocused() || layout_commodity.isFocused() ||
                layout_acution.isFocused() ||
                layout_search.isFocused() || layout_collection.isFocused() ||
                layout_order.isFocused() || layout_after_sale_service.isFocused()) {
            hasFocus = true;
        }
        return hasFocus;
    }


    /**
     * 导航栏焦点处理
     * @param isHasFocus
     */
    private void setNavFocus(boolean isHasFocus) {

        Utils.print(tag,"setNavFocus="+isHasFocus);
        if (isHasFocus) {
            layout_home.setFocusable(true);
            layout_acution.setFocusable(true);
            layout_commodity.setFocusable(true);
            layout_search.setFocusable(true);
            layout_collection.setFocusable(true);
            layout_order.setFocusable(true);
            layout_after_sale_service.setFocusable(true);

            layout_domy_user_center.setFocusable(true);
            layout_domy_shopping_cart.setFocusable(true);
        } else {
            layout_home.setFocusable(false);
            layout_acution.setFocusable(false);
            layout_commodity.setFocusable(false);
            layout_search.setFocusable(false);
            layout_collection.setFocusable(false);
            layout_order.setFocusable(false);
            layout_after_sale_service.setFocusable(false);

            layout_domy_user_center.setFocusable(false);
            layout_domy_shopping_cart.setFocusable(false);
        }
    }
}
