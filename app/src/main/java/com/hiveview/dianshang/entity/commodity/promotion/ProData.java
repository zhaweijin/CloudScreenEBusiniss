package com.hiveview.dianshang.entity.commodity.promotion;

/**
 * spu促销的信息
 * Created by carter on 12/19/17.
 */

public class ProData {

    private PromotionListData data;
    private String errorMessage;
    private int returnValue;

    public PromotionListData getData() {
        return data;
    }

    public void setData(PromotionListData data) {
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
