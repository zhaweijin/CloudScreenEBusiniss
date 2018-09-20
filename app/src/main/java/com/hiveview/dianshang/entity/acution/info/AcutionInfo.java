package com.hiveview.dianshang.entity.acution.info;

import com.hiveview.dianshang.entity.acution.common.AcutionItemData;

/**
 * Created by zwj on 3/15/18.
 */

public class AcutionInfo {

    private AcutionItemData data;
    private String errorMessage;
    private int returnValue;


    public AcutionItemData getData() {
        return data;
    }

    public void setData(AcutionItemData data) {
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
