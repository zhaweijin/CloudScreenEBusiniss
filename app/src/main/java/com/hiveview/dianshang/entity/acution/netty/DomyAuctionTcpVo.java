package com.hiveview.dianshang.entity.acution.netty;

/**
 * Created by zwj on 3/30/18.
 */

public class DomyAuctionTcpVo {

    /**
     * 动作类型
     */
    private Integer actionType;
    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 返回串
     */
    private String retMessage;

    /**
     * ID ,支付成功，失败的返回值
     */
    private Integer contentId;

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }
}