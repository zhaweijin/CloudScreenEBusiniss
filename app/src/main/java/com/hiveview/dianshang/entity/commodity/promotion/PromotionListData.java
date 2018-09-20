package com.hiveview.dianshang.entity.commodity.promotion;

import java.util.List;

/**
 * Created by carter on 12/22/17.
 */

public class PromotionListData {

    /**
     * 买赠列表数据
     */
    private List<PromotionRecord> buyGiftList;

    /**
     * 满减列表数据
     */
    private List<PromotionRecord> fullCutList;

    /**
     * 满赠列表数据
     */
    private List<PromotionRecord> fullGiftList;

    /**
     * 限购列表数据
     */
    private List<PromotionRecord> limitBuyList;

    /**
     * 解释权
     */
    private String promotionExplain;


    public List<PromotionRecord> getBuyGiftList() {
        return buyGiftList;
    }

    public void setBuyGiftList(List<PromotionRecord> buyGiftList) {
        this.buyGiftList = buyGiftList;
    }

    public List<PromotionRecord> getFullCutList() {
        return fullCutList;
    }

    public void setFullCutList(List<PromotionRecord> fullCutList) {
        this.fullCutList = fullCutList;
    }

    public List<PromotionRecord> getFullGiftList() {
        return fullGiftList;
    }

    public void setFullGiftList(List<PromotionRecord> fullGiftList) {
        this.fullGiftList = fullGiftList;
    }

    public List<PromotionRecord> getLimitBuyList() {
        return limitBuyList;
    }

    public void setLimitBuyList(List<PromotionRecord> limitBuyList) {
        this.limitBuyList = limitBuyList;
    }

    public String getPromotionExplain() {
        return promotionExplain;
    }

    public void setPromotionExplain(String promotionExplain) {
        this.promotionExplain = promotionExplain;
    }
}
