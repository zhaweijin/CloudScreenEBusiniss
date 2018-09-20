package com.hiveview.dianshang.entity;

/**
 * Created by carter on 5/16/17.
 */

public class StatusData {
    private String errorMessage;
    private int returnValue;

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
