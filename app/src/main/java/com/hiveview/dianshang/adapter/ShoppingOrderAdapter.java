package com.hiveview.dianshang.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.dialog.OrderdetailsDialog;
import com.hiveview.dianshang.dialog.StreetDialog;
import com.hiveview.dianshang.entity.order.item.OrderGiftData;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;
import com.hiveview.dianshang.entity.shoppingcart.ActivityInfoData;
import com.hiveview.dianshang.entity.shoppingcart.GiftData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartRecord;
import com.hiveview.dianshang.shoppingcart.ItemOnclickListener;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class ShoppingOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String tag = "ShoppingOrderAdapter";
    private Context mContext;
    private List<ShoppingCartRecord> list = new ArrayList<>();
    private List<ActivityInfoData> shoppingCartGiftRecords = new ArrayList<>();
    private Map<Long,View> text_numbers = new HashMap<>();

    private int MAX = 100;
    private Subscription ms;

    private final int TYPE_NORMAL = 0;  //普通商品
    private final int TYPE_BUY_GIFT = 1; //普通商品+买赠
    private final int TYPE_FULL_GIFT = 2; //满增商品列表

    //order list show
    private List<OrderGiftData> orderGiftDatas = new ArrayList<>();
    private List<OrderItemInfo> orderItemInfoList = new ArrayList<>();
    private int TYPE = 0;
    public static final int SHOPPING_CART_LIST = 0; //提交订单页面
    public static  final int ORDER_LIST = 1; //订单详情页面

    private ItemOnclickListener itemOnclickListener; //订单详情列表点击事件
    private OnItemViewSelectedListener onItemViewSelectedListener; //焦点选择列表事件


    public ShoppingOrderAdapter(Context context, List<ShoppingCartRecord> list,List<ActivityInfoData> shoppingCartGiftRecords,OnItemViewSelectedListener onItemViewSelectedListener) {
        this.mContext = context;
        this.list = list;
        this.shoppingCartGiftRecords = shoppingCartGiftRecords;
        this.onItemViewSelectedListener = onItemViewSelectedListener;
    }

    public ShoppingOrderAdapter(Context context, List<OrderGiftData> orderGiftDatas, List<OrderItemInfo> orderItemInfoList, int type, OrderdetailsDialog dialog) {
        this.mContext = context;
        this.orderGiftDatas = orderGiftDatas;
        this.orderItemInfoList = orderItemInfoList;
        this.TYPE = type;
        this.itemOnclickListener = (ItemOnclickListener) dialog;
        this.onItemViewSelectedListener = (OnItemViewSelectedListener) dialog;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==TYPE_BUY_GIFT){
            return new ExpandViewHolder(View.inflate(mContext, R.layout.shopping_cart_expand_list_item, null));
        }else if(viewType==TYPE_FULL_GIFT){
            return new FullGiftViewHolder(View.inflate(mContext, R.layout.shopping_cart_full_gift_list_item, null));
        }
        return new RecyclerViewHolder(View.inflate(mContext, R.layout.shopping_cart_list_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Utils.print(tag,"p==="+position+",type="+getItemViewType(position));
        if(getItemViewType(position)==TYPE_BUY_GIFT){
            final ExpandViewHolder viewHolder = (ExpandViewHolder) holder;
            viewHolder.commodity_icon.setTag("item"+position);
            Utils.print(tag,"1111");
            //Log.v(tag,">>"+selectItemId);
            viewHolder.item.setTag(position+"");

            handDataShow(viewHolder,position);
            viewHolder.item.setOnClickListener(onClickListener);
            handleCommodityColorStatus(viewHolder,position);
            viewHolder.item.setOnFocusChangeListener(new ItemFocusListener(viewHolder,position));
            //handle expand gift list
            if(viewHolder.layout_gift.getChildCount()==0){
                addBuyGiftListView(viewHolder.layout_gift,position);
            }
        }else if(getItemViewType(position)==TYPE_FULL_GIFT){
            Utils.print(tag,"333333");
            final FullGiftViewHolder viewHolder = (FullGiftViewHolder) holder;
            if(viewHolder.layout_full_gift.getChildCount()==0){
                addFullGiftListView(viewHolder.layout_full_gift,viewHolder.full_gift_title_name);
            }
        } else{
            final RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
            viewHolder.commodity_icon.setTag("item"+position);
            Utils.print(tag,"2222");
            //Log.v(tag,">>"+selectItemId);
            viewHolder.item.setTag(position+"");

            handDataShow(viewHolder,position);
            viewHolder.item.setOnClickListener(onClickListener);

            handleCommodityColorStatus(viewHolder,position);
            viewHolder.item.setOnFocusChangeListener(new ItemFocusListener(viewHolder,position));

            /*viewHolder.test_number.setTag(list.get(position).getCartid());

            if(!text_numbers.containsKey(list.get(position).getCartid())){
                text_numbers.put(list.get(position).getCartid(),viewHolder.test_number);
            }*/
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(itemOnclickListener!=null){
                if(v.getTag()!=null){
                    int pos = Integer.parseInt((String) v.getTag());
                    itemOnclickListener.onItemClickListener(pos);
                }
            }
        }
    };


    private void handDataShow(RecyclerView.ViewHolder holder,int position){
        if(getItemViewType(position)==TYPE_BUY_GIFT) {
            ExpandViewHolder viewHolder = (ExpandViewHolder) holder;
            if(TYPE==SHOPPING_CART_LIST){
                viewHolder.commodity_name.setText(list.get(position).getGoodsName());
                viewHolder.commodity_price.setText(Utils.getPrice(list.get(position).getOriginalPrice()));
                viewHolder.commodity_number.setText(mContext.getResources().getString(R.string.quantity)+list.get(position).getBuyNum());
                FrescoHelper.setImage(viewHolder.commodity_icon, Uri.parse(list.get(position).getSquareUrl()),new ResizeOptions(200,200));
                viewHolder.commodity_type.setText(list.get(position).getSpecValue());
            }else if(TYPE==ORDER_LIST){
                viewHolder.commodity_name.setText(orderItemInfoList.get(position).getGoodsName());
                viewHolder.commodity_price.setText(Utils.getPrice(orderItemInfoList.get(position).getPrice()));
                viewHolder.commodity_number.setText(mContext.getResources().getString(R.string.quantity)+orderItemInfoList.get(position).getQuantity());
                FrescoHelper.setImage(viewHolder.commodity_icon, Uri.parse(orderItemInfoList.get(position).getThumbnail()),new ResizeOptions(200,200));
                viewHolder.commodity_type.setText(orderItemInfoList.get(position).getSpecifications());
            }
        }else{
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
            if(TYPE==SHOPPING_CART_LIST){
                viewHolder.commodity_name.setText(list.get(position).getGoodsName());
                viewHolder.commodity_price.setText(Utils.getPrice(list.get(position).getOriginalPrice()));
                viewHolder.commodity_number.setText(mContext.getResources().getString(R.string.quantity)+list.get(position).getBuyNum());
                FrescoHelper.setImage(viewHolder.commodity_icon, Uri.parse(list.get(position).getSquareUrl()),new ResizeOptions(200,200));
                viewHolder.commodity_type.setText(list.get(position).getSpecValue());
            }else if(TYPE==ORDER_LIST){
                viewHolder.commodity_name.setText(orderItemInfoList.get(position).getGoodsName());
                viewHolder.commodity_price.setText(Utils.getPrice(orderItemInfoList.get(position).getPrice()));
                viewHolder.commodity_number.setText(mContext.getResources().getString(R.string.quantity)+orderItemInfoList.get(position).getQuantity());
                FrescoHelper.setImage(viewHolder.commodity_icon, Uri.parse(orderItemInfoList.get(position).getThumbnail()),new ResizeOptions(200,200));
                viewHolder.commodity_type.setText(orderItemInfoList.get(position).getSpecifications());
            }
        }
    }



    class ItemFocusListener implements View.OnFocusChangeListener{

        private RecyclerView.ViewHolder holder;
        private int position;

        public ItemFocusListener(RecyclerView.ViewHolder holder,int position){
            this.holder = holder;
            this.position = position;
        }


        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(getItemViewType(position)==TYPE_BUY_GIFT) {
                ExpandViewHolder viewHolder = (ExpandViewHolder) holder;
                if(hasFocus){
                    if(onItemViewSelectedListener!=null && v.getTag()!=null){
                        int pos = Integer.parseInt((String) v.getTag());
                        onItemViewSelectedListener.OnItemViewSelectedListener(pos);
                    }
                    viewHolder.invalid_text.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.white));
                }else{
                    if(TYPE==ORDER_LIST){
                        viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        return;
                    }
                    if(list.get(position).getMarketStatus()== ConStant.OFF_LINE){
                        viewHolder.invalid_text.setTextColor(mContext.getResources().getColor(R.color.white));
                        viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                        viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                        viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                        viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                        viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                    }else{
                        viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                    }
                }
            }else{
                RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;
                if(hasFocus){
                    viewHolder.invalid_text.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.white));
                    viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.white));
                }else{
                    if(TYPE==ORDER_LIST){
                        viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        return;
                    }

                    if(list.get(position).getMarketStatus()== ConStant.OFF_LINE){
                        viewHolder.invalid_text.setTextColor(mContext.getResources().getColor(R.color.white));
                        viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                        viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                        viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                        viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                        viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                    }else{
                        viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                    }
                }
            }

        }
    }



    /**
     * 处理失效商品的颜色
     * @param viewHolder
     * @param position
     */
    private void handleCommodityColorStatus(RecyclerView.ViewHolder holder,int position){

        if(getItemViewType(position)==TYPE_BUY_GIFT){
            ExpandViewHolder viewHolder = (ExpandViewHolder) holder;
            if(TYPE==ORDER_LIST){
                viewHolder.invalid_text.setVisibility(View.INVISIBLE);
                viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_icon.setAlpha(255);
                return;
            }

            if(list.get(position).getMarketStatus()== ConStant.OFF_LINE){
                //已下线商品
                viewHolder.invalid_text.setVisibility(View.VISIBLE);
                viewHolder.invalid_text.setTextColor(mContext.getResources().getColor(R.color.white));
                viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.invalid_color));

                viewHolder.commodity_icon.setAlpha(99);
                viewHolder.commodity_icon.setBackgroundColor(mContext.getResources().getColor(R.color.black));

            }else{
                viewHolder.invalid_text.setVisibility(View.INVISIBLE);
                viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_icon.setAlpha(255);
            }
        }else{
            RecyclerViewHolder viewHolder = (RecyclerViewHolder) holder;

            if(TYPE==ORDER_LIST){
                viewHolder.invalid_text.setVisibility(View.INVISIBLE);
                viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_icon.setAlpha(255);
                return;
            }

            if(list.get(position).getMarketStatus()== ConStant.OFF_LINE){
                //已下线商品
                viewHolder.invalid_text.setVisibility(View.VISIBLE);
                viewHolder.invalid_text.setTextColor(mContext.getResources().getColor(R.color.white));
                viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.invalid_color));
                viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.invalid_color));

                viewHolder.commodity_icon.setAlpha(99);
                viewHolder.commodity_icon.setBackgroundColor(mContext.getResources().getColor(R.color.black));

            }else{
                viewHolder.invalid_text.setVisibility(View.INVISIBLE);
                viewHolder.commodity_name.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_price.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_number.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.price_type.setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                viewHolder.commodity_icon.setAlpha(255);
            }
        }


    }



    @Override
    public int getItemCount() {
        Utils.print(tag,"ss="+isHasFullGift());
        if(TYPE==ORDER_LIST){
            if(isHasFullGift()){
                return orderItemInfoList.size()+1;
            }
            return orderItemInfoList.size();
        }else if(TYPE==SHOPPING_CART_LIST){
            if(isHasFullGift()){
                return list.size()+1;
            }
            return list.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Utils.print(tag,"pos=="+position+",count="+getItemCount());

        if(isHasFullGift() && position==getItemCount()-1) return TYPE_FULL_GIFT;
        if(isHasGift(position)) return TYPE_BUY_GIFT;
        return TYPE_NORMAL;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout item;

        TextView commodity_name;
        TextView commodity_type;
        TextView commodity_price;
        TextView commodity_number;

        TextView price_type;//RMB

        TextView invalid_text;

        SimpleDraweeView commodity_icon;

        TextView test_number;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            commodity_name = (TextView) itemView.findViewById(R.id.commodity_name);
            commodity_number = (TextView) itemView.findViewById(R.id.commodity_number);
            commodity_type = (TextView) itemView.findViewById(R.id.commodity_type);
            commodity_price = (TextView) itemView.findViewById(R.id.commodity_price);
            invalid_text = (TextView) itemView.findViewById(R.id.invalid_text);
            price_type = (TextView)itemView.findViewById(R.id.price_type);
            commodity_icon= (SimpleDraweeView) itemView.findViewById(R.id.commodity_icon);

            item  =(RelativeLayout)itemView.findViewById(R.id.item);

            test_number =(TextView)itemView.findViewById(R.id.test_number);
        }
    }


    /**
     * 带买赠的结构
     */
    private class ExpandViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout item;

        TextView commodity_name;
        TextView commodity_type;
        TextView commodity_price;
        TextView commodity_number;

        TextView price_type;//RMB

        TextView invalid_text;

        SimpleDraweeView commodity_icon;

        LinearLayout layout_gift;

        ExpandViewHolder(View itemView) {
            super(itemView);
            commodity_name = (TextView) itemView.findViewById(R.id.commodity_name);
            commodity_number = (TextView) itemView.findViewById(R.id.commodity_number);
            commodity_type = (TextView) itemView.findViewById(R.id.commodity_type);
            commodity_price = (TextView) itemView.findViewById(R.id.commodity_price);
            invalid_text = (TextView) itemView.findViewById(R.id.invalid_text);
            price_type = (TextView)itemView.findViewById(R.id.price_type);
            commodity_icon= (SimpleDraweeView) itemView.findViewById(R.id.commodity_icon);
            layout_gift = (LinearLayout)itemView.findViewById(R.id.layout_gift);
            item  =(RelativeLayout)itemView.findViewById(R.id.item);
        }
    }


    /**
     * 满增的结构
     */
    private class FullGiftViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout_full_gift;
        TextView full_gift_title_name;

        FullGiftViewHolder(View itemView) {
            super(itemView);
            layout_full_gift = (LinearLayout)itemView.findViewById(R.id.layout_full_gift);
            full_gift_title_name =(TextView)itemView.findViewById(R.id.full_gift_title_name);
        }
    }

    public void handTestEvent(){
        Observable<Long> mObservable =  Observable.interval(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread());

        ms =  mObservable.subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        List<Long> keys = new ArrayList<Long>(text_numbers.keySet());
                        Utils.print(tag,"==interval==");
                        for (int i = 0; i < keys.size(); i++) {
                            TextView view = (TextView)text_numbers.get(keys.get(i));
                            view.setText(MAX+"");
                        }
                        MAX--;

                        if(MAX==90){
                            setUnSubscribe();
                        }
                    }
                });



    }

    public void setUnSubscribe(){
        ms.unsubscribe();
    }


    /**
     * 检测当前item是否有赠品
     * @param position
     * @return
     */
    public boolean isHasGift(int position){
        if(TYPE==ORDER_LIST){
            if(orderItemInfoList.get(position).getBuyGiftList()!=null && orderItemInfoList.get(position).getBuyGiftList().size()!=0){
                return true;
            }
        }else{
            if(list.get(position).getSkuGifts()!=null && list.get(position).getSkuGifts().size()!=0){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否有满赠的商品
     * @return
     */
    public boolean isHasFullGift(){
        if(TYPE==ORDER_LIST){
            if(orderGiftDatas!=null && orderGiftDatas.size()!=0){
                return true;
            }
        }else {
            if(shoppingCartGiftRecords!=null && shoppingCartGiftRecords.size()!=0){
                return true;
            }
        }
        return false;
    }

    /**
     * 添加每个商品的买赠商品
     * @param layout
     */
    private void addBuyGiftListView(LinearLayout layout,int position){

        if(TYPE==ORDER_LIST){
            if(orderItemInfoList.get(position).getBuyGiftList()==null || orderItemInfoList.get(position).getBuyGiftList().size()==0)
                return;
            for (int i = 0; i < orderItemInfoList.get(position).getBuyGiftList().size(); i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_cart_buy_gift_item,null);
                if(i==0){
                    view.findViewById(R.id.buy_gift_title).setVisibility(View.VISIBLE);
                }else{
                    view.findViewById(R.id.buy_gift_title).setVisibility(View.INVISIBLE);
                }

                ((TextView)view.findViewById(R.id.buy_gift_goods_name)).setText(orderItemInfoList.get(position).getBuyGiftList().get(i).getGiftName());
                ((TextView)view.findViewById(R.id.buy_gift_goods_number)).setText("X"+orderItemInfoList.get(position).getBuyGiftList().get(i).getGiftQuantity());

                layout.addView(view);
            }
        }else if(TYPE==SHOPPING_CART_LIST){
            if(list.get(position).getSkuGifts()==null || list.get(position).getSkuGifts().size()==0)
                return;
            for (int i = 0; i < list.get(position).getSkuGifts().size(); i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_cart_buy_gift_item,null);
                if(i==0){
                    view.findViewById(R.id.buy_gift_title).setVisibility(View.VISIBLE);
                }else{
                    view.findViewById(R.id.buy_gift_title).setVisibility(View.INVISIBLE);
                }

                ((TextView)view.findViewById(R.id.buy_gift_goods_name)).setText(list.get(position).getSkuGifts().get(i).getGiftName());
                ((TextView)view.findViewById(R.id.buy_gift_goods_number)).setText("X"+list.get(position).getSkuGifts().get(i).getGiftNum());

                layout.addView(view);
            }
        }
    }




    /**
     * 添加满赠商品
     * @param layout
     */
    private void addFullGiftListView(LinearLayout layout,TextView title){

        if(TYPE==ORDER_LIST){
            if(orderGiftDatas==null || orderGiftDatas.size()==0){
                return;
            }
            title.setVisibility(View.VISIBLE);
            for (int i = 0; i < orderGiftDatas.size(); i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.full_gift_item,null);
                RelativeLayout full_gift_focus_item = (RelativeLayout)view.findViewById(R.id.full_gift_focus_item);
                full_gift_focus_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus){
                            ((TextView)view.findViewById(R.id.full_gift_goods_name)).setTextColor(mContext.getResources().getColor(R.color.white));
                            ((TextView)view.findViewById(R.id.full_gift_goods_number)).setTextColor(mContext.getResources().getColor(R.color.white));
                        }else{
                            ((TextView)view.findViewById(R.id.full_gift_goods_name)).setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                            ((TextView)view.findViewById(R.id.full_gift_goods_number)).setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        }
                    }
                });

                ((TextView)view.findViewById(R.id.full_gift_goods_name)).setText(orderGiftDatas.get(i).getGiftName());
                ((TextView)view.findViewById(R.id.full_gift_goods_number)).setText("X"+orderGiftDatas.get(i).getGiftQuantity());

                layout.addView(view);
            }
        }else if(TYPE==SHOPPING_CART_LIST){
            if(shoppingCartGiftRecords==null || shoppingCartGiftRecords.size()==0)
                return;

            title.setVisibility(View.VISIBLE);
            for (int i = 0; i < shoppingCartGiftRecords.size(); i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.full_gift_item,null);
                RelativeLayout full_gift_focus_item = (RelativeLayout)view.findViewById(R.id.full_gift_focus_item);
                full_gift_focus_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus){
                            ((TextView)view.findViewById(R.id.full_gift_goods_name)).setTextColor(mContext.getResources().getColor(R.color.white));
                            ((TextView)view.findViewById(R.id.full_gift_goods_number)).setTextColor(mContext.getResources().getColor(R.color.white));
                        }else{
                            ((TextView)view.findViewById(R.id.full_gift_goods_name)).setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                            ((TextView)view.findViewById(R.id.full_gift_goods_number)).setTextColor(mContext.getResources().getColor(R.color.main_text_color));
                        }
                    }
                });


                ((TextView)view.findViewById(R.id.full_gift_goods_name)).setText(shoppingCartGiftRecords.get(i).getGiftName());
                ((TextView)view.findViewById(R.id.full_gift_goods_number)).setText("X"+shoppingCartGiftRecords.get(i).getGiftNum());


                layout.addView(view);
            }
        }
    }
}
