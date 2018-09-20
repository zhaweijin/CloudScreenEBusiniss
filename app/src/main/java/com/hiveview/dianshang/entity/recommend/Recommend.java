package com.hiveview.dianshang.entity.recommend;

import java.util.List;

/**
 * Created by carter on 5/19/17.
 */

public class Recommend {
    private Boolean isSpec;

    /**
     * 所有商品的入口底图
     */
    private String allGoodsImage;

    private List<RecommendItemData> operationMatrixItemList;
    private String templateDescription;
    /**
     * 运营模板名称
     */
    private String templateName;
    private long updateTime;
    /**
     * 编号
     */
    private String templateSn;

    public String getAllGoodsImage() {
        return allGoodsImage;
    }

    public void setAllGoodsImage(String allGoodsImage) {
        this.allGoodsImage = allGoodsImage;
    }

    public Boolean getSpec() {
        return isSpec;
    }

    public void setSpec(Boolean spec) {
        isSpec = spec;
    }

    public List<RecommendItemData> getOperationMatrixItemList() {
        return operationMatrixItemList;
    }

    public void setOperationMatrixItemList(List<RecommendItemData> operationMatrixItemList) {
        this.operationMatrixItemList = operationMatrixItemList;
    }

    public String getTemplateDescription() {
        return templateDescription;
    }

    public void setTemplateDescription(String templateDescription) {
        this.templateDescription = templateDescription;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getTemplateSn() {
        return templateSn;
    }

    public void setTemplateSn(String templateSn) {
        this.templateSn = templateSn;
    }
}
