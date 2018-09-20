package com.hiveview.dianshang.entity.netty.search;

/**
 * Created by carter on 9/12/17.
 */

public class DomySearchTcpMsgVo {
    /**
     * 动作类型
     */
    private Integer actionType;

    /**
     * 搜索关键字
     */
    private String search;

    /**
     * 用户ID
     */
    private String userid;

    /**
     * ID ,支付成功，失败的返回值
     */
    private Integer contentId;

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}