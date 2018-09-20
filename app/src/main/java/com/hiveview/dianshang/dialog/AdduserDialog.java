package com.hiveview.dianshang.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BDialog;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.address.UserAddress;
import com.hiveview.dianshang.entity.address.qrdata.QrData;
import com.hiveview.dianshang.entity.netty.payment.DomyTcpMsgBodyVo;
import com.hiveview.dianshang.usercenter.EditAddressActivity;
import com.hiveview.dianshang.usercenter.UserAdderssSelect;
import com.hiveview.dianshang.utils.QrcodeUtil;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ThinkPad on 2017/11/15.
 */

public class AdduserDialog extends BDialog {

    private LayoutInflater mFactory = null;
    private Context context;
    private SharedPreferences preferences;
    private UserAddress userAddressData;
    private SharedPreferences.Editor editor;

    private LinearLayout receiveQr;
    private Button goSave;
    private Button ok;
    private String tag="AdduserDialog";
    private boolean isUpdateTag=false;
    private int type;

    public AdduserDialog(Context context,SharedPreferences preferences,UserAddress userAddressData,int type) {
        super(context, R.style.selectorDialog);
        this.context = context;
        this.preferences=preferences;
        this.userAddressData=userAddressData;
        this.type=type;
        setOnDismissListener(this);
        mFactory = LayoutInflater.from(context);
        View mView = mFactory.inflate(R.layout.layout_adduser, null);
        setContentView(mView);

        initView(mView);
        getReceiveQr();
    }

    public void showUI(){

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = Utils.getScreenW(context);
        lp.height = Utils.getScrrenH(context);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);


        show();
    }
    private void initView(View view){
        receiveQr=(LinearLayout)view.findViewById(R.id.receiveQr);
        goSave=(Button)view.findViewById(R.id.goSave);
        ok=(Button)view.findViewById(R.id.ok);
        regeditReceiver();
        goSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        //有数据后，根据有无地址数据来写标志！！！！
                        editor = preferences.edit();
                        if(userAddressData!=null&&userAddressData.getData().getRecords().size()!=0){
                            editor.putBoolean("flag",false);
                        }else{
                            editor.putBoolean("flag",true);
                        }

                        editor.putBoolean("isUpdate",false);
                        editor.commit();
                       AdduserDialog.this.dismiss();
                if(type==1){//从个人中心进入
                    RxBus.get().post(ConStant.obString_opt_natigation,ConStant.OP_EDIT_ADDRESS);
                }else{//从提交订单中进入
                    EditAddressActivity.launch((UserAdderssSelect)(context));
                }

            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdduserDialog.this.dismiss();
            }
        });
    }

    BroadcastReceiver nettyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msgJson = intent.getStringExtra("msgJson");
            Utils.print(tag, "netty>>>" + msgJson);
            Gson gson = new Gson();
            DomyTcpMsgBodyVo domyTcpMsgBodyVo = gson.fromJson(msgJson, DomyTcpMsgBodyVo.class);
            //Utils.print(tag,domyTcpMsgBodyVo.getInfo().get(0).getAction());
            //Utils.print(tag,domyTcpMsgBodyVo.getInfo().get(0).getContent());

       if (!isUpdateTag && domyTcpMsgBodyVo.getInfo().get(0).getActionType() == ConStant.ACTION_TYPE_ADD_ADDRESS && domyTcpMsgBodyVo.getInfo().get(0).getContentId().equals(ConStant.NETTY_OP_SUCESS)) {
               Utils.print(tag, "add address sucess-------");
               AdduserDialog.this.dismiss();
               if(type==1){
                   RxBus.get().post(ConStant.obString_opt_natigation,ConStant.OP_USER_CENTER);
               }else{
                   AdduserDialog.this.dismiss();
                   Intent in = new Intent("com.dianshang.damai.message_finish");
                   context.sendBroadcast(in);
                   //重新刷新，，，，
                   UserAdderssSelect.launch((UserAdderssSelect)context);
                 /*  Intent mIntent = new Intent("com.dianshang.damai.refreshuser");
                   //发送广播
                   context.sendBroadcast(mIntent);
                   AdduserDialog.this.dismiss();*/
               }

            }
        }
    };

    private void regeditReceiver() {
        Utils.print(tag,"regeditReceiver netty");
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConStant.ACTION_NETTRY_RECEIVER);
        context.registerReceiver(nettyReceiver, mFilter);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Utils.print(tag,"onDismiss...");
        context.unregisterReceiver(nettyReceiver);
    }




    public void getReceiveQr(){
        Utils.print(tag,"getReceiveQr");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(context).userID);
            //type=1 新增收件人地址,无须receiveId字段
            //type=2 修改收件人地址,需要传入receiveId字段
            json.put("type",1);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"2input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetReceiverQr(input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<QrData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(QrData qrData) {
                        Utils.print(tag,"status=="+qrData.getErrorMessage()+",value="+qrData.getReturnValue());
                        if(qrData.getReturnValue()==-1)
                            return;
                        Utils.print(tag,"qr=="+qrData.getData().getQrData());
                        Utils.print(tag,"qr=="+qrData.getData().getTimeStamp());


                        receiveQr.setBackground(new QrcodeUtil().generateQRCodeFirst(context,qrData.getData().getQrData()));
                    }
                });
    }

}
