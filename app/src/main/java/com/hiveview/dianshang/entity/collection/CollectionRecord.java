package com.hiveview.dianshang.entity.collection;

/**
 * Created by carter on 5/19/17.
 */

public class CollectionRecord {

    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品价格
     */
    private Double goodsPrice;
    /**
     * 商品sn
     */
    private String goodsSn;
    /**
     * 商品图片URL
     */
    private String goodsUrl;
    /**
     * 用户ID
     */
    private int userid;
    /**
     * 是否下线 0初始状态,1 上下, 2 下线
     */
    private int marketStatus;

    /**
     * 是否支持促销
     */
    private boolean isPromotion;

    /**
     * 促销类型列表
     */
    private String promotionType;

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        isPromotion = promotion;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public double getGoodsPrice() {
        return goodsPrice;
    }

    public int getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(int marketStatus) {
        this.marketStatus = marketStatus;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public String getGoodsUrl() {
        return goodsUrl;
    }

    public void setGoodsUrl(String goodsUrl) {
        this.goodsUrl = goodsUrl;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

}
