package com.hiveview.dianshang.usercenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.home.InstallService;
import com.hiveview.dianshang.utils.Utils;

/**
 * Created by ThinkPad on 2017/6/25.
 */

public class EditAddressActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private EditAddressShop editAddress;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, EditAddressActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_select_layout);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        editAddress = new EditAddressShop();
        transaction.replace(R.id.layout_contain, editAddress);
        transaction.commit();


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
        if(!Utils.isServiceWorked(this, ConStant.Install_ServiceName)){
            Intent intent = new Intent(this,InstallService.class);
            intent.putExtra("status",InstallService.EXIT);
            startService(intent);
        }
    }
}
