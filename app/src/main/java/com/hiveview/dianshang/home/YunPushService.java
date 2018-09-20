package com.hiveview.dianshang.home;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.utils.HiveCoreUtils;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.manager.IPmInstallObserver;
import com.hiveview.manager.PmInstallManager;
import com.hiveview.manager.SystemInfoManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carter on 6/26/17.
 */

public class YunPushService extends Service{

    private String tag = "YunPushService";
    private InstalledReceiver installedReceiver;

    private final String YUNPUSH_10PLUS = "YunPushServer_1.01.11_201709281558_1.0+.apk";
    private final String YUNPUSH_10S = "YunPushServer_1.01.11_201709281558_1.0s_sign.apk";
    private final String YUNPUSH_905X = "YunPushServer_1.01.11_201709281558_905x_signed.apk";
    private final String YUNPUSH_10TONGWEI = "YunPushServer_1.01.11_201710091423_tongwei_sign.apk";
    private final String YUNPUSH_RK = "YunPushServer_0817_signed.apk";

    private String yunPush_apk_path;
    private final int YUNPUSH_VERSION_CODE = 11;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(intent==null)
            return;
        regeditBrocast();

        if(!Utils.isPkgInstalled(this,ConStant.YUNPUSH_PACKAGENAME) ||
                (Utils.isPkgInstalled(this,ConStant.YUNPUSH_PACKAGENAME) && getVersionCode(ConStant.YUNPUSH_PACKAGENAME)<YUNPUSH_VERSION_CODE)){
            copyInstallFile();
        }else{
            //检测是否启动长连接服务app
            if(!Utils.isServiceWorked(this,ConStant.Netty_ServiceName)){
                Utils.print(tag,"start netty server");
                Intent intent2 = new Intent();
                intent2.setComponent(new ComponentName("com.hiveview.yunpush.server","com.hiveview.yunpush.server.service.OpenNettyService"));
                startService(intent2);
            }
        }

        Utils.print(tag,"end");

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


/*
* 因更换hiveviewcore包，以下方法暂时未使用
* */
    private void installAPK(String path){
        try{
            Utils.print(tag,"start install");
            this.yunPush_apk_path = path;
            PmInstallManager pmInstallManager = PmInstallManager.getPmInstallManager(YunPushService.this);
            pmInstallManager.pmInstall(path, new IPmInstallObserver() {
                @Override
                public void packageInstalled(String s, int i) throws RemoteException {
                    Utils.print(tag,"baidu install finished");
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


     private void copyInstallFile(){
         Utils.print(tag,"copyInstallFile");
         Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
             @Override
             public void call(Subscriber<? super String> subscriber) {
                 String filepath = copyYunPushSignApp();
                 subscriber.onNext(filepath);
             }
         });
         observable.subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Subscriber<String>() {
                     @Override
                     public void onCompleted() {

                     }

                     @Override
                     public void onError(Throwable e) {

                     }

                     @Override
                     public void onNext(String s) {
                         Utils.print(tag,">>>>>"+s);
                         Utils.print(tag,"start install");
                         yunPush_apk_path = s;
                         HiveCoreUtils.getInstance(YunPushService.this).installManager(s,YunPushService.this);
                         //installAPK(s);
                     }
                 });

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




    private void regeditBrocast(){
        installedReceiver = new InstalledReceiver();
        IntentFilter filter = new IntentFilter();

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
                Utils.print(tag, "installed :" + packageName);
                if(packageName.contains(ConStant.YUNPUSH_PACKAGENAME)){
                    try{
                        if(!yunPush_apk_path.equals(""))
                            new File(yunPush_apk_path).delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //检测是否启动长连接服务app
                    if(!Utils.isServiceWorked(YunPushService.this,ConStant.Netty_ServiceName)){
                        Utils.print(tag,"start netty server");
                        Intent intent2 = new Intent();
                        intent2.setComponent(new ComponentName("com.hiveview.yunpush.server","com.hiveview.yunpush.server.service.OpenNettyService"));
                        startService(intent2);
                    }

                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.print(tag,"onDestroy");
        unRegeditBrocast();
    }



    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public int getVersionCode(String packageName) {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }



    /**
     * 拷贝asset 文件
     * @return
     */
    private String copyYunPushSignApp() {
        Utils.print(tag,"copyYunPushSignApp");
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        String filepath="";
        try {
          /*  SystemInfoManager manager = SystemInfoManager.getSystemInfoManager();
            String version = manager.getFirmwareVersion();*/
            String version = HiveCoreUtils.getInstance(this).getSv();
            String[] vers = version.split("\\.");
            String filename;
            if(vers[1].equals("B0")){ //rk
                filename = YUNPUSH_RK;
            }else{
                if(Integer.parseInt(vers[1])==70){ //1.0s
                    filename = YUNPUSH_10S;
                }else if(Integer.parseInt(vers[1])==40){ //1.0+
                    filename = YUNPUSH_10PLUS;
                }else if(Integer.parseInt(vers[1])==30 || Integer.parseInt(vers[1])==31){//1.0tw or 1.0cw
                    filename = YUNPUSH_10TONGWEI;
                }else if(Integer.parseInt(vers[1])==72 || Integer.parseInt(vers[1])==74 ||
                        Integer.parseInt(vers[1])==76 || Integer.parseInt(vers[1])==78){
                    //1.0x,3.0x,3.0vc,4.0s
                    filename = YUNPUSH_905X;
                }else {
                    return "";
                }
            }

            Utils.print(tag,"filename=="+filename);
            filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" +filename;
            if(new File(filepath).exists()){
                Utils.print(tag,filepath+" file exist");
                return filepath;
            }

            in = assetManager.open(filename);
            File outFile = new File(Environment.getExternalStorageDirectory(), filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        Utils.print(tag,"copy finish=="+filepath);
        return filepath;
    }


}
