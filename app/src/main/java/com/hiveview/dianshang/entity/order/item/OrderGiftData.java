package com.hiveview.dianshang.entity.order.item;

/**
 * Created by carter on 12/23/17.
 */

public class OrderGiftData {

    /**
     * 赠品名称
     */
    private String giftName;
    /**
     * 赠品数量
     */
    private int giftQuantity;
    /**
     * 促销类型
     */
    private String promotionType;

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public int getGiftQuantity() {
        return giftQuantity;
    }

    public void setGiftQuantity(int giftQuantity) {
        this.giftQuantity = giftQuantity;
    }

    public String getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = promotionType;
    }
}
