package com.hiveview.dianshang.entity.commodity.detail;


/**
 * Created by carter on 5/16/17.
 */

public class CommodityDetailData {
    private CommodityDetail data;
    private String errorMessage;
    private int returnValue;

    public CommodityDetail getData() {
        return data;
    }

    public void setData(CommodityDetail data) {
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
