package com.hiveview.dianshang.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BDialog;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.AfterSaleEntity;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.commodity.CommodityAPI;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.hiveview.dianshang.adapter.AfterSaleAdapter.EXCHANGE_PROCESS;
import static com.hiveview.dianshang.adapter.AfterSaleAdapter.REFUND_PROCESS;

/**
 * Created by Gavin on 2017/5/17.
 */

public class AfterdetailsDialog extends BDialog {
    private LayoutInflater mFactory = null;
    private Context context;
    private View mView;
    private AfterSaleEntity afterSaleEntity;
    private Button cancel;
    private TextView time;
    private TextView specificTime;
    private TextView serviceNum;
    private SimpleDraweeView img;
    private SimpleDraweeView img1;
    private SimpleDraweeView img2;
    private SimpleDraweeView img3;
    private SimpleDraweeView img4;
    private SimpleDraweeView img5;
    private SimpleDraweeView img6;
    private SimpleDraweeView img7;
    private TextView name;
    private TextView content;
    private TextView count;
    private TextView money;
    private TextView phone;
    private TextView aftersaleway;
    private TextView countbottom;
    private TextView afterwhy;
    private TextView billName;
    private TextView moneytype;
    private Button state;
    private String tag="AfterdetailsDialog";
    private LinearLayout line;
    private LinearLayout secondLayout;
    private LinearLayout single;
    private LinearLayout manyItems;
    private LinearLayout countLayout;
    private TextView imgCount;
    private TextView phonetip;

    public AfterdetailsDialog(Context context,AfterSaleEntity afterSaleEntity){
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;
        this.afterSaleEntity=afterSaleEntity;

        mFactory = LayoutInflater.from(context);
        mView = mFactory.inflate(R.layout.layout_afterdetails_dialog, null);
        setContentView(mView);
        initView();
    }
    public void initView(){
        cancel=(Button)mView.findViewById(R.id.cancel);
        time=(TextView)mView.findViewById(R.id.time);
        specificTime=(TextView)mView.findViewById(R.id.specificTime);
        serviceNum=(TextView)mView.findViewById(R.id.orderNum);
        countbottom=(TextView)mView.findViewById(R.id.countbottom);
        money=(TextView)mView.findViewById(R.id.money);
        phone=(TextView)mView.findViewById(R.id.phone);
        phonetip=(TextView)mView.findViewById(R.id.phonetip);
        afterwhy=(TextView)mView.findViewById(R.id.afterwhy);
        aftersaleway=(TextView)mView.findViewById(R.id.aftersaleway);
        billName=(TextView)mView.findViewById(R.id.billName);
        moneytype=(TextView)mView.findViewById(R.id.moneytype);
        state=(Button)mView.findViewById(R.id.state);
        line=(LinearLayout)mView.findViewById(R.id.line);
        secondLayout=(LinearLayout)mView.findViewById(R.id.secondLayout);
        //单一商品的布局及图片位置
        single=(LinearLayout)mView.findViewById(R.id.single);
        img=(SimpleDraweeView)mView.findViewById(R.id.img);
        name=(TextView)mView.findViewById(R.id.name);
        content=(TextView)mView.findViewById(R.id.content);
        count=(TextView)mView.findViewById(R.id.count);
        //多件商品的布局及图片位置
        manyItems=(LinearLayout)mView.findViewById(R.id.manyItems);
        img1=(SimpleDraweeView)mView.findViewById(R.id.img1);
        img2=(SimpleDraweeView)mView.findViewById(R.id.img2);
        img3=(SimpleDraweeView)mView.findViewById(R.id.img3);
        img4=(SimpleDraweeView)mView.findViewById(R.id.img4);
        img5=(SimpleDraweeView)mView.findViewById(R.id.img5);
        img6=(SimpleDraweeView)mView.findViewById(R.id.img6);
        img7=(SimpleDraweeView)mView.findViewById(R.id.img7);
        countLayout=(LinearLayout)mView.findViewById(R.id.countLayout);
        imgCount=(TextView)mView.findViewById(R.id.imgCount);





        //时间暂未处理
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
        Date d = new Date(afterSaleEntity.getCreateTime());
        String t =  formatter.format(d);
        String date[] = t.split(",");
        time.setText(date[0]);
        specificTime.setText(date[1]);
        serviceNum.setText(afterSaleEntity.getServiceSn());
        if(afterSaleEntity.getItemList().get(0).getSellerPhone()==null){
            phone.setVisibility(View.INVISIBLE);
            phonetip.setVisibility(View.INVISIBLE);
        }else{
            phone.setVisibility(View.VISIBLE);
            phonetip.setVisibility(View.VISIBLE);
            phone.setText(afterSaleEntity.getItemList().get(0).getSellerPhone());
        }

        if(afterSaleEntity.getItemList().size()>1){//多件商品的适配
            single.setVisibility(View.GONE);
            manyItems.setVisibility(View.VISIBLE);
            //处理图片+数量
            if(afterSaleEntity.getItemList().size()<8){
                countLayout.setVisibility(View.INVISIBLE);
            }else{
                countLayout.setVisibility(View.VISIBLE);
                imgCount.setText(afterSaleEntity.getQuantity()+"");
            }
            for(int i=0;i<afterSaleEntity.getItemList().size();i++){
                if(i==0){
                    FrescoHelper.setImage(img1, Uri.parse(afterSaleEntity.getItemList().get(0).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==1){
                    FrescoHelper.setImage(img2, Uri.parse(afterSaleEntity.getItemList().get(1).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==2){
                    FrescoHelper.setImage(img3, Uri.parse(afterSaleEntity.getItemList().get(2).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==3){
                    FrescoHelper.setImage(img4, Uri.parse(afterSaleEntity.getItemList().get(3).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==4){
                    FrescoHelper.setImage(img5, Uri.parse(afterSaleEntity.getItemList().get(4).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==5){
                    FrescoHelper.setImage(img6, Uri.parse(afterSaleEntity.getItemList().get(5).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==6){
                    FrescoHelper.setImage(img7, Uri.parse(afterSaleEntity.getItemList().get(6).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }

            }
            if(afterSaleEntity.getItemList().size()==0){
                img1.setVisibility(View.GONE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.GONE);
                img5.setVisibility(View.GONE);
                img6.setVisibility(View.GONE);
                img7.setVisibility(View.GONE);
            }else if(afterSaleEntity.getItemList().size()==1){
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.GONE);
                img5.setVisibility(View.GONE);
                img6.setVisibility(View.GONE);
                img7.setVisibility(View.GONE);
            }else if(afterSaleEntity.getItemList().size()==2){
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.GONE);
                img4.setVisibility(View.GONE);
                img5.setVisibility(View.GONE);
                img6.setVisibility(View.GONE);
                img7.setVisibility(View.GONE);
            }else if(afterSaleEntity.getItemList().size()==3){
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.VISIBLE);
                img4.setVisibility(View.GONE);
                img5.setVisibility(View.GONE);
                img6.setVisibility(View.GONE);
                img7.setVisibility(View.GONE);
            }else if(afterSaleEntity.getItemList().size()==4){
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.VISIBLE);
                img4.setVisibility(View.VISIBLE);
                img5.setVisibility(View.GONE);
                img6.setVisibility(View.GONE);
                img7.setVisibility(View.GONE);


            }else if(afterSaleEntity.getItemList().size()==5){
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.VISIBLE);
                img4.setVisibility(View.VISIBLE);
                img5.setVisibility(View.VISIBLE);
                img6.setVisibility(View.GONE);
                img7.setVisibility(View.GONE);

            }else if(afterSaleEntity.getItemList().size()==6){
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.VISIBLE);
                img4.setVisibility(View.VISIBLE);
                img5.setVisibility(View.VISIBLE);
                img6.setVisibility(View.VISIBLE);
                img7.setVisibility(View.GONE);
            }else{
                img1.setVisibility(View.VISIBLE);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.VISIBLE);
                img4.setVisibility(View.VISIBLE);
                img5.setVisibility(View.VISIBLE);
                img6.setVisibility(View.VISIBLE);
                img7.setVisibility(View.VISIBLE);

            }

        }else{//单件商品的适配
            single.setVisibility(View.VISIBLE);
            manyItems.setVisibility(View.GONE);
            count.setText("X"+afterSaleEntity.getQuantity());
            FrescoHelper.setImage(img, Uri.parse(afterSaleEntity.getItemList().get(0).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
            name.setText(afterSaleEntity.getItemList().get(0).getGoodsName());
            content.setText(afterSaleEntity.getItemList().get(0).getGoodsTitle());
        }

        state.setVisibility(View.GONE);
        if(afterSaleEntity.getServiceType()==1){
            billName.setText(context.getString(R.string.refundamount));
            moneytype.setVisibility(View.VISIBLE);
            money.setVisibility(View.VISIBLE);
			 /**
             * 退款金额，修改数据，去除为0的小数部分
             */
//            String s1 = Utils.getPrice(afterSaleEntity.getAmount());
//            String s2 = s1.substring(s1.indexOf("."),s1.length());
//            String s3 = s1.substring(0,s1.indexOf("."));
//            if (s2.equals(".00")|| s2.equals(".0")){//如果小数部分为0，只显示整数部分
//                money.setText(s3);
//            }else if(s1.substring(s1.length()-1).equals("0")){//如果数据百分位为0，则显示的数据只保留到十分位
//                money.setText(s1.substring(0,s1.indexOf(".")+2)+"");
//            }else {//如果小数部分都不为0，输出整个数
//                money.setText(Utils.getPrice(afterSaleEntity.getAmount()));
//            }
            money.setText(Utils.getPrice(afterSaleEntity.getAmount()));
            //money.setText(afterSaleEntity.getAmount()+"");
            aftersaleway.setText(context.getResources().getString(R.string.refundtv));
        }else if(afterSaleEntity.getServiceType()==2){
            secondLayout.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            aftersaleway.setText(context.getResources().getString(R.string.exchange));
        }
        countbottom.setText(afterSaleEntity.getQuantity()+"");
        afterwhy.setText(afterSaleEntity.getReason());


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context, "点击取消申请！", Toast.LENGTH_SHORT).show();
                Log.i("========","===服务的类型=="+afterSaleEntity.getServiceType()+",售后编号=="+afterSaleEntity.getServiceSn());
                cancelAfterService(afterSaleEntity);

            }
        });

        if(afterSaleEntity.getServiceType()==1){//付款
            if(afterSaleEntity.getOrderType()!=null&&afterSaleEntity.getStatus()==REFUND_PROCESS&&afterSaleEntity.getOrderType()==1){//处理中且是单件商品
                cancel.setVisibility(View.VISIBLE);
            }else{
                cancel.setVisibility(View.GONE);
            }


        }else if(afterSaleEntity.getServiceType()==2) {//退换货
            if(afterSaleEntity.getStatus()==EXCHANGE_PROCESS){
                cancel.setVisibility(View.VISIBLE);
            }else{
                cancel.setVisibility(View.GONE);
            }

        }
    }

    /**
     * 取消售后订单
     */
    public void cancelAfterService(AfterSaleEntity entity){
        Utils.print(tag,"cancelAfterService");
        if(!Utils.isConnected(context)){
            String error_tips = context.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(context,error_tips);
            return;
        }
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.userID);
            json.put("orderSn",entity.getOrderSn());//订单号
            json.put("quantity",entity.getItemList().get(0).getQuantity());
            json.put("serviceType",entity.getServiceType()); //售后方式
            json.put("status",entity.getStatus());  //订单状态
            json.put("serviceSn",entity.getServiceSn());
            json.put("goodsSkuSn",entity.getItemList().get(0).getGoodsSkuSn());
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpCancelAfterService(input,ConStant.Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"===error="+e.getMessage());
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
                        Utils.print(tag,"===status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                        if(statusData.getReturnValue()==0){
                            AfterdetailsDialog.this.dismiss();
                            Intent mIntent = new Intent("com.dianshang.damai.aftersales_refresh");
                            // mIntent.putExtra("position", "0");
                            //发送广播
                            context.sendBroadcast(mIntent);
                        }else{
                            Toast.makeText(context,statusData.getErrorMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        addSubscription(subscription);
    }


}
