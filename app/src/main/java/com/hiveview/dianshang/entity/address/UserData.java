package com.hiveview.dianshang.entity.address;

/**
 * Created by carter on 5/15/17.
 */

public class UserData {
    /**
     * 收件详细地址
     */
    private String addressDetail;
    /**
     * 收件所在地区
     */
    private String addressProvince;
    /**
     * 收件所在街道
     */
    private String addressTown;
    /**
     * 收件人
     */
    private String consignee;
    /**
     * 是否默认
     */
    private Boolean isDefault;
    /**
     * 收件人电话号码
     */
    private String phone;

    /**
     * 用户省 市 县各级编码
     */
    private String treePath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 唯一性ID

     */
    private int id;


    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }


    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getAddressProvince() {
        return addressProvince;
    }

    public void setAddressProvince(String addressProvince) {
        this.addressProvince = addressProvince;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }
}
