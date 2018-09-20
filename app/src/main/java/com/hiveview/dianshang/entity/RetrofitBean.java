package com.hiveview.dianshang.entity;

/**
 * Created by carter on 3/6/17.
 */
public class RetrofitBean {

    private int resultCode;
    private Boolean success;
    private String errorMsg;
    private String module;
    private Double currentTime;
    private boolean succcess;     //变量类型不能出错

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    //{"resultCode":-1,"success":false,"errorMsg":"参数为空","module":null,"currentTime":1488795007639,"succcess":false}
    //根据服务器的返回值，必须设置对应数据类型的变量，不然回调会失败
}
