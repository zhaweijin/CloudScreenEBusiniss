package com.hiveview.dianshang.entity.recommend;


/**
 * Created by carter on 5/19/17.
 */

public class RecommendData {

    private Recommend data;
    private String errorMessage;
    private int returnValue;

    public Recommend getRecommend() {
        return data;
    }

    public void setRecommend(Recommend recommend) {
        this.data = recommend;
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
