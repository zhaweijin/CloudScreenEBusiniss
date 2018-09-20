package com.hiveview.dianshang.entity.shoppingcart;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * 满增信息数据
 * Created by carter on 12/15/17.
 */

public class GiftData implements Parcelable {

    /**
     * 赠品名称
     */
    private String giftName;

    /**
     * 赠品数量
     */
    private int giftNum;

    /**
     * 赠品价格
     */
    private double giftPrice;

    /**
     * 赠品的图标URL
     */
    private String imgUrl;

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
    }

    public GiftData() {
    }

    protected GiftData(Parcel in) {
        this.giftName = in.readString();
        this.giftNum = in.readInt();
        this.giftPrice = in.readDouble();
        this.imgUrl = in.readString();
    }

    public static final Creator<GiftData> CREATOR = new Creator<GiftData>() {
        @Override
        public GiftData createFromParcel(Parcel source) {
            return new GiftData(source);
        }

        @Override
        public GiftData[] newArray(int size) {
            return new GiftData[size];
        }
    };
}
