package com.hiveview.dianshang.entity.acution.time;

/**
 * Created by zwj on 4/4/18.
 */

public class ServerTimeData {
    private ServerTime data;

    private String errorMessage;
    private int returnValue;

    public ServerTime getData() {
        return data;
    }

    public void setData(ServerTime data) {
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
