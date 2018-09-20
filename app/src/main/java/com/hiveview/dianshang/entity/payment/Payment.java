package com.hiveview.dianshang.entity.payment;

import java.util.List;

/**
 * Created by carter on 6/2/17.
 */

public class Payment {

    List<PaymentData> good_groups;

    public List<PaymentData> getGood_groups() {
        return good_groups;
    }

    public void setGood_groups(List<PaymentData> good_groups) {
        this.good_groups = good_groups;
    }
}
