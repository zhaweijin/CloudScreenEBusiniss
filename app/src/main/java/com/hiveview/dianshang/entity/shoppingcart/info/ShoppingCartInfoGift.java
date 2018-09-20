package com.hiveview.dianshang.entity.shoppingcart.info;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by carter on 12/25/17.
 */

public class ShoppingCartInfoGift implements Parcelable {

    private String giftName;
    private int giftNum;
    private Double giftPrice;
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

    public Double getGiftPrice() {
        return giftPrice;
    }

    public void setGiftPrice(Double giftPrice) {
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
        dest.writeValue(this.giftPrice);
        dest.writeString(this.imgUrl);
    }

    public ShoppingCartInfoGift() {
    }

    protected ShoppingCartInfoGift(Parcel in) {
        this.giftName = in.readString();
        this.giftNum = in.readInt();
        this.giftPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.imgUrl = in.readString();
    }

    public static final Parcelable.Creator<ShoppingCartInfoGift> CREATOR = new Parcelable.Creator<ShoppingCartInfoGift>() {
        @Override
        public ShoppingCartInfoGift createFromParcel(Parcel source) {
            return new ShoppingCartInfoGift(source);
        }

        @Override
        public ShoppingCartInfoGift[] newArray(int size) {
            return new ShoppingCartInfoGift[size];
        }
    };
}
