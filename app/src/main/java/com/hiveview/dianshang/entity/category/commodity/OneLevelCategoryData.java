package com.hiveview.dianshang.entity.category.commodity;


/**
 * Created by carter on 5/16/17.
 */

public class OneLevelCategoryData {
    private OneLevelCategory data;
    private String errorMessage;
    private int returnValue;

    public OneLevelCategory getData() {
        return data;
    }

    public void setData(OneLevelCategory data) {
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
