package com.hiveview.dianshang.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hiveview.dianshang.auction.database.AcutionDao;
import com.hiveview.dianshang.auction.database.DatabaseHelper;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.statistics.HSApi;

/**
 * Created by kaede on 2015/10/20.
 */
public class EBusinessApplication extends Application {

	private static EBusinessApplication instance;

	private String tag = "EBusinessApplication";

	private static Context mContext;
	private static HSApi api=null;
	@Override
	public void onCreate() {
		super.onCreate();

		mContext = getApplicationContext();
		api=HSApi.getInstance(this);

		Utils.print(tag,"start");


		Fresco.initialize(this, ImagePipelineConfigUtils.getDefaultImagePipelineConfig(this));

		checkDatabaseError();

	}


	@Override
	public void attachBaseContext(Context base) {
		MultiDex.install(base);
		super.attachBaseContext(base);
	}

	public static EBusinessApplication getInstance() {
		return instance;
	}

	public static Context getContext(){
		return mContext;
	}

	public static void setmContext(Context mContext) {
		EBusinessApplication.mContext = mContext;
	}


	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static HSApi getHSApi(){
		return api;
	}


	/**
	 * 数据库出错，复位
	 */
	private void checkDatabaseError(){
		if(!DatabaseHelper.getHelper(mContext).checkColumnExist(DatabaseHelper.ACUTION_TABLE_NAME,"id")){
			Utils.print(tag,"acution table error");
			DatabaseHelper.getHelper(mContext).dropTable(DatabaseHelper.ACUTION_TABLE_NAME);
			DatabaseHelper.getHelper(mContext).reCreateTable();
			AcutionDao dao = AcutionDao.getInstance(mContext);
			Utils.print(tag,"count="+dao.queryCount());
		}
	}
}
