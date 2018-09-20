package com.hiveview.dianshang.entity.commodity.afterservice.phone;



/**
 * Created by carter on 5/16/17.
 */

/**
 * 订单售后电话
 */
public class PhoneData {

    private String phone;
    private long timeStamp;
    private String type;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
