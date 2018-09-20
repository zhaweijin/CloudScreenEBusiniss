package com.hiveview.dianshang.entity.commodity.typenum;


/**
 * Created by carter on 5/16/17.
 */

public class CommodityTypeNumData {
    private CommodityTypeNum data;
    private String errorMessage;
    private int returnValue;

    public CommodityTypeNum getData() {
        return data;
    }

    public void setData(CommodityTypeNum data) {
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
