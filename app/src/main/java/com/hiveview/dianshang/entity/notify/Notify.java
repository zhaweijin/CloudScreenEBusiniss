package com.hiveview.dianshang.entity.notify;

/**
 * Created by carter on 6/2/17.
 */

public class Notify {
    private String notifyUrl;
    private long timeStamp;

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
