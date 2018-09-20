package com.hiveview.dianshang.entity.commodity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by carter on 5/16/17.
 */

public class CommodityRecored implements Parcelable {


    private String errorImage;
    private int id;
    private Boolean isTop;
    private String name;
    private String overrideImage;
    private double price;
    private String productImages;
    /**
     * 流播放地址
     */
    private String productVideos;
    private String goodsSn;

    /**
     * 商品类型 流-图文
     * 商品类型 type=2 图片,type=1流
     */
    private int productType;

    /**
     * 是否有促销
     */
    private boolean isPromotion;

    /**
     * 促销类型 逗号分割
     */
    private String promotionType;

    /**
     * 是否下线状态
     */
    private int marketStatus;

    /**
     * 分类名字
     * @return
     */
    private String categoryTreeName;

    /**
     * 分类sn
     * @return
     */
    private String categoryTreePath;


    public String getCategoryTreeName() {
        return categoryTreeName;
    }

    public void setCategoryTreeName(String categoryTreeName) {
        this.categoryTreeName = categoryTreeName;
    }

    public String getCategoryTreePath() {
        return categoryTreePath;
    }

    public void setCategoryTreePath(String categoryTreePath) {
        this.categoryTreePath = categoryTreePath;
    }

    public String getErrorImage() {
        return errorImage;
    }

    public void setErrorImage(String errorImage) {
        this.errorImage = errorImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getTop() {
        return isTop;
    }

    public void setTop(Boolean top) {
        isTop = top;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverrideImage() {
        return overrideImage;
    }

    public void setOverrideImage(String overrideImage) {
        this.overrideImage = overrideImage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductImages() {
        return productImages;
    }

    public void setProductImages(String productImages) {
        this.productImages = productImages;
    }

    public String getProductVideos() {
        return productVideos;
    }

    public void setProductVideos(String productVideos) {
        this.productVideos = productVideos;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        isPromotion = promotion;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    public int getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(int marketStatus) {
        this.marketStatus = marketStatus;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.errorImage);
        dest.writeInt(this.id);
        dest.writeValue(this.isTop);
        dest.writeString(this.name);
        dest.writeString(this.overrideImage);
        dest.writeDouble(this.price);
        dest.writeString(this.productImages);
        dest.writeString(this.productVideos);
        dest.writeString(this.goodsSn);
        dest.writeInt(this.productType);
        dest.writeByte(this.isPromotion ? (byte) 1 : (byte) 0);
        dest.writeString(this.promotionType);
        dest.writeInt(this.marketStatus);
        dest.writeString(this.categoryTreeName);
        dest.writeString(this.categoryTreePath);
    }

    public CommodityRecored() {
    }

    protected CommodityRecored(Parcel in) {
        this.errorImage = in.readString();
        this.id = in.readInt();
        this.isTop = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.name = in.readString();
        this.overrideImage = in.readString();
        this.price = in.readDouble();
        this.productImages = in.readString();
        this.productVideos = in.readString();
        this.goodsSn = in.readString();
        this.productType = in.readInt();
        this.isPromotion = in.readByte() != 0;
        this.promotionType = in.readString();
        this.marketStatus = in.readInt();
        this.categoryTreeName = in.readString();
        this.categoryTreePath = in.readString();
    }

    public static final Creator<CommodityRecored> CREATOR = new Creator<CommodityRecored>() {
        @Override
        public CommodityRecored createFromParcel(Parcel source) {
            return new CommodityRecored(source);
        }

        @Override
        public CommodityRecored[] newArray(int size) {
            return new CommodityRecored[size];
        }
    };
}
