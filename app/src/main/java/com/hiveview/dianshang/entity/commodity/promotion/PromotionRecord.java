package com.hiveview.dianshang.entity.commodity.promotion;


import java.util.List;

/**
 * Created by carter on 12/19/17.
 */

public class PromotionRecord {

    /**
     * 开始时间
     */
    private long beginTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 促销名称，暂时不用
     */
    private String promotionName;

    /**
     * 促销标题
     */
    private String promotionTitle;

    /**
     * 促销类型
     */
    private String promotionType;

    /**
     *　满增商品列表
     */
    private List<PromotionGift> giftList;

    /**
     * 参与促销的商品规格
     */
    private List<PromotionSku> joinSkuList;

    /**
     * 参与的满减梯度
     */
    private List<PromotionCut> cutList;

    /**
     * 限购数量，只有限购策略的时候才用到
     */
    private int limitNum;

    /**
     * 促销说明信息
     */
    private String promotionNote;


    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
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

    public List<PromotionGift> getGiftList() {
        return giftList;
    }

    public void setGiftList(List<PromotionGift> giftList) {
        this.giftList = giftList;
    }

    public List<PromotionSku> getJoinSkuList() {
        return joinSkuList;
    }

    public void setJoinSkuList(List<PromotionSku> joinSkuList) {
        this.joinSkuList = joinSkuList;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    public String getPromotionNote() {
        return promotionNote;
    }

    public void setPromotionNote(String promotionNote) {
        this.promotionNote = promotionNote;
    }

    public List<PromotionCut> getCutList() {
        return cutList;
    }

    public void setCutList(List<PromotionCut> cutList) {
        this.cutList = cutList;
    }
}
