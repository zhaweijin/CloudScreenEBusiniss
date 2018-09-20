package com.hiveview.dianshang.entity.acution.common;

import java.util.List;

/**
 * Created by zwj on 4/2/18.
 */

public class DomyAuctionVo {

    /**
     * 拍卖加价额度.
     */
    private double addQuota;

    /**
     * 可用库存.
     */
    private Integer availableStock;

    /**
     * 拍卖开始时间.
     */
    private long beginTime;

    /**
     * 拍卖成本价.
     */
    private double costPrice;


    /**
     * 拍卖商品封面.
     */
    private String coverImg;


    /**
     * 原价
     */
    private double origPrice;


    /**
     * 拍卖运费.
     */
    private double freight;


    /**
     * 拍卖商品小图.
     */
    private String goodsImg;


    /**
     * 拍卖商品名.
     */
    private String goodsName;


    /**
     * 拍卖商品规格.
     */
    private String goodsSpec;



    /**
     * 拍卖表编号.
     */
    private String auctionSn;

    /**
     * 详情大图
     */
    private List<String> detailUrl;

    /**
     * 详细描述
     */
    private String description;

    public double getAddQuota() {
        return addQuota;
    }

    public void setAddQuota(double addQuota) {
        this.addQuota = addQuota;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public double getOrigPrice() {
        return origPrice;
    }

    public void setOrigPrice(double origPrice) {
        this.origPrice = origPrice;
    }

    public double getFreight() {
        return freight;
    }

    public void setFreight(double freight) {
        this.freight = freight;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsSpec() {
        return goodsSpec;
    }

    public void setGoodsSpec(String goodsSpec) {
        this.goodsSpec = goodsSpec;
    }

    public String getAuctionSn() {
        return auctionSn;
    }

    public void setAuctionSn(String auctionSn) {
        this.auctionSn = auctionSn;
    }

    public List<String> getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(List<String> detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
