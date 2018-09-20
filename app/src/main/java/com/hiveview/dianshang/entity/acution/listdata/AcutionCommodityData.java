package com.hiveview.dianshang.entity.acution.listdata;



/**
 * Created by carter on 5/16/17.
 */

public class AcutionCommodityData {
    private AcutionCommoditySuperData data;
    private String errorMessage;
    private int returnValue;

    public AcutionCommoditySuperData getData() {
        return data;
    }

    public void setData(AcutionCommoditySuperData data) {
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
