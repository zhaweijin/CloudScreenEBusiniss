package com.hiveview.dianshang.entity.netty.payment;

import java.util.List;

/**
 * Created by carter on 9/12/17.
 */

public class DomyTcpMsgBodyVo {
    /**
     * com.hiveview.dianshang.NETTY
     * Android的action字符串
     */
    private String action;

    /**
     * 内容消息
     */
    private List<DomyTcpMsgVo> info;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<DomyTcpMsgVo> getInfo() {
        return info;
    }

    public void setInfo(List<DomyTcpMsgVo> info) {
        this.info = info;
    }
}