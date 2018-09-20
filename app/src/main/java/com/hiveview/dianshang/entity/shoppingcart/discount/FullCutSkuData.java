package com.hiveview.dianshang.entity.shoppingcart.discount;

import com.hiveview.dianshang.entity.shoppingcart.LimitActivityData;

/**
 * Created by carter on 12/15/17.
 */

public class FullCutSkuData {

    /**
     * 购买数量
     */
    private int buyNum;
    private int cartItemId;
    /**
     * 原始单价格
     */
    private Double originalPrice;
    /**
     *  优惠单价格
     */
    private Double ratePrice;
    private String skuSn;
    /**
     * 总的优惠价格
     */
    private Double sumRatePrice;
    private Long updateTime;
    private String userId;
    /**
     * 限购策略
     */
    private LimitActivityData limitActivity;


    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Double getRatePrice() {
        return ratePrice;
    }

    public void setRatePrice(Double ratePrice) {
        this.ratePrice = ratePrice;
    }

    public String getSkuSn() {
        return skuSn;
    }

    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }

    public Double getSumRatePrice() {
        return sumRatePrice;
    }

    public void setSumRatePrice(Double sumRatePrice) {
        this.sumRatePrice = sumRatePrice;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public LimitActivityData getLimitActivity() {
        return limitActivity;
    }

    public void setLimitActivity(LimitActivityData limitActivity) {
        this.limitActivity = limitActivity;
    }
}
