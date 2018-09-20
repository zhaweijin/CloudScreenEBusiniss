package com.hiveview.dianshang.entity.acution.common;

/**
 * Created by zwj on 3/20/18.
 */
public class DomyAuctionRobotVo {

    /**
     * 拍卖编号
     *
     */
    private String auctionSn;

    /**
     * 机器人是否在运行
     * 0 未运行 1 已运行
     */
    private int robotStatus;

    /**
     * 拍卖启动机器人.
     * 0 未启动 1 已启动
     */
    private int isRobot;

    public String getAuctionSn() {
        return auctionSn;
    }

    public void setAuctionSn(String auctionSn) {
        this.auctionSn = auctionSn;
    }

    public int getRobotStatus() {
        return robotStatus;
    }

    public void setRobotStatus(int robotStatus) {
        this.robotStatus = robotStatus;
    }

    public int getIsRobot() {
        return isRobot;
    }

    public void setIsRobot(int isRobot) {
        this.isRobot = isRobot;
    }
}