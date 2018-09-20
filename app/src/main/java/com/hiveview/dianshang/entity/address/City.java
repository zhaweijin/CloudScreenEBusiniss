package com.hiveview.dianshang.entity.address;

/**
 * Created by carter on 4/11/17.
 */

public class City {

    private String CityID;
    private String name;
    private String ProID;
    private String CitySort;

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProID() {
        return ProID;
    }

    public void setProID(String proID) {
        ProID = proID;
    }

    public String getCitySort() {
        return CitySort;
    }

    public void setCitySort(String citySort) {
        CitySort = citySort;
    }
}
