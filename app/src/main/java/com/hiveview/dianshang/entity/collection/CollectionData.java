package com.hiveview.dianshang.entity.collection;


/**
 * Created by carter on 5/16/17.
 */

public class CollectionData {
    private Collection data;
    private String errorMessage;
    private int returnValue;

    public Collection getData() {
        return data;
    }

    public void setData(Collection data) {
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
