package com.hiveview.dianshang.entity.recommend.special;



/**
 * Created by carter on 5/19/17.
 */

public class RecommendSpecialData {

    private RecommendSpecial data;
    private String errorMessage;
    private int returnValue;

    public RecommendSpecial getData() {
        return data;
    }

    public void setData(RecommendSpecial data) {
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
