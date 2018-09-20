package com.hiveview.dianshang.entity.special;


/**
 * Created by carter on 5/16/17.
 */

public class SpecialData {
    private Special data;
    private String errorMessage;
    private int returnValue;

    public Special getData() {
        return data;
    }

    public void setData(Special data) {
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
