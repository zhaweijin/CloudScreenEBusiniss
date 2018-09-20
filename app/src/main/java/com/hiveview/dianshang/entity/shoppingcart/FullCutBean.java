package com.hiveview.dianshang.entity.shoppingcart;

/**
 * Created by ThinkPad on 2017/12/28.
 */

public class FullCutBean {
    /**
     * 满减的减额
     */
    private double cutPrice;
    /**
     * 满减的梯度额
     */
    private double enoughPrice;

    public double getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(double cutPrice) {
        this.cutPrice = cutPrice;
    }

    public double getEnoughPrice() {
        return enoughPrice;
    }

    public void setEnoughPrice(double enoughPrice) {
        this.enoughPrice = enoughPrice;
    }
}
