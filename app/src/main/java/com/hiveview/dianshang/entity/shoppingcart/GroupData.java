package com.hiveview.dianshang.entity.shoppingcart;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by carter on 12/15/17.
 */

public class GroupData implements Parcelable {
    /**
     * 赠品列表或者满减的规则金额
     */
    private List<ActivityInfoData> promotionInfo;

    /**
     * 分组ID,FULL_CUT FULL_GIFTS COMMON INVALID
     */
    private String promotionSn;

    /**
     * 是否满足策略
     */
    private boolean reachCondition;

    /**
     * 满额就赠的额度
     */
    private double limitPrice;

    /**
     * 策略类型
     */
    private String promotionType;

    /**
     * 商户id
     */
    private String sellerId;

    /**
     * 商品列表
     */
    private List<ShoppingCartRecord> skuList;
    /**
     * 每一个策略所选商品的总金额
     */
    private double sumPrice;
    /*
    * 满赠是否显示赠品
    * */
    private boolean show;
    /*
    * 根据数据变化的满赠，满减策略内容
    * */
    private String activecontent;


    public List<ActivityInfoData> getPromotionInfo() {
        return promotionInfo;
    }

    public void setPromotionInfo(List<ActivityInfoData> promotionInfo) {
        this.promotionInfo = promotionInfo;
    }

    public String getPromotionSn() {
        return promotionSn;
    }

    public void setPromotionSn(String promotionSn) {
        this.promotionSn = promotionSn;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public boolean isReachCondition() {
        return reachCondition;
    }

    public void setReachCondition(boolean reachCondition) {
        this.reachCondition = reachCondition;
    }


    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public List<ShoppingCartRecord> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<ShoppingCartRecord> skuList) {
        this.skuList = skuList;
    }


    public double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getActivecontent() {
        return activecontent;
    }

    public void setActivecontent(String activecontent) {
        this.activecontent = activecontent;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.promotionInfo);
        dest.writeString(this.promotionSn);
        dest.writeByte(this.reachCondition ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.limitPrice);
        dest.writeString(this.promotionType);
        dest.writeString(this.sellerId);
        dest.writeTypedList(this.skuList);
        dest.writeDouble(this.sumPrice);
        dest.writeByte(this.show ? (byte) 1 : (byte) 0);
        dest.writeString(this.activecontent);
    }

    public GroupData() {
    }

    protected GroupData(Parcel in) {
        this.promotionInfo = in.createTypedArrayList(ActivityInfoData.CREATOR);
        this.promotionSn = in.readString();
        this.reachCondition = in.readByte() != 0;
        this.limitPrice = in.readDouble();
        this.promotionType = in.readString();
        this.sellerId = in.readString();
        this.skuList = in.createTypedArrayList(ShoppingCartRecord.CREATOR);
        this.sumPrice = in.readDouble();
        this.show = in.readByte() != 0;
        this.activecontent = in.readString();
    }

    public static final Parcelable.Creator<GroupData> CREATOR = new Parcelable.Creator<GroupData>() {
        @Override
        public GroupData createFromParcel(Parcel source) {
            return new GroupData(source);
        }

        @Override
        public GroupData[] newArray(int size) {
            return new GroupData[size];
        }
    };
}
