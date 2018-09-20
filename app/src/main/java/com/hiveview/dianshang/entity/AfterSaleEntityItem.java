package com.hiveview.dianshang.entity;

/**
 * Created by ThinkPad on 2017/6/29.
 */

public class AfterSaleEntityItem {

    private String goodsName;
    private String goodsSkuSn;
    private String goodsSn;
    private String goodsTitle;
    private double price;
    private int quantity;
    private String sellerPhone;
    private String thumbnail;


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

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGoodsSkuSn() {
        return goodsSkuSn;
    }

    public void setGoodsSkuSn(String goodsSkuSn) {
        this.goodsSkuSn = goodsSkuSn;
    }
}
