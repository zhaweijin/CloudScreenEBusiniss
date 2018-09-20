package com.hiveview.dianshang.auction.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwj on 3/19/18.
 */

public class AcutionDao {
    private DatabaseHelper mHelper;
    private Dao<AcutionBean, Integer> dao;
    private Context mContext;
    private static AcutionDao instance;

    protected AcutionDao(Context context) {
        this.mContext = context;
        try {
            mHelper = DatabaseHelper.getHelper(mContext);
            dao = mHelper.getDao(AcutionBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static AcutionDao getInstance(Context context) {
        if (instance == null) {
            synchronized (AcutionDao.class) {
                if (instance == null) {
                    instance = new AcutionDao(context);
                }
            }
        }
        return instance;
    }


    public void insert(AcutionBean bean) {
        try {
            dao.create(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    public void delete(String sn) {
        ArrayList<AcutionBean> list = null;
        try {
            list = (ArrayList<AcutionBean>) dao.queryForEq("sn", sn);
            if (list != null) {
                for (AcutionBean bean : list) {
                    dao.delete(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public long queryCount() {
        long number = 0;
        try {
            number = dao.queryBuilder().countOf();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }


    public List<AcutionBean> querySn(String sn) {
        List<AcutionBean> list = null;
        try {
            list = dao.queryBuilder().where().eq("sn", sn).query();//上述相当与：select * from Book where name = name1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AcutionBean> queryAll() {
        List<AcutionBean> list = null;
        try {
            list = dao.queryForAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /*public void close(){
        mHelper.close();
    }*/
}