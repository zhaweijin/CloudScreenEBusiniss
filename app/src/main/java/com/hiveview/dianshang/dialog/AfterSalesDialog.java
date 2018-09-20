package com.hiveview.dianshang.dialog;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.base.BDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.AfterSalesDialogAdapter;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.AfterSaleslistEntity;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.commodity.CommodityAPI;
import com.hiveview.dianshang.entity.order.OrderRecord;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Gavin on 2017/5/10.
 */

public class AfterSalesDialog extends BDialog {
    private LayoutInflater mFactory = null;
    private Context context;
    private View mView;
    private TextView phone;
    private TextView servicephonetip;
    private TextView time;
    private TextView specificTime;
    private TextView orderNum;
    private SimpleDraweeView img;
    private TextView title;
    private TextView name;
    private TextView count;
    private TextView money;
    private TextView moneytype;
    private ListView detailListView;
    private Button submit;
    private ReceiverAfterSales receiverAfterSales;
    private int outputType=0;
    private String outputType_tv="退款";
    private int whyType=0;
    private String whyType_tv="商品买错了";
    private int currentCount=1;
    private OrderItemInfo orderItemInfo;
    private String tag="AfterSalesDialog";
    private OrderRecord orderRecord;
    private AfterSalesDialogAdapter adapter;


    public AfterSalesDialog(Context context, OrderItemInfo orderItemInfo,OrderRecord orderRecord){
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.orderItemInfo=orderItemInfo;
        this.orderRecord=orderRecord;

        mFactory = LayoutInflater.from(context);
        mView = mFactory.inflate(R.layout.layout_aftersalesdialog, null);

        setContentView(mView);
        initView();
        //注册广播
        receiverAfterSales = new ReceiverAfterSales ();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dianshang.damai.message_changed");
        context.registerReceiver(receiverAfterSales, intentFilter);
        AfterSalesDialog.this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                context.unregisterReceiver(receiverAfterSales);
            }
        });
    }

    public void initView(){
        phone=(TextView)mView.findViewById(R.id.phone);
        servicephonetip=(TextView)mView.findViewById(R.id.servicephonetip);
        time=(TextView)mView.findViewById(R.id.time);
        specificTime=(TextView)mView.findViewById(R.id.specificTime);
        orderNum=(TextView)mView.findViewById(R.id.orderNum);
        img=(SimpleDraweeView)mView.findViewById(R.id.img);
        name=(TextView)mView.findViewById(R.id.name);
        title=(TextView)mView.findViewById(R.id.title);
        count=(TextView)mView.findViewById(R.id.count);
        money=(TextView)mView.findViewById(R.id.money);
        moneytype=(TextView)mView.findViewById(R.id.moneytype);
        detailListView=(ListView)mView.findViewById(R.id.detailListView);
        submit=(Button)mView.findViewById(R.id.submit);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
        Date d = new Date(orderRecord.getCreateTime());
        String time1 =  formatter.format(d);
        String date[] = time1.split(",");
        //获取时间的处理！！！！
        Log.i("=========","===时间=="+time1);
        time.setText(date[0]);
        specificTime.setText(date[1]);
        if(orderItemInfo.getSellerPhone()!=null&&!orderItemInfo.getSellerPhone().equals("")){
            servicephonetip.setVisibility(View.VISIBLE);
            phone.setVisibility(View.VISIBLE);
            phone.setText(orderItemInfo.getSellerPhone());
        }else{
            servicephonetip.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.INVISIBLE);
        }

        orderNum.setText(orderRecord.getOrderSn());
        FrescoHelper.setImage(img, Uri.parse(orderItemInfo.getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_mainactivity_68),context.getResources().getDimensionPixelSize(R.dimen.height_mainactivity_68)));

        name.setText(orderItemInfo.getGoodsName());
        title.setText(orderItemInfo.getGoodsTitle());
        count.setText("X"+orderItemInfo.getQuantity());
        money.setText(Utils.getPrice(orderItemInfo.getPrice()));




       //根据传入数值，适配具体数据，暂无处理！！！
        List<AfterSaleslistEntity> entityList=new ArrayList<AfterSaleslistEntity>();
        for(int i=0;i<3;i++){
            if(i==0){
                AfterSaleslistEntity entity=new AfterSaleslistEntity();
                entity.setName(context.getResources().getString(R.string.aftersaletype));
                entity.setPageLeft(context.getResources().getDrawable(
                        R.drawable.triangle_left));
                entity.setPageRight(context.getResources().getDrawable(
                        R.drawable.triangle_right));
                entity.setItemName(context.getResources().getString(R.string.refundtv));
                entityList.add(entity);
            }else if(i==1){
                AfterSaleslistEntity entity=new AfterSaleslistEntity();
                entity.setName(context.getResources().getString(R.string.purchasequantity));
                entity.setPageLeft(context.getResources().getDrawable(
                        R.drawable.triangle_left));
                entity.setPageRight(context.getResources().getDrawable(
                        R.drawable.triangle_right));
                entity.setItemName("1");
                entityList.add(entity);

            }else if(i==2){
                AfterSaleslistEntity entity=new AfterSaleslistEntity();
                entity.setName(context.getResources().getString(R.string.afterwhyno));
                entity.setPageLeft(context.getResources().getDrawable(
                        R.drawable.triangle_left));
                entity.setPageRight(context.getResources().getDrawable(
                        R.drawable.triangle_right));
                entity.setItemName(context.getResources().getString(R.string.refundreason1));
                entityList.add(entity);

            }
        }
        detailListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter=new AfterSalesDialogAdapter(context,entityList,0);
        detailListView.setAdapter(adapter);
        detailListView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int selectItems = detailListView.getSelectedItemPosition();
                View selectView = detailListView.getSelectedView();
                if(selectView==null) return true;
                TextView tv = (TextView) selectView.findViewById(R.id.item_setting);
                //按钮按下时
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_RIGHT: {
                            if (selectItems == 0) {
                                String[] arrays = context.getResources().getStringArray(R.array.aftersaleway);
                                outputType = (outputType + 1) % arrays.length ;
                                tv.setText(getStringArrays(context, R.array.aftersaleway,
                                        outputType));
                                outputType_tv=getStringArrays(context, R.array.aftersaleway, outputType);

                            }else if(selectItems == 1){
                              //设置最大值限制
                                currentCount=currentCount+1;
                                if(currentCount<orderItemInfo.getQuantity()+1){//设置最大值！！！
                                    tv.setText(currentCount+"");
                                }else{
                                    currentCount=orderItemInfo.getQuantity();
                                }
                                Log.i("=========","====当前数字right=="+currentCount);

                            }else if(selectItems == 2){
                                String[] arrays = context.getResources().getStringArray(R.array.aftersalewhy);
                                whyType = (whyType + 1) % arrays.length ;
                                tv.setText(getStringArrays(context, R.array.aftersalewhy,
                                        whyType));
                                whyType_tv=getStringArrays(context, R.array.aftersalewhy, whyType);


                            }

                            break;
                        }
                        case KeyEvent.KEYCODE_DPAD_LEFT: {
                            if (selectItems == 0) {
                                String[] arrays = context.getResources().getStringArray(R.array.aftersaleway);
                                outputType = (outputType + 1) % arrays.length ;
                                tv.setText(getStringArrays(context, R.array.aftersaleway,
                                        outputType));
                                outputType_tv=getStringArrays(context, R.array.aftersaleway, outputType);
                            }else if(selectItems == 1){
                                //设置最大值为处理假如最多3件！！！
                                currentCount=currentCount-1;
                                if(currentCount>0){//设置最大值！！！
                                    tv.setText(currentCount+"");
                                }else{
                                    currentCount=1;
                                }
                                Log.i("=========","====当前数字left=="+currentCount);
                            }else if(selectItems == 2){
                                String[] arrays = context.getResources().getStringArray(R.array.aftersalewhy);
                                //左键循环(arrays.length-1)
                                whyType = (whyType +5) % arrays.length ;
                                tv.setText(getStringArrays(context, R.array.aftersalewhy,
                                        whyType));
                                whyType_tv=getStringArrays(context, R.array.aftersalewhy, whyType);

                            }
                            break;
                        }
                    }
                }


                return false;
            }
        });

        //点击提交
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afterServiceRefund();
            }
        });
        submit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ((LinearLayout)(detailListView.getChildAt(2))).getChildAt(0).setBackground(context.getResources().getDrawable(R.drawable.chooseno));
                    ((ImageView)((LinearLayout)(((LinearLayout)detailListView.getChildAt(2)).getChildAt(0))).getChildAt(1)).setImageDrawable(context.getResources().getDrawable(
                            R.drawable.triangle_leftun));
                    ((ImageView)((LinearLayout)(((LinearLayout)detailListView.getChildAt(2)).getChildAt(0))).getChildAt(3)).setImageDrawable(context.getResources().getDrawable(
                            R.drawable.triangle_rightun));
                }

            }
        });
        detailListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ((LinearLayout)(detailListView.getChildAt(2))).getChildAt(0).setBackground(context.getResources().getDrawable(R.drawable.aftersales_selector));
            }
        });

        detailListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((ImageView)((LinearLayout)(((LinearLayout)view).getChildAt(0))).getChildAt(1)).setImageDrawable(context.getResources().getDrawable(
                        R.drawable.triangle_left));
                ((ImageView)((LinearLayout)(((LinearLayout)view).getChildAt(0))).getChildAt(3)).setImageDrawable(context.getResources().getDrawable(
                        R.drawable.triangle_right));
                for(int i=0;i<3;i++){
                    if(i!=position){
                        ((ImageView)((LinearLayout)(((LinearLayout)detailListView.getChildAt(i)).getChildAt(0))).getChildAt(1)).setImageDrawable(context.getResources().getDrawable(
                                R.drawable.triangle_leftun));
                        ((ImageView)((LinearLayout)(((LinearLayout)detailListView.getChildAt(i)).getChildAt(0))).getChildAt(3)).setImageDrawable(context.getResources().getDrawable(
                                R.drawable.triangle_rightun));

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //监听焦点在提交申请时，点击上键
        submit.setOnKeyListener(onKeyListener);



    }

    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(v.getId()==R.id.submit && keyCode==KeyEvent.KEYCODE_DPAD_UP) {
                    ((ImageView)((LinearLayout)(((LinearLayout)detailListView.getChildAt(2)).getChildAt(0))).getChildAt(1)).setImageDrawable(context.getResources().getDrawable(
                            R.drawable.triangle_left));
                    ((ImageView)((LinearLayout)(((LinearLayout)detailListView.getChildAt(2)).getChildAt(0))).getChildAt(3)).setImageDrawable(context.getResources().getDrawable(
                            R.drawable.triangle_right));
                }
            }
            return false;
        }
    };

       public class ReceiverAfterSales extends BroadcastReceiver {

           @Override
       public void onReceive(Context context, Intent intent) {
               if(AfterSalesDialog.this!=null&& AfterSalesDialog.this.isShowing()){
                   AfterSalesDialog.this.dismiss();
               }

                }

           }
    public static String getStringArrays(Context mContext, int id, int position) {
        String[] arrays = mContext.getResources().getStringArray(id);
        if (null != arrays) {
            return arrays[position];
        }
        return "";
    }

    /**
     * 申请售后退款
     */
    public void afterServiceRefund(){
        Utils.print(tag,"afterServiceRefund");
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(context).userID);
            json.put("orderSn",orderRecord.getOrderSn()); //订单号
            json.put("reason",whyType_tv); //原因
            json.put("status",orderRecord.getStatus());//订单状态
            json.put("quantity",currentCount); //数量
            //退款类型: 2换货 1退款
            if(outputType_tv.equals(context.getResources().getString(R.string.refundtv))){
                json.put("serviceType","1");
            }else{
                json.put("serviceType","2");
            }
            json.put("refundType","1"); //1单件退款 ,2整单退款

            double endPrice=((orderItemInfo.getPrice())/(orderItemInfo.getQuantity()))*currentCount;
            json.put("amount",endPrice); //总价
            json.put("goodsSkuSn",orderItemInfo.getGoodsSkuSn());
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpSubmitGoodsSkuService(input,ConStant.getInstance(context).Token)
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
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        if(statusData.getReturnValue()==0){
                            ApplyPromptDialog applyPromptDialog = new ApplyPromptDialog(context);
                            if(!applyPromptDialog.isShowing()){
                                applyPromptDialog.show();
                            }
                        }else{
                            ToastUtils.showToast(context,statusData.getErrorMessage());
                        }
                    }
                });
        addSubscription(subscription);
    }

}
