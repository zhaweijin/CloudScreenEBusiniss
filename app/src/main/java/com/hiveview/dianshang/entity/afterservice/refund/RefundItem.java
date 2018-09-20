package com.hiveview.dianshang.entity.afterservice.refund;

/**
 * Created by carter on 5/25/17.
 */

public class RefundItem {

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品sn
     */
    private String goodsSn;
    /**
     * 商品类型sn
     */
    private String goodsSkuSn;
    /**
     * 退款金额
     */
    private double price;
    /**
     * 商品标题
     */
    private String goodsTitle;
    /**
     * 售后订单内某个商品的数量
     */
    private int quantity;
    /**
     * 售后人ID
     */
    private String sellerId;
    /**
     * 售后联系人
     */
    private String sellerName;
    /**
     * 售后人联系电话
     */
    private String sellerPhone;
    /**
     * 售后商品的类型
     */
    private String specifications;
    /**
     * 售后商品图标url
     */
    private String thumbnail;

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

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
