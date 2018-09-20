package com.hiveview.dianshang.entity.order;


import java.util.List;

/**
 * Created by carter on 5/19/17.
 */

public class OrderRecord {

    /**
     * 订单创建时间
     */
    private long createTime;
	/**
     * 订单完成时间
     */
    private long completeDate;
    /**
     * 一个订单里面的明细商品
     */
    private List<OrderInfo> orderInfoList;

    /**
     * 订单sn
     */
    private String orderSn;
	/**
     * 商品价格
     */
    private double price;
    private int status;
    /*
    * 订单类型
    * */
    private int orderType;
    /**
     * 订单更新时间
     */
    private long updateTime;
    /**
     * 订单总价
     */
    private double amount;
    /**
     * 总数量
     */
    private int totalQuantity;

    /**
     * 是否可以退货,因为订单超过维护器,就不允许退货
     */
    private boolean serviceStatus;

    /**
     * 待收货是否可退款标识
     */
    private Integer refundStatus;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<OrderInfo> getOrderInfoList() {
        return orderInfoList;
    }

    public void setOrderInfoList(List<OrderInfo> orderInfoList) {
        this.orderInfoList = orderInfoList;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
 public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public long getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(long completeDate) {
        this.completeDate = completeDate;
    }

    public boolean isServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(boolean serviceStatus) {
        this.serviceStatus = serviceStatus;
    }


    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }
}

