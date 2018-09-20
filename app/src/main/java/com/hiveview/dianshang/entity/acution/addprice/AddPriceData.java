package com.hiveview.dianshang.entity.acution.addprice;

/**
 * Created by zwj on 3/26/18.
 */

public class AddPriceData {

    private AddPriceRetunData data;
    private String errorMessage;
    private int returnValue;

    public AddPriceRetunData getData() {
        return data;
    }

    public void setData(AddPriceRetunData data) {
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
