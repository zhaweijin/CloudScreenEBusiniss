package com.hiveview.dianshang.entity.shoppingcart.post;

import java.util.List;

/**
 * Created by carter on 9/11/17.
 */

public class ShoppingPostCartInfo {

    String userid;
    int receiveId;
    List<Long> itemIdList;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(int receiveId) {
        this.receiveId = receiveId;
    }

    public List<Long> getItemIdList() {
        return itemIdList;
    }

    public void setItemIdList(List<Long> itemIdList) {
        this.itemIdList = itemIdList;
    }
}
