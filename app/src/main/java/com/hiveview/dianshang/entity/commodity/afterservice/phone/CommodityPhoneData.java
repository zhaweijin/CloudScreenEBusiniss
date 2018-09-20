package com.hiveview.dianshang.entity.commodity.afterservice.phone;


import java.util.List;

/**
 * Created by carter on 5/16/17.
 */

public class CommodityPhoneData {
    private List<PhoneData> data;
    private String errorMessage;
    private int returnValue;

    public List<PhoneData> getData() {
        return data;
    }

    public void setData(List<PhoneData> data) {
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
