package com.hiveview.dianshang.entity.commodity.promotion;

/**
 * Created by carter on 12/19/17.
 */

public class PromotionSku {

    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品规格sn
     */
    private String skuSn;
    /**
     * 商品规格类型
     */
    private String specValues;

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getSkuSn() {
        return skuSn;
    }

    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }

    public String getSpecValues() {
        return specValues;
    }

    public void setSpecValues(String specValues) {
        this.specValues = specValues;
    }
}
