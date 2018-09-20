package com.hiveview.dianshang.entity.acution.common;

/**
 * Created by zwj on 3/20/18.
 */

public class DomyAuctionBidRecordVo {

    /**
     * 拍卖表编号.
     */
    private String sn;

    /**
     * 拍卖出价
     */
    private double bidPrice;

    /**
     * 拍卖出价的用户ID
     */
    private Long userid;


    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }
}