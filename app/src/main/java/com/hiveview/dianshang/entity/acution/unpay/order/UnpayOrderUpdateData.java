package com.hiveview.dianshang.entity.acution.unpay.order;

/**
 * Created by zwj on 3/26/18.
 */

public class UnpayOrderUpdateData {

    private DomyUpdateOrderVo data;
    private String errorMessage;
    private int returnValue;

    public DomyUpdateOrderVo getData() {
        return data;
    }

    public void setData(DomyUpdateOrderVo data) {
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
