package com.hiveview.dianshang.entity.netty.invoice;

/**
 * Created by carter on 9/12/17.
 */

public class DomyInvoiceTcpMsgVo{

    /**
     * 操作类型:
     * 1 个人中心
     * 2 发票
     * 3 搜索
     */
    private int actionType;

    /**
     * 发票类型
     * 1 无需发票
     * 2 个人
     * 3 企业
     */
    private int invoiceType;

    /**
     * 发票内容
     */
    private String invoiceContent;

    /**
     * 纳税人发票识别号
     */
    private String invoiceId;

    /**
     * 发票抬头
     */
    private String invoiceTitle;

    /**
     * ID 支付成功，失败的返回值
     */
    private Integer contentId;



    private String userid;

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(int invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}