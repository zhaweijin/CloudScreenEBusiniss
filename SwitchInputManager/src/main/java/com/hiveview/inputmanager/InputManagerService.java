package com.hiveview.inputmanager;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * 此文件只做切输入法的用处
 * Created by carter on 6/26/17.
 */

public class InputManagerService extends Service {

    private String tag = "InputManagerService";


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent == null)
            return;


        Utils.print(tag, "enter");
        String readySwitchPackage = intent.getStringExtra(Utils.ACTION_INPUT_METHOD_PACKAGE_NAME);
        String readySwitchClass = intent.getStringExtra(Utils.ACTION_INPUT_METHOD_CLASS_NAME);
        switchToTargetInput(readySwitchPackage,readySwitchClass);
        stopSelf();
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


    private void switchToTargetInput(String packageName,String className) {
        Utils.print(tag, "switchInputManager");
        if (Utils.isPkgInstalled(this, packageName)) {
            Utils.print(tag, "switch input package exist");
            switchInputMethod(this, className);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.print(tag, "onDestroy");
    }

    private void switchInputMethod(Context context, String inputmethod) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Utils.print(tag, "switch=" + inputmethod);
            String inputMethod = Settings.Secure.getString(resolver,
                    Settings.Secure.DEFAULT_INPUT_METHOD);
            if (inputmethod != null && !inputMethod.equals(inputmethod)) {
                Settings.Secure.putString(resolver,
                        Settings.Secure.DEFAULT_INPUT_METHOD, inputmethod);
                Settings.Secure.putString(resolver,
                        Settings.Secure.ENABLED_INPUT_METHODS, inputmethod);
            }
            Utils.print(tag, "switchInput finish");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
