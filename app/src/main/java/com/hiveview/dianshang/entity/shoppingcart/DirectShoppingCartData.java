package com.hiveview.dianshang.entity.shoppingcart;

import android.os.Parcel;
import android.os.Parcelable;

import com.hiveview.dianshang.entity.commodity.type.SkuList;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfo;

/**
 * Created by carter on 5/23/17.
 */

public class DirectShoppingCartData implements Parcelable {

    /**
     * 商品规格详情
     */
    private SkuList skuInfo;
    /**
     * 商品规格sn
     */
    private String goodskuSn;
    /**
     * 商品数量
     */
    private int quantity;
    /**
     * 商品名称
     */
    private String goodName;

    /**
     * 收件人地址详情
     */
    private String address_info="";

    /**
     * 地址唯一ID
     */
    private int receiveId=-1;

    /**
     * 商品价格数据
     */
    private ShoppingCartInfo shoppingCartInfo;

    /**
     * 优惠价格
     */
    private double discountPrice;

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public SkuList getSkuInfo() {
        return skuInfo;
    }

    public void setSkuInfo(SkuList skuInfo) {
        this.skuInfo = skuInfo;
    }

    public String getGoodskuSn() {
        return goodskuSn;
    }

    public void setGoodskuSn(String goodskuSn) {
        this.goodskuSn = goodskuSn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAddress_info() {
        return address_info;
    }

    public void setAddress_info(String address_info) {
        this.address_info = address_info;
    }

    public int getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(int receiveId) {
        this.receiveId = receiveId;
    }

    public ShoppingCartInfo getShoppingCartInfo() {
        return shoppingCartInfo;
    }

    public void setShoppingCartInfo(ShoppingCartInfo shoppingCartInfo) {
        this.shoppingCartInfo = shoppingCartInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.skuInfo, flags);
        dest.writeString(this.goodskuSn);
        dest.writeInt(this.quantity);
        dest.writeString(this.goodName);
        dest.writeString(this.address_info);
        dest.writeInt(this.receiveId);
        dest.writeParcelable(this.shoppingCartInfo, flags);
        dest.writeDouble(this.discountPrice);
    }

    public DirectShoppingCartData() {
    }

    protected DirectShoppingCartData(Parcel in) {
        this.skuInfo = in.readParcelable(SkuList.class.getClassLoader());
        this.goodskuSn = in.readString();
        this.quantity = in.readInt();
        this.goodName = in.readString();
        this.address_info = in.readString();
        this.receiveId = in.readInt();
        this.shoppingCartInfo = in.readParcelable(ShoppingCartInfo.class.getClassLoader());
        this.discountPrice = in.readDouble();
    }

    public static final Creator<DirectShoppingCartData> CREATOR = new Creator<DirectShoppingCartData>() {
        @Override
        public DirectShoppingCartData createFromParcel(Parcel source) {
            return new DirectShoppingCartData(source);
        }

        @Override
        public DirectShoppingCartData[] newArray(int size) {
            return new DirectShoppingCartData[size];
        }
    };
}
