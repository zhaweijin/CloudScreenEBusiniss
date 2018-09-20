package com.hiveview.dianshang.entity.shoppingcart.info;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by carter on 5/19/17.
 */

public class ShoppingCartInfoData implements Parcelable {

    private ShoppingCartInfo data;
    private String errorMessage;
    private int returnValue;

    public ShoppingCartInfo getData() {
        return data;
    }

    public void setData(ShoppingCartInfo data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.data, flags);
        dest.writeString(this.errorMessage);
        dest.writeInt(this.returnValue);
    }


    protected ShoppingCartInfoData(Parcel in) {
        this.data = in.readParcelable(ShoppingCartInfo.class.getClassLoader());
        this.errorMessage = in.readString();
        this.returnValue = in.readInt();
    }

    public static final Parcelable.Creator<ShoppingCartInfoData> CREATOR = new Parcelable.Creator<ShoppingCartInfoData>() {
        @Override
        public ShoppingCartInfoData createFromParcel(Parcel source) {
            return new ShoppingCartInfoData(source);
        }

        @Override
        public ShoppingCartInfoData[] newArray(int size) {
            return new ShoppingCartInfoData[size];
        }
    };
}
