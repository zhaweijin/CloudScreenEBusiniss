package com.hiveview.dianshang.entity.afterservice.submit;

/**
 * Created by carter on 6/1/17.
 */

public class GoodsSkuData {
    /**
     * 用户ID
     */
    private String userid;


    /**
     * 订单sn
     */
    private String orderSn;

    /**
     * 退款,换货数量
     */
    private int quantity;

    /**
     * 退款,换货原因
     */
    private String reason;

    /**
     * 退款类型: 2换货 1退款
     */
    private int serviceType;
    /**
     * 商品分整个订单退款,还是退整个订单里面的某个商品: 单件退款 1,整单退款2
     */
    private int refundType;

    /**
     * 售后商品图标url
     */
    private String thumbnail;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品分类sku sn
     */
    private String  goodsSkuSn;

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
     * 商品单价
     */
    private double price;

    /**
     * 商品总价
     */
    private double amount;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getRefundType() {
        return refundType;
    }

    public void setRefundType(int refundType) {
        this.refundType = refundType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

