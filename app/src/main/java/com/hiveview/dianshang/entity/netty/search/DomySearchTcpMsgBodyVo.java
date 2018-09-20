package com.hiveview.dianshang.entity.netty.search;

import java.util.List;

/**
 * Created by carter on 9/12/17.
 */

public class DomySearchTcpMsgBodyVo {
    /**
     * com.hiveview.dianshang.NETTY
     * Android的action字符串
     */
    private String action;

    /**
     * 内容消息
     */
    private List<DomySearchTcpMsgVo> info;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<DomySearchTcpMsgVo> getInfo() {
        return info;
    }

    public void setInfo(List<DomySearchTcpMsgVo> info) {
        this.info = info;
    }
}