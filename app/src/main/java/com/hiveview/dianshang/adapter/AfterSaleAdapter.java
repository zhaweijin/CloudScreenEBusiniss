package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.entity.AfterSaleEntity;
import com.hiveview.dianshang.entity.AfterSaleEntityItem;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by Gavin on 2017/5/17.
 */

public class AfterSaleAdapter extends BaseAdapter {
    private Context context;
    private List<AfterSaleEntity> list;
    /**
     * 退款状态(处理中/退款中(确认|家视)/已退款)/申请关闭.
     */
    public static final int REFUND_PROCESS = 1;
    /**
     * 退款中-商户
     */
    public static final int REFUND_GOING_SELLER = 2;
    /**
     * 退款中-家视
     */
    public static final int REFUND_GOING_HIVEVIEW = 3;
    /**
     * 已退款
     */
    public static final int REFUND_SUCCESS = 4;
    /**
     * 申请关闭
     */
    public static final int REFUND_CLOSE = 5;


    /**
     * 换货单(处理中)
     */
    public static final int EXCHANGE_PROCESS = 1;

    /**
     * 换货单(已处理)
     */
    public static final int EXCHANGE_SUCCESS = 2;

    /**
     * 换货单(申请关闭)
     */
    public static final int EXCHANGE_CLOSE = 3;

    public AfterSaleAdapter(Context context, List<AfterSaleEntity> list) {
        this.context = context;
        this.list = list;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_aftersale, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
        Date d = new Date(list.get(position).getCreateTime());
        String time =  formatter.format(d);
        String date[] = time.split(",");
        //获取时间的处理！！！！
        Log.i("=========","===时间=="+time);
        vh.time.setText(date[0]);
        vh.spcificTime.setText(date[1]);
        vh.orderNum.setText(list.get(position).getServiceSn());

        //单一商品的适配
        if(list.get(position).getItemList().size()>1){//多件商品
            vh.single.setVisibility(View.GONE);
            vh.manyItems.setVisibility(View.VISIBLE);
            //处理图片+数量
            if(list.get(position).getItemList().size()<8){
                vh.countLayout.setVisibility(View.INVISIBLE);
            }else{
                vh.countLayout.setVisibility(View.VISIBLE);
                vh.imgCount.setText(list.get(position).getQuantity()+"");
            }
            for(int i=0;i<list.get(position).getItemList().size();i++){
                if(i==0){
                    FrescoHelper.setImage(vh.img1, Uri.parse(list.get(position).getItemList().get(0).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==1){
                    FrescoHelper.setImage(vh.img2, Uri.parse(list.get(position).getItemList().get(1).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==2){
                    FrescoHelper.setImage(vh.img3, Uri.parse(list.get(position).getItemList().get(2).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==3){
                    FrescoHelper.setImage(vh.img4, Uri.parse(list.get(position).getItemList().get(3).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==4){
                    FrescoHelper.setImage(vh.img5, Uri.parse(list.get(position).getItemList().get(4).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==5){
                    FrescoHelper.setImage(vh.img6, Uri.parse(list.get(position).getItemList().get(5).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }else if(i==6){
                    FrescoHelper.setImage(vh.img7, Uri.parse(list.get(position).getItemList().get(6).getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83),context.getResources().getDimensionPixelSize(R.dimen.height_afterdetail_83)));
                }

            }
            if(list.get(position).getItemList().size()==0){
                vh.img1.setVisibility(View.GONE);
                vh.img2.setVisibility(View.GONE);
                vh.img3.setVisibility(View.GONE);
                vh.img4.setVisibility(View.GONE);
                vh.img5.setVisibility(View.GONE);
                vh.img6.setVisibility(View.GONE);
                vh.img7.setVisibility(View.GONE);
            }else if(list.get(position).getItemList().size()==1){
                vh.img1.setVisibility(View.VISIBLE);
                vh.img2.setVisibility(View.GONE);
                vh.img3.setVisibility(View.GONE);
                vh.img4.setVisibility(View.GONE);
                vh.img5.setVisibility(View.GONE);
                vh.img6.setVisibility(View.GONE);
                vh.img7.setVisibility(View.GONE);
            }else if(list.get(position).getItemList().size()==2){
                vh.img1.setVisibility(View.VISIBLE);
                vh.img2.setVisibility(View.VISIBLE);
                vh.img3.setVisibility(View.GONE);
                vh.img4.setVisibility(View.GONE);
                vh.img5.setVisibility(View.GONE);
                vh.img6.setVisibility(View.GONE);
                vh.img7.setVisibility(View.GONE);
            }else if(list.get(position).getItemList().size()==3){
                vh.img1.setVisibility(View.VISIBLE);
                vh.img2.setVisibility(View.VISIBLE);
                vh.img3.setVisibility(View.VISIBLE);
                vh.img4.setVisibility(View.GONE);
                vh.img5.setVisibility(View.GONE);
                vh.img6.setVisibility(View.GONE);
                vh.img7.setVisibility(View.GONE);
            }else if(list.get(position).getItemList().size()==4){
                vh.img1.setVisibility(View.VISIBLE);
                vh.img2.setVisibility(View.VISIBLE);
                vh.img3.setVisibility(View.VISIBLE);
                vh.img4.setVisibility(View.VISIBLE);
                vh.img5.setVisibility(View.GONE);
                vh.img6.setVisibility(View.GONE);
                vh.img7.setVisibility(View.GONE);


            }else if(list.get(position).getItemList().size()==5){
                vh.img1.setVisibility(View.VISIBLE);
                vh.img2.setVisibility(View.VISIBLE);
                vh.img3.setVisibility(View.VISIBLE);
                vh.img4.setVisibility(View.VISIBLE);
                vh.img5.setVisibility(View.VISIBLE);
                vh.img6.setVisibility(View.GONE);
                vh.img7.setVisibility(View.GONE);

            }else if(list.get(position).getItemList().size()==6){
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

        }else{//单件商品
            vh.single.setVisibility(View.VISIBLE);
            vh.manyItems.setVisibility(View.GONE);
            AfterSaleEntityItem item=list.get(position).getItemList().get(0);
            FrescoHelper.setImage(vh.img, Uri.parse(item.getThumbnail()),new ResizeOptions(context.getResources().getDimensionPixelSize(R.dimen.height_mainactivity_68),context.getResources().getDimensionPixelSize(R.dimen.height_mainactivity_68)));
            vh.name.setText(item.getGoodsName());
            vh.content.setText(item.getGoodsTitle());
            vh.count.setText("X"+list.get(position).getQuantity());
        }



        if(list.get(position).getServiceType()==1){//付款
            vh.billName.setText(context.getString(R.string.refundamount));
            vh.money.setVisibility(View.VISIBLE);
            vh.moneytype.setVisibility(View.VISIBLE);
			/**
             * 退款金额，修改数据，去除为0的小数部分
             */
//            String s1 = Utils.getPrice(list.get(position).getAmount());
//            String s2 = s1.substring(s1.indexOf("."),s1.length());
//            String s3 = s1.substring(0,s1.indexOf("."));
//            if (s2.equals(".00")|| s2.equals(".0")){//如果线束不问为0，只显示整数部分
//                vh.money.setText(s3);
//            }else if(s1.substring(s1.length()-1).equals("0")){//如果数据百分位为0，则显示的数据只保留到十分位
//                vh.money.setText(s1.substring(0,s1.indexOf(".")+2)+"");
//            }else {//如果小数部分都不为0，输出整个数
//                vh.money.setText(Utils.getPrice(list.get(position).getAmount()));
//            }

            vh.money.setText(Utils.getPrice(list.get(position).getAmount()));
            //vh.money.setText(list.get(position).getAmount()+"");
            if(list.get(position).getStatus()==REFUND_PROCESS||list.get(position).getStatus()==6){//处理中
                vh.state.setText(context.getResources().getString(R.string.processing));
                Drawable img= context.getResources().getDrawable(R.drawable.processing_icon);
                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
            }else if(list.get(position).getStatus()==REFUND_SUCCESS){//已退款
                vh.state.setText(context.getResources().getString(R.string.refunded));
                Drawable img= context.getResources().getDrawable(R.drawable.completionstatus_icon);
                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
            }else if(list.get(position).getStatus()==REFUND_CLOSE){//退款关闭
                vh.state.setText(context.getResources().getString(R.string.close));
                Drawable img= context.getResources().getDrawable(R.drawable.close);
                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                vh.state.setCompoundDrawables(img, null, null, null); //设置左图标

            }else{//退款中
                vh.state.setText(context.getResources().getString(R.string.refunding));
                Drawable img= context.getResources().getDrawable(R.drawable.refunding_icon);
                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
            }
            vh.state.setPadding(context.getResources().getDimensionPixelSize(R.dimen.length_22),0,context.getResources().getDimensionPixelSize(R.dimen.length_26),0);
        }else if(list.get(position).getServiceType()==2){//退换货
            vh.billName.setText(context.getString(R.string.replacegoods));
            vh.money.setVisibility(View.GONE);
            vh.moneytype.setVisibility(View.GONE);
            if(list.get(position).getStatus()==EXCHANGE_PROCESS){
                vh.state.setText(context.getResources().getString(R.string.processing));
                Drawable img= context.getResources().getDrawable(R.drawable.processing_icon);
                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
            }else if(list.get(position).getStatus()==EXCHANGE_SUCCESS){
                vh.state.setText(context.getResources().getString(R.string.processed));
                Drawable img= context.getResources().getDrawable(R.drawable.completionstatus_icon);
                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
            }else if(list.get(position).getStatus()==EXCHANGE_CLOSE){//换货关闭
                vh.state.setText(context.getResources().getString(R.string.close));
                Drawable img= context.getResources().getDrawable(R.drawable.close);
                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
                vh.state.setCompoundDrawables(img, null, null, null); //设置左图标
            }

            vh.state.setPadding(context.getResources().getDimensionPixelSize(R.dimen.length_30),0,context.getResources().getDimensionPixelSize(R.dimen.length_30),0);
        }


        return convertView;
    }
    public void addAll(Collection<?extends AfterSaleEntity> collection){
        list.addAll(collection);
        notifyDataSetChanged();
    }


    public static class ViewHolder {
        private TextView time;
        private TextView spcificTime;
        private TextView orderNum;
        private TextView money;
        private Button state;
        private SimpleDraweeView img;
        private TextView count;
        private TextView content;
        private TextView name;
        private TextView billName;
        private TextView moneytype;
        private LinearLayout single;
        private LinearLayout manyItems;
        private SimpleDraweeView img1;
        private SimpleDraweeView img2;
        private SimpleDraweeView img3;
        private SimpleDraweeView img4;
        private SimpleDraweeView img5;
        private SimpleDraweeView img6;
        private SimpleDraweeView img7;
        private LinearLayout countLayout;
        private TextView imgCount;

        public ViewHolder(View viewItem) {
            time = (TextView) viewItem.findViewById(R.id.time);
            spcificTime = (TextView) viewItem.findViewById(R.id.specificTime);
            orderNum = (TextView) viewItem.findViewById(R.id.orderNum);
            money = (TextView) viewItem.findViewById(R.id.money);
            moneytype=(TextView)viewItem.findViewById(R.id.moneytype);
            state = (Button) viewItem.findViewById(R.id.state);
            img= (SimpleDraweeView) viewItem.findViewById(R.id.img);
            count=(TextView)viewItem.findViewById(R.id.count);
            content=(TextView)viewItem.findViewById(R.id.content);
            name=(TextView)viewItem.findViewById(R.id.name);
            billName=(TextView)viewItem.findViewById(R.id.billName);

            //关于多件商品整订单和单件商品整订单的布局不同的显示的
            //现在是统一按单件商品整订单处理的，，，故manyItems的布局暂时隐藏啦未显示。
            single=(LinearLayout)viewItem.findViewById(R.id.single);
            manyItems=(LinearLayout)viewItem.findViewById(R.id.manyItems);




            //以下是多商品整订单用到的控件
            img1=(SimpleDraweeView)viewItem.findViewById(R.id.img1);
            img2=(SimpleDraweeView)viewItem.findViewById(R.id.img2);
            img3=(SimpleDraweeView)viewItem.findViewById(R.id.img3);
            img4=(SimpleDraweeView)viewItem.findViewById(R.id.img4);
            img5=(SimpleDraweeView)viewItem.findViewById(R.id.img5);
            img6=(SimpleDraweeView)viewItem.findViewById(R.id.img6);
            img7=(SimpleDraweeView)viewItem.findViewById(R.id.img7);
            //数量的显示
            countLayout=(LinearLayout)viewItem.findViewById(R.id.countLayout);
            imgCount=(TextView)viewItem.findViewById(R.id.imgCount);

        }

    }
}
