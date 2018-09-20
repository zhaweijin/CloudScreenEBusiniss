package com.hiveview.dianshang.entity.order.item;


/**
 * Created by carter on 5/23/17.
 */

public class OrderItemData {
    private OrderItemTopData data;
    private String errorMessage;
    private int returnValue;

    public OrderItemTopData getData() {
        return data;
    }

    public void setData(OrderItemTopData data) {
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
