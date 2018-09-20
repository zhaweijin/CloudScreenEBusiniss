package com.hiveview.dianshang.shoppingcart;

/**
 * Created by carter on 7/15/17.
 */

public class OpShoppingUpdate {

    /**
     * 是否删除该商品
     */
    private boolean isDelete;


    /**
     * 商品规格详情
     */
    private String info;

    /**
     * 商品数量
     */
    private int number;

    /**
     * 商品位置
     */
    private int position;

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
