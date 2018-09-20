package com.hiveview.dianshang.entity;

import java.util.List;

/**
 * Created by Gavin on 2017/5/17.
 */

public class AfterSaleEntity {
    private double amount;
    private long createTime;
    private String orderSn;
    private Integer orderType;
    private String serviceSn;
    private int quantity;
    private String reason;
    private int serviceType;
    private int status;
    private List<AfterSaleEntityItem> itemList;





    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
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

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getServiceSn() {
        return serviceSn;
    }

    public void setServiceSn(String serviceSn) {
        this.serviceSn = serviceSn;
    }

    public List<AfterSaleEntityItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<AfterSaleEntityItem> itemList) {
        this.itemList = itemList;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
}
