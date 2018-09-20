package com.hiveview.dianshang.entity.order.item;

import java.util.List;

/**
 * Created by carter on 12/23/17.
 */

public class OrderItemTopData {

    /**
     * 满增商品列表
     */
    private List<OrderGiftData> fullGiftList;

    private OrderItem orderContentDataVoPage;

    public List<OrderGiftData> getFullGiftList() {
        return fullGiftList;
    }

    public void setFullGiftList(List<OrderGiftData> fullGiftList) {
        this.fullGiftList = fullGiftList;
    }

    public OrderItem getOrderContentDataVoPage() {
        return orderContentDataVoPage;
    }

    public void setOrderContentDataVoPage(OrderItem orderContentDataVoPage) {
        this.orderContentDataVoPage = orderContentDataVoPage;
    }
}
