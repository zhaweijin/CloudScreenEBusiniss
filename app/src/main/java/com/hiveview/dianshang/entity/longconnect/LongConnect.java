package com.hiveview.dianshang.entity.longconnect;

/**
 * Created by carter on 7/17/17.
 */

public class LongConnect {

    private String payIp;
    private String port;
    private long timeStamp;

    public String getPayIp() {
        return payIp;
    }

    public void setPayIp(String payIp) {
        this.payIp = payIp;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
