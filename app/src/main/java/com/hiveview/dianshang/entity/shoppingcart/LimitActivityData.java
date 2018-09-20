package com.hiveview.dianshang.entity.shoppingcart;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ThinkPad on 2017/12/16.
 */

public class LimitActivityData implements Parcelable {
    /**
     * 已购买的商品数量
     */
    private int buyNum;
    /**
     * 限购数量
     */
    private int limitNum;
    /**
     * 商品sn
     */
    private String skuSn;
    /**
     * 是否限制购买数量
     */
    private boolean limitFlag;



    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    public String getSkuSn() {
        return skuSn;
    }

    public void setSkuSn(String skuSn) {
        this.skuSn = skuSn;
    }

    public boolean isLimitFlag() {
        return limitFlag;
    }

    public void setLimitFlag(boolean limitFlag) {
        this.limitFlag = limitFlag;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.buyNum);
        dest.writeInt(this.limitNum);
        dest.writeString(this.skuSn);
        dest.writeByte(this.limitFlag ? (byte) 1 : (byte) 0);
    }

    public LimitActivityData() {
    }

    protected LimitActivityData(Parcel in) {
        this.buyNum = in.readInt();
        this.limitNum = in.readInt();
        this.skuSn = in.readString();
        this.limitFlag = in.readByte() != 0;
    }

    public static final Creator<LimitActivityData> CREATOR = new Creator<LimitActivityData>() {
        @Override
        public LimitActivityData createFromParcel(Parcel source) {
            return new LimitActivityData(source);
        }

        @Override
        public LimitActivityData[] newArray(int size) {
            return new LimitActivityData[size];
        }
    };
}
