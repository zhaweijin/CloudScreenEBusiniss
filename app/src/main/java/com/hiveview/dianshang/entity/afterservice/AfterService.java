package com.hiveview.dianshang.entity.afterservice;

import java.util.List;

/**
 * Created by carter on 5/25/17.
 */

public class AfterService {

    private boolean empty;
    private int firstResult;
    private boolean hasPrevious;
    private int pageIndex;
    private int pageSize;
    private List<AfterServiceRecord> records;
    private int totalCount;

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<AfterServiceRecord> getRecords() {
        return records;
    }

    public void setRecords(List<AfterServiceRecord> records) {
        this.records = records;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
