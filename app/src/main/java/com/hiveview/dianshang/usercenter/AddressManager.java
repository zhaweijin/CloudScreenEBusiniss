package com.hiveview.dianshang.usercenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.AdduserDialog;
import com.hiveview.dianshang.entity.address.UserAddress;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.Utils;
import com.jakewharton.rxbinding.view.RxView;


import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;


/**
 * Created by carter on 4/23/17.
 */

public class AddressManager extends BaseFragment {



    LayoutInflater layoutInflater;

    @BindView(R.id.add_adresss)
    Button add_adresss;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_layout_address;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.print("test","onActivityCreated");
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferences = getActivity().getSharedPreferences("address",
                getActivity().MODE_WORLD_READABLE);

        initShowAddressUI();
    }



    private void initShowAddressUI(){

        add_adresss.requestFocus();

        RxView.clicks(add_adresss)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                      /*  RxBus.get().post(ConStant.obString_opt_natigation,ConStant.OP_EDIT_ADDRESS);
                        editor = preferences.edit();
                        editor.putBoolean("flag",true);
                        editor.putBoolean("isUpdate",false);
                        editor.commit();*/
                        UserAddress userAddressData=null;
                        AdduserDialog adduserDialog=new AdduserDialog(mContext,preferences,userAddressData,1);
                        if(!adduserDialog.isShowing()){
                            adduserDialog.showUI();
                        }
                    }
                });

    }



}
