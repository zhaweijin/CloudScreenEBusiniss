package com.hiveview.dianshang.entity.acution.cancelorder;

/**
 * Created by zwj on 3/27/18.
 */

public class CancelOrderData {

    private CancelOrderReturnData data;
    private String errorMessage;
    private int returnValue;

    public CancelOrderReturnData getData() {
        return data;
    }

    public void setData(CancelOrderReturnData data) {
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
