package com.hiveview.dianshang.entity.address;


import java.util.List;

/**
 * 快鸟接口
 */
public class DomyKDNIAOTrackerVo {


    /**
     * 物流用户ID
     */
    private String eBusinessID;

    /**
     * 物流订单编号.
     */
    private String ordersn;

    /**
     * 物流快递公司编码.
     */
    private String shipperCode;

    /**
     * 物流运单号.
     */
    private String logisticCode;

    /**
     * 商城成功与否.
     */
    private Boolean success;

    /**
     * 商城失败原因.
     */
    private String reason;

    /**
     * 物流状态：2-在途中,3-签收,4-问题件.
     */
    private String state;

    /**
     * 物流跟综信息AcceptTime-AcceptStation-Remark.
     */
    private List<KDNIAOExpressTraces> traces;


    public String geteBusinessID() {
        return eBusinessID;
    }

    public void seteBusinessID(String eBusinessID) {
        this.eBusinessID = eBusinessID;
    }

    public String getOrdersn() {
        return ordersn;
    }

    public void setOrdersn(String ordersn) {
        this.ordersn = ordersn;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getLogisticCode() {
        return logisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        this.logisticCode = logisticCode;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<KDNIAOExpressTraces> getTraces() {
        return traces;
    }

    public void setTraces(List<KDNIAOExpressTraces> traces) {
        this.traces = traces;
    }
}
