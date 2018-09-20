package com.hiveview.dianshang.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.usercenter.UserCenterFragment;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.address.AddressAPI;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.usercenter.EditAddressActivity;
import com.hiveview.dianshang.usercenter.UserAdderssSelect;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.DeleteAddressPopWindow;
import com.hiveview.dianshang.view.DeleteAddressWindowInterface;


import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gavin on 2017/6/1.
 */

public class UserCenterDialogAdapter extends BaseAdapter {
    private Context context;
    private List<UserData> list;
    private DeleteAddressPopWindow deleteAddressWindow;
    private double dialogAlpha = 0.45;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String tag="AddressAdapter";
    private int select_item = -1;
    private Boolean isValid=true;
    private Boolean first=true;

    public UserCenterDialogAdapter(Context context, List<UserData> list) {
        this.context = context;
        this.list = list;
        preferences = context.getSharedPreferences("address",
                context.MODE_WORLD_READABLE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_itemusercenter, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();

        vh.name.setText(list.get(position).getConsignee());
        vh.phoneNum.setText(list.get(position).getPhone());
        if(list.get(position).getAddressTown()==null){
            vh.address.setText(list.get(position).getAddressProvince()+list.get(position).getAddressDetail());
        }else{
            vh.address.setText(list.get(position).getAddressProvince()+list.get(position).getAddressTown()+list.get(position).getAddressDetail());
        }

        //默认地址图标是否显示
        if(list.get(position).getDefault()){//默认
            vh.angle_icon.setVisibility(View.VISIBLE);
        }else{
            vh.angle_icon.setVisibility(View.GONE);
        }

        this.select_item = UserCenterFragment.select_item;
        try{
            if(this.select_item == position){//选中
             vh.deleteImg.setVisibility(View.VISIBLE);
                vh.edit.setVisibility(View.VISIBLE);
                vh.first_layout.setFocusable(true);
                vh.first_layout.requestFocus();
                vh.edit.setFocusable(true);
                vh.deleteImg.setFocusable(true);

            }else{//未选中
                vh.edit.setFocusable(false);
                vh.edit.setVisibility(View.GONE);
                vh.first_layout.setFocusable(false);
                vh.deleteImg.setFocusable(false);
                vh.deleteImg.setVisibility(View.GONE);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        //删除和编辑的监听
        vh.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid&&list.size()!=0){
                    showDeleteAddressWindow(position);
                }
            }
        });
        vh.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = preferences.edit();
                editor.putBoolean("flag",false);
                editor.putBoolean("isUpdate",true);
                editor.putInt("id",list.get(position).getId());
                editor.putString("consignee",list.get(position).getConsignee());
                editor.putString("phone",list.get(position).getPhone());
                editor.putString("addressProvince",list.get(position).getAddressProvince());
                editor.putString("addressTown",list.get(position).getAddressTown());
                editor.putString("addressDetail",list.get(position).getAddressDetail());
                editor.putString("treePath",list.get(position).getTreePath());
                editor.putBoolean("isDefault",list.get(position).getDefault());
                editor.commit();

               // Toast.makeText(context, "点击编辑！", Toast.LENGTH_SHORT).show();
                EditAddressActivity.launch((UserAdderssSelect)(context));


            }
        });
       vh.first_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "点击中间！", Toast.LENGTH_SHORT).show();
                //数据对象
               // list.get(position)
                //@add by carter
                Utils.print(tag,"onItemClick="+select_item);
                String receiveID = list.get(select_item).getId()+"#";
                String address = list.get(select_item).getConsignee()+"     "+list.get(select_item).getPhone()
                        +"\n"+list.get(select_item).getAddressProvince()+
                        list.get(select_item).getAddressTown()+
                        list.get(select_item).getAddressDetail();
                ((UserAdderssSelect)context).onItemClick(receiveID+address);
            }
        });
        vh.deleteImg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){//失去焦点且布局也没有焦点，，，隐藏删除键
                    if(!vh.first_layout.hasFocus()){
                        vh.deleteImg.setVisibility(View.GONE);
                        vh.edit.setVisibility(View.GONE);
                    }
                }

            }
        });

        vh.edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){//失去焦点且布局也没有焦点，，，隐藏删除键
                    if(!vh.first_layout.hasFocus()){
                        vh.deleteImg.setVisibility(View.GONE);
                        vh.edit.setVisibility(View.GONE);
                    }
                }
            }
        });
        vh.first_layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {//失去焦点且删除键也没有焦点，，，隐藏删除键
                    if(!vh.deleteImg.hasFocus()&&!vh.edit.hasFocus()){
                        vh.deleteImg.setVisibility(View.GONE);
                        vh.edit.setVisibility(View.GONE);
                    }
                }else{
                    vh.deleteImg.setVisibility(View.VISIBLE);
                    vh.edit.setVisibility(View.VISIBLE);
                }
            }
        });




        return convertView;
    }
    public static class ViewHolder{
        private TextView name;
        private TextView phoneNum;
        private TextView address;
        private ImageButton deleteImg;
        private ImageButton edit;
        private LinearLayout first_layout;
        private ImageView angle_icon;
        public ViewHolder(View viewItem){
            name = (TextView) viewItem.findViewById(R.id.name);
            phoneNum=(TextView)viewItem.findViewById(R.id.phoneNum);
            address=(TextView)viewItem.findViewById(R.id.address);
            deleteImg=(ImageButton)viewItem.findViewById(R.id.delete);
            edit=(ImageButton)viewItem.findViewById(R.id.edit);
            first_layout=(LinearLayout)viewItem.findViewById(R.id.first);
            angle_icon=(ImageView)viewItem.findViewById(R.id.angle_icon);
        }
    }

    //添加地址删除的弹出框
    public void showDeleteAddressWindow(int i){
        deleteAddressWindow=new DeleteAddressPopWindow(context, context.getResources().getString(R.string.deleteaddresstip),
                new DeleteAddressWindowInterface() {
                    @Override
                    public void onConfirm() {
                        // Toast.makeText(context,"点击确认删除！",Toast.LENGTH_LONG).show();
                        Log.i("========","=====删除=="+i);
                        if(first){
                            if(list.size()>i){
                                first=false;
                                deleteUserAddress(list.get(i).getId(),list.get(i).getDefault());
                            }
                        }


                    }

                    @Override
                    public void onCancel() {
                        deleteAddressWindow.dismiss();
                        isValid=true;

                    }
                });
        deleteAddressWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                backgroundAlpha((float) 1.0);
            }
        });
        backgroundAlpha((float) dialogAlpha);
        deleteAddressWindow.showWindow();



    }
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        ((Activity)context).getWindow().setAttributes(lp);
    }

    /**
     * 删除用户地址
     */
    public void deleteUserAddress(int receiveId,boolean isDefault){
        Utils.print(tag,"deleteUserAddress");
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            isValid=true;
            first=true;
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.userID);
            json.put("receiveId",receiveId);
            json.put("isDefault",isDefault);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpdeleteUserAddressData(input,ConStant.Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"=====error="+e.getMessage());
                        isValid=true;
                        first=true;
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"=====status="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        first=true;
                        if(statusData.getReturnValue()==0){
                            deleteAddressWindow.dismiss();
                            isValid=true;
                            Intent mIntent = new Intent("com.dianshang.damai.refreshuser");
                            //发送广播
                            context.sendBroadcast(mIntent);
                        }else{//操作失败，服务器异常，请稍后再试。
                            isValid=true;
                            Toast.makeText(context, statusData.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
