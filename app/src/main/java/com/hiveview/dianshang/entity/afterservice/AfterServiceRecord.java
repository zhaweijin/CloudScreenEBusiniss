package com.hiveview.dianshang.entity.afterservice;


import com.hiveview.dianshang.entity.afterservice.exchange.Exchange;
import com.hiveview.dianshang.entity.afterservice.refund.Refund;

/**
 * Created by carter on 5/25/17.
 */

public class AfterServiceRecord {
    private Exchange exchangeVo;
    private Refund refundVo;

    /**
     * 售后处理方式: 2换货 1退款
     */
    private int serviceType;

    public Exchange getExchangeVo() {
        return exchangeVo;
    }

    public void setExchangeVo(Exchange exchangeVo) {
        this.exchangeVo = exchangeVo;
    }


    public Refund getRefundVo() {
        return refundVo;
    }

    public void setRefundVo(Refund refundVo) {
        this.refundVo = refundVo;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }
}
