package com.hiveview.dianshang.entity.acution.common;

/**
 * Created by zwj on 3/20/18.
 */

public class DomyAuctionScheduleVo {

    /**
     * 拍卖编号
     *
     */
    private String auctionSn;

    /**
     * 拍卖当前轮次
     * 定时器
     * 0 初始值
     */
    private int currentTimes;


    /**
     * 活动起始时间
     */
    private long auctionTime;

    /**
     * 拍卖定时累计运行总轮次
     * 包括复位前与复位后的累加
     */
    private int sumTotalTimes;

    /**
     * 拍卖累计运行时长
     * 定时器
     * 0 初始值
     */
    private int sumTotalRunTime;

    /**
     * 复位次数
     */
    private int resetTimes;

    /**
     * 拍卖总轮次.
     */
    private int times;

    /**
     * 每轮拍卖持续时间.
     */
    private int durationTime;


    public long getAuctionTime() {
        return auctionTime;
    }

    public void setAuctionTime(long auctionTime) {
        this.auctionTime = auctionTime;
    }

    public String getAuctionSn() {
        return auctionSn;
    }

    public void setAuctionSn(String auctionSn) {
        this.auctionSn = auctionSn;
    }

    public int getCurrentTimes() {
        return currentTimes;
    }

    public void setCurrentTimes(int currentTimes) {
        this.currentTimes = currentTimes;
    }

    public int getSumTotalTimes() {
        return sumTotalTimes;
    }

    public void setSumTotalTimes(int sumTotalTimes) {
        this.sumTotalTimes = sumTotalTimes;
    }

    public int getSumTotalRunTime() {
        return sumTotalRunTime;
    }

    public void setSumTotalRunTime(int sumTotalRunTime) {
        this.sumTotalRunTime = sumTotalRunTime;
    }

    public int getResetTimes() {
        return resetTimes;
    }

    public void setResetTimes(int resetTimes) {
        this.resetTimes = resetTimes;
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
}