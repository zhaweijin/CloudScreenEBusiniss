package com.hiveview.dianshang.entity.createorder;

/**
 * Created by carter on 5/16/17.
 */

public class CommitOrderStatusData {
    private CommitOrderData data;
    private String errorMessage;
    private int returnValue;

    public CommitOrderData getData() {
        return data;
    }

    public void setData(CommitOrderData data) {
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
