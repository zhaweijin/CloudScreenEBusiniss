package com.hiveview.dianshang.entity.address.qrdata;


import java.util.Date;

/**
 * Created by carter on 5/19/17.
 */

public class Qr {

    private String qrData;
    private long timeStamp;

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
