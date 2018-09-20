package com.hiveview.dianshang.entity.shopping.cart.price;


/**
 * Created by carter on 5/19/17.
 */

public class ShoppingCartPriceData {

    private ShoppingCartPrice data;
    private String errorMessage;
    private int returnValue;

    public ShoppingCartPrice getData() {
        return data;
    }

    public void setData(ShoppingCartPrice data) {
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
