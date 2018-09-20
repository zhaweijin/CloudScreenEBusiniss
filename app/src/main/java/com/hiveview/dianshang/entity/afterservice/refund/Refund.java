package com.hiveview.dianshang.entity.afterservice.refund;


import java.util.List;

/**
 * 对应UI每行的具体数据
 * Created by carter on 5/25/17.
 */

public class Refund {
    /**
     * 商品总价
     */
    private double amount;

    private long createTime;
    private List<RefundItem> refundItemList;
    private String orderSn;

    private Integer orderType;
    /**
     * 售后货物数量
     */
    private int quantity;
    /**
     * 售后原因
     */
    private String reason;

    /**
     * 售后ID
     */
    private String serviceSn;

    /**
     * 售后状态
     */
    private int status;

    private long updateTime;
    private int serviceType;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<RefundItem> getRefundItemList() {
        return refundItemList;
    }

    public void setRefundItemList(List<RefundItem> refundItemList) {
        this.refundItemList = refundItemList;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getServiceSn() {
        return serviceSn;
    }

    public void setServiceSn(String serviceSn) {
        this.serviceSn = serviceSn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
}
