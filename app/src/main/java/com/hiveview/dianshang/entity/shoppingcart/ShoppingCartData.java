package com.hiveview.dianshang.entity.shoppingcart;


import java.util.List;

/**
 * Created by carter on 5/19/17.
 */

public class ShoppingCartData {

    private List<GroupData> data;
    private String errorMessage;
    private int returnValue;

    public List<GroupData> getData() {
        return data;
    }

    public void setData(List<GroupData> data) {
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
