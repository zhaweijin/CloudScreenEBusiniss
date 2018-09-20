package com.hiveview.dianshang.entity.afterservice.exchange;


import java.util.List;

/**
 * 对应UI每行的具体数据
 * Created by carter on 5/25/17.
 */

public class Exchange {

    private long createTime;
    private List<ExchangeItem> exchangeItemList;
    private String orderSn;
    /**
     * 售后货物数量
     */
    private int quantity;
    /**
     * 售后原因
     */
    private String reason;
    /**
     * 订单总价
     */
    private double amount;

    /**
     * 售后ID
     */
    private String serviceSn;

    /**
     * 售后状态,如退款成功,处理中,退款中
     */
    private int status;

    private int serviceType;

    private long updateTime;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<ExchangeItem> getExchangeItemList() {
        return exchangeItemList;
    }

    public void setExchangeItemList(List<ExchangeItem> exchangeItemList) {
        this.exchangeItemList = exchangeItemList;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
