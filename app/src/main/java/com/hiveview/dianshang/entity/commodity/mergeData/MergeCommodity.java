package com.hiveview.dianshang.entity.commodity.mergeData;

import com.hiveview.dianshang.entity.commodity.Commodity;


/**
 * Created by carter on 12/19/17.
 */

public class MergeCommodity {


    private Commodity megerGoodsVoPage;
    /**
     * 促销标题
     */
    private String promotionTitle;
    /**
     * 促销类型
     */
    private String promotionType;


    public Commodity getMegerGoodsVoPage() {
        return megerGoodsVoPage;
    }

    public void setMegerGoodsVoPage(Commodity megerGoodsVoPage) {
        this.megerGoodsVoPage = megerGoodsVoPage;
    }

    public String getPromotionTitle() {
        return promotionTitle;
    }

    public void setPromotionTitle(String promotionTitle) {
        this.promotionTitle = promotionTitle;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }
}
