package com.hiveview.dianshang.entity.acution.addprice;

/**
 * Created by zwj on 3/27/18.
 */

public class AddPriceRetunData {

    private double addQuota;
    private String auctionSn;
    private double curPrice;
    private String retMsg;
    private int retNum;

    public double getAddQuota() {
        return addQuota;
    }

    public void setAddQuota(double addQuota) {
        this.addQuota = addQuota;
    }

    public String getAuctionSn() {
        return auctionSn;
    }

    public void setAuctionSn(String auctionSn) {
        this.auctionSn = auctionSn;
    }

    public double getCurPrice() {
        return curPrice;
    }

    public void setCurPrice(double curPrice) {
        this.curPrice = curPrice;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public int getRetNum() {
        return retNum;
    }

    public void setRetNum(int retNum) {
        this.retNum = retNum;
    }
}
