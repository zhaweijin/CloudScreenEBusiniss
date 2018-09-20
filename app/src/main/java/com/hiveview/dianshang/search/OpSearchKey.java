package com.hiveview.dianshang.search;


/**
 * 搜索方式,关键字
 * Created by carter on 4/23/17.
 */

public class OpSearchKey {

    private String key;
    private Integer id;
    private String value;

    OpSearchKey(String key){
        this.key = key;
    }

    public OpSearchKey(){
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
