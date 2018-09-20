package com.hiveview.dianshang.entity.recommend;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by carter on 5/19/17.
 */

public class RecommendItemData implements Parcelable {
    private String goodsName;
    private String goodsSn;
    //private String goodsUrl;
    /**
     *商品流覆盖图
     */
    private String overrideImage;
    /**
     * 商品图
     */
    private String productImages;
    /**
     * 错误图
     */
    private String errorImage;
    /**
     * 流播放地址
     */
    private String productVideos;
    private int layouth;
    private int layoutw;
    private int layoutx;
    private int layouty;
    private int positionseq;
    private double price;
    private String remark;
    private int screenNumber;
    //页面首先根据refType区分显示，然后根据type区分显示
    /**
     * 商品类型 type=2 图文,type=1流
     */
    private int type;
    /**
     * 2专题 1 商品
     */
    private int refType;

    /**
     * 专题参数
     */
    private String specName;
    private String specSn;
    private String specUrl;


    /**
     * 是否已下架 0:下架　１:上架
     */
    private int onlineStatus;

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

    public String getProductVideos() {
        return productVideos;
    }

    public void setProductVideos(String productVideos) {
        this.productVideos = productVideos;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public int getLayouth() {
        return layouth;
    }

    public void setLayouth(int layouth) {
        this.layouth = layouth;
    }

    public int getLayoutw() {
        return layoutw;
    }

    public void setLayoutw(int layoutw) {
        this.layoutw = layoutw;
    }

    public int getLayoutx() {
        return layoutx;
    }

    public void setLayoutx(int layoutx) {
        this.layoutx = layoutx;
    }

    public int getLayouty() {
        return layouty;
    }

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

    public void setLayouty(int layouty) {
        this.layouty = layouty;
    }

    public int getPositionseq() {
        return positionseq;
    }

    public void setPositionseq(int positionseq) {
        this.positionseq = positionseq;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getRefType() {
        return refType;
    }

    public void setRefType(int refType) {
        this.refType = refType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getScreenNumber() {
        return screenNumber;
    }

    public void setScreenNumber(int screenNumber) {
        this.screenNumber = screenNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public String getSpecSn() {
        return specSn;
    }

    public void setSpecSn(String specSn) {
        this.specSn = specSn;
    }

    public String getSpecUrl() {
        return specUrl;
    }

    public void setSpecUrl(String specUrl) {
        this.specUrl = specUrl;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
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

    public String getErrorImage() {
        return errorImage;
    }

    public void setErrorImage(String errorImage) {
        this.errorImage = errorImage;
    }

    public RecommendItemData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.goodsName);
        dest.writeString(this.goodsSn);
        dest.writeString(this.overrideImage);
        dest.writeString(this.productImages);
        dest.writeString(this.errorImage);
        dest.writeString(this.productVideos);
        dest.writeInt(this.layouth);
        dest.writeInt(this.layoutw);
        dest.writeInt(this.layoutx);
        dest.writeInt(this.layouty);
        dest.writeInt(this.positionseq);
        dest.writeDouble(this.price);
        dest.writeString(this.remark);
        dest.writeInt(this.screenNumber);
        dest.writeInt(this.type);
        dest.writeInt(this.refType);
        dest.writeString(this.specName);
        dest.writeString(this.specSn);
        dest.writeString(this.specUrl);
        dest.writeInt(this.onlineStatus);
        dest.writeString(this.categoryTreeName);
        dest.writeString(this.categoryTreePath);
    }

    protected RecommendItemData(Parcel in) {
        this.goodsName = in.readString();
        this.goodsSn = in.readString();
        this.overrideImage = in.readString();
        this.productImages = in.readString();
        this.errorImage = in.readString();
        this.productVideos = in.readString();
        this.layouth = in.readInt();
        this.layoutw = in.readInt();
        this.layoutx = in.readInt();
        this.layouty = in.readInt();
        this.positionseq = in.readInt();
        this.price = in.readDouble();
        this.remark = in.readString();
        this.screenNumber = in.readInt();
        this.type = in.readInt();
        this.refType = in.readInt();
        this.specName = in.readString();
        this.specSn = in.readString();
        this.specUrl = in.readString();
        this.onlineStatus = in.readInt();
        this.categoryTreeName = in.readString();
        this.categoryTreePath = in.readString();
    }

    public static final Creator<RecommendItemData> CREATOR = new Creator<RecommendItemData>() {
        @Override
        public RecommendItemData createFromParcel(Parcel source) {
            return new RecommendItemData(source);
        }

        @Override
        public RecommendItemData[] newArray(int size) {
            return new RecommendItemData[size];
        }
    };
}
