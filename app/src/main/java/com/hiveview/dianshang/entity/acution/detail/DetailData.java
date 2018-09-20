package com.hiveview.dianshang.entity.acution.detail;

public class DetailData {
    private DetailUrl data;

    private String errorMessage;
    private int returnValue;

    public DetailUrl getData() {
        return data;
    }

    public void setData(DetailUrl data) {
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
