package com.hiveview.dianshang.entity.address;

/**
 * Created by carter on 4/11/17.
 */

public class Area {
    private String Id;
    private String name;
    private String CityID;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }
}
