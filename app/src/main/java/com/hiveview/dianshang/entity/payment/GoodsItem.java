package com.hiveview.dianshang.entity.payment;

/**
 * Created by carter on 6/1/17.
 */

public class GoodsItem {

    private String body;
    private double price;
    private String product_id;
    private int quailty;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getQuailty() {
        return quailty;
    }

    public void setQuailty(int quailty) {
        this.quailty = quailty;
    }
}
