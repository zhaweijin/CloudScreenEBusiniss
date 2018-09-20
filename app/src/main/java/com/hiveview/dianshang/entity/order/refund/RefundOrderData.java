package com.hiveview.dianshang.entity.order.refund;

import java.util.List;

/**
 * Created by carter on 5/25/17.
 */

public class RefundOrderData {

    /**
     * 用户ID
     */
    private String userid;
    /**
     * 退款商品订单sn
     */
    private String orderSn;
    /**
     * 退款商品总数量
     */
    private int quantity;
    /**
     * 退款原因
     */
    private String reason;
    /**
     * 退款类型: 2换货 1退款
     */
    private int type;
    /**
     * 商品分整个订单退款,还是退整个订单里面的某个商品: 单件退款 1,整单退款2
     */
    private int refundType;
    /**
     * 退款总价
     */
    private double amount;
    /**
     * 退款商品
     */
    private List<OrderGoods> orderGoodsSkuDtoList;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRefundType() {
        return refundType;
    }

    public void setRefundType(int refundType) {
        this.refundType = refundType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<OrderGoods> getOrderGoodsSkuDtoList() {
        return orderGoodsSkuDtoList;
    }

    public void setOrderGoodsSkuDtoList(List<OrderGoods> orderGoodsSkuDtoList) {
        this.orderGoodsSkuDtoList = orderGoodsSkuDtoList;
    }
}
