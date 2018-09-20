package com.hiveview.dianshang.entity.shoppingcart;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carter on 5/20/17.
 */

public class ShoppingCartRecord implements Parcelable {

    /**
     * 购物车sn
     */
    private String cartSn;
    /**
     * 购物车ID
     */
    private long cartId;

    /**
     * 商品的购物车ID
     */
    private long cartItemId;

    /**
     * 购物车商品名称
     */
    private String goodsName;
    /**
     * 购物车商品原价格
     */
    private double originalPrice;

    /**
     * 购物车商品优惠价格
     */
    private double ratePrice;

    /**
     * 限购策略
     */
    private LimitActivityData limitActivity;
    /**
     * 购物车商品sn
     */
    private String goodsSn;

    /**
     * 是否有库存
     */
    private boolean stock;
    /**
     * 库存数量
     */
    private int stockNum;

    /**
     * 购物车商品已选择类型sn
     */
    private String skuSn;
    /**
     * 购物车商品是否下架状态
     * 商品上架状态 0:初始新增状态 1:已上架 2:已下架
     */
    private int marketStatus;

    /**
     * 商品西类型sn,如:861455526255448064_3,861455526255448065_1,861455526255448066_2
     */
    private String specSns;

    /**
     * 购物车商品图标URL
     */
    private String squareUrl;
    /**
     * 购物车商品数量
     */
    private int buyNum;
    /**
     * 购物车选择商品的类型名称,如:绿色,L,128GB
     */
    private String specValue;

    /**
     * 购物车更新时间
     */
    private Long updateTime;

    /**
     * 买赠的商品列表
     */
    private List<GiftData> skuGifts;
    /**
     * 是否是本组的第一个
     */
    private boolean First;
    /**
     * 所在分组的类型
     */
    private String type;
    /**
     * 是否是title
     */
    private boolean titleBoolean;
    /**
     * promotionSn
     */
    private String promotionSn;
    /*
    *满赠的限制价格
    * */
    private double limitPrice;
      /*
    * 根据数据变化的满赠，满减策略内容
    * */
    private String activecontent;
    /**
     * 赠品列表或者满减的规则金额
     */
    private List<ActivityInfoData> promotionInfo;
    /*
    * 满赠是否显示赠品
    * */
    private boolean show;

    public boolean isStock() {
        return stock;
    }

    public void setStock(boolean stock) {
        this.stock = stock;
    }

    public int getStockNum() {
        return stockNum;
    }

    public void setStockNum(int stockNum) {
        this.stockNum = stockNum;
    }

    public String getCartSn() {
        return cartSn;
    }

    public void setCartSn(String cartSn) {
        this.cartSn = cartSn;
    }

    public long getCartId() {
        return cartId;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

    public long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getRatePrice() {
        return ratePrice;
    }

    public void setRatePrice(double ratePrice) {
        this.ratePrice = ratePrice;
    }

    public LimitActivityData getLimitActivity() {
        return limitActivity;
    }

    public void setLimitActivity(LimitActivityData limitActivity) {
        this.limitActivity = limitActivity;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public String getSkuSn() {
        return skuSn;
    }

    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }

    public int getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(int marketStatus) {
        this.marketStatus = marketStatus;
    }

    public String getSpecSns() {
        return specSns;
    }

    public void setSpecSns(String specSns) {
        this.specSns = specSns;
    }

    public String getSquareUrl() {
        return squareUrl;
    }

    public void setSquareUrl(String squareUrl) {
        this.squareUrl = squareUrl;
    }

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

    public String getSpecValue() {
        return specValue;
    }

    public void setSpecValue(String specValue) {
        this.specValue = specValue;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public List<GiftData> getSkuGifts() {
        return skuGifts;
    }

    public void setSkuGifts(List<GiftData> skuGifts) {
        this.skuGifts = skuGifts;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFirst() {
        return First;
    }

    public void setFirst(boolean first) {
        First = first;
    }

    public boolean isTitleBoolean() {
        return titleBoolean;
    }

    public void setTitleBoolean(boolean titleBoolean) {
        this.titleBoolean = titleBoolean;
    }

    public String getPromotionSn() {
        return promotionSn;
    }

    public void setPromotionSn(String promotionSn) {
        this.promotionSn = promotionSn;
    }

    public double getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(double limitPrice) {
        this.limitPrice = limitPrice;
    }

    public String getActivecontent() {
        return activecontent;
    }

    public void setActivecontent(String activecontent) {
        this.activecontent = activecontent;
    }

    public List<ActivityInfoData> getPromotionInfo() {
        return promotionInfo;
    }

    public void setPromotionInfo(List<ActivityInfoData> promotionInfo) {
        this.promotionInfo = promotionInfo;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cartSn);
        dest.writeLong(this.cartId);
        dest.writeLong(this.cartItemId);
        dest.writeString(this.goodsName);
        dest.writeDouble(this.originalPrice);
        dest.writeDouble(this.ratePrice);
        dest.writeParcelable(this.limitActivity, flags);
        dest.writeString(this.goodsSn);
        dest.writeByte(this.stock ? (byte) 1 : (byte) 0);
        dest.writeInt(this.stockNum);
        dest.writeString(this.skuSn);
        dest.writeInt(this.marketStatus);
        dest.writeString(this.specSns);
        dest.writeString(this.squareUrl);
        dest.writeInt(this.buyNum);
        dest.writeString(this.specValue);
        dest.writeValue(this.updateTime);
        dest.writeTypedList(this.skuGifts);
        dest.writeByte(this.First ? (byte) 1 : (byte) 0);
        dest.writeString(this.type);
        dest.writeByte(this.titleBoolean ? (byte) 1 : (byte) 0);
        dest.writeString(this.promotionSn);
        dest.writeDouble(this.limitPrice);
        dest.writeString(this.activecontent);
        dest.writeTypedList(this.promotionInfo);
        dest.writeByte(this.show ? (byte) 1 : (byte) 0);
    }

    public ShoppingCartRecord() {
    }

    protected ShoppingCartRecord(Parcel in) {
        this.cartSn = in.readString();
        this.cartId = in.readLong();
        this.cartItemId = in.readLong();
        this.goodsName = in.readString();
        this.originalPrice = in.readDouble();
        this.ratePrice = in.readDouble();
        this.limitActivity = in.readParcelable(LimitActivityData.class.getClassLoader());
        this.goodsSn = in.readString();
        this.stock = in.readByte() != 0;
        this.stockNum = in.readInt();
        this.skuSn = in.readString();
        this.marketStatus = in.readInt();
        this.specSns = in.readString();
        this.squareUrl = in.readString();
        this.buyNum = in.readInt();
        this.specValue = in.readString();
        this.updateTime = (Long) in.readValue(Long.class.getClassLoader());
        this.skuGifts = in.createTypedArrayList(GiftData.CREATOR);
        this.First = in.readByte() != 0;
        this.type = in.readString();
        this.titleBoolean = in.readByte() != 0;
        this.promotionSn = in.readString();
        this.limitPrice = in.readDouble();
        this.activecontent = in.readString();
        this.promotionInfo = in.createTypedArrayList(ActivityInfoData.CREATOR);
        this.show = in.readByte() != 0;
    }

    public static final Creator<ShoppingCartRecord> CREATOR = new Creator<ShoppingCartRecord>() {
        @Override
        public ShoppingCartRecord createFromParcel(Parcel source) {
            return new ShoppingCartRecord(source);
        }

        @Override
        public ShoppingCartRecord[] newArray(int size) {
            return new ShoppingCartRecord[size];
        }
    };
}
