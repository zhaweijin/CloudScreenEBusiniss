package com.hiveview.dianshang.entity.shoppingcart.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by carter on 12/25/17.
 */

public class ShoppingCartInfoType implements Parcelable {

    /**
     * 促销商品列表
     */
    private List<ShoppingCartInfoGift> giftList;

    /**
     * 促销类型
     */
    private String promotionType;

    public List<ShoppingCartInfoGift> getGiftList() {
        return giftList;
    }

    public void setGiftList(List<ShoppingCartInfoGift> giftList) {
        this.giftList = giftList;
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
        dest.writeTypedList(this.giftList);
        dest.writeString(this.promotionType);
    }

    public ShoppingCartInfoType() {
    }

    protected ShoppingCartInfoType(Parcel in) {
        this.giftList = in.createTypedArrayList(ShoppingCartInfoGift.CREATOR);
        this.promotionType = in.readString();
    }

    public static final Parcelable.Creator<ShoppingCartInfoType> CREATOR = new Parcelable.Creator<ShoppingCartInfoType>() {
        @Override
        public ShoppingCartInfoType createFromParcel(Parcel source) {
            return new ShoppingCartInfoType(source);
        }

        @Override
        public ShoppingCartInfoType[] newArray(int size) {
            return new ShoppingCartInfoType[size];
        }
    };
}
