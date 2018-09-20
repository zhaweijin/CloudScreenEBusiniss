package com.hiveview.dianshang.entity.acution.common;

/**
 * Created by zwj on 3/15/18.
 */

public class AcutionItemData {

    /**
     * 拍卖出价
     */
    //private List<DomyAuctionBidRecordVo> auctionBidList;

    /**
     * 拍卖当前状态
     */
    private DomyAuctionStatusVo auctionStatusVo;

    /**
     * 拍卖机器人状态
     */
    private DomyAuctionRobotVo auctionRobotVo;

    /**
     * 机器人定时状态
     */
    private DomyAuctionScheduleVo auctionScheduleVo;


    private boolean isJoinAcution;

    private DomyAuctionVo auctionVo;

    /**
     * 进度条的当前值
     */
    private int progressValue;

    public int getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
    }

    /*public List<DomyAuctionBidRecordVo> getAuctionBidList() {
        return auctionBidList;
    }

    public void setAuctionBidList(List<DomyAuctionBidRecordVo> auctionBidList) {
        this.auctionBidList = auctionBidList;
    }*/

    public DomyAuctionStatusVo getAuctionStatusVo() {
        return auctionStatusVo;
    }

    public void setAuctionStatusVo(DomyAuctionStatusVo auctionStatusVo) {
        this.auctionStatusVo = auctionStatusVo;
    }

    public DomyAuctionRobotVo getAuctionRobotVo() {
        return auctionRobotVo;
    }

    public void setAuctionRobotVo(DomyAuctionRobotVo auctionRobotVo) {
        this.auctionRobotVo = auctionRobotVo;
    }

    public DomyAuctionScheduleVo getAuctionScheduleVo() {
        return auctionScheduleVo;
    }

    public void setAuctionScheduleVo(DomyAuctionScheduleVo auctionScheduleVo) {
        this.auctionScheduleVo = auctionScheduleVo;
    }

    public DomyAuctionVo getAuctionVo() {
        return auctionVo;
    }

    public void setAuctionVo(DomyAuctionVo auctionVo) {
        this.auctionVo = auctionVo;
    }

    public boolean isJoinAcution() {
        return isJoinAcution;
    }

    public void setJoinAcution(boolean joinAcution) {
        isJoinAcution = joinAcution;
    }
}
