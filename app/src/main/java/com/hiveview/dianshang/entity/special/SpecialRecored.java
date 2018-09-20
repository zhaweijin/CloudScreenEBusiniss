package com.hiveview.dianshang.entity.special;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by carter on 5/16/17.
 */

public class SpecialRecored implements Parcelable {

    /**
     * TV商城运营专题更新时间.
     */
    private long timeStamp;

    /**
     * TV商城运营专题图片.
     */
    private String specialImg;

    /**
     * TV商城运营专题商品编号.
     */
    private String goodsSn;

    /**
     * 商城商品垫图.
     */
    private String errorImage;

    /**
     * 商城商品覆盖图
     */
    private String overrideImage;

    /**
     * 货品产品图片.
     */
    private String productImages;

    /**
     * 货品产品视频.
     */
    private String productVideos;

    /**
     * 商品类型 流-图文 1流 2图文
     */
    private Integer productType;

    /**
     * TV商城运营专题商品名.
     */
    private String goodsName;
    /**
     * TV商城运营专题价格.
     */
    private double price;

    /**
     * 是否下线 0初始状态,1 上下, 2 下线
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

    /**
     * 是否支持促销
     */
    private boolean isPromotion;

    /**
     * 促销类型
     */
    private String promotionType;

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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSpecialImg() {
        return specialImg;
    }

    public void setSpecialImg(String specialImg) {
        this.specialImg = specialImg;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public String getErrorImage() {
        return errorImage;
    }

    public void setErrorImage(String errorImage) {
        this.errorImage = errorImage;
    }

    public String getOverrideImage() {
        return overrideImage;
    }

    public void setOverrideImage(String overrideImage) {
        this.overrideImage = overrideImage;
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

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(int marketStatus) {
        this.marketStatus = marketStatus;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timeStamp);
        dest.writeString(this.specialImg);
        dest.writeString(this.goodsSn);
        dest.writeString(this.errorImage);
        dest.writeString(this.overrideImage);
        dest.writeString(this.productImages);
        dest.writeString(this.productVideos);
        dest.writeValue(this.productType);
        dest.writeString(this.goodsName);
        dest.writeDouble(this.price);
        dest.writeInt(this.marketStatus);
        dest.writeString(this.categoryTreeName);
        dest.writeString(this.categoryTreePath);
        dest.writeByte(this.isPromotion ? (byte) 1 : (byte) 0);
        dest.writeString(this.promotionType);
    }

    public SpecialRecored() {
    }

    protected SpecialRecored(Parcel in) {
        this.timeStamp = in.readLong();
        this.specialImg = in.readString();
        this.goodsSn = in.readString();
        this.errorImage = in.readString();
        this.overrideImage = in.readString();
        this.productImages = in.readString();
        this.productVideos = in.readString();
        this.productType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.goodsName = in.readString();
        this.price = in.readDouble();
        this.marketStatus = in.readInt();
        this.categoryTreeName = in.readString();
        this.categoryTreePath = in.readString();
        this.isPromotion = in.readByte() != 0;
        this.promotionType = in.readString();
    }

    public static final Creator<SpecialRecored> CREATOR = new Creator<SpecialRecored>() {
        @Override
        public SpecialRecored createFromParcel(Parcel source) {
            return new SpecialRecored(source);
        }

        @Override
        public SpecialRecored[] newArray(int size) {
            return new SpecialRecored[size];
        }
    };
}
