package com.hiveview.dianshang.entity.commodity.type;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 促销活动
 * Created by carter on 12/19/17.
 */

public class PromotionActiivty implements Parcelable {

    /**
     * 限购的数量
     */
    private int canBuyNum;

    /**
     * 限购类型
     */
    private String saleType;

    /**
     * 限购标题
     */
    private String title;


    public int getCanBuyNum() {
        return canBuyNum;
    }

    public void setCanBuyNum(int canBuyNum) {
        this.canBuyNum = canBuyNum;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.canBuyNum);
        dest.writeString(this.saleType);
        dest.writeString(this.title);
    }

    public PromotionActiivty() {
    }

    protected PromotionActiivty(Parcel in) {
        this.canBuyNum = in.readInt();
        this.saleType = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<PromotionActiivty> CREATOR = new Parcelable.Creator<PromotionActiivty>() {
        @Override
        public PromotionActiivty createFromParcel(Parcel source) {
            return new PromotionActiivty(source);
        }

        @Override
        public PromotionActiivty[] newArray(int size) {
            return new PromotionActiivty[size];
        }
    };
}
