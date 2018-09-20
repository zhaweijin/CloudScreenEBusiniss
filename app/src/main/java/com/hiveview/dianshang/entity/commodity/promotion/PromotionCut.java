package com.hiveview.dianshang.entity.commodity.promotion;

/**
 * Created by carter on 12/19/17.
 */

public class PromotionCut {

    /**
     * 减价格
     */
    private Double cutPrice;

    /**
     * 满价格
     */
    private Double enoughPrice;

    public Double getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(Double cutPrice) {
        this.cutPrice = cutPrice;
    }

    public Double getEnoughPrice() {
        return enoughPrice;
    }

    public void setEnoughPrice(Double enoughPrice) {
        this.enoughPrice = enoughPrice;
    }
}
