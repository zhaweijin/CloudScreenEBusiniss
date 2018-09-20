package com.hiveview.dianshang.usercenter;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.AddressAdapter;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.AdduserDialog;
import com.hiveview.dianshang.entity.address.AddressAPI;
import com.hiveview.dianshang.entity.address.UserAddress;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.entity.token.TokenData;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.utils.DeviceInfo;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.CustomProgressDialog;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Gavin on 2017/5/12.
 */

public class UserCenterManager extends BaseFragment {
    LayoutInflater layoutInflater;

    @BindView(R.id.add_address)
    Button add_address;

    @BindView(R.id.addresslv)
    ListView addresslv;

    @BindView(R.id.first)
    LinearLayout first;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferencesLocation;
    private SharedPreferences.Editor editorLocation;
    private String tag="UserCenterManager";
    private UserAddress userAddressData;
    private ReceiverUserCenter receiverUserCenter ;
    //全局变量，记录选中的item
    public static int select_item = -1;
    private AddressAdapter adapter;
    private Boolean firstBoolean=true;
    private static RelativeLayout layout_domy_user_center;
    private static RelativeLayout layout_domy_shopping_cart;
    private static RelativeLayout layout_home;
    private static RelativeLayout layout_commodity;
    private static RelativeLayout layout_search;
    private static RelativeLayout layout_collection;
    private static RelativeLayout layout_order;
    private static RelativeLayout layout_after_sale_service;
    private CustomProgressDialog mProgressDialog = null;
    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;
    private int totalCount=0;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_addressusercenter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.print("test","onActivityCreated");
        //初始化触底反弹动画器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            feedbackAnimator = FeedbackTranslationAnimatorUtil.getInstance().getAnimationSet(first, FeedbackTranslationAnimatorUtil.Orientation.VERTICAL, -50f);
        }

        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferences = getActivity().getSharedPreferences("address",
                getActivity().MODE_WORLD_READABLE);
        preferencesLocation = getActivity().getSharedPreferences("location",
                getActivity().MODE_WORLD_READABLE);
        editorLocation = preferencesLocation.edit();
        initShowAddressUI();

        //适配地址集合  适配器AddressAdapte
        //addresslv是要适配的ListView
        String user = (String) SPUtils.get(mContext, ConStant.USERID, "");
        String token = (String) SPUtils.get(mContext, ConStant.USER_TOKEN, "");
        startProgressDialog();
        if (!user.equals("") && !token.equals("")) {
            Utils.print(tag, "set token");
            ConStant.userID = user;
            ConStant.Token = token;
            getUserAddressList();
        }else{
            getTokenData();
        }

        addresslv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                select_item=preferencesLocation.getInt("pos",-1);
                if(select_item<0){
                    if(select_item==-1){
                      //  Toast.makeText(mContext, "selection-1", Toast.LENGTH_SHORT).show();
                        select_item = position; //当前选择的节目item
                        adapter.notifyDataSetChanged(); //通知adapter刷新数据
                        layout_domy_user_center.requestFocus();
                    }else if(select_item==-2){
                       //Toast.makeText(mContext, "selection-2", Toast.LENGTH_SHORT).show();
                        add_address.setVisibility(View.VISIBLE);
                        add_address.requestFocus();
                    }

                    for(int i=0;i<parent.getChildCount();i++){
                        LinearLayout firstUn=(LinearLayout)((LinearLayout)parent.getChildAt(i)).getChildAt(0);
                        ImageButton deleteUn=(ImageButton)((LinearLayout)parent.getChildAt(i)).getChildAt(1);
                        firstUn.setFocusable(false);
                        deleteUn.setFocusable(false);
                        deleteUn.setVisibility(View.GONE);

                    }

                }else{
                  //  Toast.makeText(mContext, "selection=="+position, Toast.LENGTH_SHORT).show();
                    select_item = position; //当前选择的节目item
                    adapter.notifyDataSetChanged(); //通知adapter刷新数据
                  /*  LinearLayout first=(LinearLayout)((LinearLayout)parent.getChildAt(position)).getChildAt(0);
                    ImageButton delete=(ImageButton)((LinearLayout)parent.getChildAt(position)).getChildAt(1);
                    delete.setVisibility(View.VISIBLE);
                    first.setFocusable(true);
                    first.requestFocus();
                    delete.setFocusable(true);
                    for(int i=0;i<parent.getChildCount();i++){
                        if(i!=position){
                            LinearLayout firstUn=(LinearLayout)((LinearLayout)parent.getChildAt(i)).getChildAt(0);
                            ImageButton deleteUn=(ImageButton)((LinearLayout)parent.getChildAt(i)).getChildAt(1);
                            firstUn.setFocusable(false);
                            deleteUn.setFocusable(false);
                            deleteUn.setVisibility(View.GONE);
                        }
                    }*/
                }

                /*if(firstBoolean&&position==0){
                    layout_domy_user_center.requestFocus();
                    addresslv.setFocusable(false);
                }else{

                }
*/
                //当此选中的item的子控件需要获得焦点时
                parent.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            }
        });

        //监听滚动  获取总数量
        addresslv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                totalCount=totalItemCount;
            }
        });

        //注册广播
        receiverUserCenter = new ReceiverUserCenter ();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dianshang.damai.refreshuser");
        mContext.registerReceiver(receiverUserCenter, intentFilter);

        add_address.setOnKeyListener(onKeyListener);
        if(layout_domy_user_center!=null){
            layout_domy_user_center.setOnKeyListener(onKeyListener);
        }

    }

    private void initShowAddressUI(){
        add_address.setVisibility(View.INVISIBLE);
        RxView.clicks(add_address)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        select_item=-2;
                      /* RxBus.get().post(ConStant.obString_opt_natigation,ConStant.OP_EDIT_ADDRESS);
                        //有数据后，根据有无地址数据来写标志！！！！
                        editor = preferences.edit();
                        if(userAddressData!=null&&userAddressData.getData().getRecords().size()!=0){
                            editor.putBoolean("flag",false);
                        }else{
                            editor.putBoolean("flag",true);
                        }

                        editor.putBoolean("isUpdate",false);
                        editor.commit();*/
                        AdduserDialog adduserDialog=new AdduserDialog(mContext,preferences,userAddressData,1);
                        if(!adduserDialog.isShowing()){
                            adduserDialog.showUI();
                        }
                    }
                });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            layout_domy_user_center = (RelativeLayout) mainActivity.findViewById(R.id.layout_domy_user_center);
            layout_domy_shopping_cart=(RelativeLayout)mainActivity.findViewById(R.id.layout_domy_shopping_cart);
            layout_home=(RelativeLayout)mainActivity.findViewById(R.id.layout_home);
            layout_commodity=(RelativeLayout)mainActivity.findViewById(R.id.layout_commodity);
            layout_search=(RelativeLayout)mainActivity.findViewById(R.id.layout_search);
            layout_collection=(RelativeLayout)mainActivity.findViewById(R.id.layout_collection);
            layout_order=(RelativeLayout)mainActivity.findViewById(R.id.layout_order);
            layout_after_sale_service=(RelativeLayout)mainActivity.findViewById(R.id.layout_after_sale_service);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editorLocation.putInt("pos",select_item);
        editorLocation.commit();
       // Toast.makeText(mContext, "地址列表销毁写入"+select_item, Toast.LENGTH_SHORT).show();
        mContext.unregisterReceiver(receiverUserCenter);
    }

    public static class Utility {
        public static void setListViewHeightBasedOnChildren(ListView listView) {
            // 获取ListView对应的Adapter
            ListAdapter listAdapter = listView.getAdapter();
            if(listAdapter == null) {
                return;
            }
            int totalHeight = 0;
            for(int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0); // 计算子项View 的宽高
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            // listView.getDividerHeight()获取子项间分隔符占用的高度
            // params.height最后得到整个ListView完整显示需要的高度
            listView.setLayoutParams(params);
        }
    }
    /**
     * 获取用户地址列表
     */
    public void getUserAddressList(){
        Utils.print(tag,"getUserAddressList");
       // add_address.setVisibility(View.INVISIBLE);
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            add_address.setVisibility(View.VISIBLE);
            add_address.setFocusable(true);
            add_address.requestFocus();
            return;
        }

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
                        if(userAddressData!=null){
                            adapter=new AddressAdapter(mContext,userAddressData.getData().getRecords(),firstBoolean);
                            addresslv.setAdapter(adapter);
                            Utility.setListViewHeightBasedOnChildren(addresslv);
                            add_address.setVisibility(View.VISIBLE);
                            if(userAddressData.getData().getRecords().size()==0){
                                add_address.setFocusable(true);
                                add_address.requestFocus();
                            }
                        }

                        select_item=preferencesLocation.getInt("pos",-1);
                      //  Toast.makeText(mContext, "定位=="+select_item, Toast.LENGTH_SHORT).show();
                        //setFocuse(false);
                        if(select_item>-1&&userAddressData!=null&&userAddressData.getData().getRecords().size()>select_item){
                            addresslv.post(new Runnable() {
                                @Override
                                public void run() {
                                //  Toast.makeText(mContext, "列表定位=="+select_item, Toast.LENGTH_SHORT).show();
                                    addresslv.setSelection(select_item);
                                }
                            });
                        }else{
                          //  Toast.makeText(mContext, "添加按钮定位=="+select_item, Toast.LENGTH_SHORT).show();
                            //setFocuse(false);
                            add_address.setVisibility(View.VISIBLE);
                            add_address.requestFocus();
                           // setFocuse(true);
                        }
                        if(select_item==-1){
                           // Toast.makeText(mContext, "列表焦点落在个人中心=="+select_item, Toast.LENGTH_SHORT).show();
                            layout_domy_user_center.setFocusable(true);
                            layout_domy_user_center.requestFocus();
                        }



                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error>>>>");
                        add_address.setVisibility(View.VISIBLE);
                        if(userAddressData!=null&&userAddressData.getData().getRecords().size()==0){
                            add_address.setFocusable(true);
                            add_address.requestFocus();
                        }
                        if(userAddressData==null){
                            add_address.setFocusable(true);
                            add_address.requestFocus();
                        }
                        stopProgressDialog();
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);

                    }

                    @Override
                    public void onNext(UserAddress userAddress) {

                        //add_address.setVisibility(View.VISIBLE);

                        if(userAddress!=null&&userAddress.getReturnValue()==-1){
                            stopProgressDialog();
                            if(userAddressData!=null&&userAddressData.getData()!=null&&userAddressData.getData().getRecords()!=null){
                                userAddressData.getData().getRecords().clear();
                            }
                            Log.i("=======","==sss==无数据，焦点落入添加按钮！==-1");
                           // setFocuse(false);
                            add_address.setVisibility(View.VISIBLE);
                            add_address.requestFocus();
                           // setFocuse(true);
                            return;
                        }

                        if(userAddress.getReturnValue()==-2){   //token已经失效,再次请求
                            getTokenData();
                            return;
                        }

                        stopProgressDialog();
                        List<UserData> userDatas = userAddress.getData().getRecords();
                        userAddressData=userAddress;
                        //b=true  无数据，焦点落添加按钮上
                            if(userDatas.size()==0){//userAddressData.getData().getRecords()
                                Log.i("=======","==sss==无数据，焦点落入添加按钮！==");
                              //  setFocuse(false);
                                add_address.setVisibility(View.VISIBLE);
                                add_address.requestFocus();
                              //  setFocuse(true);
                            }

                    }
                });
        addSubscription(subscription);
    }


    public class ReceiverUserCenter extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            // orderListView.setSelection(currentPosition);
            String s=intent.getStringExtra("tag");
            if(s.equals("delete")){
                Log.i("=======","==sss==收到删除广播=="+userAddressData);
                getUserAddressList();
            }else{
                Log.i("=======","==sss==收到焦点上移广播==");
                //select_item
                if(select_item-1>-1&&select_item-1<adapter.getCount()){
                    if(addresslv.getChildAt(select_item-1)!=null){
                        Log.i("=======","==sss==焦点上移==");
                        addresslv.requestFocus();
                      /*  View v=((LinearLayout)(addresslv.getChildAt(select_item-1))).getChildAt(0);
                        v.setFocusable(true);
                        v.requestFocus();*/
                        //select_item
                        select_item = select_item-1; //当前选择的节目item
                        adapter.notifyDataSetChanged(); //通知adapter刷新数据
                    }

                }


            }



        }

    }

    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
              // setFocuse(true);
                /*if(v.getId()==R.id.addresslv&&keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    Toast.makeText(mContext, "左键", Toast.LENGTH_SHORT).show();
                  //  setFocuse(true);
                }*/
             /*   if(v.getId()==R.id.layout_domy_user_center&& keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                   // Toast.makeText(mContext, "按下键！！！！", Toast.LENGTH_SHORT).show();
                    setFocuse(false);
                    layout_domy_shopping_cart.setFocusable(true);
                    layout_domy_shopping_cart.requestFocus();

                }*/
                if(v.getId()==R.id.layout_domy_user_center ){
                 //  Toast.makeText(mContext, "个人中心编辑页写入0", Toast.LENGTH_SHORT).show();
                    select_item=0;
                    //userAddressData.getData().getRecords()
                    if(add_address!=null){
                        add_address.setVisibility(View.VISIBLE);
                    }
                   /* if(userAddressData!=null&&userAddressData.getData().getRecords().size()==0&&add_address!=null){
                        add_address.requestFocus();
                    }
                    if(userAddressData==null&&add_address!=null){
                        add_address.requestFocus();
                    }*/

                }else if(v.getId()==R.id.add_address && keyCode==KeyEvent.KEYCODE_DPAD_UP){
                   // Toast.makeText(mContext, "添加上移写入0", Toast.LENGTH_SHORT).show();
                    editorLocation.putInt("pos",0);
                    editorLocation.commit();
                    if(userAddressData!=null&&userAddressData.getData().getRecords().size()!=0&&addresslv.getChildAt(userAddressData.getData().getRecords().size()-1)!=null){
                        // Toast.makeText(mContext, "上移定位！", Toast.LENGTH_SHORT).show();
                        select_item = userAddressData.getData().getRecords().size()-1; //当前选择的节目item
                        adapter.notifyDataSetChanged(); //通知adapter刷新数据
                    }

                }else if(v.getId()==R.id.add_address && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                    //!!!!return true  不能直接用，可以试试nextfocus 或者外围按钮的焦点失效
                    layout_after_sale_service.setFocusable(false);
                    if (totalCount>1) {
                        if (null != feedbackAnimator && !feedbackAnimator.isRunning()) {
                            Utils.print(tag,"start animation");
                            feedbackAnimator.start();
                        }
                    }

                }else if(v.getId()==R.id.add_address && keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    setFocuse(true);
                }
            }
            return false;
        }
    };
    public static void setFocuse(Boolean b){
        layout_domy_user_center.setFocusable(b);
        layout_domy_shopping_cart.setFocusable(b);
        layout_home.setFocusable(b);
        layout_commodity.setFocusable(b);
        layout_search.setFocusable(b);
        layout_collection.setFocusable(b);
        layout_order.setFocusable(b);
        layout_after_sale_service.setFocusable(b);
    }

    public void startProgressDialog() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new CustomProgressDialog(mContext);
            }
            Utils.print(tag, "startProgressDialog");
            //mProgressDialog.setMessage(msg);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            mProgressDialog.startLoading();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public void stopProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                Utils.print(tag, "stopProgressDialog");
                mProgressDialog.stopLoading();
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
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

                        getUserAddressList();

                    }
                });
        addSubscription(s);
    }
}
