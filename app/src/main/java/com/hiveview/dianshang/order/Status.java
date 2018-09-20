package com.hiveview.dianshang.order;

/**
 * Created by carter on 5/26/17.
 */

public enum Status {

    /** 等待付款 */
    pendingPayment,

    /** 等待发货 */
    pendingShipment,
    /**
     *  待收货
     */
    pendingReceive,

    /** 交易成功 */
    completed,

    /**
     * 退款处理中（用户发起-商户确认）
     */
    processing,

    /**
     * 退款中（商户确认）
     */
    refund_seller,

    /**
     * 退款中（家视）
     */
    refund_hiveview,

    /**
     * 已退款
     */
    refunded,

    /**
     * 换货单
     */
    exchange,

    /**
     * 交易关闭
     */
    close;

}