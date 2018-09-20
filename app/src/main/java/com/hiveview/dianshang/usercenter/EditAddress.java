package com.hiveview.dianshang.usercenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseFragment;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.AreasSelectDialog;
import com.hiveview.dianshang.dialog.StreetDialog;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.entity.address.qrdata.QrData;
import com.hiveview.dianshang.entity.netty.payment.DomyTcpMsgBodyVo;
import com.hiveview.dianshang.entity.netty.payment.DomyTcpMsgVo;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.utils.QrcodeUtil;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by carter on 4/23/17.
 */

public class EditAddress extends BaseFragment {


    LayoutInflater layoutInflater;

    @BindView(R.id.save_address)
    Button save_address;

    @BindView(R.id.saveDefault)
    Button saveDefault;

    @BindView(R.id.region)
    Button region;

    @BindView(R.id.street)
    Button street;

    @BindView(R.id.text_address)
    TextView text_address;

    @BindView(R.id.text_street)
    TextView text_street;

    @BindView(R.id.recipient)
    EditText recipient;

    @BindView(R.id.phone)
    EditText phone;

    @BindView(R.id.detailedAddress)
    EditText detailedAddress;

    @BindView(R.id.first)
    LinearLayout first;

    @BindView(R.id.second)
    LinearLayout second;

    @BindView(R.id.third)
    LinearLayout third;

    @BindView(R.id.tv_areascode)
    TextView tv_areascode;

    @BindView(R.id.totalCode)
    TextView totalCode;

    @BindView(R.id.four)
    LinearLayout four;

    /**
     * 手机扫描的二维码展示区域
     */
    @BindView(R.id.receiveQr)
    LinearLayout receiveQr;




    private AreasSelectDialog areasSelectDialog;
    private StreetDialog streetDialog;
    private boolean flag;
    private boolean isUpdate;
    private SharedPreferences preferences;
    private String tag="EditAddress";
    private int id;
    private SharedPreferences preferencesLocation;
    private SharedPreferences.Editor editorLocation;
    private static RelativeLayout layout_domy_user_center;
    private static RelativeLayout layout_domy_shopping_cart;
    private static RelativeLayout layout_home;
    private static RelativeLayout layout_commodity;
    private static RelativeLayout layout_search;
    private static RelativeLayout layout_collection;
    private static RelativeLayout layout_order;
    private static RelativeLayout layout_after_sale_service;
    private int MAXLENG=40;
    private int MAXLENGRE=16;

    @Override
    protected int getLayoutId() {
        return R.layout.edit_address;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.print("test","onActivityCreated");
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preferences = getActivity().getSharedPreferences("address",
                getActivity().MODE_WORLD_READABLE);
        flag=preferences.getBoolean("flag",false);
        isUpdate=preferences.getBoolean("isUpdate",false);
        preferencesLocation = getActivity().getSharedPreferences("location",
                getActivity().MODE_WORLD_READABLE);
        editorLocation = preferencesLocation.edit();
        initEditAddressUI();

        regeditReceiver();

        //由上一页的用户数据映射
        if(isUpdate){
            recipient.setText(preferences.getString("consignee",""));
            phone.setText(preferences.getString("phone",""));
            text_address.setText(preferences.getString("addressProvince",""));
            text_street.setText(preferences.getString("addressTown",""));//
            detailedAddress.setText(preferences.getString("addressDetail",""));
            id=preferences.getInt("id",0);
            totalCode.setText(preferences.getString("treePath",""));
            String d[] = preferences.getString("treePath","").split(",");
            if(d!=null&&d.length>2&&d[2]!=null&&!d[2].equals("")){
                tv_areascode.setText(d[2]);
            }
        }

      //焦点移动的处理
        detailedAddress.setOnKeyListener(onKeyListener);
        phone.setOnKeyListener(onKeyListener);
        recipient.setOnKeyListener(onKeyListener);
        street.setOnKeyListener(onKeyListener);
        save_address.setOnKeyListener(onKeyListener);
        saveDefault.setOnKeyListener(onKeyListener);
        region.setOnKeyListener(onKeyListener);


        getReceiveQr();

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

    private void initEditAddressUI(){
        Log.i("=========","====初始化");
        if(flag){//只有一个默认保存键
            save_address.setVisibility(View.GONE);
        }

        if(save_address.getVisibility()==View.VISIBLE){
            save_address.requestFocus();
        }else{
            saveDefault.requestFocus();
            saveDefault.setNextFocusUpId(R.id.detailedAddress);
        }
       //打开编辑页面，焦点落入收件人的处理
        recipient.setFocusable(true);
        recipient.setFocusableInTouchMode(true);
        recipient.requestFocus();
        recipient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                recipient.setSelection(recipient.getText().toString().length());
                //限制收件人的输入字符 16
                Editable editable = recipient.getText();
                int len = editable.length();

                if(len > MAXLENGRE)
                {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.wordlimitre), Toast.LENGTH_SHORT).show();
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = str.substring(0,MAXLENGRE);
                    recipient.setText(newStr);
                    editable = recipient.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if(selEndIndex > newLen)
                    {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);

                }

            }
        });

        first.setBackground(mContext.getResources().getDrawable(R.drawable.shape2));

        RxView.clicks(save_address)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // RxBus.get().post(ConStant.obString_opt_natigation,ConStant.OP_USER_CENTER);
                            save_address.setEnabled(false);
                            if(isUpdate){//更新保存  isDefault=false
                                updateUser(preferences.getBoolean("isDefault",false));
                            }else{//新增  isDefault=false
                                addUser(false);
                            }


                    }
                });
        RxView.clicks(saveDefault)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        saveDefault.setEnabled(false);
                       if(isUpdate){//更新设为默认   isDefault=true
                           updateUser(true);
                       }else{//新增设为默认  isDefault=true
                           addUser(true);
                       }

                    }
                });

        RxView.clicks(region)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        areasSelectDialog = new AreasSelectDialog(mContext,text_address,tv_areascode,text_street,totalCode);
                        if(!areasSelectDialog.isShowing()){
                            areasSelectDialog.show();
                        }
                    }
                });
        RxView.clicks(street)
                .throttleFirst(ConStant.throttDuration, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                       // Toast.makeText(mContext, "点击街道！", Toast.LENGTH_SHORT).show();
                       String areascode=tv_areascode.getText().toString();
                        Log.i(tag,"===streetCode=="+areascode);
                        if(areascode.equals("")){
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.areasselecttip), Toast.LENGTH_SHORT).show();
                        }else{
                            streetDialog=new StreetDialog(mContext,areascode,text_street,totalCode);

                            if(!streetDialog.isShowing()){
                                streetDialog.show();
                            }

                        }
                    }
                });

       //处理焦点的问题
        recipient.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(first!=null){
                        recipient.setSelection(recipient.getText().toString().length());
                        first.setBackground(mContext.getResources().getDrawable(R.drawable.shape2));
                    }
                }else{
                    if(first!=null){
                        first.setBackground(mContext.getResources().getDrawable(R.drawable.shape1));
                    }


                }

            }
        });

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    phone.setSelection(phone.getText().toString().length());
                    if(second!=null){
                        second.setBackground(mContext.getResources().getDrawable(R.drawable.shape2));
                    }
                }else{
                    if(second!=null){
                        second.setBackground(mContext.getResources().getDrawable(R.drawable.shape1));
                    }
                }
            }
        });
        region.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //关闭软键盘
                    View view = ((MainActivity)mContext).getWindow().peekDecorView();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    if(third!=null){
                        third.setBackground(mContext.getResources().getDrawable(R.drawable.shape2));
                    }
                }else{
                    if(third!=null){
                        third.setBackground(mContext.getResources().getDrawable(R.drawable.shape1));
                    }

                }
            }
        });

        detailedAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 限制最大输入字数  40
                Editable editable = detailedAddress.getText();
                int len = editable.length();

                if(len > MAXLENG)
                {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.wordlimit), Toast.LENGTH_SHORT).show();
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串  
                    String newStr = str.substring(0,MAXLENG);
                    detailedAddress.setText(newStr);
                    editable = detailedAddress.getText();

                    //新字符串的长度  
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度  
                    if(selEndIndex > newLen)
                    {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置  
                    Selection.setSelection(editable, selEndIndex);

                }

            }
        });

        detailedAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    detailedAddress.setSelection(detailedAddress.getText().toString().length());
                }
            }
        });

        street.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if(four!=null){
                        four.setBackground(mContext.getResources().getDrawable(R.drawable.shape2));
                    }

                }else{
                    if(four!=null){
                        four.setBackground(mContext.getResources().getDrawable(R.drawable.shape1));
                    }
                }
            }
        });
        //addTextChangedListener(new TextWatcher() {
        text_street.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(mContext.getResources().getString(R.string.nostreet))){
                    text_street.setTextColor(android.graphics.Color.parseColor("#D0021B"));
                }else{
                    text_street.setTextColor(android.graphics.Color.parseColor("#3B3B3B"));
                }



            }
        });

    }




    /**
     * 添加用户地址
     */
    public void addUserAddress(UserData userData,boolean isDefault){
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            save_address.setEnabled(true);
            saveDefault.setEnabled(true);
            return;
        }

        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("addressDetail",format(userData.getAddressDetail()));
            Log.i(tag,"===addressDetail=="+format(userData.getAddressDetail()));
            json.put("addressProvince",userData.getAddressProvince());
            if(userData.getAddressTown().equals(mContext.getResources().getString(R.string.nostreet))){
                json.put("addressTown","");
                Log.i("===sss===","===addressTown==null");
            }else{
                json.put("addressTown",userData.getAddressTown());
                Log.i("===sss===","===addressTown=="+userData.getAddressTown());
            }

            json.put("consignee",format(userData.getConsignee()));
            Log.i(tag,"===consignee=="+format(userData.getConsignee()));
            json.put("isDefault",isDefault);
            json.put("phone",userData.getPhone());
            json.put("treePath",userData.getTreePath());
            Log.i("===sss===","===treePath=="+userData.getTreePath());
            input = json.toString();//input={"userid":"4055212","phone":"123456789","addressProvince":"北京北京市东城区","isDefault":false,"addressDetail":"222222","consignee":"222222","treePath":"1111,2222"}
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpAddUserAddressData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //error=HTTP 500 Server Error
                        Utils.print(tag,"error="+e.getMessage());
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        save_address.setEnabled(true);
                        saveDefault.setEnabled(true);
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"====status="+statusData.getErrorMessage());
                        save_address.setEnabled(true);
                        saveDefault.setEnabled(true);
                        if(statusData.getReturnValue()==0){
                            editorLocation.putInt("pos",0);
                            editorLocation.commit();
                            RxBus.get().post(ConStant.obString_opt_natigation,ConStant.OP_USER_CENTER);
                        }else{//操作失败，服务器异常，请稍后再试。
                            Toast.makeText(mContext, statusData.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 更新用户地址
     */
    public void updateUserAddress(UserData userData,boolean isDefault){
        Utils.print(tag,"updateUserAddress");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            save_address.setEnabled(true);
            saveDefault.setEnabled(true);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("receiveId",userData.getId());
            json.put("addressDetail",format(userData.getAddressDetail()));
            json.put("addressProvince",userData.getAddressProvince());
            Log.i(tag,"更新省市县=="+userData.getAddressProvince());
            if(userData.getAddressTown().equals(mContext.getResources().getString(R.string.nostreet))){
                json.put("addressTown","");
                Log.i("===sss===","===更新后的addressTown==null");
            }else{
                json.put("addressTown",userData.getAddressTown());
                Log.i("===sss===","===更新后的addressTown=="+userData.getAddressTown());
            }
            json.put("consignee",format(userData.getConsignee()));
            json.put("isDefault",isDefault);
            json.put("phone",userData.getPhone());
            json.put("treePath",userData.getTreePath());
            Log.i("===sss===","===更新后的treePath=="+userData.getTreePath());
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpUpdateUserAddressData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(mContext,error_tips);
                        save_address.setEnabled(true);
                        saveDefault.setEnabled(true);
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"status="+statusData.getErrorMessage());
                        save_address.setEnabled(true);
                        saveDefault.setEnabled(true);
                        if(statusData.getReturnValue()==0){
                            editorLocation.putInt("pos",0);
                            editorLocation.commit();
                            RxBus.get().post(ConStant.obString_opt_natigation,ConStant.OP_USER_CENTER);
                        }else{//操作失败，服务器异常，请稍后再试。
                            Toast.makeText(mContext, statusData.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        addSubscription(subscription);
    }


    //新增用户的方法
    public void addUser(boolean b){

        if(recipient.getText().toString().equals("")||phone.getText().toString().equals("")||text_address.getText().toString().equals("")||text_street.getText().toString().equals("")||detailedAddress.getText().toString().equals("")){
            Toast.makeText(mContext,mContext.getResources().getString(R.string.perfectinformationtip), Toast.LENGTH_SHORT).show();
            save_address.setEnabled(true);
            saveDefault.setEnabled(true);
        }else{

            String num=phone.getText().toString();
            if(isMobile(num)){//正确格式的手机号
                UserData userDataNew=new UserData();
                userDataNew.setConsignee(recipient.getText().toString());
                userDataNew.setPhone(phone.getText().toString());
                userDataNew.setAddressProvince(text_address.getText().toString());
                userDataNew.setAddressDetail(detailedAddress.getText().toString());
                userDataNew.setAddressTown(text_street.getText().toString());
                userDataNew.setTreePath(totalCode.getText().toString());
                addUserAddress(userDataNew,b);
            }else{//错误格式的手机号
                save_address.setEnabled(true);
                saveDefault.setEnabled(true);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.phonetip), Toast.LENGTH_SHORT).show();
            }
        }

    }
    //更新用户
    public void updateUser(boolean b){
        if(recipient.getText().toString().equals("")||phone.getText().toString().equals("")||text_address.getText().toString().equals("")||text_street.getText().toString().equals("")||detailedAddress.getText().toString().equals("")){
            Toast.makeText(mContext, mContext.getResources().getString(R.string.perfectinformationtip), Toast.LENGTH_SHORT).show();
            save_address.setEnabled(true);
            saveDefault.setEnabled(true);
        }else{//暂时不处理街道
            String num=phone.getText().toString();
            if(isMobile(num)){//正确格式的手机号
                UserData userDataUpdate=new UserData();
                userDataUpdate.setId(id);
                userDataUpdate.setConsignee(recipient.getText().toString());
                userDataUpdate.setPhone(phone.getText().toString());
                userDataUpdate.setAddressProvince(text_address.getText().toString());
                userDataUpdate.setAddressDetail(detailedAddress.getText().toString());
                userDataUpdate.setAddressTown(text_street.getText().toString());
                userDataUpdate.setTreePath(totalCode.getText().toString());
                updateUserAddress(userDataUpdate,b);
            }else{//错误格式的手机号
                save_address.setEnabled(true);
                saveDefault.setEnabled(true);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.phonetip), Toast.LENGTH_SHORT).show();
            }
        }
    }

    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    setFocuse(true);
                }

                if(v.getId()==R.id.detailedAddress && keyCode==KeyEvent.KEYCODE_DPAD_DOWN) {
                    Log.i("========","=====点击下键");
                    if(save_address.getVisibility()==View.VISIBLE){
                        Log.i("========","=====保存键获得焦点");
                        detailedAddress.setNextFocusDownId(R.id.save_address);
                    }else{
                        Log.i("========","=====默认保存键获得焦点");
                        detailedAddress.setNextFocusDownId(R.id.saveDefault);
                    }

                }else if(v.getId()==R.id.detailedAddress && keyCode==KeyEvent.KEYCODE_DPAD_UP){
                    detailedAddress.setNextFocusUpId(R.id.street);
                }else if(v.getId()==R.id.phone && keyCode==KeyEvent.KEYCODE_DPAD_LEFT ){//焦点在电话编辑框且按左键
                    phone.setSelection(0);
                }else if(v.getId()==R.id.phone && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){//焦点在电话编辑框且按右键
                    phone.setNextFocusRightId(R.id.phone);
                }else if(v.getId()==R.id.recipient && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){//焦点在收件人编辑框且按右键
                    /*recipient.clearFocus();
                    recipient.setNextFocusDownId(R.id.phone);*/
                    recipient.setSelection(recipient.getText().toString().length());
                  //  Toast.makeText(mContext, "点击右键在收件人", Toast.LENGTH_SHORT).show();
                }else if(v.getId()==R.id.recipient && keyCode==KeyEvent.KEYCODE_DPAD_LEFT ){
                    recipient.setSelection(0);
                    v.setNextFocusLeftId(R.id.layout_domy_user_center);
                }else if(v.getId()==R.id.detailedAddress && keyCode==KeyEvent.KEYCODE_DPAD_LEFT ){
                    detailedAddress.setSelection(0);
                    v.setNextFocusLeftId(R.id.layout_domy_user_center);
                }else if(v.getId()==R.id.street && keyCode==KeyEvent.KEYCODE_DPAD_LEFT ){
                    v.setNextFocusLeftId(R.id.layout_domy_user_center);
                }else if(v.getId()==R.id.street && keyCode==KeyEvent.KEYCODE_DPAD_DOWN ){
                    v.setNextFocusDownId(R.id.detailedAddress);
                }else if(v.getId()==R.id.detailedAddress && keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    v.setNextFocusRightId(R.id.detailedAddress);
                }else if(v.getId()==R.id.save_address && keyCode==KeyEvent.KEYCODE_DPAD_LEFT ){
                    v.setNextFocusLeftId(R.id.layout_domy_user_center);
                }else if(v.getId()==R.id.saveDefault && keyCode==KeyEvent.KEYCODE_DPAD_LEFT &&save_address.getVisibility()==View.GONE){
                    v.setNextFocusLeftId(R.id.layout_domy_user_center);
                }else if(v.getId()==R.id.save_address && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                     return  true;
                }else if(v.getId()==R.id.saveDefault && keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
                    return true;
                }else if(v.getId()==R.id.saveDefault && keyCode==KeyEvent.KEYCODE_DPAD_UP){
                    v.setNextFocusUpId(R.id.detailedAddress);
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

    public static String format(String s){
        String str=s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& ;*（）——+|{}【】‘；：”“’。、？|-]", "");
        str = str.replace("\\s","");
        str = str.replace("\n","");
        return str;
    }
    public static boolean isMobile(String mobiles) {
        Pattern p = Pattern.compile("^((0\\d{2,3}\\d{7,8})|(1[35784]\\d{9}))$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    public static boolean isMobileNew(String mobile){
        String regex = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";
        return Pattern.matches(regex, mobile);
    }
    //(^(\d{2,4}[-_－—]?)?\d{3,8}([-_－—]?\d{3,8})?([-_－—]?\d{1,7})?$)|(^0?1[35]\d{9}$)
    public static boolean isMobileTaiwan(String mobile){
        String regex = "(^(\\d{2,4}[-_－—]?)?\\d{3,8}([-_－—]?\\d{3,8})?([-_－—]?\\d{1,7})?$)|(^0?1[35]\\d{9}$)";
        return Pattern.matches(regex, mobile);
    }




    public void getReceiveQr(){
        Utils.print(tag,"getReceiveQr");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            //type=1 新增收件人地址,无须receiveId字段
            //type=2 修改收件人地址,需要传入receiveId字段
            if(isUpdate){ //修改地址
                json.put("type",2);
                json.put("receiveId",id);
            }else{ //增加地址
                json.put("type",1);
            }
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"2input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetReceiverQr(input,ConStant.getInstance(mContext).Token)
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


                        receiveQr.setBackground(new QrcodeUtil().generateQRCode(mContext,qrData.getData().getQrData()));
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
            DomyTcpMsgVo info = domyTcpMsgBodyVo.getInfo().get(0);
            if (isUpdate && domyTcpMsgBodyVo.getInfo().get(0).getActionType() == ConStant.ACTION_TYPE_MODITY_ADDRESS && domyTcpMsgBodyVo.getInfo().get(0).getContentId().equals(ConStant.NETTY_OP_SUCESS)) {  //修改地址
                Utils.print(tag, "update address sucess-------");
                RxBus.get().post(ConStant.obString_opt_natigation, ConStant.OP_USER_CENTER);
            } else if (!isUpdate && domyTcpMsgBodyVo.getInfo().get(0).getActionType() == ConStant.ACTION_TYPE_ADD_ADDRESS && domyTcpMsgBodyVo.getInfo().get(0).getContentId().equals(ConStant.NETTY_OP_SUCESS)) { //添加地址
                Utils.print(tag, "add address sucess-------");
                RxBus.get().post(ConStant.obString_opt_natigation, ConStant.OP_USER_CENTER);
            }

        }
    };

    private void regeditReceiver() {
        Utils.print(tag,"regeditReceiver netty");
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConStant.ACTION_NETTRY_RECEIVER);
        mContext.registerReceiver(nettyReceiver, mFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(nettyReceiver);
    }
}
