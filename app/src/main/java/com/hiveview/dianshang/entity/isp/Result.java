package com.hiveview.dianshang.entity.isp;

import java.util.List;

/**
 * Created by carter on 4/18/17.
 */

public class Result{
    private String ccode;
    private boolean isGroupUser;
    private String isp;
    private String province;
    private String pcode;
    private String district;
    private List<TelList> telList;
    private String ipServiceTel;

    public String getCcode() {
        return ccode;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public boolean isGroupUser() {
        return isGroupUser;
    }

    public void setGroupUser(boolean groupUser) {
        isGroupUser = groupUser;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public List<TelList> getTelList() {
        return telList;
    }

    public void setTelList(List<TelList> telList) {
        this.telList = telList;
    }

    public String getIpServiceTel() {
        return ipServiceTel;
    }

    public void setIpServiceTel(String ipServiceTel) {
        this.ipServiceTel = ipServiceTel;
    }


}
