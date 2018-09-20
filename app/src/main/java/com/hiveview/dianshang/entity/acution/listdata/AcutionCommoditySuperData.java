package com.hiveview.dianshang.entity.acution.listdata;

import com.hiveview.dianshang.entity.acution.common.AcutionItemData;

import java.util.List;

/**
 * Created by carter on 5/16/17.
 */

public class AcutionCommoditySuperData {


    private int totalCount;

    private List<AcutionItemData> records;


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<AcutionItemData> getRecords() {
        return records;
    }

    public void setRecords(List<AcutionItemData> records) {
        this.records = records;
    }
}
