package com.hiveview.dianshang.entity.commodity.promotion;

/**
 * Created by carter on 12/19/17.
 */

public class PromotionGift {

    /**
     * 赠品名称
     */
    private String giftName;

    /**
     * 赠品数量
     */
    private int giftNum;

    /**
     * 赠品价格
     */
    private Double giftPrice;

    /**
     * 赠品图的地址
     */
    private String imgUrl;


    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }

    public Double getGiftPrice() {
        return giftPrice;
    }

    public void setGiftPrice(Double giftPrice) {
        this.giftPrice = giftPrice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
