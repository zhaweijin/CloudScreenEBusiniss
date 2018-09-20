package com.hiveview.dianshang.entity.shoppingcart.discount;




import java.util.List;

/**
 * Created by carter on 5/19/17.
 */

public class DiscountData {

    private List<DiscountType> data;
    private String errorMessage;
    private int returnValue;

    public List<DiscountType> getData() {
        return data;
    }

    public void setData(List<DiscountType> data) {
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
