package com.hiveview.dianshang.entity.category.commodity;

/**
 * Created by carter on 5/16/17.
 */

public class OneLevelCategoryRecored {

    private int grade;
    private String imgUrl;
    private String name;
    private String orders;
    private String categorySn;
    private String treePath;
    private String description;

    //右边广告图片
    private String headImgUrl;

    //左边顶部名称图标
    private String navImgUrl;

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

    public String getCategorySn() {
        return categorySn;
    }

    public void setCategorySn(String categorySn) {
        this.categorySn = categorySn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getNavImgUrl() {
        return navImgUrl;
    }

    public void setNavImgUrl(String navImgUrl) {
        this.navImgUrl = navImgUrl;
    }
}
