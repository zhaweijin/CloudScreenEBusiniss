package com.hiveview.dianshang.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionCut;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionGift;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionListData;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionRecord;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017\12\29 0029.
 */

public class PromotionAdapter extends BaseAdapter {
    private static final String TAG="PromotionAdapter";
    private Context mContext;
    /**
     * 限购数量适配器
     */
    private LimitCommoditySizeAdapter commoditySizeAdapter;
    /**
     * 所有促销策略信息
     */
    private PromotionListData promotionListData;
    private List<PromotionRecord> list = new ArrayList<>();
    private PromotionRecord promotionRecord;
    /**
     * 满减金额信息
     */
    private List<PromotionCut> cutList = new ArrayList<>();
    /**
     * 满减梯度中间值
     */
    private String promotionStr = "";
    /**
     * 满金额
     */
    private String enoughPrice = "";
    /**
     * 减金额
     */
    private String cutPrice = "";
    /**
     * 满减梯度
     */
    private String fullCutGradient = "";
    private boolean isok = false;

    public PromotionAdapter(Context context,PromotionListData promotionListData){
        this.mContext=context;
        this.promotionListData=promotionListData;
        initData();
    }

    public PromotionListData getPromotionListData() {
        return promotionListData;
    }

    public void setPromotionListData(PromotionListData promotionListData) {
        this.promotionListData = promotionListData;
    }

    private void initData(){
        addData(promotionListData.getBuyGiftList());
        addData(promotionListData.getFullCutList());
        addData(promotionListData.getFullGiftList());
        addData(promotionListData.getLimitBuyList());
    }
    private void addData(List<PromotionRecord> list){
        Log.i(TAG,list.toString()+":"+list.size());
        if (list.size()>0){
            for (int i=0;i<list.size();i++){
                this.list.add(list.get(i));
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_promotion_item, parent, false);
            holder = new ViewHolder(convertView);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.slogan.setText(list.get(position).getPromotionTitle());

        if (list.get(position).getBeginTime() != 0 && list.get(position).getEndTime() != 0){
            holder.time_mark.setVisibility(View.VISIBLE);
			holder.validity.setVisibility(View.VISIBLE);
            holder.deadline.setVisibility(View.VISIBLE);
            holder. validity.setText(Utils.getDateToString(list.get(position).getBeginTime()));
            holder.deadline.setText(Utils.getDateToString(list.get(position).getEndTime()));
        }else {
            holder.time_mark.setVisibility(View.GONE);
            holder. validity.setText(mContext.getResources().getString(R.string.forever));
            holder.deadline.setVisibility(View.GONE);
        }
        holder.state.setText(list.get(position).getPromotionNote());
        String specValues="";
        //产品规格与参数----遍历商品参与的所有买赠活动下的所有规格并通过斜杠隔开
        for (int j = 0; j < list.get(position).getJoinSkuList().size(); j++) {
            if(j==list.get(position).getJoinSkuList().size()-1){
                 specValues =specValues+list.get(position).getJoinSkuList().get(j).getSpecValues();
            }else {
                specValues = list.get(position).getJoinSkuList().get(j).getSpecValues() + "/" + specValues;
            }
        }



            switch (list.get(position).getPromotionType()){
                case "FULL_GIFTS":
                    holder.mark.setText(mContext.getResources().getString(R.string.full_gift_title));
                    
                    holder.limitnum_title.setVisibility(View.GONE);
					holder.commodity_size.setVisibility(View.VISIBLE);
                    holder.present.setVisibility(View.VISIBLE);
                    holder.promotion.setVisibility(View.GONE);
                    holder.commoditySizeList.setVisibility(View.GONE);
                    holder.promotion_title.setVisibility(View.GONE);
                    holder.line_four.setVisibility(View.GONE);

                    holder.commodity_size.setText(specValues);
                    List<PromotionGift> giftList= new ArrayList<>();
                    giftList= list.get(position).getGiftList();
                    //赠品适配
                    PresentAdapter presentAdapter2;
                    presentAdapter2 = new PresentAdapter(giftList,mContext);
                    holder.present.setAdapter(presentAdapter2);
                    presentAdapter2.notifyDataSetChanged();
                    break;
                case "FULL_CUT":
                    holder.mark.setText(mContext.getResources().getString(R.string.full_cut_title));
                    holder.limitnum_title.setVisibility(View.GONE);
                    holder.present.setVisibility(View.GONE);
					holder.commodity_size.setVisibility(View.VISIBLE);
                    holder.promotion.setVisibility(View.VISIBLE);
                    holder.commoditySizeList.setVisibility(View.GONE);
                    holder.promotion_title.setVisibility(View.VISIBLE);
                    holder.line_four.setVisibility(View.VISIBLE);


                    holder.slogan.setText(list.get(position).getPromotionTitle());
					holder.promotion_title.setText(mContext.getResources().getString(R.string.full_cut_gradient));
                    cutList = list.get(position).getCutList();
                    Utils.print(TAG,"cutList size is "+cutList.size());
                    Utils.print(TAG,"promotion.getText() is "+holder.promotion.getText());

                        for (int i = 0; i < cutList.size(); i++) {
                            Utils.print(TAG, "enough price " + i + " is " + cutList.get(i).getEnoughPrice());
                            Utils.print(TAG, "cut price " + i + " is " + cutList.get(i).getCutPrice());
                            enoughPrice = String.valueOf(cutList.get(i).getEnoughPrice());
                            if (i == cutList.size() - 1) {
                                cutPrice = String.valueOf(cutList.get(i).getCutPrice()) + "元";
                            } else {
                                cutPrice = String.valueOf(cutList.get(i).getCutPrice()) + "元" + "，";
                            }

                            promotionStr = promotionStr + "满" + enoughPrice + "减" + cutPrice;
                            Utils.print(TAG, "#满减梯度为#：" + promotionStr);

                            if (i == cutList.size() - 1 && !isok) {
                                Utils.print(TAG, "promotionStr：" + promotionStr);
                                fullCutGradient = promotionStr;
                                isok = true;
                                break;
                            }
                            Utils.print(TAG, "满减梯度为：" + promotionStr);
                        }
                    if (isok) {
                        holder.promotion.setText(fullCutGradient);
                        isok = false;
                        promotionStr = "";
                    }
                    holder.commodity_size.setText(specValues);
                    break;
                case"BUY_GIFTS":
                    holder.mark.setText(mContext.getResources().getString(R.string.buy_gift_title));
                    
                    holder.limitnum_title.setVisibility(View.GONE);
					holder.commodity_size.setVisibility(View.VISIBLE);
                    holder.present.setVisibility(View.VISIBLE);
                    holder.promotion.setVisibility(View.GONE);
                    holder.commoditySizeList.setVisibility(View.GONE);
                    holder.promotion_title.setVisibility(View.GONE);
                    holder.line_four.setVisibility(View.GONE);

                    holder.commodity_size.setText(specValues);
                    List<PromotionGift> giftList2= new ArrayList<>();
                    giftList2= list.get(position).getGiftList();
                    //赠品适配
                    PresentAdapter presentAdapter3;
                    presentAdapter3 = new PresentAdapter(giftList2,mContext);
                    holder.present.setAdapter(presentAdapter3);
                    presentAdapter3.notifyDataSetChanged();
                    break;
                case "LIMIT_BUY":
                    holder.mark.setText(mContext.getResources().getString(R.string.limit_buy_title));
                    
                    holder.present.setVisibility(View.GONE);
                    holder.promotion.setVisibility(View.GONE);
                    holder.limitnum_title.setVisibility(View.VISIBLE);
                    holder.commodity_size.setVisibility(View.GONE);
                    holder.commoditySizeList.setVisibility(View.VISIBLE);
                    holder.promotion_title.setVisibility(View.GONE);
                    holder.line_four.setVisibility(View.GONE);

                    //产品规格适配
                    promotionRecord = list.get(position);
                    commoditySizeAdapter = new LimitCommoditySizeAdapter(mContext,promotionRecord,list.get(position).getJoinSkuList());
                    holder.commoditySizeList.setAdapter(commoditySizeAdapter);
                    commoditySizeAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;

            }

        convertView.setTag(holder);
        return convertView;
    }

    class ViewHolder{
        private TextView mark;
        private TextView slogan;
        private TextView validity;
        private TextView deadline;
        private TextView state;
        private TextView commodity_size;
        private TextView promotion;
        private TextView limitnum_title;
        private TextView promotion_title;
        private LinearLayout line_four;
        private TextView time_mark;
        private MyListView present;
        private MyListView commoditySizeList;
        ViewHolder(View view){
            mark = (TextView) view.findViewById(R.id.mark);
            slogan = (TextView) view.findViewById(R.id.slogan);
            validity = (TextView) view.findViewById(R.id.validity);
            deadline = (TextView) view.findViewById(R.id.deadline);
            state = (TextView) view.findViewById(R.id.state);
            commodity_size = (TextView) view.findViewById(R.id.commodity_size);
            promotion = (TextView) view.findViewById(R.id.promotion);
            present = (MyListView) view.findViewById(R.id.present);
            limitnum_title = (TextView) view.findViewById(R.id.limitnum_title);
            commoditySizeList = (MyListView) view.findViewById(R.id.listView_commoditySize);
            promotion_title = (TextView) view.findViewById(R.id.promotion_title);
            line_four = (LinearLayout) view.findViewById(R.id.line_four);
            time_mark = (TextView) view.findViewById(R.id.time_mark);
//            promotion.setText("data");


        }

    }

}
