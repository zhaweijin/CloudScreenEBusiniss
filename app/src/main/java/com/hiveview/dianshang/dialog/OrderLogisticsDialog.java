package com.hiveview.dianshang.dialog;

import com.hiveview.dianshang.adapter.OrderAdapter;
import com.hiveview.dianshang.base.BDialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.LogisticsAdapter;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.TracesBean;
import com.hiveview.dianshang.entity.address.AddressAPI;
import com.hiveview.dianshang.entity.address.DomyDeliveryTrackerVo;
import com.hiveview.dianshang.entity.address.ExpressData;
import com.hiveview.dianshang.entity.address.KD100ExpressTraces;
import com.hiveview.dianshang.entity.address.KDNIAOExpressTraces;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfo;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.CustomProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Gavin on 2017/5/8.
 */

public class OrderLogisticsDialog extends BDialog {
    private LayoutInflater mFactory = null;
    private Context context;
    private View mView;
    private ListView listView_logistics;
    private String tag="OrderLogisticsDialog";
    private OrderStatusInfo infoData;
    private TextView invoiceNo;
    private ImageView reach1;
    private LinearLayout bar1;
    private ImageView reach2;
    private LinearLayout bar2;
    private ImageView reach3;
    private LinearLayout bar3;
    private ImageView reach4;
    private TextView tiptime1;
    private TextView tiptime2;
    private TextView tiptime3;
    private TextView tiptime4;
    private DomyDeliveryTrackerVo vo;
    private List<TracesBean> tracesBeanList;
    private LogisticsAdapter adapter;
    public static int select_item=-1;


    public OrderLogisticsDialog(Context context,OrderStatusInfo infoData) {
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.infoData=infoData;


        mFactory = LayoutInflater.from(context);
        mView = mFactory.inflate(R.layout.layout_orderlogistics, null);

        setContentView(mView);
        initView();

    }
    public void initView(){
        listView_logistics=(ListView)mView.findViewById(R.id.listView_logistics);
        //快递公司名称和货单号
        invoiceNo=(TextView)mView.findViewById(R.id.invoiceNo);
        //物流状态图片
        reach1=(ImageView)mView.findViewById(R.id.reach1);
        bar1=(LinearLayout)mView.findViewById(R.id.bar1);
        reach2=(ImageView)mView.findViewById(R.id.reach2);
        bar2=(LinearLayout)mView.findViewById(R.id.bar2);
        reach3=(ImageView)mView.findViewById(R.id.reach3);
        bar3=(LinearLayout)mView.findViewById(R.id.bar3);
        reach4=(ImageView)mView.findViewById(R.id.reach4);
        //状态提示时间
        tiptime1=(TextView)mView.findViewById(R.id.tiptime1);
        tiptime2=(TextView)mView.findViewById(R.id.tiptime2);
        tiptime3=(TextView)mView.findViewById(R.id.tiptime3);
        tiptime4=(TextView)mView.findViewById(R.id.tiptime4);
        //进度条显示
        if(infoData!=null&&infoData.getOrderTime()!=0){//默认是0，，，即为非空
            Log.i(tag,"===orderTime=="+infoData.getOrderTime());
            reach1.setImageDrawable(context.getResources().getDrawable(R.drawable.reach));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = new Date(infoData.getOrderTime());
            String time =  formatter.format(d);
            Log.i(tag,"===time=="+time);
            tiptime1.setText(time);
        }
        if(infoData.getPayTime()!=0){
            reach2.setImageDrawable(context.getResources().getDrawable(R.drawable.reach));
            bar1.setBackground(context.getResources().getDrawable(R.drawable.red));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = new Date(infoData.getPayTime());
            String time =  formatter.format(d);
            tiptime2.setText(time);
        }
        if(infoData.getShippingTime()!=0){
            reach3.setImageDrawable(context.getResources().getDrawable(R.drawable.reach));
            bar2.setBackground(context.getResources().getDrawable(R.drawable.red));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = new Date(infoData.getShippingTime());
            String time =  formatter.format(d);
            tiptime3.setText(time);
        }
        if(infoData.getReceiverTime()!=0){
            reach4.setImageDrawable(context.getResources().getDrawable(R.drawable.reach));
            bar3.setBackground(context.getResources().getDrawable(R.drawable.red));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = new Date(infoData.getReceiverTime());
            String time =  formatter.format(d);
            tiptime4.setText(time);
        }
        //具体物流信息的显示
        if(infoData!=null&&infoData.getDeliverySn()!=null){
            invoiceNo.setText(infoData.getDeliveryCorpName()+infoData.getDeliverySn());
            getExpressInfo(infoData.getOrderSn(),infoData.getDeliverySn(),infoData.getDeliveryCorpCode());
        }else{
            if(infoData.getStatus()!= OrderAdapter.Status.pendingShipment.ordinal()&&infoData.getStatus()!=12){
                Toast.makeText(context, context.getResources().getString(R.string.logisticstip), Toast.LENGTH_SHORT).show();
            }

        }

       // listView_logistics.setOnKeyListener(onKeyListener);
        listView_logistics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_item=position;
                adapter.notifyDataSetChanged(); //通知adapter刷新数据
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * 获取快递信息
     */
    public void getExpressInfo(String orderSn,String deliverySn,String deliveryCorpCode){
        Utils.print(tag,"getExpressInfo");
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(context).userID);
            json.put("orderSn",orderSn);
            json.put("deliverySn",deliverySn);
            Log.i(tag,"===deliverySn=="+deliverySn);
            json.put("deliveryCorpCode",deliveryCorpCode);
            Log.i(tag,"===deliveryCorpCode=="+deliveryCorpCode);
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetExpressInfo(input, ConStant.Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressData>() {
                    @Override
                    public void onCompleted() {
                       // expressInfo
                        listView_logistics.setSelector(new ColorDrawable(Color.TRANSPARENT));
                        //数据源的顺序颠倒一下
                        tracesBeanList=new ArrayList<TracesBean>();
                        if(vo!=null){
                            if(vo.getDeliveryType()==1){
                                Log.i(tag,"===走的类型1");
                                if(vo.getKdniaoTrackerVo().getTraces()!=null){
                                    int count=vo.getKdniaoTrackerVo().getTraces().size();
                                    Log.i(tag,"=====Kdn个数=="+count);
                                    for(int i=0;i<count;i++){
                                        KDNIAOExpressTraces traces=vo.getKdniaoTrackerVo().getTraces().get(count-i-1);
                                        TracesBean bean=new TracesBean();
                                        bean.setStation(traces.getAcceptStation());
                                        bean.setTime(traces.getAcceptTime());
                                        tracesBeanList.add(bean);
                                    }
                                }

                            }else{//2是排好序的
                                Log.i(tag,"===走的类型2");
                                if(vo.getKd100TrackerVo().getData()!=null){
                                    int count= vo.getKd100TrackerVo().getData().size();
                                    Log.i(tag,"=====Kd100个数=="+count);
                                    for(int i=0;i<count;i++){
                                        KD100ExpressTraces traces=vo.getKd100TrackerVo().getData().get(i);
                                        TracesBean bean=new TracesBean();
                                        bean.setStation(traces.getContext());
                                        bean.setTime(traces.getTime());
                                        tracesBeanList.add(bean);
                                    }
                                }
                            }
                            Log.i(tag,"====个数=="+tracesBeanList.size());
                            for(int i=0;i<tracesBeanList.size();i++){
                                Log.i(tag,"===="+tracesBeanList.get(i).getStation());
                            }
                            adapter=new LogisticsAdapter(context,tracesBeanList);

                            listView_logistics.setAdapter(adapter);

                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(tag,"走错误！");
                        Utils.print(tag,"error=="+e.getMessage());
                        String error_tips = "";
                        if(Utils.isConnected(context)){
                            error_tips = context.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = context.getResources().getString(R.string.error_network_exception);
                        }
                        ToastUtils.showToast(context,error_tips);
                    }

                    @Override
                    public void onNext(ExpressData expressData) {
                        Log.i(tag,"===走onNext");
                        if(expressData.getReturnValue()==-1)
                            return;
                        vo= expressData.getData();
                       // Log.i(tag,"====vo数据=="+expressData.getData().getKdniaoTrackerVo().getTraces().size());
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 状态
     */
    public enum Status {
        //Status.pendingPayment.ordinal()

        /** 等待付款 */
        pendingPayment,

        /** 等待发货 */
        pendingShipment,
        /**
         *  待收货
         */
        pendingReceive,

        /** 交易成功 */
        completed,

        /**
         * 退款处理中（用户发起-商户确认）
         */
        processing,

        /**
         * 退款中（商户确认）
         */
        refund_seller,

        /**
         * 退款中（家视）
         */
        refund_hiveview,

        /**
         * 已退款
         */
        refunded,

        /**
         * 换货单
         */
        exchange,

        /**
         * 交易关闭
         */
        close,

    }


   /* View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if(v.getId()==R.id.listView_logistics && keyCode==KeyEvent.KEYCODE_DPAD_DOWN) {
                            if(listView_logistics.getChildAt(listView_logistics.getLastVisiblePosition())!=null){
                                    listView_logistics.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            listView_logistics.setSelection(listView_logistics.getLastVisiblePosition());
                                        }
                                    });

                            }
                 *//*   if(listView_logistics.getSelectedItemPosition()==0||listView_logistics.getSelectedItemPosition()==1){
                        if(listView_logistics.getChildAt(2)!=null){
                            listView_logistics.post(new Runnable() {
                                @Override
                                public void run() {
                                    listView_logistics.setSelection(2);
                                }
                            });
                        }
                    }*//*


                }else if(v.getId()==R.id.listView_logistics && keyCode==KeyEvent.KEYCODE_DPAD_UP){
                    if(tracesBeanList.size()>3){

                    }
                }
            }
            return false;
        }
    };*/

}
