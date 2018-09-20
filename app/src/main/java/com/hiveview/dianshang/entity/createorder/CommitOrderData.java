package com.hiveview.dianshang.entity.createorder;

/**
 * Created by carter on 6/14/17.
 */

public class CommitOrderData {
    /**
     * 订单总价
     */
    private double amount;
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 时间
     */
    private long timeStamp;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
