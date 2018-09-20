package com.hiveview.dianshang.entity.address.qrdata;



/**
 * Created by carter on 5/19/17.
 */

public class QrData {

    private Qr data;
    private String errorMessage;
    private int returnValue;

    public Qr getData() {
        return data;
    }

    public void setData(Qr data) {
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
