package com.hiveview.dianshang.entity.order;

/**
 * Created by carter on 5/23/17.
 */

public class OrderInfo {

    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品分类sn
     */
    private String goodsSkuSn;

    /**
     * 商品sn
     */
    private String goodsSn;
    /**
     * 商品标题
     */
    private String goodsTitle;
    /**
     * 商品单价
     */
    private double price;
    /**
     * 商品数量
     */
    private int quantity;
    /**
     * 商品详情
     */
    private String specifications;
    /**
     * 商品图标url
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
}
