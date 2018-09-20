package com.hiveview.dianshang.entity.commodity.type;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carter on 5/22/17.
 */

public class SkuList implements Parcelable {
    private int availableStock;
    /**
     * 商品总价
     */
    private double discountPrice;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品图标url
     */
    private String imgUrl;
    /**
     * 原来市场价
     */
    private double marketPrice;
    /**
     * 商品分类sn
     */
    private String goodskuSn;

    /**
     * 是否支持促销
     */
    private boolean isPromotion;



    /**
     * 商品分类明细,分级sn
     */
    private String specItemSns;
    /**
     * 商品分类明细,如红色,32G手机
     */
    private String specItemValues;


    /**
     * 促销活动列表
     */
    private List<PromotionActiivty> promotionActList;

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getGoodskuSn() {
        return goodskuSn;
    }

    public void setGoodskuSn(String goodskuSn) {
        this.goodskuSn = goodskuSn;
    }

    public String getSpecItemSns() {
        return specItemSns;
    }

    public void setSpecItemSns(String specItemSns) {
        this.specItemSns = specItemSns;
    }

    public String getSpecItemValues() {
        return specItemValues;
    }

    public void setSpecItemValues(String specItemValues) {
        this.specItemValues = specItemValues;
    }


    public boolean isPromotion() {
        return isPromotion;
    }

    public void setIsPromotion(boolean promotion) {
        isPromotion = promotion;
    }

    public List<PromotionActiivty> getPromotionActList() {
        return promotionActList;
    }

    public void setPromotionActList(List<PromotionActiivty> promotionActList) {
        this.promotionActList = promotionActList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.availableStock);
        dest.writeDouble(this.discountPrice);
        dest.writeString(this.goodsName);
        dest.writeString(this.imgUrl);
        dest.writeDouble(this.marketPrice);
        dest.writeString(this.goodskuSn);
        dest.writeByte(this.isPromotion ? (byte) 1 : (byte) 0);
        dest.writeString(this.specItemSns);
        dest.writeString(this.specItemValues);
        dest.writeList(this.promotionActList);
    }

    public SkuList() {
    }

    protected SkuList(Parcel in) {
        this.availableStock = in.readInt();
        this.discountPrice = in.readDouble();
        this.goodsName = in.readString();
        this.imgUrl = in.readString();
        this.marketPrice = in.readDouble();
        this.goodskuSn = in.readString();
        this.isPromotion = in.readByte() != 0;
        this.specItemSns = in.readString();
        this.specItemValues = in.readString();
        this.promotionActList = new ArrayList<PromotionActiivty>();
        in.readList(this.promotionActList, PromotionActiivty.class.getClassLoader());
    }

    public static final Creator<SkuList> CREATOR = new Creator<SkuList>() {
        @Override
        public SkuList createFromParcel(Parcel source) {
            return new SkuList(source);
        }

        @Override
        public SkuList[] newArray(int size) {
            return new SkuList[size];
        }
    };
}
