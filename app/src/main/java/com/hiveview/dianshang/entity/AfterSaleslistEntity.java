package com.hiveview.dianshang.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by Gavin on 2017/5/10.
 */

public class AfterSaleslistEntity {
    private String name;
    private Drawable pageLeft;
    private String itemName;
    private Drawable pageRight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getPageLeft() {
        return pageLeft;
    }

    public void setPageLeft(Drawable pageLeft) {
        this.pageLeft = pageLeft;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Drawable getPageRight() {
        return pageRight;
    }

    public void setPageRight(Drawable pageRight) {
        this.pageRight = pageRight;
    }
}
