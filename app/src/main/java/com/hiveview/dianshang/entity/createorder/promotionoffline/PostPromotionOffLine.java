package com.hiveview.dianshang.entity.createorder;

import java.util.List;

/**
 * Created by zwj on 5/8/18.
 */

public class PostPromotionOffLine {

    private String goodsSkuSn;

    private List<String> skuSnList;

    public String getGoodsSkuSn() {
        return goodsSkuSn;
    }

    public void setGoodsSkuSn(String goodsSkuSn) {
        this.goodsSkuSn = goodsSkuSn;
    }

    public List<String> getSkuSnList() {
        return skuSnList;
    }

    public void setSkuSnList(List<String> skuSnList) {
        this.skuSnList = skuSnList;
    }
}
