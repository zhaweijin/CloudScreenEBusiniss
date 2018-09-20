package com.hiveview.dianshang.entity.shopping.cart.price;

/**
 * Created by carter on 7/15/17.
 */

public class ShoppingCartPrice {

    private int totalNumber;
    private Double totalPrice;
    private int offlineGoods;

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getOfflineGoods() {
        return offlineGoods;
    }

    public void setOfflineGoods(int offlineGoods) {
        this.offlineGoods = offlineGoods;
    }
}
