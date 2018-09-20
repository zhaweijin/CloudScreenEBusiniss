package com.hiveview.dianshang.entity.advertisement;


import java.util.List;

/**
 * Created by carter on 5/16/17.
 */

public class AdvertisementData {
    private List<Advertisement> data;
    private String errorMessage;
    private int returnValue;

    public List<Advertisement> getData() {
        return data;
    }

    public void setData(List<Advertisement> data) {
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
