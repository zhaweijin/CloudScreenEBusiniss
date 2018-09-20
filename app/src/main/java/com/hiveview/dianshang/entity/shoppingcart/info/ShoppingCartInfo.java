package com.hiveview.dianshang.entity.shoppingcart.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by carter on 5/23/17.
 */

public class ShoppingCartInfo implements Parcelable {

    /**
     * 总数量
     */
    private int totalNumber;
    /**
     * 总订单价格
     */
    private double totalOrderPrice;
    /**
     * 总价
     */
    private double totalPrice;

    /**
     * 运费
     */
    private double totaldeliveryPrice;

    /**
     * 优惠价格
     */
    private double discountPrice;

    /**
     * 下架商品数量
     */
    private int offlineGoods;
    /**
     * 下架商品itemid
     */
    private List<String> offlineIdList;

    private List<ShoppingCartInfoType> giftData;

    public List<ShoppingCartInfoType> getGiftData() {
        return giftData;
    }

    public void setGiftData(List<ShoppingCartInfoType> giftData) {
        this.giftData = giftData;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public double getTotalOrderPrice() {
        return totalOrderPrice;
    }

    public void setTotalOrderPrice(double totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotaldeliveryPrice() {
        return totaldeliveryPrice;
    }

    public void setTotaldeliveryPrice(double totaldeliveryPrice) {
        this.totaldeliveryPrice = totaldeliveryPrice;
    }

    public int getOfflineGoods() {
        return offlineGoods;
    }

    public void setOfflineGoods(int offlineGoods) {
        this.offlineGoods = offlineGoods;
    }

    public List<String> getOfflineIdList() {
        return offlineIdList;
    }

    public void setOfflineIdList(List<String> offlineIdList) {
        this.offlineIdList = offlineIdList;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.totalNumber);
        dest.writeDouble(this.totalOrderPrice);
        dest.writeDouble(this.totalPrice);
        dest.writeDouble(this.totaldeliveryPrice);
        dest.writeDouble(this.discountPrice);
        dest.writeInt(this.offlineGoods);
        dest.writeStringList(this.offlineIdList);
        dest.writeTypedList(this.giftData);
    }

    public ShoppingCartInfo() {
    }

    protected ShoppingCartInfo(Parcel in) {
        this.totalNumber = in.readInt();
        this.totalOrderPrice = in.readDouble();
        this.totalPrice = in.readDouble();
        this.totaldeliveryPrice = in.readDouble();
        this.discountPrice = in.readDouble();
        this.offlineGoods = in.readInt();
        this.offlineIdList = in.createStringArrayList();
        this.giftData = in.createTypedArrayList(ShoppingCartInfoType.CREATOR);
    }

    public static final Creator<ShoppingCartInfo> CREATOR = new Creator<ShoppingCartInfo>() {
        @Override
        public ShoppingCartInfo createFromParcel(Parcel source) {
            return new ShoppingCartInfo(source);
        }

        @Override
        public ShoppingCartInfo[] newArray(int size) {
            return new ShoppingCartInfo[size];
        }
    };
}
