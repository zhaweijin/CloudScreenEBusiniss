package com.hiveview.dianshang.entity.address;

/**
 * Created by carter on 5/15/17.
 */

public class UserAddress {
    private UserAddressData data;
    private String errorMessage;
    private int returnValue;

    public UserAddressData getData() {
        return data;
    }

    public void setData(UserAddressData data) {
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
