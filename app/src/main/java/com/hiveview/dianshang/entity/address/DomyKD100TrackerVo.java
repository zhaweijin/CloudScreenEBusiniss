package com.hiveview.dianshang.entity.address;


import java.util.List;

/**
 * 快递100接口
 */
public class DomyKD100TrackerVo {
    /**
     * 物流公司编号
     */
    private String com;
    /**
     * 物流单号
     */
    private String nu;

    /**
     * 每条跟踪信息的时间
     */
    private String time;

    /**
     * 每条跟综信息的描述
     */
    private String context;

    /**
     * 快递单当前的状态 ：　
     0：在途，即货物处于运输过程中；
     1：揽件，货物已由快递公司揽收并且产生了第一条跟踪信息；
     2：疑难，货物寄送过程出了问题；
     3：签收，收件人已签收；
     4：退签，即货物由于用户拒签、超区等原因退回，而且发件人已经签收；
     5：派件，即快递正在进行同城派件；
     6：退回，货物正处于退回发件人的途中；
     */
    private String state;

    /**
     * 	查询结果状态：
     0：物流单暂无结果，
     1：查询成功，
     2：接口出现异常，
     */
    private String status;

    /**
     * 列表
     */
    private List<KD100ExpressTraces> data;

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<KD100ExpressTraces> getData() {
        return data;
    }

    public void setData(List<KD100ExpressTraces> data) {
        this.data = data;
    }
}
