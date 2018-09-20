package com.hiveview.inputmanager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by carter on 11/7/17.
 */

public class Utils {

    public static final String ACTION_INPUT_METHOD_PACKAGE_NAME ="input_method_package_name";
    public static final String ACTION_INPUT_METHOD_CLASS_NAME ="input_method_class_name";
    /**
     * 打印测试
     * @param tag
     * @param value
     */
    public static void print(String tag,String value){
        Log.v(tag,value);
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
}
