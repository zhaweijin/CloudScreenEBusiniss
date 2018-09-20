package com.hiveview.dianshang.entity.search.key;

import java.util.List;

/**
 * Created by carter on 5/19/17.
 */

public class SearchKeyData {

    private List<String> data;
    private String errorMessage;
    private int returnValue;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
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
