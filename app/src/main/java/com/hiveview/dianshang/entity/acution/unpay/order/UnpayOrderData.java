package com.hiveview.dianshang.entity.acution.unpay.order;

/**
 * Created by zwj on 3/26/18.
 */

public class UnpayOrderData {

    private DomyAuctionOrderVo data;
    private String errorMessage;
    private int returnValue;

    private boolean hasTopPrice=false;

    public boolean isHasTopPrice() {
        return hasTopPrice;
    }

    public void setHasTopPrice(boolean hasTopPrice) {
        this.hasTopPrice = hasTopPrice;
    }

    public DomyAuctionOrderVo getData() {
        return data;
    }

    public void setData(DomyAuctionOrderVo data) {
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
