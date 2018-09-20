package com.hiveview.dianshang.auction.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hiveview.dianshang.utils.Utils;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zwj on 3/19/18.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DB_NAME = "AcutionStore.db";
    public static final int DB_VERSION = 1;
    public static final String ACUTION_TABLE_NAME="acution";

    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Utils.print("table","oncreate");
            TableUtils.createTable(connectionSource, AcutionBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        System.out.println("MyDatabaseHelper.onUpgrade oldVersion=" + oldVersion + "  newVersion=" + newVersion);
        try {
            /*switch (oldVersion) {
                case 1:
                    getDao(MyBean.class).executeRaw("alter table Book add column book_type varchar(20)");
                    //在数据库版本1的下一版本，Book表中新添加了 book_type 字段
                case 2:
                    // TableUtils.createTable(connectionSource, MyBean2.class);
                    //在数据库版本2的下一版本，新增加了一张表
                default:
                    break;
            }*/


            //显然这样处理比较暴力
            //TableUtils.dropTable(connectionSource, MyBean.class, true);
            //onCreate(sqLiteDatabase, connectionSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }
        return instance;
    }

    /**
     * 重新创建数据库
     */
    public void reCreateTable(){
         try {
             TableUtils.createTable(connectionSource, AcutionBean.class);
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    private Map<String, Dao> daos = new HashMap<>();

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        try {
            String className = clazz.getSimpleName();
            if (daos.containsKey(className)) {
                dao = daos.get(clazz);
            }
            if (dao == null) {
                dao = super.getDao(clazz);
                daos.put(className, dao);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dao;
    }


    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }



    /**
     * 判断数据库中指定表的指定字段是否存在
     * @param db
     * @param strTableName 指定表名称
     * @param strFieldName 执行字段名称
     * @return
     */
    public boolean checkColumnExist(String tableName, String columnName) {
        boolean result = false ;
        Cursor cursor = null ;
        try{
            //查询一行
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1 ;
        }catch (Exception e){
            Log.e("e","checkColumnExists..." + e.getMessage()) ;
        }finally{
            if(null != cursor && !cursor.isClosed()){
                cursor.close() ;
            }
        }

        return result ;
    }


    //删除某一个表
    public void dropTable(String tName){
        getReadableDatabase().execSQL("drop table "+tName);
    }

}