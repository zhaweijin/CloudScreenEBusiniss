package com.hiveview.dianshang.dialog;

import com.hiveview.dianshang.base.BDialog;
import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.ChildDataAadapter;
import com.hiveview.dianshang.adapter.ChildDataAadapter2;
import com.hiveview.dianshang.adapter.ProvinceAdapter;
import com.hiveview.dianshang.constant.ConStant;

import com.hiveview.dianshang.entity.address.AddressAPI;
import com.hiveview.dianshang.entity.address.AreaData;
import com.hiveview.dianshang.entity.address.ProviceData;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.CustomProgressDialog;
import com.hiveview.dianshang.view.MemListView;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hiveview.dianshang.R.id.cityList;
import static com.hiveview.dianshang.R.id.countyList;
import static com.hiveview.dianshang.R.id.provinceList;


/**
 * 快递地址选择对话框
 * Created by carter on 4/11/17.
 */

public class AreasSelectDialog extends BDialog {

    private LayoutInflater mFactory = null;
    private Context context;
    private TextView tv;
    private TextView tv_areascode;
    private String tag="AreasSelectDialog";


    private MemListView provinceListView;
    private MemListView cityListView;
    private LinearLayout cityListLayout;
    private MemListView countyListView;
    private LinearLayout countyListLayout;
    private String[] addressSet;
    private ProviceData proviceDataItem;
    private ProvinceAdapter provinceAdapter;
    private ChildDataAadapter childDataAadapter;
    private ChildDataAadapter2 childDataAadapter2;
    private AreaData areaDataItem;
    private AreaData areaDataItemcounty;
    private String secondCode="";
    //全局变量，记录选中的item
    public static int select_item_provinces = -1;
    public static int select_item_city=-1;
    public static int select_item_county=-1;
    private boolean bottom=false;
    private boolean bottomCity=false;
    private boolean bottomcounty=false;
    private TextView text_street;
    private TextView totalCode;
    private boolean isOk=true;
    public AreasSelectDialog(Context context, TextView tv,TextView tv_areascode,TextView text_street,TextView totalCode) {
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.tv = tv;
        this.tv_areascode=tv_areascode;
        this.text_street=text_street;
        this.totalCode=totalCode;
        addressSet = new String[3];

        mFactory = LayoutInflater.from(context);
        View mView = mFactory.inflate(R.layout.areas_select, null);

        setContentView(mView);



        provinceListView = (MemListView) mView.findViewById(provinceList);
        cityListView = (MemListView) mView.findViewById(cityList);
        cityListLayout = (LinearLayout) mView.findViewById(R.id.cityListLayout);
        countyListView = (MemListView) mView.findViewById(countyList);
        countyListLayout = (LinearLayout) mView.findViewById(R.id.countyListLayout);
        provinceListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        cityListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        countyListView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        getProviceList();
        provinceListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                isOk=false;
                TextView text = (TextView) ((LinearLayout) view).getChildAt(0);
                addressSet[0] = text.getText().toString();
                //点击适配下一级列表市

                getAreaList2(proviceDataItem.getData().get(position).getCode());

            }
        });

        //设置省份选择滑动的视觉效果
        provinceListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int count = provinceListView.getCount();
                if(provinceListView.hasFocus()){
                    select_item_provinces=position;
                    provinceAdapter.notifyDataSetChanged();
                    if(position==proviceDataItem.getData().size()-1){
                        bottom=true;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        cityListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                isOk=false;
                TextView text = (TextView) ((LinearLayout) view).getChildAt(0);
                addressSet[1] = text.getText().toString();
                secondCode=areaDataItem.getData().get(position).getParentCode();
               //适配下一级列表区
                getAreaList3(areaDataItem.getData().get(position).getCode());
            }
        });

        //设置市选择滑动的视觉效果
        cityListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int count = cityListView.getCount();
               // Toast.makeText(context, "市selection！", Toast.LENGTH_SHORT).show();
                cityListView.requestFocus();
                if(cityListView.hasFocus()){
                   // Toast.makeText(context, "市列表有焦点selection！", Toast.LENGTH_SHORT).show();
                    select_item_city=position;
                    childDataAadapter.notifyDataSetChanged();
                    if(position==areaDataItem.getData().size()-1){
                        bottomCity=true;
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        countyListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                //finish();

                TextView text = (TextView) ((LinearLayout) view).getChildAt(0);
                addressSet[2] = text.getText().toString();
                tv.setText(addressSet[0] + addressSet[1] + addressSet[2]);
                tv_areascode.setText(areaDataItemcounty.getData().get(position).getCode());
                text_street.setText("");
                totalCode.setText(areaDataItemcounty.getData().get(position).getTreePath());
                AreasSelectDialog.this.dismiss();
                provinceListView.setFocusable(true);
                cityListView.setFocusable(true);
            }
        });

        //设置区选择滑动的视觉效果
        countyListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int count = countyListView.getCount();
                countyListView.requestFocus();
                if(countyListView.hasFocus()){
                    select_item_county=position;
                    childDataAadapter2.notifyDataSetChanged();
                    if(position==areaDataItemcounty.getData().size()-1){
                        bottomcounty=true;
                    }
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //各列表焦点变化。从而决定显隐状态
        countyListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && (cityListLayout.getVisibility() == View.VISIBLE) && (provinceListView.getVisibility() == View.VISIBLE)) {
                  //  Toast.makeText(context, "区列表失去焦点！隐藏区", Toast.LENGTH_SHORT).show();
                    countyListLayout.setVisibility(View.INVISIBLE);
                    provinceListView.setFocusable(true);
                    cityListView.setFocusable(true);
                }
                if(hasFocus){
                    cityListLayout.setVisibility(View.VISIBLE);
                    countyListLayout.setVisibility(View.VISIBLE);
                }else{
                   // Toast.makeText(context, "区列表失去焦点！", Toast.LENGTH_SHORT).show();
                    cityListView.setEnabled(true);
                   provinceListView.setEnabled(true);
                }

            }
        });
        cityListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && (countyListLayout.getVisibility() == View.INVISIBLE) && (provinceListView.getVisibility() == View.VISIBLE)) {
                    //Toast.makeText(context, "市列表失去焦点！", Toast.LENGTH_SHORT).show();
                    if(!countyListView.hasFocus()){
                        cityListLayout.setVisibility(View.INVISIBLE);
                     //  Toast.makeText(context, "市列表失去焦点且消失！", Toast.LENGTH_SHORT).show();
                    }

                }
                if(!hasFocus&&cityListLayout.getVisibility()==View.VISIBLE){
                    cityListView.getSelectedView().setBackground(context.getResources().getDrawable(R.drawable.selectcover));
                }
                if (hasFocus) {
                  //  Toast.makeText(context, "市列表h获得焦点！", Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < cityListView.getChildCount(); i++) {
                        cityListView.getChildAt(i).setBackground(context.getResources().getDrawable(R.drawable.provinces_focus));
                    }
                   // Toast.makeText(context, "市列表h获得焦点！,,,区隐藏！！！", Toast.LENGTH_SHORT).show();
                    countyListLayout.setVisibility(View.INVISIBLE);
                    provinceListView.setFocusable(true);
                    cityListView.setFocusable(true);
                    cityListLayout.setVisibility(View.VISIBLE);
                }else{
                  //  Toast.makeText(context, "市列表失去焦点！", Toast.LENGTH_SHORT).show();
                    provinceListView.setEnabled(true);
                }
            }
        });

        provinceListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //Toast.makeText(context, "省列表获得焦点！", Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < provinceListView.getChildCount(); i++) {
                        provinceListView.getChildAt(i).setBackground(context.getResources().getDrawable(R.drawable.provinces_focus));
                    }
                  // Toast.makeText(context, "省份焦点获取时隐藏市和区列表！", Toast.LENGTH_SHORT).show();
                    cityListLayout.setVisibility(View.INVISIBLE);
                    cityListView.setFocusable(true);
                    countyListLayout.setVisibility(View.INVISIBLE);

                }

                if(!hasFocus&&provinceListView.getVisibility()==View.VISIBLE&&provinceListView!=null&&provinceListView.getSelectedView()!=null){
                    provinceListView.getSelectedView().setBackground(context.getResources().getDrawable(R.drawable.selectcover));
                }
            }
        });





    }


    /**
     * 获取省份列表
     */
    public void getProviceList(){
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }
        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(context).userID);
            json.put("parentCode","");
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetProvice(input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ProviceData>() {
                    @Override
                    public void onCompleted() {
                        Utils.print(tag,"====省份完成");
                        if(proviceDataItem!=null){
                            provinceAdapter = new ProvinceAdapter(context, proviceDataItem.getData());
                            provinceListView.setAdapter(provinceAdapter);
                           // UserCenterFragment.Utility.setListViewHeightBasedOnChildren(provinceListView);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        stopProgressDialog();
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);

                    }

                    @Override
                    public void onNext(ProviceData proviceData) {
                        stopProgressDialog();
                        Utils.print(tag,"====省份onNext");
                        if(proviceData.getReturnValue()==-1)
                            return;
                        Utils.print(tag,"====size="+proviceData.getData().size());
                        proviceDataItem=proviceData;
                        for(int i=0;i<proviceData.getData().size();i++){
                            Utils.print(tag,"====name="+proviceData.getData().get(i).getName());
                            Utils.print(tag,"====code="+proviceData.getData().get(i).getCode());
                        }
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 获取市,区,街道列表
     * eg:北京110000
     * @param code
     */
    //北京市 天津市 上海市 重庆市    直辖市有二级目录，需要自己处理一下数据变成三级数据统一起来！！！
    //直辖市数据暂未处理！！！
    public void getAreaList2(String code){
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(context).userID);
            json.put("parentCode",code);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetAreaData(input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AreaData>() {
                    @Override
                    public void onCompleted() {

                        if(areaDataItem!=null){
                            cityListLayout.setVisibility(View.VISIBLE);
                            childDataAadapter = new ChildDataAadapter(context,areaDataItem.getData());
                            cityListView.setAdapter(childDataAadapter);
                            cityListView.requestFocus();
                           // Toast.makeText(context, "市列表适配，，，区隐藏！！！", Toast.LENGTH_SHORT).show();
                            countyListLayout.setVisibility(View.INVISIBLE);
                            provinceListView.setFocusable(true);
                            cityListView.setFocusable(true);
                            isOk=true;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isOk=true;
                    }

                    @Override
                    public void onNext(AreaData areaData) {

                        if(areaData.getReturnValue()==-1){
                            isOk=true;
                            return;
                        }

                        areaDataItem=areaData;
                        Utils.print(tag,"====size="+areaData.getData().size());
                        Utils.print(tag,"====returnValue="+areaData.getErrorMessage());
                        for(int i=0;i<areaData.getData().size();i++){
                            Utils.print(tag,"====name="+areaData.getData().get(i).getName());
                            Utils.print(tag,"====code="+areaData.getData().get(i).getCode());
                            Utils.print(tag,"====parentCode="+areaData.getData().get(i).getParentCode());
                        }
                    }
                });
        addSubscription(subscription);

    }

    //获取区的数据
    public void getAreaList3(String code){
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(context).userID);
            json.put("parentCode",code);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetAreaData(input,ConStant.getInstance(context).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AreaData>() {
                    @Override
                    public void onCompleted() {
                        if(areaDataItemcounty!=null){
                            countyListLayout.setVisibility(View.VISIBLE);
                            childDataAadapter2 = new ChildDataAadapter2(context, areaDataItemcounty.getData());
                            countyListView.setAdapter(childDataAadapter2);
                            countyListView.requestFocus();
                            isOk=true;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isOk=true;
                    }

                    @Override
                    public void onNext(AreaData areaData) {
                        if(areaData.getReturnValue()==-1){
                            isOk=true;
                            return;
                        }

                        areaDataItemcounty=areaData;
                        Utils.print(tag,"====size="+areaData.getData().size());
                        Utils.print(tag,"====returnValue="+areaData.getErrorMessage());
                        for(int i=0;i<areaData.getData().size();i++){
                            Utils.print(tag,"====name="+areaData.getData().get(i).getName());
                            Utils.print(tag,"====code="+areaData.getData().get(i).getCode());
                            Utils.print(tag,"====parentCode="+areaData.getData().get(i).getParentCode());
                        }
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.i("========", "=====按下");
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    Log.i("========", "=====点击下键");
                    if(isOk){
                        if (bottom&&provinceListView!=null&&provinceListView.hasFocus()) {
                            provinceListView.setSelection(0);
                            bottom=false;
                            //   Toast.makeText(context, "省份循环！", Toast.LENGTH_SHORT).show();
                        }
                        if(bottomcounty&&countyListView!=null&&countyListView.hasFocus()){
                            bottomcounty=false;
                            cityListView.setEnabled(false);
                            provinceListView.setEnabled(false);
                            provinceListView.setFocusable(false);
                            cityListView.setFocusable(false);
                            countyListView.setSelection(0);
                            //  Toast.makeText(context, "区循环！", Toast.LENGTH_SHORT).show();
                        }
                        if(bottomCity&&cityListView!=null&&cityListView.hasFocus()){
                            bottomCity=false;
                            provinceListView.setEnabled(false);
                            cityListView.setSelection(0);
                            //  Toast.makeText(context, "市循环！", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    }else{
                        return true;
                    }

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(provinceListView.getVisibility()==View.VISIBLE&&cityListLayout.getVisibility()==View.INVISIBLE&&countyListLayout.getVisibility()==View.INVISIBLE){//焦点在一级列表，即第二级第三级列表不显示
                      if(proviceDataItem!=null&&select_item_provinces<proviceDataItem.getData().size()&&select_item_provinces>-1){
                          addressSet[0] = proviceDataItem.getData().get(select_item_provinces).getName();
                          //点击适配下一级列表市
                          getAreaList2(proviceDataItem.getData().get(select_item_provinces).getCode());
                      }
                    }
                    if(provinceListView.getVisibility()==View.VISIBLE&&cityListLayout.getVisibility()==View.VISIBLE&&countyListLayout.getVisibility()==View.INVISIBLE){//焦点在二级列表即一二级列表显示三级列表不显示
                        //select_item_city   areaDataItem
                        if(areaDataItem!=null&&select_item_city<areaDataItem.getData().size()&&select_item_city>-1){
                            addressSet[1] =areaDataItem.getData().get(select_item_city).getName();
                            secondCode=areaDataItem.getData().get(select_item_city).getParentCode();
                            //适配下一级列表区
                            getAreaList3(areaDataItem.getData().get(select_item_city).getCode());
                        }

                    }

                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(countyListLayout.getVisibility()==View.VISIBLE){
                        provinceListView.setFocusable(false);
                    }else{
                        provinceListView.setFocusable(true);
                    }
                    cityListView.setFocusable(true);
                    break;
            }
        }
            return super.dispatchKeyEvent(event);


    }

}














