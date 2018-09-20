package com.hiveview.dianshang.entity.category.commodity;

/**
 * Created by carter on 5/16/17.
 */

public class LevelCategoryRecored {

    /**
     * 几级分类
     */
    private int grade;
    /**
     * 图片URL
     */
    private String imgUrl;
    /**
     * 分类名称
     */
    private String name;

    private String orders;
    /**
     * 父类基本sn
     */
    private String parentSn;
    /**
     * 是否下线
     */
    private boolean onlineStatus;
    /**
     * 分类sn
     */
    private String categorySn;
    /**
     * 分类路径
     */
    private String treePath;

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getParentSn() {
        return parentSn;
    }

    public void setParentSn(String parentSn) {
        this.parentSn = parentSn;
    }

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getCategorySn() {
        return categorySn;
    }

    public void setCategorySn(String categorySn) {
        this.categorySn = categorySn;
    }

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }
}
