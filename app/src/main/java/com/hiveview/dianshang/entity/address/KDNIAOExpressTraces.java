package com.hiveview.dianshang.entity.address;

/**
 * Created by carter on 5/16/17.
 */

public class KDNIAOExpressTraces {

    /**
     * 物流到达位置
     */
    private String acceptStation;
    /**
     * 物流到达时间
     */
    private String acceptTime;

    public String getAcceptStation() {
        return acceptStation;
    }

    public void setAcceptStation(String acceptStation) {
        this.acceptStation = acceptStation;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

}
