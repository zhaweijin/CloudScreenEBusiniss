package com.hiveview.dianshang.entity.notify;

/**
 * Created by carter on 6/2/17.
 */

public class NotifyData {
    private Notify data;
    private String errorMessage;
    private int returnValue;

    public Notify getData() {
        return data;
    }

    public void setData(Notify data) {
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
