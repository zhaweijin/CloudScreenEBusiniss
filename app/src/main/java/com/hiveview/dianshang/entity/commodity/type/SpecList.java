package com.hiveview.dianshang.entity.commodity.type;

import java.util.List;

/**
 * Created by carter on 5/22/17.
 */

public class SpecList {

    private String name;
    private List<SpecItemList> specItemList;
    private String specSn;
    private String type;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SpecItemList> getSpecItemList() {
        return specItemList;
    }

    public void setSpecItemList(List<SpecItemList> specItemList) {
        this.specItemList = specItemList;
    }

    public String getSpecSn() {
        return specSn;
    }

    public void setSpecSn(String specSn) {
        this.specSn = specSn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
