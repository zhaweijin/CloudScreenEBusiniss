package com.hiveview.dianshang.entity.netty.invoice;

import java.util.List;

/**
 * Created by carter on 9/12/17.
 */

public class DomyInvoiceTcpMsgBodyVo {
    /**
     * com.hiveview.dianshang.NETTY
     * Android的action字符串
     */
    private String action;

    /**
     * 内容消息
     */
    private List<DomyInvoiceTcpMsgVo> info;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<DomyInvoiceTcpMsgVo> getInfo() {
        return info;
    }

    public void setInfo(List<DomyInvoiceTcpMsgVo> info) {
        this.info = info;
    }
}