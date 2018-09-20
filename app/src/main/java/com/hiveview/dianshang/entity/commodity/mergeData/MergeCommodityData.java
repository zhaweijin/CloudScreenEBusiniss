package com.hiveview.dianshang.entity.commodity.mergeData;


/**
 * Created by carter on 12/19/17.
 */

public class MergeCommodityData {
    private MergeCommodity data;
    private String errorMessage;
    private int returnValue;

    public MergeCommodity getData() {
        return data;
    }

    public void setData(MergeCommodity data) {
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
