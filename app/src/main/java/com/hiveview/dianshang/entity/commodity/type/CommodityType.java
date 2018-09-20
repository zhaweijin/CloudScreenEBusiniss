package com.hiveview.dianshang.entity.commodity.type;


import java.util.List;

/**
 * Created by carter on 5/16/17.
 */

public class CommodityType {
    private String defaultSpecItemSns;
    private String goodsSn;
    private List<SkuList> skuList;
    private List<SpecList> specList;

    public String getDefaultSpecItemSns() {
        return defaultSpecItemSns;
    }

    public void setDefaultSpecItemSns(String defaultSpecItemSns) {
        this.defaultSpecItemSns = defaultSpecItemSns;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public List<SkuList> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<SkuList> skuList) {
        this.skuList = skuList;
    }

    public List<SpecList> getSpecList() {
        return specList;
    }

    public void setSpecList(List<SpecList> specList) {
        this.specList = specList;
    }
}
