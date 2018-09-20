package com.hiveview.dianshang.upgrade.manager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class UpgradeManager extends AppCompatActivity {


    public final static String DIANSHANG = "com.hiveview.dianshang";

    public static final String DIANSHANG_PACKAGENAME = "com.hiveview.dianshang";
    public static final String DIANSHANG_ACTIVITYNAME ="com.hiveview.dianshang.home.Welcome";

    private ListenerReceiver listenerReceiver=null;
    public static final String ACTION_INSTALL_LISTENER = "com.hiveview.dianshang.upgrade.manager.install.listener";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.v("main","version="+getVersion(DIANSHANG));


        if(isPkgInstalled(this,DIANSHANG)){
            if(getVersion(DIANSHANG).startsWith("1.1") || getVersion(DIANSHANG).startsWith("1.0")) {
                //先卸载电商，启动服务卸载，然后再安装
                Log.v("main","start service");
                Intent mIntent = new Intent(this,UpgradeManagerService.class);
                mIntent.putExtra("status",UpgradeManagerService.UNINSTALL);
                startService(mIntent);
            }else{
                startAppFromName(this,DIANSHANG_PACKAGENAME,DIANSHANG_ACTIVITYNAME);
                finish();
                return;
            }
        }else{
            //盒子没有安装，直接先安装电商
            Intent mIntent = new Intent(this,UpgradeManagerService.class);
            mIntent.putExtra("status",UpgradeManagerService.INSTALL);
            startService(mIntent);
        }

        setListenerReceiver();

    }


    /**
     * 判断包是否存在
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isPkgInstalled(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;

        }
    }



    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion(String packageName) {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 根据app包名和类名启动
     * @param context
     * @param packageName
     * @param activityName
     */
    public static void startAppFromName(Context context,String packageName,String activityName){
        Intent mIntent = new Intent();

        ComponentName comp = new ComponentName(packageName,activityName);
        mIntent.setComponent(comp);
        context.startActivity(mIntent);
    }


    public class ListenerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("main","action");
            finish();
        }
    }


    private void setListenerReceiver(){
        listenerReceiver=new ListenerReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(ACTION_INSTALL_LISTENER);
        registerReceiver(listenerReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(listenerReceiver);
    }
}
