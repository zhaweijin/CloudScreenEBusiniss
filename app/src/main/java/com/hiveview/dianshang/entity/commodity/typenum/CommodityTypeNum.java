package com.hiveview.dianshang.entity.commodity.typenum;

/**
 * Created by carter on 5/16/17.
 */

public class CommodityTypeNum {


    private int availableStock;
    private String goodsSn;
    private int occupyStock;
    private String skuSn;
    private int totalStock;

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public int getOccupyStock() {
        return occupyStock;
    }

    public void setOccupyStock(int occupyStock) {
        this.occupyStock = occupyStock;
    }

    public String getSkuSn() {
        return skuSn;
    }

    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }
}
