package com.hiveview.dianshang.entity.address;

/**
 * Created by carter on 5/16/17.
 */

public class ExpressData {

    private DomyDeliveryTrackerVo data;
    private String errorMessage;
    private int returnValue;

    public DomyDeliveryTrackerVo getData() {
        return data;
    }

    public void setData(DomyDeliveryTrackerVo data) {
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
