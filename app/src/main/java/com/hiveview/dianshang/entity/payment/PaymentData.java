package com.hiveview.dianshang.entity.payment;

import java.util.List;

/**
 * Created by carter on 6/1/17.
 */

public class PaymentData {

    private String domypay_id;
    private List<GoodsItem> good_items;

    public String getDomypay_id() {
        return domypay_id;
    }

    public void setDomypay_id(String domypay_id) {
        this.domypay_id = domypay_id;
    }

    public List<GoodsItem> getGood_items() {
        return good_items;
    }

    public void setGood_items(List<GoodsItem> good_items) {
        this.good_items = good_items;
    }
}
