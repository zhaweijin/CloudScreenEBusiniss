package com.hiveview.dianshang.entity.acution.common;

/**
 * Created by zwj on 3/20/18.
 */

public class DomyAuctionStatusVo {
    /**
     * 拍卖编号
     *
     */
    private String auctionSn;

    /**
     * 拍卖是否结束
     * 0 初始值 1 拍卖中 2 拍卖结束
     */
    private int status;

    /**
     * 最新时间
     * 定时器
     * 获取nginx的最新时间
     */
    private double timeStamp;

    /**
     * 是否到达成本价提交
     */
    private int isCostTake;


    /**
     * 拍卖成本价.
     */
    private double costPrice;

    /**
     * 拍卖最终成交价格
     * 0.00 初始值
     */
    private double dealPrice;

    /**
     * 拍卖当前价格
     * 0.00 初始值
     */
    private double currentPrice;

    /**
     * 拍卖启动机器人.
     * 0 未启动 1 已启动
     */
    private int isRobot;

    /**
     * 拍卖起拍价.
     */
    private double startPrice;

    /**
     * 拍卖总轮次.
     */
    private int times;

    /**
     * 每轮拍卖持续时间.
     */
    private int durationTime;


    /**
     * 拍卖加价额度.
     */
    private double addQuota;

    /**
     * 拍卖开始时间.
     */
    private double beginTime;

    /**
     * 拍卖开始时间是否设置.
     */
    private int beginStatus;

    /**
     * 商品申请审核状态. 0:初始状态,1:提交上架申请,2:上架申请通过,3:上架申请未通过,4:已下架
     */
    private int apply;

    /**
     * 商户用户名
     */
    private String partnerName;

    /**
     * 用户ID
     */
    private long userid;

    /**
     * 出价方
     * 0 初始值 1 用户出价 2 机器人出价
     */
    private int fromBidPrice;


    /**
     * 商品sn
     */
    private String goodsSn;

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public String getAuctionSn() {
        return auctionSn;
    }

    public void setAuctionSn(String auctionSn) {
        this.auctionSn = auctionSn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(double timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getIsCostTake() {
        return isCostTake;
    }

    public void setIsCostTake(int isCostTake) {
        this.isCostTake = isCostTake;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(double dealPrice) {
        this.dealPrice = dealPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getIsRobot() {
        return isRobot;
    }

    public void setIsRobot(int isRobot) {
        this.isRobot = isRobot;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
    }

    public double getAddQuota() {
        return addQuota;
    }

    public void setAddQuota(double addQuota) {
        this.addQuota = addQuota;
    }

    public double getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(double beginTime) {
        this.beginTime = beginTime;
    }

    public int getBeginStatus() {
        return beginStatus;
    }

    public void setBeginStatus(int beginStatus) {
        this.beginStatus = beginStatus;
    }

    public int getApply() {
        return apply;
    }

    public void setApply(int apply) {
        this.apply = apply;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public int getFromBidPrice() {
        return fromBidPrice;
    }

    public void setFromBidPrice(int fromBidPrice) {
        this.fromBidPrice = fromBidPrice;
    }
}