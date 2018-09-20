package com.hiveview.dianshang.usercenter;

import android.animation.AnimatorSet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.UserCenterDialogAdapter;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.AdduserDialog;
import com.hiveview.dianshang.entity.address.AddressAPI;
import com.hiveview.dianshang.entity.address.UserAddress;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.utils.FeedbackTranslationAnimatorUtil;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.CustomProgressDialog;

import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gavin on 2017/6/1.
 */

public class UserCenterFragment extends BaseFragment {
    private LayoutInflater mFactory = null;
    private String tag="UserCenterFragment";
    private UserAddress userAddressData;
    //全局变量，记录选中的item
    public static int select_item = -1;
    private UserCenterDialogAdapter adapter;
    private ReceiverUserCenterDialog receiverUserCenter;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    LayoutInflater layoutInflater;
    private int receiveIdTotal=0;
    private CustomProgressDialog mProgressDialog = null;
    /**
     * 触底反弹动画
     */
    public AnimatorSet feedbackAnimator;
    private int totalCount=0;

    @BindView(R.id.addresslv)
    ListView addresslv;


    @BindView(R.id.add_address)
    Button add_address;

    @BindView(R.id.first)
    LinearLayout first;


    @Override
    protected int getLayoutId() {
        return R.layout.layout_usercenterdialog;
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
        add_address.setVisibility(View.INVISIBLE);
       // add_address.setNextFocusDownId(R.id.add_address);
        initView();
        //注册广播
        receiverUserCenter = new ReceiverUserCenterDialog ();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dianshang.damai.refreshuser");
        mContext.registerReceiver(receiverUserCenter, intentFilter);
        add_address.setOnKeyListener(onKeyListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(receiverUserCenter);
    }

    public void initView(){
        preferences = mContext.getSharedPreferences("address",
                mContext.MODE_WORLD_READABLE);
        //适配地址集合  适配器AddressAdapte
        //addresslv是要适配的ListView
        getUserAddressList();

        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  EditAddressActivity.launch((UserAdderssSelect)(mContext));
                //有数据后，根据有无地址数据来写标志！！！！
                editor = preferences.edit();
                if(userAddressData!=null&&userAddressData.getData().getRecords().size()!=0){
                    editor.putBoolean("flag",false);
                }else{
                    editor.putBoolean("flag",true);
                }

                editor.putBoolean("isUpdate",false);
                editor.commit();*/
                AdduserDialog adduserDialog=new AdduserDialog(mContext,preferences,userAddressData,2);
                if(!adduserDialog.isShowing()){
                    adduserDialog.showUI();
                }
            }
        });
        addresslv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_item = position; //当前选择的节目item
                adapter.notifyDataSetChanged(); //通知adapter刷新数据
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //监听滚动 获取总数量
        addresslv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                totalCount=totalItemCount;

            }
        });


    }

    //获取数据！！！！
    public void getUserAddressList(){
        Utils.print(tag,"getUserAddressList");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            add_address.setVisibility(View.VISIBLE);
            add_address.setFocusable(true);
            add_address.requestFocus();
            return;
        }
        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.userID);
            json.put("pageIndex","1");
            json.put("pageSize",ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetUserAddressListData(input,ConStant.Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserAddress>() {
                    @Override
                    public void onCompleted() {
                        try {

                            if(userAddressData!=null){
                                addresslv.setSelector(new ColorDrawable(Color.TRANSPARENT));
                                adapter=new UserCenterDialogAdapter(mContext,userAddressData.getData().getRecords());
                                addresslv.setAdapter(adapter);
                                Utility.setListViewHeightBasedOnChildren(addresslv);

                                //进行定位
                                if(userAddressData!=null&&userAddressData.getData().getRecords()!=null){
                                    for(int i=0;i<userAddressData.getData().getRecords().size();i++){
                                        int pos=i;
                                        if(userAddressData.getData().getRecords().get(i).getId()==receiveIdTotal){//找到相对应id的位置
                                            addresslv.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addresslv.setSelection(pos);
                                                }
                                            });

                                            break;
                                        }
                                    }

                                }


                            }
                            add_address.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error>>>>");
                        stopProgressDialog();
                        add_address.setVisibility(View.VISIBLE);
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
                        try {
                        stopProgressDialog();
                        add_address.setVisibility(View.INVISIBLE);
                        if(userAddress.getReturnValue()==-1){
                            if(userAddressData!=null&&userAddressData.getData()!=null&&userAddressData.getData().getRecords()!=null){
                                userAddressData.getData().getRecords().clear();
                            }
                            return;
                        }

                            List<UserData> userDatas = userAddress.getData().getRecords();
                            userAddressData=userAddress;
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    }
                });
        addSubscription(subscription);
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

    public class ReceiverUserCenterDialog extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            getUserAddressList();
        }

    }

    /**
     * 默认选择的地址行
     * @param receiveID
     */
    public void setDefaultReceiveID(int receiveID){
        //设置默认选中的地址行
        receiveIdTotal=receiveID;
        Log.i("=======","==sss=="+receiveID);
    }


    public int getAddressListSize(){
        if(adapter!=null)
            return adapter.getCount();
        return 0;
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


    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(v.getId()==R.id.add_address && keyCode==KeyEvent.KEYCODE_DPAD_UP) {
                    //Toast.makeText(mContext, "添加按钮上移！", Toast.LENGTH_SHORT).show();
                    if(userAddressData!=null&&userAddressData.getData().getRecords().size()!=0&&addresslv.getChildAt(userAddressData.getData().getRecords().size()-1)!=null){
                       // Toast.makeText(mContext, "上移定位！", Toast.LENGTH_SHORT).show();
                        select_item = userAddressData.getData().getRecords().size()-1; //当前选择的节目item
                        adapter.notifyDataSetChanged(); //通知adapter刷新数据
                    }
                }else if(v.getId()==R.id.add_address && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                    if (totalCount>1) {
                        if (null != feedbackAnimator && !feedbackAnimator.isRunning()) {
                            Utils.print(tag,"start animation");
                            feedbackAnimator.start();
                        }

                    }
                }
            }
            return false;
        }
    };
    /**
     * 获取所有地址列表
     * @return
     */
    public UserAddress getAllUserData(){
        return userAddressData;
    }
}
