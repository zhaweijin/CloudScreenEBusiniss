package com.hiveview.dianshang.shoppingcart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.ShoppingAdapternew;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.ConfirmDialog;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.shoppingcart.ActivityInfoData;
import com.hiveview.dianshang.entity.shoppingcart.FullCutBean;
import com.hiveview.dianshang.entity.shoppingcart.GroupData;
import com.hiveview.dianshang.entity.shoppingcart.LimitActivityData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartRecord;
import com.hiveview.dianshang.entity.shoppingcart.discount.DiscountData;
import com.hiveview.dianshang.entity.shoppingcart.discount.DiscountType;
import com.hiveview.dianshang.entity.shoppingcart.discount.FullCutSkuData;
import com.hiveview.dianshang.entity.shoppingcart.discount.PostCartItemInfo;
import com.hiveview.dianshang.entity.token.TokenData;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.utils.DeviceInfo;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import java.lang.reflect.Array;
import java.math.BigDecimal;
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
 * 购物车页面 网格显示UI
 * Created by Gavin on 4/10/17.
 */

public class ShoppingCartGrid extends BaseActivity implements View.OnClickListener{
    /**
     * 商品个数
     */
    @BindView(R.id.goodscount)
    TextView goodscount;
    /**
     * 商品列表
     */
    @BindView(R.id.listView)
    ListView listView;
    /**
     * 全选商品按钮
     */
    @BindView(R.id.btn_select)
    Button btn_select;
    /**
     * 清空商品按钮
     */
    @BindView(R.id.btn_delete)
    Button btn_delete;
    /**
     * 价格
     */
    @BindView(R.id.price)
    TextView price;
    /**
     * 优惠
     */
    @BindView(R.id.discount)
    TextView discount;
    /**
     * 购买数量
     */
    @BindView(R.id.count)
    TextView count;
    /**
     * 合计价格
     */
    @BindView(R.id.totalprice)
    TextView totalprice;
    /**
     * 结算按钮
     */
    @BindView(R.id.btn_shopping)
    Button btn_shopping;

    /**
     * 购物车优惠价格layout
     */
    @BindView(R.id.layout_shoppingcart_discount)
    LinearLayout layout_shoppingcart_discount;

    /*
    * 购物车不为空的布局
    * */
    private RelativeLayout layout_has;
    /*
    * 购物车为空时的布局
    * */
    private LinearLayout layout_empty;
    /*
    * 购物车为空时，去购物的按钮
    * */
    private Button goshopping;

    //辨别显示是全选还是取消字样
    private static boolean selectBoolean=true;
    private String tag="ShoppingCartGrid";
    //全局变量，记录选中的item
    public static int select_item = -1;
    private ShoppingAdapternew adapter;
    private boolean isFrist=true;
    //是否是落焦点，还是点击处理的标识
    public static boolean isFocus=true;
    /*
    * 点击结算，然后按返回后，刷新数据的标识
    * */
    private boolean isjiasuanBackRefresh=false;
    //用于记录选中商品的sn
    private List<Long> selectedIdList=new ArrayList<Long>();
    /*
    *购物车列表集合
    * */
    private  List<GroupData> groupDatas;
    //确定清空购物车对话框
    private ConfirmDialog confirmDialog;

    /**
     * 服务器交互标志位
     */
    public boolean op_status = false;
    //进入详情页后是否刷新购物车页面的标识
    public static boolean isRefresh=false;
    /*
    * 是否第一次进入购物车，用于详情加入购物车时，再次进入购物车加载数据的标识
    * */
    private boolean firstShopping=true;
    private boolean isFromLauncher = false;
    //接收更新消息的变量
    private Observable<String > observable_refresh;
    private ReceiverShopping receiverShopping;
    //为解决右键本应落入结算按钮，结果落在了全选按钮上
    private boolean isRightFocus=false;



    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ShoppingCartGrid.class);
        intent.putExtra("isFromLauncher",false);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoppingnew_layout_total);
        initView();
        onListViewListener();
        //注册广播
        receiverShopping = new ReceiverShopping();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dianshang.damai.shoppingnew");
        mContext.registerReceiver(receiverShopping, intentFilter);
    }
    public void initView(){
        layout_has=(RelativeLayout)findViewById(R.id.layout_has);
        layout_empty=(LinearLayout)findViewById(R.id.layout_empty);
        goshopping=(Button)findViewById(R.id.goshopping);
        isFromLauncher = getIntent().getBooleanExtra("isFromLauncher",true);
        btn_select.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_shopping.setOnClickListener(this);
        goshopping.setOnClickListener(this);
        btn_select.setOnKeyListener(onKeyListener);
        btn_delete.setOnKeyListener(onKeyListener);
        btn_shopping.setOnKeyListener(onKeyListener);
        listView.setItemsCanFocus(true); //若不添加该行代码，将会导致item点击无效
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isFrist&&!firstShopping){//再次刷新第一次
                   adapter.notifyDataSetChanged();
                    isFrist=false;
                    return;
                }
                if(!isFrist&&!firstShopping){//再次刷新非首次
                    select_item=position;
                    Log.i("==22222==","mian214");
                    isFocus=true;
                    adapter.notifyDataSetChanged();
                    return;

                }
                if(firstShopping){//第一次进入购物车
                    select_item=position;
                    Log.i("==22222==","mian222"+select_item);
                    if(!isFrist){
                        isFocus=true;
                       adapter.notifyDataSetChanged();
                    }
                    isFrist=false;
                    return;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_select.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("===3333s===","全选=="+hasFocus);
                Log.i("==22sss==","btn_select==hasFocus=="+hasFocus);
                if(hasFocus&&adapter!=null){
                    Log.i("===333===","全选焦点落入");
                    select_item=-1;
                    isFocus=true;
                    adapter.notifyDataSetChanged();
                }

                if(hasFocus&&isRightFocus){
                    btn_shopping.setFocusable(true);
                    btn_shopping.requestFocus();
                    isRightFocus=false;
                }
            }
        });
        btn_delete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("===3333s===","清空=="+hasFocus);
               if(hasFocus&&adapter!=null){
                    Log.i("===333===","清空焦点落入");
                    select_item=-1;
                    isFocus=true;
                    adapter.notifyDataSetChanged();
                }
            }
        });
        btn_shopping.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("===3333s===","结算=="+hasFocus);
                if(hasFocus&&adapter!=null){
                    Log.i("===333===","结算焦点落入");
                    select_item=-1;
                    isFocus=true;
                  adapter.notifyDataSetChanged();
                }
            }
        });
        /*
        * 接收商品更新消息！！！
        * */
        observable_refresh = RxBus.get().register(ConStant.obString_update_shopping_cart, String.class);
        observable_refresh.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Utils.print("position!!!===", "11update..."+s);
                String sn=s;
                //和进入详情需要再次刷新的代码一样，逻辑不变
                isFrist=true;
                isFocus=true;
                isRefresh=false;
                adapter=new ShoppingAdapternew(mContext,new ArrayList<GroupData>(),listView,selectedIdList,goshopping,layout_empty,layout_has,price,discount,count,totalprice,btn_select,goodscount,layout_shoppingcart_discount);
                listView.setAdapter(adapter);
                getShoppingCartCommodity(false);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("=====sssss=====","select_itemonResume=="+select_item);
        Log.i("=====sssss=====","isFocusonResume=="+isFocus);
        Log.i("=====sssss=====","isRefreshonResume=="+isRefresh);
        Log.i("=====sssss=====","isFristonResume=="+isFrist);
        if(isFrist){//首次进入购物车，请求数据  只在本类中做了定义，其他地方暂无定义
            //在selection里设置为isFrist=false;
            Log.i("=====sssss=====","进入==320");
            select_item = -1;
            isFocus=true;
            isRefresh=false;
            isjiasuanBackRefresh=false;
            adapter=new ShoppingAdapternew(mContext,new ArrayList<GroupData>(),listView,selectedIdList,goshopping,layout_empty,layout_has,price,discount,count,totalprice,btn_select,goodscount,layout_shoppingcart_discount);
            listView.setAdapter(adapter);
            //add 检测token是否失效
            String user = (String) SPUtils.get(mContext, ConStant.USERID, "");
            String token = (String) SPUtils.get(mContext, ConStant.USER_TOKEN, "");
            if (!user.equals("") && !token.equals("")) {
                Utils.print(tag, "set token");
                ConStant.userID = user;
                ConStant.Token = token;
                getShoppingCartCommodity(true);
            }else{
                getTokenData();
            }


        }
        //进入详情页后是否刷新购物车页面的标识
        if(isRefresh){//进入详情页后，点击了加入购物车，数据需要刷新此时isRefresh设为true;其他进入详情页的情况无需刷新
            //除了本类定义，点击详情按钮定义，还有就是进入详情后，点击加入购物车时定义啦，其他地方暂无定义
            //isRefresh用完true,直接再设置为false；
            //在更新数据时，用了一样的代码
            Log.i("=====sssss=====","进入==346");
            isFrist=true;
            isFocus=true;
            isRefresh=false;
            isjiasuanBackRefresh=false;
            adapter=new ShoppingAdapternew(mContext,new ArrayList<GroupData>(),listView,selectedIdList,goshopping,layout_empty,layout_has,price,discount,count,totalprice,btn_select,goodscount,layout_shoppingcart_discount);
            listView.setAdapter(adapter);
            getShoppingCartCommodity(false);
        }
        //进入结算页面后返回到购物车页面，数据需要刷新一下
       // isjiasuanBackRefresh=false表示不刷新，，，true表示刷新
        //只有在点击结算时为true，初始都为false
        if(isjiasuanBackRefresh){
            Log.i("=====sssss=====","进入==359");
            getShoppingCartCommodityToRefreshIcon();
            isjiasuanBackRefresh=false;
        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(ConStant.obString_update_shopping_cart, observable_refresh);
        try{
            this.unregisterReceiver(receiverShopping);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        //正在交互
        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            return;
        }
        if (op_status){
            return;
        }
        switch (v.getId()) {
            case R.id.btn_select:
                if(btn_select.getText().toString().equals(mContext.getResources().getString(R.string.checkall))){//显示的全选（目前商品没有全选）
                    List<ShoppingCartRecord> newBeanList=adapter.getAllList();
                    boolean isOk=true;
                    boolean isLimit=false;
                    boolean isStokeInvalid=false;
                    for(int i=0;i<newBeanList.size();i++){
                        int buyCount=newBeanList.get(i).getBuyNum();
                        LimitActivityData limitBean=newBeanList.get(i).getLimitActivity();
                        if(limitBean!=null){
                           int limitCount=limitBean.getLimitNum();
                           int buyedCount=limitBean.getBuyNum();
                            if(buyCount+buyedCount>limitCount&&limitBean.isLimitFlag()&&(!newBeanList.get(i).getType().equals(ConStant.INVALID))){
                                isOk=false;
                                isLimit=true;
                                break;
                            }
                        }
                    }

                    for(int i=0;i<newBeanList.size();i++){
                        int buyCount=newBeanList.get(i).getBuyNum();
                        int stokeNum=newBeanList.get(i).getStockNum();
                        if(stokeNum<buyCount&&(!newBeanList.get(i).getType().equals(ConStant.INVALID))){//库存不足
                            isOk=false;
                            isStokeInvalid=true;
                            break;
                        }
                    }

                    if(isOk){
                        btn_select.setText(mContext.getResources().getString(R.string.cancel));
                        Drawable img= mContext.getResources().getDrawable(R.drawable.cancel_icon);
                        //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                        btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                        btn_select.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                        selectBoolean=false;
                    }else{
                        selectBoolean=true;
                        if(isLimit){//限购提示
                            ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.limitbuytip));
                        }else{//库存不足提示
                            if(isStokeInvalid){
                                ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.stocklimittip));
                            }

                        }

                    }

                    //将所有有效商品的sn，都记录在选中集合内
                    //selectedIdList
                    if(groupDatas!=null){
                        selectedIdList.clear();
                        boolean ishasselect=false;
                        for(int i=0;i<groupDatas.size();i++){
                            if(!groupDatas.get(i).getPromotionType().equals(ConStant.INVALID)){//只统计有效商品
                                List<ShoppingCartRecord> shoppingList=groupDatas.get(i).getSkuList();
                                for(int j=0;j<shoppingList.size();j++){
                                    LimitActivityData limit=shoppingList.get(j).getLimitActivity();
                                    int num=shoppingList.get(j).getBuyNum();
                                    int stokeNum=shoppingList.get(j).getStockNum();
                                    if(limit!=null){
                                        int numed=limit.getBuyNum();
                                        int limitNumber=limit.getLimitNum();
                                        if(num+numed<=limitNumber){
                                            if(stokeNum>=num){
                                                selectedIdList.add(shoppingList.get(j).getCartItemId());
                                            }

                                        }else{
                                            if(!limit.isLimitFlag()){
                                                if(stokeNum>=num){
                                                    selectedIdList.add(shoppingList.get(j).getCartItemId());
                                                }
                                            }
                                        }
                                    }else{
                                        if(stokeNum>=num){
                                            selectedIdList.add(shoppingList.get(j).getCartItemId());
                                        }

                                    }

                                }

                            }
                        }

                        select_item=-1;
                        isFocus=true;
                        adapter.notifyDataSetChanged();

                    }


                }else{//显示的取消(目前商品状态处于全选状态)
                    btn_select.setText(mContext.getResources().getString(R.string.checkall));
                    Drawable img= mContext.getResources().getDrawable(R.drawable.select_icon);
                    //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                    btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                    btn_select.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                    selectBoolean=true;
                    if(adapter!=null){
                        selectedIdList.clear();
                        select_item=-1;
                        isFocus=true;
                        adapter.notifyDataSetChanged();
                    }


                }
                //刷新一下价格等变量
                if(selectedIdList.size()==0){
                    //原始总价
                    price.setText("¥0.00");
                    //优惠总价
                    totalprice.setText("¥0.00");
                    //优惠了的总价
                    discount.setText("-¥0.00");
                    count.setText("0");
                }else{
                    getShoppingCartDiscountRefrsh(getCartItemIdList());
                }

                break;
            case R.id.btn_delete:
                showClearShoppingCartDialog();
                break;
            case R.id.btn_shopping:
                //进入提交订单页后，按返回键，是否刷新页面
                isjiasuanBackRefresh=true;
                //检测网络状态
                if(!Utils.isConnected(mContext)){
                    String error_tips = mContext.getResources().getString(R.string.error_network_exception);
                    ToastUtils.showToast(mContext,error_tips);
                    return;
                }
                if(selectedIdList.size()==0){//无选中商品
                    ToastUtils.showToast(mContext, mContext.getResources().getString(R.string.paytip));
                }else{
                   //获得选中的数据
                    ArrayList<ShoppingCartRecord> totalData=getList();
                    ArrayList<ShoppingCartRecord> selectedData=new ArrayList<ShoppingCartRecord>();
                    List<Long> cartItemIdList= new ArrayList<Long>();
                    for(int i=0;i<selectedIdList.size();i++){
                        for(int j=0;j<totalData.size();j++){
                            if(!totalData.get(j).isTitleBoolean()&&totalData.get(j).getCartItemId()==(selectedIdList.get(i))){//正常商品数据，非头部数据
                                selectedData.add(totalData.get(j));
                                cartItemIdList.add(totalData.get(j).getCartItemId());
                                break;
                            }
                        }
                    }
                    getShoppingCartDiscount(selectedData,cartItemIdList);
                }
                break;
            case R.id.goshopping:
                if(isFromLauncher)
                    MainActivity.launch(ShoppingCartGrid.this);
                finish();

                break;
            default:
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT&& event.getAction() == KeyEvent.ACTION_DOWN) {
            if(listView.getSelectedView()!=null){
                if(listView.getSelectedView().getTop()>mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_180)){
                    isRightFocus=true;
                }else{
                    isRightFocus=false;
                }
                btn_select.setFocusable(true);
                listView.setFocusable(false);
            }

        }
     /*   if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER&& event.getAction() == KeyEvent.ACTION_DOWN){
            if(adapter!=null&&adapter.getItemViewType(select_item)==1){
                ShoppingCartGrid.isFocus=false;
                Log.i("==4444==","select_item"+select_item);
                adapter.notifyDataSetChanged();

            }

        }*/
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取购物车商品列表
     */
    public void getShoppingCartCommodity(boolean b) {//b为true，是第一次进入购物车刷新数据；b为false，为并非第一次进入购物车刷新数据
        Utils.print(tag, "getShoppingCartCommodity");
        //检测网络状态
        if (!Utils.isConnected(mContext)) {
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext, error_tips);
            return;
        }

        //正在交互
        if (mProgressDialog != null && mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag, "op_status==" + op_status);
        if (op_status)
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

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetShoppingCartData(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartData>() {
                    @Override
                    public void onCompleted() {
                        firstShopping=b;
                        if(!b){//非首次进入购物车
                            if(groupDatas.get(0).getPromotionType().equals(ConStant.COMMON)){//判断一下，数据一个是什么，是按钮就落在第二个，否则就落在第一个数据上
                                select_item=0;
                            }else{//满赠，满减，失效商品，因为有凑单，清空按钮，焦点要落在数据的第二个位置，即第一个商品的位置
                                select_item=1;
                                Log.i("==22222==","mian569");
                            }

                        }
                        if(!b){//只有在数据更新时才会有检测选中标识，b为true时，是第一次进入购物车，数据全新
                             /*
                        * 检测一下，记录选中集合里是否存在失效商品
                        * */
                            for(int i=0;i<groupDatas.size();i++){
                                if(groupDatas.get(i).getPromotionType().equals(ConStant.INVALID)){
                                    List<ShoppingCartRecord> invalidList=groupDatas.get(i).getSkuList();
                                    for(int j=0;j<invalidList.size();j++){
                                        if(selectedIdList.contains(invalidList.get(j).getCartItemId())){//选中集合中含有无效商品，则删除
                                            selectedIdList.remove(invalidList.get(j).getCartItemId());
                                        }
                                    }

                                }
                            }

                              /*
                        * 存在有效商品已结算提交，购物车不存在此商品
                        * */
                            ArrayList<ShoppingCartRecord> beanList=getList();
                            List<Long> selectedIdListTotal=new ArrayList<Long>();
                            for(int i=0;i<beanList.size();i++){
                                selectedIdListTotal.add(beanList.get(i).getCartItemId());
                            }
                            List<Long> selectedIdListTotalDelete=new ArrayList<Long>();
                            for(int i=0;i<selectedIdList.size();i++){
                                if(!selectedIdListTotal.contains(selectedIdList.get(i))){
                                    selectedIdListTotalDelete.add(selectedIdList.get(i));

                                }
                            }
                            if(selectedIdListTotalDelete.size()!=0){
                                selectedIdList.removeAll(selectedIdListTotalDelete);
                                //检测全选按钮的文本显示
                                if(btn_select.getText().toString().equals(mContext.getResources().getString(R.string.cancel))){
                                    btn_select.setText(mContext.getResources().getString(R.string.checkall));
                                    Drawable img= mContext.getResources().getDrawable(R.drawable.select_icon);
                                    //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                    btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                    btn_select.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                                }
                            }

                        }
                        if(groupDatas!=null){
                            adapter.addAll(groupDatas);
                            isRequestItemFocus = true;
                            int countValid=0;
                            for(int i=0;i<groupDatas.size();i++){
                                if(!groupDatas.get(i).getPromotionType().equals(ConStant.INVALID)){
                                    List<ShoppingCartRecord> validList=groupDatas.get(i).getSkuList();
                                    for(int j=0;j<validList.size();j++){
                                        countValid=countValid+1;
                                    }
                                }
                                }
                            if(selectedIdList.size()!=countValid){
                                //检测全选按钮的文本显示
                                if(btn_select.getText().toString().equals(mContext.getResources().getString(R.string.cancel))){
                                    btn_select.setText(mContext.getResources().getString(R.string.checkall));
                                    Drawable img= mContext.getResources().getDrawable(R.drawable.select_icon);
                                    //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                    btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                    btn_select.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                                }
                            }
                        }


                        //刷新一下价格等变量
                        if(selectedIdList.size()==0){
                            //原始总价
                            price.setText("¥0.00");
                            //优惠总价
                            totalprice.setText("¥0.00");
                            //优惠了的总价
                            discount.setText("-¥0.00");
                            count.setText("0");
                        }else{
                            getShoppingCartDiscountRefrsh(getCartItemIdList());
                        }




                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag, "2error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartData shoppingCartData) {
                        Utils.print(tag, "status==" + shoppingCartData.getErrorMessage() + ",value=" + shoppingCartData.getReturnValue());
                        stopProgressDialog();
                        op_status = false;
                        if (shoppingCartData.getReturnValue() == -1) {
                            ToastUtils.showToast(mContext, shoppingCartData.getErrorMessage());
                            return;
                        }
                        //仅第一次才判断token是否失效
                        if(b && shoppingCartData.getReturnValue()==-2){   //token已经失效,再次请求
                            getTokenData();
                            return;
                        }
                        groupDatas = shoppingCartData.getData();
                        for(int i=0;i<groupDatas.size();i++){
                            for(int j=0;j<groupDatas.get(i).getSkuList().size();j++){
                                ShoppingCartRecord bean=groupDatas.get(i).getSkuList().get(j);
                                Log.i("=====55555s=====","price=="+bean.getOriginalPrice());
                            }

                        }
                        if(b){//首次进入购物车，数据为空时的UI处理
                            if(groupDatas==null){
                                layout_has.setVisibility(View.GONE);
                                layout_empty.setVisibility(View.VISIBLE);
                                goshopping.requestFocus();
                                select_item=-1;
                                selectedIdList.clear();
                                return;

                            }
                            if(groupDatas!=null&&groupDatas.size()==0){
                                layout_has.setVisibility(View.GONE);
                                layout_empty.setVisibility(View.VISIBLE);
                                goshopping.requestFocus();
                                select_item=-1;
                                selectedIdList.clear();
                                return;

                            }

                        }
                    }
                });
    }
    //获取购物车列表数据
    public void getShoppingCartCommodityToRefreshIcon() {
        Utils.print(tag, "getShoppingCartCommodity2");
        Utils.print(tag, "clearShoppingCartCommodity");
        //检测网络状态
        if (!Utils.isConnected(mContext)) {
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext, error_tips);
            return;
        }

        //正在交互
        if (mProgressDialog != null && mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag, "op_status==" + op_status);
        if (op_status)
            return;
        op_status = true;

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
                .httpGetShoppingCartData(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag, "2error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartData shoppingCartData) {
                        Utils.print(tag, "status==" + shoppingCartData.getErrorMessage() + ",value=" + shoppingCartData.getReturnValue());
                        op_status = false;
                        if (shoppingCartData.getReturnValue() == -1) {
                            ToastUtils.showToast(mContext, shoppingCartData.getErrorMessage());
                            showNullCard();
                            return;
                        }
                        groupDatas = shoppingCartData.getData();
                        if(groupDatas==null){
                            layout_has.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                            goshopping.requestFocus();
                            select_item=-1;
                            selectedIdList.clear();
                            return;

                        }
                        if(groupDatas!=null&&groupDatas.size()==0){
                            layout_has.setVisibility(View.GONE);
                            layout_empty.setVisibility(View.VISIBLE);
                            goshopping.requestFocus();
                            select_item=-1;
                            selectedIdList.clear();
                            return;

                        }
                        /*
                        * 检测一下，记录选中集合里是否存在失效商品
                        * */
                        for(int i=0;i<groupDatas.size();i++){
                            if(groupDatas.get(i).getPromotionType().equals(ConStant.INVALID)){
                                List<ShoppingCartRecord> invalidList=groupDatas.get(i).getSkuList();
                                for(int j=0;j<invalidList.size();j++){
                                    if(selectedIdList.contains(invalidList.get(j).getCartItemId())){//选中集合中含有无效商品，则删除
                                        selectedIdList.remove(invalidList.get(j).getCartItemId());
                                    }
                                }

                            }
                        }
                        /*
                        * 存在有效商品已结算提交，购物车不存在此商品
                        * */
                        ArrayList<ShoppingCartRecord> beanList=getList();
                        List<Long> selectedIdListTotal=new ArrayList<Long>();
                        List<Long> selectedIdListStoke=new ArrayList<Long>();
                        for(int i=0;i<beanList.size();i++){
                            selectedIdListTotal.add(beanList.get(i).getCartItemId());
                            if(beanList.get(i).getBuyNum()>beanList.get(i).getStockNum()){//库存不足
                                selectedIdListStoke.add(beanList.get(i).getCartItemId());
                            }
                        }
                        List<Long> selectedIdListTotalDelete=new ArrayList<Long>();
                        for(int i=0;i<selectedIdList.size();i++){
                            if(!selectedIdListTotal.contains(selectedIdList.get(i))){
                                //selectedIdList.remove(selectedIdList.get(i));
                                selectedIdListTotalDelete.add(selectedIdList.get(i));

                            }
                        }
                        if(selectedIdListTotalDelete.size()!=0){
                            selectedIdList.removeAll(selectedIdListTotalDelete);
                            //检测全选按钮的文本显示
                            if(btn_select.getText().toString().equals(mContext.getResources().getString(R.string.cancel))){
                                btn_select.setText(mContext.getResources().getString(R.string.checkall));
                                Drawable img= mContext.getResources().getDrawable(R.drawable.select_icon);
                                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                btn_select.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                            }
                        }
                           /*
                        * 重新回到购物车页面，存在有效商品中库存不足的情况，若选中状态，要取消选中。
                        * */
                           //selectedIdListStoke
                        for(int i=0;i<selectedIdListStoke.size();i++){
                            if(selectedIdList.contains(selectedIdListStoke.get(i))){
                                selectedIdList.remove(selectedIdListStoke.get(i));
                                //检测全选按钮的文本显示
                                if(btn_select.getText().toString().equals(mContext.getResources().getString(R.string.cancel))){
                                    btn_select.setText(mContext.getResources().getString(R.string.checkall));
                                    Drawable img= mContext.getResources().getDrawable(R.drawable.select_icon);
                                    //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                    img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                    btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                    btn_select.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                                }
                            }
                        }

                        adapter.addAll(groupDatas);
                        int countValid=0;
                        for(int i=0;i<groupDatas.size();i++){
                            if(!groupDatas.get(i).getPromotionType().equals(ConStant.INVALID)){
                                List<ShoppingCartRecord> validList=groupDatas.get(i).getSkuList();
                                for(int j=0;j<validList.size();j++){
                                    countValid=countValid+1;
                                }
                            }
                        }
                        if(selectedIdList.size()!=countValid){
                            //检测全选按钮的文本显示
                            if(btn_select.getText().toString().equals(mContext.getResources().getString(R.string.cancel))){
                                btn_select.setText(mContext.getResources().getString(R.string.checkall));
                                Drawable img= mContext.getResources().getDrawable(R.drawable.select_icon);
                                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                                btn_select.setCompoundDrawables(img, null, null, null); //设置左图标
                                btn_select.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0,mContext.getResources().getDimensionPixelSize(R.dimen.gap_shopping_50),0);
                            }
                        }


                        //刷新一下价格等变量
                        if(selectedIdList.size()==0){
                            //原始总价
                            price.setText("¥0.00");
                            //优惠总价
                            totalprice.setText("¥0.00");
                            //优惠了的总价
                            discount.setText("-¥0.00");
                            count.setText("0");
                        }else{
                            getShoppingCartDiscountRefrsh(getCartItemIdList());
                        }



                    }
                });
    }


    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                //正在交互
                if(((ShoppingCartGrid)mContext).mProgressDialog!=null && ((ShoppingCartGrid)mContext).mProgressDialog.isShowing())
                    return true;
                if((v.getId()) == (btn_select.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    listView.setFocusable(true);
                    if(adapter!=null){
                        adapter.isItemFocus=true;
                    }

                   if(listView.getSelectedItemPosition()==0&&adapter!=null&&adapter.getItemViewType(0)==1){//焦点落在首个普通商品上
                        if(listView.getSelectedItemPosition()!=-1&&adapter!=null){
                            select_item=listView.getSelectedItemPosition();
                            Log.i("===333344===","select_item=="+select_item);
                            Log.i("===333344===","type=="+adapter.getItemViewType(listView.getSelectedItemPosition()));
                            Log.i("==22222==","mian849=="+select_item);
                            isFocus=true;
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                select_item=listView.getSelectedItemPosition();
                                Log.i("===333344===","select_item=="+select_item);
                                listView.setSelection(listView.getFirstVisiblePosition());
                            }
                        });
                        Log.i("===333344===","type=="+adapter.getItemViewType(listView.getSelectedItemPosition()));
                        if(adapter.getItemViewType(listView.getSelectedItemPosition())==0){
                            Button btn=(Button)((LinearLayout)((LinearLayout)listView.getSelectedView()).getChildAt(0)).getChildAt(2);
                            btn.setFocusable(true);
                            btn.requestFocus();
                        }
                    if(adapter.getItemViewType(listView.getSelectedItemPosition())==2){
                        Button btn=(Button)((LinearLayout)((LinearLayout)listView.getSelectedView()).getChildAt(0)).getChildAt(1);
                        btn.setFocusable(true);
                        btn.requestFocus();
                    }
                    }

                }else if((v.getId()) == (btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    listView.setFocusable(true);
                    if(listView.getSelectedItemPosition()!=-1&&adapter!=null){
                        select_item=listView.getSelectedItemPosition();
                        Log.i("==22222==","mian857");
                        isFocus=true;
                        adapter.notifyDataSetChanged();
                    }
                }/*else if((v.getId()) == (btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                    btn_shopping.setFocusable(true);
                    btn_shopping.requestFocus();
                }*/else if((v.getId()) == (btn_delete.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){
                    isRightFocus=false;
                   /* btn_select.setFocusable(true);
                    btn_select.requestFocus();*/
                }else if((v.getId()) == (btn_shopping.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    listView.setFocusable(true);
                    if(listView.getSelectedItemPosition()!=-1&&adapter!=null){
                        select_item=listView.getSelectedItemPosition();
                        Log.i("==22222==","mian871");
                        isFocus=true;
                        adapter.notifyDataSetChanged();
                    }
                }else if((v.getId()) == (btn_shopping.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){
                    isRightFocus=false;
                   /* btn_select.setFocusable(false);
                    btn_delete.setFocusable(true);
                    btn_delete.requestFocus();*/
                }else if((v.getId()) == (btn_select.getId()) && keyCode==KeyEvent.KEYCODE_DPAD_UP){
                    return true;
                }
                }
            return false;
        }
    };
    /**
     * 清空购物车确定对话框
     */
    private void showClearShoppingCartDialog() {
        //检测网络状态
        if (!Utils.isConnected(mContext)) {
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext, error_tips);
            return;
        }

        //正在交互
        if (mProgressDialog != null && mProgressDialog.isShowing())
            return;

        confirmDialog = new ConfirmDialog(mContext, new ConfirmDialog.ConfirmOnClickListener() {
            @Override
            public void onOk() {
                Utils.print(tag, "onOk");
                clearShoppingCartCommodity();

            }

            @Override
            public void onCancel() {
                Utils.print(tag, "onCancel");
                confirmDialog.dismiss();
            }

            @Override
            public void onDismiss() {
                Utils.print(tag, "onDismiss");
            }
        });
        confirmDialog.setTitle(mContext.getResources().getString(R.string.clear_shopping_cart_message));
        confirmDialog.setOkButton(mContext.getResources().getString(R.string.ok));
        confirmDialog.setCancelButton(mContext.getResources().getString(R.string.cancel));
        confirmDialog.showUI();
    }

    /**
     * 清空购物车商品
     */
    public void clearShoppingCartCommodity() {
        Utils.print(tag, "clearShoppingCartCommodity");
        //检测网络状态
        if (!Utils.isConnected(mContext)) {
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext, error_tips);
            return;
        }

        //正在交互
        if (mProgressDialog != null && mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag, "op_status==" + op_status);
        if (op_status)
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

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpClearShoppingcartData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "clearShoppingCartCommodity status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        stopProgressDialog();
                        op_status = false;
                        if (statusData.getReturnValue() == -1) {
                            ToastUtils.showToast(mContext, statusData.getErrorMessage());
                            return;
                        }
                        //购物车清除成功后，UI处理！！！
                        layout_has.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
                        goshopping.requestFocus();
                        select_item=-1;
                        selectedIdList.clear();
                        confirmDialog.dismiss();


                    }
                });
        addSubscription(s);
    }

    public ArrayList<ShoppingCartRecord> getList(){
        //处理数据格式
        ArrayList<ShoppingCartRecord> list=new ArrayList<ShoppingCartRecord>();
        if(groupDatas!=null){
            for(int i=0;i<groupDatas.size();i++){//分成满减，满赠，普通，失效四组
                String totalType=groupDatas.get(i).getPromotionType();
                List<ShoppingCartRecord> records = groupDatas.get(i).getSkuList();
                for(int j=0;j<records.size();j++){

                    if(j==0){
                        records.get(j).setFirst(true);
                        if(!totalType.equals(ConStant.COMMON)){
                            ShoppingCartRecord data=new ShoppingCartRecord();
                            data.setTitleBoolean(true);
                            data.setType(totalType);
                            //每个Item加入满赠，满减的sn
                            if(totalType.equals(ConStant.FULL_GIFTS)||totalType.equals(ConStant.FULL_CUT)){
                                data.setPromotionSn(groupDatas.get(i).getPromotionSn());
                            }
                            if(totalType.equals(ConStant.FULL_GIFTS)){
                                data.setLimitPrice(groupDatas.get(i).getLimitPrice());
                                data.setActivecontent(groupDatas.get(i).getActivecontent());
                                data.setPromotionInfo(groupDatas.get(i).getPromotionInfo());
                                data.setShow(groupDatas.get(i).isShow());
                            }else if(totalType.equals(ConStant.FULL_CUT)){
                                data.setActivecontent(groupDatas.get(i).getActivecontent());
                                data.setPromotionInfo(groupDatas.get(i).getPromotionInfo());
                            }

                            list.add(data);
                        }

                    }else{
                        records.get(j).setFirst(false);
                    }
                    records.get(j).setTitleBoolean(false);
                    records.get(j).setType(totalType);
                    list.add(records.get(j));
                }
            }
        }
        return list;
    }
    /**
     * 获取购物车选择商品的满增商品已经优惠价格
     */
    public void getShoppingCartDiscount(ArrayList<ShoppingCartRecord> selectedDatas,List<Long> cartItemIds) {
        Utils.print(tag, "getShoppingCartDiscount");
        //检测网络状态
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        //正在交互
        if(mProgressDialog!=null &&mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+op_status);
        if(op_status)
            return;
        op_status = true;

        //startProgressDialog();

        String input = "";
        try {
            Gson json = new Gson();
            PostCartItemInfo postCartInfo = new PostCartItemInfo();
            postCartInfo.setUserid(ConStant.getInstance(mContext).userID);
            postCartInfo.setItemIdList(cartItemIds);
            input = json.toJson(postCartInfo);
            input = input.replace("{","%7B").replace("}","%7D");
            input = input.replace("[","%5B").replace("]","%5D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetShoppingCartDiscount(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DiscountData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                       // stopProgressDialog();
                        op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(DiscountData data) {
                        Utils.print(tag, "status==" + data.getErrorMessage() + ",value=" + data.getReturnValue());
                       // stopProgressDialog();
                        op_status=false;
                        if(data.getReturnValue()==-1){
                            Utils.print(tag,data.getErrorMessage());
                            ToastUtils.showToast(mContext,data.getErrorMessage());
                            return;
                        }else if(data.getReturnValue()==-4){//需要刷新数据，存在失效商品
                            ToastUtils.showToast(mContext,data.getErrorMessage());
                            isFocus=true;
                            getShoppingCartCommodityToRefreshIcon();
                            return;
                        }
                        // 满赠的赠品的集合
                        ArrayList<ActivityInfoData> activityInfoDataList=new ArrayList<ActivityInfoData>();
                        List<DiscountType> discountTypeList=data.getData();
                        /*
                        * 商品中突然有限购政策，需要检测处理，提示用户手动修改合理数量
                        * */
                        boolean isCycle=true;
                        for(int i=0;i<discountTypeList.size();i++){
                            if(!isCycle){
                               return;
                            }
                            List<FullCutSkuData> refreshBeanList=discountTypeList.get(i).getSkuList();
                            for(int j=0;j<refreshBeanList.size();j++){
                                int itemCount=refreshBeanList.get(j).getBuyNum();
                                LimitActivityData limitBean=refreshBeanList.get(j).getLimitActivity();
                                if(limitBean!=null){
                                    int limitNum=limitBean.getLimitNum();
                                    int buyedNum=limitBean.getBuyNum();
                                    if(itemCount+buyedNum>limitNum&&limitBean.isLimitFlag()){
                                        ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.limitbuytip));
                                        isFocus=true;
                                        getShoppingCartCommodityToRefreshIcon();
                                        isCycle=false;
                                        return;
                                    }
                                }

                            }

                        }

                        for(int i=0;i<discountTypeList.size();i++){
                            if(discountTypeList.get(i).getPromotionType().equals(ConStant.FULL_GIFTS)&&discountTypeList.get(i).isReachCondition()){//有满赠且达到了满赠条件
                                // 获取满赠的赠品
                                for(int j=0;j<groupDatas.size();j++){
                                   if(groupDatas.get(j).getPromotionType().equals(ConStant.FULL_GIFTS)&&discountTypeList.get(i).getPromotionSn().equals(groupDatas.get(j).getPromotionSn())){
                                       ArrayList<ActivityInfoData> activityList=(ArrayList<ActivityInfoData>)groupDatas.get(j).getPromotionInfo();
                                       activityInfoDataList.addAll(activityList);
                                   break;
                                    }
                                }
                            }
                        }
                        ShoppingCartList.launch((ShoppingCartGrid)mContext,ConStant.SHOPPING_CART,selectedDatas,activityInfoDataList);
                    }
                });
    }

    /**
     * 获取选中情况而需要更新的变量
     */
    public void getShoppingCartDiscountRefrsh(List<Long> cartItemIds) {
        Utils.print(tag, "getShoppingCartDiscountRefrsh");
        //检测网络状态
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            return;
        }

        //正在交互
        if(mProgressDialog!=null &&mProgressDialog.isShowing())
            return;

        //检测是否正在与服务器交互
        Utils.print(tag,"op_status=="+op_status);
        if(op_status)
            return;
        op_status = true;


        String input = "";
        try {
            Gson json = new Gson();
            PostCartItemInfo postCartInfo = new PostCartItemInfo();
            postCartInfo.setUserid(ConStant.getInstance(mContext).userID);
            postCartInfo.setItemIdList(cartItemIds);
            input = json.toJson(postCartInfo);
            input = input.replace("{","%7B").replace("}","%7D");
            input = input.replace("[","%5B").replace("]","%5D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetShoppingCartDiscount(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DiscountData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        op_status=false;
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_op_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(DiscountData data) {
                        Utils.print(tag, "status==" + data.getErrorMessage() + ",value=" + data.getReturnValue());
                        op_status=false;
                        if(data.getReturnValue()==-1){
                            Utils.print(tag,data.getErrorMessage()+":2");
                            ToastUtils.showToast(mContext,data.getErrorMessage());
                            return;
                        }else if(data.getReturnValue()==-4){//需要刷新数据，存在失效商品
                            ToastUtils.showToast(mContext,data.getErrorMessage());
                            isFocus=true;
                            getShoppingCartCommodityToRefreshIcon();
                            return;
                        }
                        //根据获取的数据计算
                        List<DiscountType> discountTypeList=data.getData();
                           /*
                        * 总价的计算对应14
                        * */
                        double originalTotalPrice=0;
                          /*
                        * 优惠总价对应15
                        * */
                        double discountPrice=0;
                         /*
                        * 数量对应16
                        * */
                        int buyNumTotal=0;
                        /*
                        * 满赠是否显示赠品
                        * */
                        boolean isShow=false;
                        /*
                        * 满赠里有商品
                        * */
                        boolean ishas=false;
                         /*
                        * 满减里有商品
                        * */
                        boolean ishasfullcut=false;
                         /*
                        * 满减是否满足条件
                        * */
                        boolean isShowfullcut=false;

                        //计算满赠里的商品优惠后的总价
                        double fullgiftPrice=0;
                        //计算满减里的商品优惠后的总价
                        double fullcutPrice=0;
                        //先初始化数据
                        for(int j=0;j<groupDatas.size();j++){
                            if(groupDatas.get(j).getPromotionType().equals(ConStant.FULL_GIFTS)){//该满赠分组中，没有选中的商品
                                ishas=false;
                                //根本就不存在满赠商品（购满XX元得赠品，赠完即止）
                                groupDatas.get(j).setActivecontent(mContext.getResources().getString(R.string.giveawaytip1)+groupDatas.get(j).getLimitPrice()+mContext.getResources().getString(R.string.giveawaytip2));
                                groupDatas.get(j).setShow(false);
                            }
                            if(groupDatas.get(j).getPromotionType().equals(ConStant.FULL_CUT)){//该满减分组中，没有选中的商品
                                ishasfullcut=false;
                                //根本就不存在满赠商品（满xx元减yy元,用逗号隔开）
                                //满减总梯度
                                List<ActivityInfoData> fullcutBeanTotalList=groupDatas.get(j).getPromotionInfo();
                                String fullcut_tv="";
                                for(int k=0;k<fullcutBeanTotalList.size();k++){
                                    if(k!=fullcutBeanTotalList.size()-1){//非最后一个
                                        fullcut_tv=fullcut_tv+mContext.getResources().getString(R.string.fullcuttip1)+fullcutBeanTotalList.get(k).getEnoughPrice()+mContext.getResources().getString(R.string.fullcuttip2)+fullcutBeanTotalList.get(k).getCutPrice()+mContext.getResources().getString(R.string.fullgifttip4)+",";
                                    }else{
                                        fullcut_tv=fullcut_tv+mContext.getResources().getString(R.string.fullcuttip1)+fullcutBeanTotalList.get(k).getEnoughPrice()+mContext.getResources().getString(R.string.fullcuttip2)+fullcutBeanTotalList.get(k).getCutPrice()+mContext.getResources().getString(R.string.fullgifttip4);
                                    }

                                }
                                groupDatas.get(j).setActivecontent(fullcut_tv);

                            }


                        }
                        for(int j=0;j<groupDatas.size();j++){
                        for(int i=0;i<discountTypeList.size();i++){
                                if(discountTypeList.get(i).getPromotionType().equals(ConStant.FULL_GIFTS)&&discountTypeList.get(i).getPromotionSn().equals(groupDatas.get(j).getPromotionSn())){
                                    ishas=true;
                                    //计算刷新接口满赠商品的优惠总价格
                                    List<FullCutSkuData> skuList=discountTypeList.get(i).getSkuList();
                                    for(int k=0;k<skuList.size();k++){
                                        fullgiftPrice=addValue(fullgiftPrice,skuList.get(k).getSumRatePrice());
                                    }

                                    if(discountTypeList.get(i).isReachCondition()){
                                        isShow=true;
                                    }else{
                                        isShow=false;
                                    }
                                    groupDatas.get(j).setShow(isShow);


                                    if(isShow){//满足条件（已购满XX元，可得赠品，赠完即止）
                                        groupDatas.get(j).setActivecontent(mContext.getResources().getString(R.string.fullgifttip1)+groupDatas.get(j).getLimitPrice()+mContext.getResources().getString(R.string.fullgifttip2));
                                    }else{//不满足（购满XX元得赠品，赠完即止，还差yy元。）
                                        if(ishas){//有满赠商品但不满足条件
                                            groupDatas.get(j).setActivecontent(mContext.getResources().getString(R.string.giveawaytip1)+groupDatas.get(j).getLimitPrice()+mContext.getResources().getString(R.string.giveawaytip2)+mContext.getResources().getString(R.string.fullgifttip3)+sub(groupDatas.get(j).getLimitPrice(),fullgiftPrice)+mContext.getResources().getString(R.string.fullgifttip4));
                                        }/*else{//根本就不存在满赠商品（购满XX元得赠品，赠完即止）
                                            groupDatas.get(j).setActivecontent(mContext.getResources().getString(R.string.giveawaytip1)+groupDatas.get(j).getLimitPrice()+mContext.getResources().getString(R.string.giveawaytip2));
                                        }*/

                                    }

                                }

                                fullgiftPrice=0;


                                if(discountTypeList.get(i).getPromotionType().equals(ConStant.FULL_CUT)&&discountTypeList.get(i).getPromotionSn().equals(groupDatas.get(j).getPromotionSn())){
                                    ishasfullcut=true;
                                    //选中满减商品里的总优惠价格
                                    List<FullCutSkuData> skuList=discountTypeList.get(i).getSkuList();
                                    for(int k=0;k<skuList.size();k++){
                                        fullcutPrice=addValue(fullcutPrice,skuList.get(k).getSumRatePrice());
                                    }


                                    if(discountTypeList.get(i).isReachCondition()){
                                        isShowfullcut=true;
                                    }else{
                                        isShowfullcut=false;
                                    }
                                    //得到刷新接口里的满减梯度
                                    List<FullCutBean> fullCutBeanList=null;
                                    fullCutBeanList=discountTypeList.get(i).getReachActInfo();

                                    Log.i("====2222====","isShowfullcut==="+isShowfullcut);
                                    //当前满减的策略
                                    if(isShowfullcut){//满足条件（满xx元减yy元，已优惠yy元。满zz元减mm元。）
                                        String fullcut_tv="";
                                        for(int k=0;k<fullCutBeanList.size();k++){
                                            if(k==0){
                                                fullcut_tv=mContext.getResources().getString(R.string.fullcuttip1)+fullCutBeanList.get(0).getEnoughPrice()+mContext.getResources().getString(R.string.fullcuttip2)+fullCutBeanList.get(0).getCutPrice()+mContext.getResources().getString(R.string.fullcuttip3)+fullCutBeanList.get(0).getCutPrice()+mContext.getResources().getString(R.string.fullgifttip4);
                                            }else if(k==1){
                                                fullcut_tv=fullcut_tv+mContext.getResources().getString(R.string.fullcuttip4)+fullCutBeanList.get(1).getEnoughPrice()+mContext.getResources().getString(R.string.fullcuttip2)+fullCutBeanList.get(1).getCutPrice()+mContext.getResources().getString(R.string.fullgifttip4);
                                            }
                                        }
                                        groupDatas.get(j).setActivecontent(fullcut_tv);
                                    }else{//不满足（满xx元减yy元，还差mm元。）//fullcutPrice
                                        if(ishasfullcut){//有满赠商品但不满足条件
                                            String fullcut_tv="";
                                            for(int k=0;k<fullCutBeanList.size();k++){
                                                if(k==0){
                                                    fullcut_tv=mContext.getResources().getString(R.string.fullcuttip1)+fullCutBeanList.get(0).getEnoughPrice()+mContext.getResources().getString(R.string.fullcuttip2)+fullCutBeanList.get(0).getCutPrice()+mContext.getResources().getString(R.string.fullcuttip5)+sub(fullCutBeanList.get(0).getEnoughPrice(),fullcutPrice)+mContext.getResources().getString(R.string.fullgifttip4);
                                                    break;
                                                }
                                            }
                                            groupDatas.get(j).setActivecontent(fullcut_tv);
                                        }/*else{//根本就不存在满赠商品（满xx元减yy元,用逗号隔开）

                                        }*/

                                    }


                                }

                                fullcutPrice=0;
                            }

                        }
                        for(int i=0;i<discountTypeList.size();i++){
                            originalTotalPrice=addValue(originalTotalPrice,discountTypeList.get(i).getSumPrice());
                            List<FullCutSkuData> skuList=discountTypeList.get(i).getSkuList();
                            for(int j=0;j<skuList.size();j++){
                                discountPrice=addValue(discountPrice,skuList.get(j).getSumRatePrice());
                                buyNumTotal=buyNumTotal+skuList.get(j).getBuyNum();
                            }

                        }

                        Log.i("===cccc===","sumPrice=="+originalTotalPrice);
                        //原始总价
                        price.setText("¥"+Utils.getPrice(originalTotalPrice));
                        Log.i("===cccc===","sumRatePrice=="+discountPrice);
                        //优惠总价
                        totalprice.setText("¥"+Utils.getPrice(discountPrice));
                        //优惠了的总价
                        discount.setText("-¥"+Utils.getPrice(sub(originalTotalPrice,discountPrice)));
                        if(discount.getText().toString().equals("-¥0.00")||discount.getText().toString().equals("-¥0.0")){
                            discount.setText("-¥0.00");
                        }
                        count.setText(""+buyNumTotal);
                        Utils.print(tag,"hasCutFull="+hasCutFull(data));
                        if(hasCutFull(data)){
                            if(!(discount.getText().toString().equals("-¥0.00")||discount.getText().toString().equals("-¥0.0"))) {
                                layout_shoppingcart_discount.setVisibility(View.VISIBLE);
                            }
                        }else {
                            discount.setText("-¥0.00");
                        }
                        getList();
                        adapter.addAll(groupDatas);

                    }
                });
    }





    /*
    *
    * 获取由specSn选中集合的cartItemId的list集合形式
    * */
    public List<Long> getCartItemIdList(){
        ArrayList<ShoppingCartRecord> totalData=getList();
        List<Long> cartItemIdList= new ArrayList<Long>();
        for(int i=0;i<selectedIdList.size();i++){
            for(int j=0;j<totalData.size();j++){
                if(!totalData.get(j).isTitleBoolean()&&totalData.get(j).getCartItemId()==(selectedIdList.get(i))){//正常商品数据，非头部数据
                    cartItemIdList.add(totalData.get(j).getCartItemId());
                    break;
                }
            }
        }
        return cartItemIdList;

    }

    public double addValue(double d1,double d2){
        BigDecimal b1=new BigDecimal(Double.toString(d1));
        BigDecimal b2=new BigDecimal(Double.toString(d2));
        return b1.add(b2).doubleValue();
    }
    public static double sub(double d1,double d2){
        BigDecimal b1=new BigDecimal(Double.toString(d1));
        BigDecimal b2=new BigDecimal(Double.toString(d2));
        return b1.subtract(b2).doubleValue();

    }

    private void showNullCard(){
        Utils.print(tag,"show null cardList"+"tip:规避点击结算订单后返回时，购物车界面不刷新");
        stopProgressDialog();
        op_status = false;
        //显示空状态的购物车！
        layout_has.setVisibility(View.GONE);
        layout_empty.setVisibility(View.VISIBLE);
        goshopping.requestFocus();
        select_item=-1;
        selectedIdList.clear();
        confirmDialog.dismiss();
    }
    private boolean isRequestItemFocus = false;
    private void onListViewListener(){

        listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(isRequestItemFocus && !firstShopping){
                    isRequestItemFocus = false;
                    int type = adapter.getItemViewType(0);
                    if(type==0){
                        View v = listView.getChildAt(1);
                        int top = (v == null) ? 0 : v.getTop();
                        Utils.print(tag,"top==="+top);
                        listView.setSelectionFromTop(1, top);
                    }
                }
            }
        });
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

                        getShoppingCartCommodity(true);

                    }
                });
        addSubscription(s);
    }

    /**
     * 判断是否有满减活动
     * @param data
     * @return
     */
    private boolean hasCutFull(DiscountData data){
        boolean result = false;
        for (int i = 0; i < data.getData().size(); i++) {
            DiscountType discountType = data.getData().get(i);
            if(discountType.getPromotionType().equals(ConStant.FULL_CUT)){
                result = true;
                break;
            }
        }
        return result;
    }

    public class ReceiverShopping extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //接收删除和选中（取消选中）的广播！！！
            String direction= intent.getStringExtra("tag");
            if(direction.equals("delete")){//删除键的广播
                ArrayList<GroupData> dataList=intent.getParcelableArrayListExtra("list");
                groupDatas=(List)dataList;
                getList();
            }else if(direction.equals("refresh")){
                isFocus=true;
                getShoppingCartCommodityToRefreshIcon();
            }


        }

    }

}
