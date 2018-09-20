package com.hiveview.dianshang.entity.acution.unpay.order;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zwj on 3/20/18.
 */

public class DomyAuctionOrderVo implements Parcelable {
    /**
     * 订单创建时间
     */
    private long createTime;

    /**
     * 订单过期日期
     */
    private long expireTime;
    /**
     * 订单编号
     */
    private String orderSn;
    /**
     * 订单价格
     */
    private double amount;
    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 待收货是否可退款标识 0不可退 1可退
     *
     * 1 2 不可退, 0 可退
     */
    private Integer refundStatus;

    /**
     * 总数量
     */
    private Integer quantity;

    /**
     * 订单子项商品缩略图
     */
    private String thumbnail;

    /**
     * 订单子项商品名
     */
    private String goodsName;

    /**
     * 订单子项规格.
     */
    private String specifications;

    /**
     * 是否有未支付的拍卖订单
     */
    private boolean haveUnpayOrder;

    /**
     * 当前价格
     */
    private double price;

    /**
     * 运费
     */
    private double freight;

    /**
     * 时间
     */
    private long timeStamp;

    /**
     * 收件人地址
     */
    private String address;

    /**
     * 收件人电话号码
     */
    private String phone;

    /**
     * 商品sn
     */
    private String goodsSn;

    /**
     * 收件人姓名
     */
    private String consignee;

    /**
     * 是否有无发票，0代表无发票,1是有发票
     */
    private Integer isInvoice;
    /**
     * 发票纳税人识别号
     */
    private String invoiceId;
    /**
     * 发票类型(单位，个人)
     */
    private String invoiceType;
    /**
     * 发票抬头
     */
    private String invoiceTitle;
    /**
     * 发票内容
     */
    private String invoiceContent;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public boolean isHaveUnpayOrder() {
        return haveUnpayOrder;
    }

    public void setHaveUnpayOrder(boolean haveUnpayOrder) {
        this.haveUnpayOrder = haveUnpayOrder;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getFreight() {
        return freight;
    }

    public void setFreight(double freight) {
        this.freight = freight;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public Integer getIsInvoice() {
        return isInvoice;
    }

    public void setIsInvoice(Integer isInvoice) {
        this.isInvoice = isInvoice;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public String getGoodSn() {
        return goodsSn;
    }

    public void setGoodSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.createTime);
        dest.writeLong(this.expireTime);
        dest.writeString(this.orderSn);
        dest.writeDouble(this.amount);
        dest.writeValue(this.status);
        dest.writeValue(this.refundStatus);
        dest.writeValue(this.quantity);
        dest.writeString(this.thumbnail);
        dest.writeString(this.goodsName);
        dest.writeString(this.specifications);
        dest.writeByte(this.haveUnpayOrder ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.price);
        dest.writeDouble(this.freight);
        dest.writeLong(this.timeStamp);
        dest.writeString(this.address);
        dest.writeString(this.phone);
        dest.writeString(this.goodsSn);
        dest.writeString(this.consignee);
        dest.writeValue(this.isInvoice);
        dest.writeString(this.invoiceId);
        dest.writeString(this.invoiceType);
        dest.writeString(this.invoiceTitle);
        dest.writeString(this.invoiceContent);
    }

    public DomyAuctionOrderVo() {
    }

    protected DomyAuctionOrderVo(Parcel in) {
        this.createTime = in.readLong();
        this.expireTime = in.readLong();
        this.orderSn = in.readString();
        this.amount = in.readDouble();
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.refundStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.quantity = (Integer) in.readValue(Integer.class.getClassLoader());
        this.thumbnail = in.readString();
        this.goodsName = in.readString();
        this.specifications = in.readString();
        this.haveUnpayOrder = in.readByte() != 0;
        this.price = in.readDouble();
        this.freight = in.readDouble();
        this.timeStamp = in.readLong();
        this.address = in.readString();
        this.phone = in.readString();
        this.goodsSn = in.readString();
        this.consignee = in.readString();
        this.isInvoice = (Integer) in.readValue(Integer.class.getClassLoader());
        this.invoiceId = in.readString();
        this.invoiceType = in.readString();
        this.invoiceTitle = in.readString();
        this.invoiceContent = in.readString();
    }

    public static final Creator<DomyAuctionOrderVo> CREATOR = new Creator<DomyAuctionOrderVo>() {
        @Override
        public DomyAuctionOrderVo createFromParcel(Parcel source) {
            return new DomyAuctionOrderVo(source);
        }

        @Override
        public DomyAuctionOrderVo[] newArray(int size) {
            return new DomyAuctionOrderVo[size];
        }
    };
}