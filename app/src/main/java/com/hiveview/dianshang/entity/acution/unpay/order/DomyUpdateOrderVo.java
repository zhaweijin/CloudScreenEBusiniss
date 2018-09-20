package com.hiveview.dianshang.entity.acution.unpay.order;

/**
 * Created by zwj on 3/20/18.
 */

public class DomyUpdateOrderVo {
    /**
     * 时间更新
     */
    private long timeStamp;

    /**
     * 订单编号
     */
    private String orderSn;


    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
}