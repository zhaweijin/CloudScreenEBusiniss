package com.hiveview.dianshang.entity.shoppingcart.discount;

import com.hiveview.dianshang.entity.shoppingcart.FullCutBean;

import java.util.List;

/**
 * Created by carter on 12/15/17.
 */

public class DiscountType {

    /**
     * 优惠策略sn
     */
    private String promotionSn;
    /**
     * 优惠价格
     */
    private double limitPrice;
    /**
     * 是否满足优惠策略
     */
    private boolean reachCondition;

    /**
     * 优惠策略类型
     */
    private String promotionType;

    /**
     * 商户电话
     */
    private String sellerId;


    /**
     * 原始总价格
     */
    private Double sumPrice;

    /**
     * 满多少减多少
     */
    private List<FullCutSkuData> skuList;
    /*
    *满减策略
    * */
    private List<FullCutBean> reachActInfo;

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

    public boolean isReachCondition() {
        return reachCondition;
    }

    public void setReachCondition(boolean reachCondition) {
        this.reachCondition = reachCondition;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public List<FullCutSkuData> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<FullCutSkuData> skuList) {
        this.skuList = skuList;
    }

    public List<FullCutBean> getReachActInfo() {
        return reachActInfo;
    }

    public void setReachActInfo(List<FullCutBean> reachActInfo) {
        this.reachActInfo = reachActInfo;
    }
}
