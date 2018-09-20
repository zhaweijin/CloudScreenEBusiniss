package com.hiveview.dianshang.home;

import android.app.Service;
import android.content.BroadcastReceiver;
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

public class InstallService extends Service{

    private String tag = "InstallService";
    private InstalledReceiver installedReceiver;
    private String path="";

    public static final int ENTER = 1;
    public static final int EXIT = 2;

    private String oldDefaultInput = "";
    private boolean switchFlag = false;

    private String INPUT_KEY="input_key_name";
    private String SWICH_KEY="input_swich_key";


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(intent==null)
            return;
        regeditBrocast();


        if(intent.getIntExtra("status",ENTER)==ENTER){
            Utils.print(tag,"enter");
            startInstall();
        }else if(intent.getIntExtra("status",ENTER)==EXIT){
            oldDefaultInput = (String) SPUtils.get(this,INPUT_KEY,"");
            switchFlag=(Boolean) SPUtils.get(this,SWICH_KEY,false);
            Utils.print(tag,"exit-package="+oldDefaultInput+",switchFlag="+switchFlag);
            if(switchFlag && !oldDefaultInput.equals("") && !oldDefaultInput.equals(ConStant.BAIDU_INPUT)){
                Utils.print(tag,"recovery input method");
                switchInputMethod(InstallService.this,oldDefaultInput);
                SPUtils.putApply(this,SWICH_KEY,false); //恢复不用切换
            }
            stopSelf();
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



    private void startInstall(){

        Utils.print(tag,"startInstall");
        if(Utils.isPkgInstalled(this,ConStant.BAIDU_PACKAGENAME)){
            ContentResolver resolver = getContentResolver();
            String inputMethod = Settings.Secure.getString(resolver,
                    Settings.Secure.DEFAULT_INPUT_METHOD);
            Utils.print(tag,"default="+inputMethod);
            if(!inputMethod.equals(ConStant.BAIDU_INPUT)){
                oldDefaultInput = inputMethod;
                switchFlag = true;
                SPUtils.putApply(this,INPUT_KEY,oldDefaultInput);
                SPUtils.putApply(this,SWICH_KEY,switchFlag);
                switchInputMethod(InstallService.this,ConStant.BAIDU_INPUT);
            }
            Utils.print(tag,"startInstall2");
            stopSelf();
            return;
        }
        Utils.print(tag,"startInstall3");

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String filepath = copyBaiduApkAssets();
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
                        path = s;
                        HiveCoreUtils.getInstance(InstallService.this).installManager(s,InstallService.this);
                       // installAPK(s);
                    }
                });




    }

    /*
    * 因更换hiveviewcore包，以下方法暂时未使用
    * */
    private void installAPK(String path){
        try{
            Utils.print(tag,"start install");
            this.path = path;
            PmInstallManager pmInstallManager = PmInstallManager.getPmInstallManager(InstallService.this);
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


    /**
     * 拷贝asset 文件
     * @return
     */
    private String copyBaiduApkAssets() {
        Utils.print(tag,"copy");
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        String filepath="";
        try {

            String filename = "BaiduInput_Android_4-0-8-24_1000.apk";
            filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" +filename;
            if(new File(filepath).exists()){
                Utils.print(tag,"file exist");
                return filepath;
            }

            in = assetManager.open(filename);
            File outFile = new File(Environment.getExternalStorageDirectory(), filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.print(tag,"copy finish=="+filepath);
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
                if(packageName.contains(ConStant.BAIDU_PACKAGENAME)){
                    ContentResolver resolver = context.getContentResolver();
                    String inputMethod = Settings.Secure.getString(resolver,
                            Settings.Secure.DEFAULT_INPUT_METHOD);
                    oldDefaultInput=inputMethod;
                    switchFlag = true;
                    SPUtils.putApply(context,INPUT_KEY,oldDefaultInput);
                    SPUtils.putApply(context,SWICH_KEY,switchFlag);
                    switchInputMethod(InstallService.this,ConStant.BAIDU_INPUT);
                    try{
                        if(!path.equals(""))
                            new File(path).delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    stopSelf();
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

    private void switchInputMethod(Context context, String inputmethod){
        try{
            ContentResolver resolver = context.getContentResolver();
            Utils.print(tag,"switch="+inputmethod);
            String inputMethod = Settings.Secure.getString(resolver,
                    Settings.Secure.DEFAULT_INPUT_METHOD);
            if(inputmethod!=null &&!inputMethod.equalsIgnoreCase(inputmethod)){
                Settings.Secure.putString(resolver,
                        Settings.Secure.DEFAULT_INPUT_METHOD, inputmethod);
                Settings.Secure.putString(resolver,
                        Settings.Secure.ENABLED_INPUT_METHODS, inputmethod);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
