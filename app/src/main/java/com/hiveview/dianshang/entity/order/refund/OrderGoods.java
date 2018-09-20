package com.hiveview.dianshang.entity.order.refund;

/**
 * Created by carter on 5/25/17.
 */

public class OrderGoods {

    /**
     * 退款商品url
     */
    private String thumbnail;
    /**
     * 退款商品数量
     */
    private int quantity;
    /**
     * 退款商品名称
     */
    private String goodsName;
    /**
     * 退款商品标题
     */
    private String goodsTitle;
    /**
     * 退款商品类型sn
     */
    private String goodsSkuSn;
    /**
     * 退款商品sn
     */
    private String goodsSn;

    /**
     * 售后ID
     */
    private String sellerId;
    /**
     * 售后人名字
     */
    private String sellerName;
    /**
     * 售后电话
     */
    private String sellerPhone;
    /**
     * 退款商品类型描述
     */
    private String specifications;
    /**
     * 退款商品单价
     */
    private double price;


    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
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

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
