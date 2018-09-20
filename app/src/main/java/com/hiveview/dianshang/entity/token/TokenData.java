package com.hiveview.dianshang.entity.token;

/**
 * Created by carter on 5/26/17.
 */

public class TokenData {
    private Token data;
    private String errorMessage;
    private int returnValue;

    public Token getData() {
        return data;
    }

    public void setData(Token data) {
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
