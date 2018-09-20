package com.hiveview.dianshang.entity.shoppingcart;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ThinkPad on 2017/12/16.
 */

public class ActivityInfoData implements Parcelable {
    /**
     * 满赠商品的名字
     */
    private String giftName;
    /**
     * 满赠商品的数量
     */
    private int giftNum;
    /**
     * 满赠商品的价格
     */
    private double giftPrice;

    /**
     * 赠品的图标URL
     */
    private String imgUrl;

    /**
     * 满减的减额
     */
    private double cutPrice;
    /**
     * 满减的梯度额
     */
    private double enoughPrice;


    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }

    public double getGiftPrice() {
        return giftPrice;
    }

    public void setGiftPrice(double giftPrice) {
        this.giftPrice = giftPrice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(double cutPrice) {
        this.cutPrice = cutPrice;
    }

    public double getEnoughPrice() {
        return enoughPrice;
    }

    public void setEnoughPrice(double enoughPrice) {
        this.enoughPrice = enoughPrice;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.giftName);
        dest.writeInt(this.giftNum);
        dest.writeDouble(this.giftPrice);
        dest.writeString(this.imgUrl);
        dest.writeDouble(this.cutPrice);
        dest.writeDouble(this.enoughPrice);
    }

    public ActivityInfoData() {
    }

    protected ActivityInfoData(Parcel in) {
        this.giftName = in.readString();
        this.giftNum = in.readInt();
        this.giftPrice = in.readDouble();
        this.imgUrl = in.readString();
        this.cutPrice = in.readDouble();
        this.enoughPrice = in.readDouble();
    }

    public static final Parcelable.Creator<ActivityInfoData> CREATOR = new Parcelable.Creator<ActivityInfoData>() {
        @Override
        public ActivityInfoData createFromParcel(Parcel source) {
            return new ActivityInfoData(source);
        }

        @Override
        public ActivityInfoData[] newArray(int size) {
            return new ActivityInfoData[size];
        }
    };
}
