package com.hiveview.dianshang.entity.order.limit;




/**
 * Created by carter on 5/23/17.
 */

public class OrderLimitData {
    private OrderLimit data;
    private String errorMessage;
    private int returnValue;

    public OrderLimit getData() {
        return data;
    }

    public void setData(OrderLimit data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
    }
}
