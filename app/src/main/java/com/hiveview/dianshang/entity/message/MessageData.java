package com.hiveview.dianshang.entity.message;



/**
 * Created by carter on 5/16/17.
 */

public class MessageData {
    private Message data;
    private String errorMessage;
    private int returnValue;

    public Message getData() {
        return data;
    }

    public void setData(Message data) {
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
