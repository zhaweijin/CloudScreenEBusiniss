package com.hiveview.dianshang.usercenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.home.InstallService;
import com.hiveview.dianshang.shoppingcart.OpShoppingCart;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.Utils;

import java.util.List;

/**
 * Created by carter on 6/2/17.
 */

public class UserAdderssSelect extends BaseActivity{

    private String tag = "UserAdderssSelect";

    private FragmentManager fragmentManager;
    private UserCenterFragment userCenterFragment;
    private ReceiverUserAelect receiverUserAelect;
    private static boolean hasBoolean=false;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static boolean idFlag=false;


    private int receiveIDTotal = -1;

    public static void launch(Activity activity,int receiveID) {
        Intent intent = new Intent(activity, UserAdderssSelect.class);
        intent.putExtra("receiveID",receiveID);
        activity.startActivity(intent);
    }
    public static void launch(Activity activity,boolean id,int receiveID) {
        Intent intent = new Intent(activity, UserAdderssSelect.class);
        intent.putExtra("receiveID",receiveID);
        idFlag=id;
        activity.startActivity(intent);
    }
    public static void launch(Activity activity,int receiveID,boolean b) {
        Intent intent = new Intent(activity, UserAdderssSelect.class);
        intent.putExtra("receiveID",receiveID);
        hasBoolean=b;
        activity.startActivity(intent);
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, UserAdderssSelect.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_select_layout);

        preferences = this.getSharedPreferences("receiveID",
                this.MODE_WORLD_READABLE);
        editor = preferences.edit();


        //检测是否启动安装服务app
        if(!Utils.isServiceWorked(this, ConStant.Install_ServiceName)){
            //启动安装服务
            Intent intent = new Intent(mContext,InstallService.class);
            intent.putExtra("status",InstallService.ENTER);
            startService(intent);
        }

        if(getIntent()!=null){
            receiveIDTotal = getIntent().getIntExtra("receiveID",-1);
           // Toast.makeText(mContext, "id=="+receiveIDTotal, Toast.LENGTH_SHORT).show();
        }
        // Log.i("====id====","receiveID=="+receiveID);
       // editor.putString("img_url1",img_url);
        editor.putInt("receiveID",receiveIDTotal);
        editor.commit();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        userCenterFragment = new UserCenterFragment();
        userCenterFragment.setDefaultReceiveID(receiveIDTotal);
        transaction.replace(R.id.layout_contain, userCenterFragment);
        transaction.commit();



        //注册广播
        receiverUserAelect = new ReceiverUserAelect();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dianshang.damai.message_finish");
        this.registerReceiver(receiverUserAelect, intentFilter);
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

    /**
     * 处理接收选择的新地址信息
     * @param value
     */
    public void onItemClick(String value){
        OpShoppingCart opShoppingCart = new OpShoppingCart();
        opShoppingCart.setKey(OpShoppingCart.MODIFY_ADDRESS);
        opShoppingCart.setValue(value);
        RxBus.get().post(ConStant.obString_modify_shopping_cart, opShoppingCart);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiverUserAelect);
    }

   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
           if(userCenterFragment.getAddressListSize()==0){
               //检测所有地址被删除的情况
               Utils.print(tag,"set -1");
               onItemClick(-1+"");
           }else {
               //没有选择地址
               List<UserData> userDatas = userCenterFragment.getAllUserData().getData().getRecords();
               //虽然有选择地址，但已选择的地址已被删除
               //receiveID  isHas ==false;表示不存在原有地址，，，true表示存在原有地址
              // Toast.makeText(mContext, "receiveID=="+receiveIDTotal, Toast.LENGTH_SHORT).show();
               boolean isHas=false;
               for(int i=0;i<userDatas.size();i++){
                   if(userDatas.get(i).getId()==receiveIDTotal){
                       isHas=true;
                       break;
                   }
               }
              // Toast.makeText(mContext, "isHas=="+isHas, Toast.LENGTH_SHORT).show();

               if(!hasBoolean||!isHas){
                   //if(!idFlag){
                       for(int i=0;i<userDatas.size();i++){
                           if(userDatas.get(i).getDefault()){
                               Utils.print(tag,"has default");
                               String receiveID = userDatas.get(i).getId()+"#";
                               String address = userDatas.get(i).getConsignee()+"     "+userDatas.get(i).getPhone()+"\n"+userDatas.get(i).getAddressProvince()+userDatas.get(i).getAddressTown()+userDatas.get(i).getAddressDetail();
                               onItemClick(receiveID+address);
                           }
                       }
                  // }




               }

           }
        }
        return super.onKeyDown(keyCode, event);
    }


    public class ReceiverUserAelect extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
           // Toast.makeText(mContext,"接收到信号！OOOO",Toast.LENGTH_LONG).show();
            finish();
        }

    }
}
