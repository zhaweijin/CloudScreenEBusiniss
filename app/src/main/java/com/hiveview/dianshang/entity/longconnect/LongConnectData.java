package com.hiveview.dianshang.entity.longconnect;


/**
 * Created by carter on 5/19/17.
 */

public class LongConnectData {

    private LongConnect data;
    private String errorMessage;
    private int returnValue;

    public LongConnect getData() {
        return data;
    }

    public void setData(LongConnect data) {
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
