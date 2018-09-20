package com.hiveview.dianshang.auction.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by zwj on 3/19/18.
 */

@DatabaseTable(tableName = DatabaseHelper.ACUTION_TABLE_NAME)
public class AcutionBean {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "sn")
    public String sn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}