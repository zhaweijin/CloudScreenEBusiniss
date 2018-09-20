package com.hiveview.dianshang.entity.shoppingcart.discount;

import java.util.List;

/**
 * Created by carter on 12/27/17.
 */

public class PostCartItemInfo {
    String userid;
    List<Long> itemIdList;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public List<Long> getItemIdList() {
        return itemIdList;
    }

    public void setItemIdList(List<Long> itemIdList) {
        this.itemIdList = itemIdList;
    }
}
