package com.hiveview.dianshang.entity.acution.cancelorder;

/**
 * Created by zwj on 3/27/18.
 */

public class CancelOrderReturnData {

    private String orderSn;
    private long timeStamp;

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
