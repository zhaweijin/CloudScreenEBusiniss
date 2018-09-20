package com.hiveview.dianshang.entity.commodity.type;


/**
 * Created by carter on 5/16/17.
 */

public class CommodityTypeData {
    private CommodityType data;
    private String errorMessage;
    private int returnValue;

    public CommodityType getData() {
        return data;
    }

    public void setData(CommodityType data) {
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
