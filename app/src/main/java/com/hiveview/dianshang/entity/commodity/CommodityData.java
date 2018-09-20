package com.hiveview.dianshang.entity.commodity;


/**
 * Created by carter on 5/16/17.
 */

public class CommodityData {
    private Commodity data;
    private String errorMessage;
    private int returnValue;

    public Commodity getData() {
        return data;
    }

    public void setData(Commodity data) {
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
