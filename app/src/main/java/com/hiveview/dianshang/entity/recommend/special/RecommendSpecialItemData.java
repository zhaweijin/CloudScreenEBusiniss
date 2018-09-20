package com.hiveview.dianshang.entity.recommend.special;



/**
 * Created by carter on 5/19/17.
 */

public class RecommendSpecialItemData  {
    /**
     * 专题名称.
     */
    private String name;

    /**
     * 专题编码.
     */
    private String specSn;

    /**
     * 专题里图片.
     */
    private String imgUrl;

    /**
     * 专题封面图,显示专题列表
     */
    private String coverImgUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecSn() {
        return specSn;
    }

    public void setSpecSn(String specSn) {
        this.specSn = specSn;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }
}
