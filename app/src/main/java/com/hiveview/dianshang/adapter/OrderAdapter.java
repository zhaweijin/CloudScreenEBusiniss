package com.hiveview.dianshang.adapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.OtherAuctionOrderdetailsDialog;
import com.hiveview.dianshang.dialog.PayAuctionOrderdetailsDialog;
import com.hiveview.dianshang.entity.order.OrderRecord;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.dialog.AfterSalesDialog;
import com.hiveview.dianshang.dialog.OrderdetailsDialog;
import com.hiveview.dianshang.dialog.RefundDialog;
import com.hiveview.dianshang.entity.order.item.OrderItemData;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;
import com.hiveview.dianshang.entity.order.limit.OrderLimitData;
import com.hiveview.dianshang.home.MainActivity;
import com.hiveview.dianshang.home.PaymentPage;
import com.hiveview.dianshang.order.OrderMain;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gavin on 2017/5/3.
 */

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<OrderRecord> list;
    private ListView orderListView;
    private  ViewHolder vh;
    private int select_item=-1;
    private String tag="OrderAdapter";

    public OrderAdapter(Context context, List<OrderRecord> list,ListView orderListView) {
        this.context = context;
        this.list = list;
        this.orderListView=orderListView;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_itemorder, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        vh = (ViewHolder) convertView.getTag();



        //订单图标的标识拍卖与正常订单!!!!未处理
        //list.get(position)
        if(list.get(position).getOrderType()==ConStant.ORDER_TYPE_COMMOM){//普通订单
            vh.orderflag.setBackgroundResource(R.drawable.notes);
        }else{//拍卖订单
            vh.orderflag.setBackgroundResource(R.drawable.auction_icon);
        }


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
        Date d = new Date(list.get(position).getCreateTime());
        String time =  formatter.format(d);
        String date[] = time.split(",");
       //获取时间的处理！！！！
        Log.i("=========","===时间=="+time);
        vh.time.setText(date[0]);
        vh.spcificTime.setText(date[1]);
        vh.orderNum.setText(list.get(position).getOrderSn());
		/**
         *修改数据，去除小数部分为0 的部分
         */
//        String s  = Utils.getPrice(list.get(position).getAmount());
//        String s  = String.valueOf(list.get(position).getAmount());
//        Log.e("--OrderAdapter.java>>s",s);
//        String s3 = s.substring(s.indexOf("."),s.length());
//        Log.e("--OrderAdapter.java>>s3",s3);
//        String s2 = s.substring(0,s.indexOf("."));
//        Log.e("--OrderAdapter.java>>s2",s2);
//        if (s3.equals(".00")|| s3.equals(".0")){//如果线束不问为0，只显示整数部分
//            vh.money.setText(s2);
//        }else if(s.substring(s.length()-1).equals("0")){//如果数据百分位为0，则显示的数据只保留到十分位
//            vh.money.setText(s.substring(0,s.indexOf(".")+2)+"");
//        }else {//如果小数部分都不为0，输出整个数
//            vh.money.setText(Utils.getPrice(list.get(position).getAmount()));
//        }

        vh.money.setText(Utils.getPrice(list.get(position).getAmount()));

        //vh.money.setText(list.get(position).getAmount()+"");
        //有关订单状态的处理
       if(list.get(position).getStatus()==Status.pendingPayment.ordinal()){//待付款
           vh.state.setText(context.getResources().getString(R.string.waitingpayment));
           Drawable img= context.getResources().getDrawable(R.drawable.payfinished_icon);
           //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
           img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
           vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
        }else if(list.get(position).getStatus()==Status.pendingShipment.ordinal()||list.get(position).getStatus()==12){//待发货
           vh.state.setText(context.getResources().getString(R.string.sendgoods));
           Drawable img= context.getResources().getDrawable(R.drawable.sendgoods_icon);
           //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
           img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
           vh.state.setCompoundDrawables(img, null, null, null); //设置左图标

       }else if(list.get(position).getStatus()==Status.pendingReceive.ordinal()){//待收货
           vh.state.setText(context.getResources().getString(R.string.receivinggoods));
           Drawable img= context.getResources().getDrawable(R.drawable.deliverygoods_icon);
           //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
           img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
           vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
           Drawable img2= context.getResources().getDrawable(R.drawable.refundbutton_selector);
           img2.setBounds(0, 0, img2.getMinimumWidth(), img2.getMinimumHeight());
       }else if(list.get(position).getStatus()==Status.completed.ordinal()){//交易完成
           vh.state.setText(context.getResources().getString(R.string.completetransaction));
           Drawable img= context.getResources().getDrawable(R.drawable.payment_icon);
           //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
           img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
           vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
       }else{//交易关闭
           vh.state.setText(context.getResources().getString(R.string.tradingclosed));
           Drawable img= context.getResources().getDrawable(R.drawable.payment_icon);
           //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
           img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
           vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
       }
        vh.state.setPadding(context.getResources().getDimensionPixelSize(R.dimen.length_22),0,context.getResources().getDimensionPixelSize(R.dimen.length_26),0);
        //vh.function.setPadding(260,0,160,0);


        select_item = OrderMain.currentPosition;
        //有关图片的处理
        if(list.get(position).getOrderInfoList().size()<8){
            vh.countLayout.setVisibility(View.INVISIBLE);
        }else{
            vh.countLayout.setVisibility(View.VISIBLE);
        }
        vh.imgCount.setText(list.get(position).getTotalQuantity()+"");
       for(int i=0;i<list.get(position).getOrderInfoList().size();i++){
            if(i==0){
                FrescoHelper.setImage(vh.img1, Uri.parse(list.get(position).getOrderInfoList().get(0).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
            }else if(i==1){
                FrescoHelper.setImage(vh.img2, Uri.parse(list.get(position).getOrderInfoList().get(1).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
            }else if(i==2){
                FrescoHelper.setImage(vh.img3, Uri.parse(list.get(position).getOrderInfoList().get(2).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
            }else if(i==3){
                FrescoHelper.setImage(vh.img4, Uri.parse(list.get(position).getOrderInfoList().get(3).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
            }else if(i==4){
                FrescoHelper.setImage(vh.img5, Uri.parse(list.get(position).getOrderInfoList().get(4).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
            }else if(i==5){
                FrescoHelper.setImage(vh.img6, Uri.parse(list.get(position).getOrderInfoList().get(5).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
            }else if(i==6){
                FrescoHelper.setImage(vh.img7, Uri.parse(list.get(position).getOrderInfoList().get(6).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
            }

        }
        //list.get(position).getOrderInfoList().size()
        if(list.get(position).getOrderInfoList().size()==0){
            vh.img1.setVisibility(View.GONE);
            vh.img2.setVisibility(View.GONE);
            vh.img3.setVisibility(View.GONE);
            vh.img4.setVisibility(View.GONE);
            vh.img5.setVisibility(View.GONE);
            vh.img6.setVisibility(View.GONE);
            vh.img7.setVisibility(View.GONE);
        }else if(list.get(position).getOrderInfoList().size()==1){
            vh.img1.setVisibility(View.VISIBLE);
            vh.img2.setVisibility(View.GONE);
            vh.img3.setVisibility(View.GONE);
            vh.img4.setVisibility(View.GONE);
            vh.img5.setVisibility(View.GONE);
            vh.img6.setVisibility(View.GONE);
            vh.img7.setVisibility(View.GONE);
        }else if(list.get(position).getOrderInfoList().size()==2){
            vh.img1.setVisibility(View.VISIBLE);
            vh.img2.setVisibility(View.VISIBLE);
            vh.img3.setVisibility(View.GONE);
            vh.img4.setVisibility(View.GONE);
            vh.img5.setVisibility(View.GONE);
            vh.img6.setVisibility(View.GONE);
            vh.img7.setVisibility(View.GONE);
        }else if(list.get(position).getOrderInfoList().size()==3){
            vh.img1.setVisibility(View.VISIBLE);
            vh.img2.setVisibility(View.VISIBLE);
            vh.img3.setVisibility(View.VISIBLE);
            vh.img4.setVisibility(View.GONE);
            vh.img5.setVisibility(View.GONE);
            vh.img6.setVisibility(View.GONE);
            vh.img7.setVisibility(View.GONE);
        }else if(list.get(position).getOrderInfoList().size()==4){
            vh.img1.setVisibility(View.VISIBLE);
            vh.img2.setVisibility(View.VISIBLE);
            vh.img3.setVisibility(View.VISIBLE);
            vh.img4.setVisibility(View.VISIBLE);
            vh.img5.setVisibility(View.GONE);
            vh.img6.setVisibility(View.GONE);
            vh.img7.setVisibility(View.GONE);


        }else if(list.get(position).getOrderInfoList().size()==5){
            vh.img1.setVisibility(View.VISIBLE);
            vh.img2.setVisibility(View.VISIBLE);
            vh.img3.setVisibility(View.VISIBLE);
            vh.img4.setVisibility(View.VISIBLE);
            vh.img5.setVisibility(View.VISIBLE);
            vh.img6.setVisibility(View.GONE);
            vh.img7.setVisibility(View.GONE);

        }else if(list.get(position).getOrderInfoList().size()==6){
            vh.img1.setVisibility(View.VISIBLE);
            vh.img2.setVisibility(View.VISIBLE);
            vh.img3.setVisibility(View.VISIBLE);
            vh.img4.setVisibility(View.VISIBLE);
            vh.img5.setVisibility(View.VISIBLE);
            vh.img6.setVisibility(View.VISIBLE);
            vh.img7.setVisibility(View.GONE);
        }else{
            vh.img1.setVisibility(View.VISIBLE);
            vh.img2.setVisibility(View.VISIBLE);
            vh.img3.setVisibility(View.VISIBLE);
            vh.img4.setVisibility(View.VISIBLE);
            vh.img5.setVisibility(View.VISIBLE);
            vh.img6.setVisibility(View.VISIBLE);
            vh.img7.setVisibility(View.VISIBLE);

        }
        return convertView;
    }

       public void addAll(Collection<?extends OrderRecord> collection){
        list.addAll(collection);
        notifyDataSetChanged();
    }
    public void clearAll(){
        list.clear();
    }

     public List<OrderRecord> getList(){
         return list;
     }

    public static class ViewHolder{
        private ImageView orderflag;
        private TextView time;
        private TextView spcificTime;
        private TextView orderNum;
        private TextView money;
        private TextView state;
        private LinearLayout countLayout;
        private TextView imgCount;
        private SimpleDraweeView img1;
        private SimpleDraweeView img2;
        private SimpleDraweeView img3;
        private SimpleDraweeView img4;
        private SimpleDraweeView img5;
        private SimpleDraweeView img6;
        private SimpleDraweeView img7;
        public ViewHolder(View viewItem){
            orderflag=(ImageView)viewItem.findViewById(R.id.orderflag);
            time = (TextView) viewItem.findViewById(R.id.time);
            spcificTime=(TextView)viewItem.findViewById(R.id.specificTime);
            orderNum=(TextView)viewItem.findViewById(R.id.orderNum);
            money=(TextView)viewItem.findViewById(R.id.money);
            state=(TextView)viewItem.findViewById(R.id.state);

            countLayout=(LinearLayout)viewItem.findViewById(R.id.countLayout);
            imgCount=(TextView)viewItem.findViewById(R.id.imgCount);
            img1=(SimpleDraweeView)viewItem.findViewById(R.id.img1);
            img2=(SimpleDraweeView)viewItem.findViewById(R.id.img2);
            img3=(SimpleDraweeView)viewItem.findViewById(R.id.img3);
            img4=(SimpleDraweeView)viewItem.findViewById(R.id.img4);
            img5=(SimpleDraweeView)viewItem.findViewById(R.id.img5);
            img6=(SimpleDraweeView)viewItem.findViewById(R.id.img6);
            img7=(SimpleDraweeView)viewItem.findViewById(R.id.img7);
        }
    }
    public enum Location {

        LEFT,
        RIGHT,
        TOP,
        BOTTOM;

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

        /**
         * 退款取消申请
         */
        refund_cancel,
        /**
         * 换货取消申请
         */
        exchange_cancel,
        /**
         * 处理中（待发货－商户确认中）
         */
        processing_shipment_seller,
        /**
         * 非支付交易关闭
         */
        unpay_close,

    }


}
