package com.hiveview.dianshang.entity.address;

import java.util.List;

/**
 * Created by carter on 5/15/17.
 */

public class ProviceData {
    private List<Province> data;
    private String errorMessage;
    private int returnValue;

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

    public List<Province> getData() {
        return data;
    }

    public void setData(List<Province> data) {
        this.data = data;
    }
}
