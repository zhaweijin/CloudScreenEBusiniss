package com.hiveview.dianshang.entity.isp;

/**
 * Created by carter on 4/18/17.
 */

public class TelList{
    private int id;
    private String district;
    private String brand;
    private Object serviceTel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Object getServiceTel() {
        return serviceTel;
    }

    public void setServiceTel(Object serviceTel) {
        this.serviceTel = serviceTel;
    }


}
