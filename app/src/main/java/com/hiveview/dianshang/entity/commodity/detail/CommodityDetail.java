package com.hiveview.dianshang.entity.commodity.detail;

import java.util.List;

/**
 * Created by carter on 5/16/17.
 */

public class CommodityDetail {

    /**
     * 分类sn ,一级,二级,三级
     */
    private String categoryTreePath;
    /**
     * 分类名称 ,一级,二级,三级
     */
    private String categoryTreeName;
    /**
     * 快递价格
     */
    private double deliveryPrice;
    /**
     * 大图
     */
    private List<String> detailUrl;
    /**
     * 收藏状态
     */
    private Boolean isFavorite;
    private Boolean isTop;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品是否已下架
     */
    private int marketStatus;
    /**
     * 原价
     */
    private double marketPrice;
    /**
     * 活动价格，真实价格
     */
    private double price;

    private boolean supportFlag;

    /**
     * 是否有促销
     */
    private boolean isPromotion;

    /**
     * 所有促销策略展示
     */
    List<String> promotionName;

    /**
     * 商品手机扫描的二维码
     */
    private String qrCode;


    /**
     * 倒计时时间
     */
    private Long endTime;

    /**
     * 商品图标
     */
    private String productImages;
    /**
     * 商品标题
     */
    private String productTitle;

    /**
     * 商品sn
     */
    private String goodsSn;
    /**
     * 商品支持退货类型
     */
    private List<String> support;
    //商户ID
    private String sellerId;
    //商户名称
    private String sellerName;
    //商户电话
    private String sellerPhone;

    public String getCategoryTreePath() {
        return categoryTreePath;
    }

    public void setCategoryTreePath(String categoryTreePath) {
        this.categoryTreePath = categoryTreePath;
    }

    public String getCategoryTreeName() {
        return categoryTreeName;
    }

    public void setCategoryTreeName(String categoryTreeName) {
        this.categoryTreeName = categoryTreeName;
    }

    public List<String> getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(List<String> detailUrl) {
        this.detailUrl = detailUrl;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    public Boolean getTop() {
        return isTop;
    }

    public void setTop(Boolean top) {
        isTop = top;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductImages() {
        return productImages;
    }

    public void setProductImages(String productImages) {
        this.productImages = productImages;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public List<String> getSupport() {
        return support;
    }

    public void setSupport(List<String> support) {
        this.support = support;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public int getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(int marketStatus) {
        this.marketStatus = marketStatus;
    }

    public boolean isSupportFlag() {
        return supportFlag;
    }

    public void setSupportFlag(boolean supportFlag) {
        this.supportFlag = supportFlag;
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


    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        isPromotion = promotion;
    }

    public List<String> getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(List<String> promotionName) {
        this.promotionName = promotionName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
