package com.hiveview.dianshang.entity.order;



/**
 * Created by carter on 5/23/17.
 */

public class OrderData {
    private Order data;
    private String errorMessage;
    private int returnValue;

    public Order getData() {
        return data;
    }

    public void setData(Order data) {
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
