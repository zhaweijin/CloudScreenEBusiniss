package com.hiveview.dianshang.upgrade.manager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.IBinder;

import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import com.hiveview.manager.IPmDeleteObserver;
import com.hiveview.manager.IPmInstallObserver;
import com.hiveview.manager.PmInstallManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by carter on 6/26/17.
 */

public class UpgradeManagerService extends Service{

    private String tag = "UpgradeManagerService";
    private InstalledReceiver installedReceiver;
    private String path="";

    public final static int INSTALL = 0;
    public final static int UNINSTALL = 1;
    private final int NULL = -1;

    private String dianshangFileName = "DianshangCore-1.2.0-20171109.apk";

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        regeditBrocast();

        if(intent.getIntExtra("status",NULL)==INSTALL){ //直接安装
            Log.v(tag,"install");
            installDianshang();
        }else if(intent.getIntExtra("status",NULL)==UNINSTALL){//先卸载
            Log.v(tag,"uninstall");
            uninsallAPK(UpgradeManager.DIANSHANG_PACKAGENAME);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void sendCloseMessage(){
        Intent mIntent = new Intent();
        mIntent.setAction(UpgradeManager.ACTION_INSTALL_LISTENER);
        sendBroadcast(mIntent);

        startAppFromName(UpgradeManagerService.this,UpgradeManager.DIANSHANG_PACKAGENAME,UpgradeManager.DIANSHANG_ACTIVITYNAME);

        stopSelf();
    }

    /**
     * 根据app包名和类名启动
     * @param context
     * @param packageName
     * @param activityName
     */
    public static void startAppFromName(Context context,String packageName,String activityName){
        Intent mIntent = new Intent();
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        ComponentName comp = new ComponentName(packageName,activityName);
        mIntent.setComponent(comp);
        context.startActivity(mIntent);
    }

    /**
     * 安装apk
     * @param path
     */
    private void installAPK(String path){
        try{
            Log.v(tag,"start install="+path);
            PmInstallManager pmInstallManager = PmInstallManager.getPmInstallManager(UpgradeManagerService.this);
            pmInstallManager.pmInstall(path, new IPmInstallObserver() {
                @Override
                public void packageInstalled(String s, int i) throws RemoteException {
                }

                @Override
                public IBinder asBinder() {
                    return null;
                }
            },0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 安装电商
     */
    private void installDianshang(){
        new Thread(() -> {
            try{
                String filepath = copyApkAssets();
                path = filepath;
                installAPK(path);
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }



    /**
     * 拷贝asset 文件
     * @return
     */
    private String copyApkAssets() {
        Log.v(tag,"copyApkAssets");
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        String filepath="";
        try {
            String filename = dianshangFileName;
            filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" +filename;
            if(new File(filepath).exists()){
                Log.v(tag,filepath+" file exist");
                return filepath;
            }

            in = assetManager.open(filename);
            File outFile = new File(Environment.getExternalStorageDirectory(), filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(tag,"copy finish=="+filepath);
        return filepath;
    }


    /**
     * 拷贝文件
     * @param in
     * @param out
     * @throws IOException
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * 卸载apk
     * @param packageName
     */
    private void uninsallAPK(String packageName){
        try{

            PmInstallManager pmInstallManager = PmInstallManager.getPmInstallManager(UpgradeManagerService.this);
            pmInstallManager.pmUninstall(packageName, new IPmDeleteObserver() {
                @Override
                public void packageDeleted(String s, int i) throws RemoteException {
                    Log.v("main","package delete sucess");
                }

                @Override
                public IBinder asBinder() {
                    return null;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void regeditBrocast(){
        installedReceiver = new InstalledReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addDataScheme("package");

        registerReceiver(installedReceiver, filter);
    }

    private void unRegeditBrocast(){
        if(installedReceiver != null) {
            unregisterReceiver(installedReceiver);
        }
    }


    public class InstalledReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {     // install
                String packageName = intent.getDataString();
                Log.v(tag, "installed :" + packageName);
                try{
                    if(!path.equals(""))
                        new File(path).delete();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(packageName.equals("package:"+UpgradeManager.DIANSHANG_PACKAGENAME)){
                    sendCloseMessage();
                }
            }else if(intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")){ //
                String packageName = intent.getDataString();
                Log.v(tag, "uninstalled :" + packageName);
                if(packageName.equals("package:"+UpgradeManager.DIANSHANG_PACKAGENAME)){
                    installDianshang();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(tag,"onDestroy");
        unRegeditBrocast();
    }

}
