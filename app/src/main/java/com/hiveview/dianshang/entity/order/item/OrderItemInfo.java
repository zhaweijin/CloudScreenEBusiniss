package com.hiveview.dianshang.entity.order.item;

import java.util.List;

/**
 * Created by carter on 5/23/17.
 */

public class OrderItemInfo {

    private String goodsName;
    private String goodsSkuSn;
    private String goodsSn;
    private String goodsTitle;
    private double price;
    private int quantity;
    private String sellerId;
    private String sellerName;
    private String sellerPhone;
    private String specifications;
    private String thumbnail;

    /**
     * 买赠商品列表
     */
    private List<OrderGiftData> buyGiftList;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsSkuSn() {
        return goodsSkuSn;
    }

    public void setGoodsSkuSn(String goodsSkuSn) {
        this.goodsSkuSn = goodsSkuSn;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<OrderGiftData> getBuyGiftList() {
        return buyGiftList;
    }

    public void setBuyGiftList(List<OrderGiftData> buyGiftList) {
        this.buyGiftList = buyGiftList;
    }
}
