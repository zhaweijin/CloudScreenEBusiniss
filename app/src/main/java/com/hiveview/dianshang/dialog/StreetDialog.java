package com.hiveview.dianshang.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.ChildDataAadapter;
import com.hiveview.dianshang.adapter.StreetAdapter;
import com.hiveview.dianshang.base.BDialog;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.address.AreaData;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.MemListView;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ThinkPad on 2017/8/25.
 */

public class StreetDialog extends BDialog {
    private LayoutInflater mFactory = null;
    private Context context;
    private String tag="StreetDialog";
    private String parentCode;
    private MemListView streetList;
    private AreaData areaDataItem;
    public static int select_item_city=-1;
    private StreetAdapter adapter;
    private TextView text_street;
    private TextView totalCode;
    private boolean bottom=false;
    private LinearLayout layout_tips;
    private Button ok;

    public StreetDialog(Context context,String parentCode,TextView text_street,TextView totalCode) {
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.parentCode = parentCode;
        this.text_street=text_street;
        this.totalCode=totalCode;

        mFactory = LayoutInflater.from(context);
        View mView = mFactory.inflate(R.layout.street_select, null);

        setContentView(mView);

        streetList=(MemListView) mView.findViewById(R.id.streetList);
        layout_tips=(LinearLayout)mView.findViewById(R.id.layout_tips);
        ok=(Button)mView.findViewById(R.id.ok);
        streetList.setSelector(new ColorDrawable(Color.TRANSPARENT));

        getAreaList(parentCode);
        streetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //areaDataItem  areaDataItem.getData().get(position).getCode()
                text_street.setText(areaDataItem.getData().get(position).getName());
                totalCode.setText(areaDataItem.getData().get(position).getTreePath());
                StreetDialog.this.dismiss();
            }
        });
        streetList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_item_city=position;
                adapter.notifyDataSetChanged();
                //areaDataItem.getData()
                if(position==areaDataItem.getData().size()-1){
                    bottom=true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //无街道数据时的确认键
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_street.setText(context.getResources().getString(R.string.nostreet));
                StreetDialog.this.dismiss();
            }
        });

    }

    public void getAreaList(String code){
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }
        startProgressDialog();
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(context).userID);
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
                            adapter = new StreetAdapter(context,areaDataItem.getData());
                            streetList.setAdapter(adapter);
                        }else{
                            streetList.setVisibility(View.INVISIBLE);
                            layout_tips.setVisibility(View.VISIBLE);
                            ok.requestFocus();
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
                    public void onNext(AreaData areaData) {
                        stopProgressDialog();
                        if(areaData.getReturnValue()==-1){
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

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.i("========", "=====按下");
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    Log.i("========", "=====点击下键");
                    if (bottom) {
                        streetList.setSelection(0);
                        bottom=false;
                    }



                    break;

            }
        }
        return super.dispatchKeyEvent(event);


    }
}
