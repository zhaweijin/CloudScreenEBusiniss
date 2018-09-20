package com.hiveview.dianshang.entity.order.limit;

/**
 * Created by carter on 1/15/18.
 */

public class OrderLimit {
    /**
     * 是否允许购买
     */
    private boolean canPay;
    private long timeStamp;

    public boolean isCanPay() {
        return canPay;
    }

    public void setCanPay(boolean canPay) {
        this.canPay = canPay;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
