package com.hiveview.dianshang.entity.acution.netty;

import java.util.List;

/**
 * Created by zwj on 3/30/18.
 */

public class DomyAcutionTcpMsgBodyVo {
    /**
     * com.hiveview.dianshang.NETTY
     * Android的action字符串
     */
    private String action;

    /**
     * 内容消息
     */
    private List<DomyAuctionTcpVo> info;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<DomyAuctionTcpVo> getInfo() {
        return info;
    }

    public void setInfo(List<DomyAuctionTcpVo> info) {
        this.info = info;
    }
}
