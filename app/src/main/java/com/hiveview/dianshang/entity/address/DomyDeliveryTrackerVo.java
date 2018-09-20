package com.hiveview.dianshang.entity.address;




public class DomyDeliveryTrackerVo {
    /**
     * 物流公司类型
     * 1 -> kdniaoTrackerVo
     * 2 -> kd100TrackerVo
     */
    private Integer deliveryType;

    /**
     * 快递100
     */
    private DomyKD100TrackerVo kd100TrackerVo;

    /**
     * 快递鸟
     */
    private DomyKDNIAOTrackerVo kdniaoTrackerVo;


    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public DomyKD100TrackerVo getKd100TrackerVo() {
        return kd100TrackerVo;
    }

    public void setKd100TrackerVo(DomyKD100TrackerVo kd100TrackerVo) {
        this.kd100TrackerVo = kd100TrackerVo;
    }

    public DomyKDNIAOTrackerVo getKdniaoTrackerVo() {
        return kdniaoTrackerVo;
    }

    public void setKdniaoTrackerVo(DomyKDNIAOTrackerVo kdniaoTrackerVo) {
        this.kdniaoTrackerVo = kdniaoTrackerVo;
    }
}
