package com.hiveview.dianshang.entity.address;

/**
 * Created by carter on 4/11/17.
 */

public class ChildData {

    /**
     * 区域唯一编码
     */
    private String code;
    /**
     * 区域名字
     */
    private String name;
    private String id;
    private int level;
    private String parentCode;
    /**
     * 省,市,区路径编码
     */
    private String treePath;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }
}
