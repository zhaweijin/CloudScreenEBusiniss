package com.hiveview.dianshang.utils;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.hiveview.manager.IPmInstallObserver;
import com.hiveview.manager.PmInstallManager;
import com.hiveview.manager.ScreenManager;
import com.hiveview.manager.SystemInfoManager;
import com.hiveview.manager2.SpFunManager;

/**
 * Created by ThinkPad on 2018/8/13.
 */

public class HiveCoreUtils {
    private static final String TAG = "HiveCoreUtils";
    public static HiveCoreUtils instance;
   // private Context context;
    private boolean isSupportCore2 = false;
    private String mac;
    private String sn;
    private String hv;
    private String sv;
    private String model;
    private String deviceCode;
    private String wlanMac;
    private String version;
    private int softwareVersion=1;

    public HiveCoreUtils(Context context) {
        //this.context = context;
        checkSupportCore2();
        getDeviceInfoFromCore(context);
    }

    /**
     * 静默安装APK
     * @param path    需要安装的APK路径
     * @param context 执行该安装操作的上下文环境
     */
    public void installManager(String path, Context context) {
        if (isSupportCore2) {
            com.hiveview.manager2.PmInstallManager pmInstallManager = com.hiveview.manager2.PmInstallManager.getPmInstallManager(context);
            pmInstallManager.pmInstall(path, new com.hiveview.manager2.IPmInstallObserver() {
                @Override
                public void packageInstalled(String s, int i) throws RemoteException {
                    Log.i(TAG, "baidu install finished");
                }

                @Override
                public IBinder asBinder() {
                    return null;
                }
            }, 0);
        } else {
            try {
                Log.i(TAG, "start install");
                PmInstallManager pmInstallManager = PmInstallManager.getPmInstallManager(context);
                pmInstallManager.pmInstall(path, new IPmInstallObserver() {
                    @Override
                    public void packageInstalled(String s, int i) throws RemoteException {
                        Log.i(TAG, "baidu install finished");
                    }

                    @Override
                    public IBinder asBinder() {
                        return null;
                    }
                }, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getDeviceInfoFromCore(Context context) {
        if (isSupportCore2) {
            com.hiveview.manager2.SystemInfoManager systemInfoManager2 = com.hiveview.manager2.SystemInfoManager.getSystemInfoManager();
            if (null == systemInfoManager2) {
                return;
            } else {
                try {
                    model = systemInfoManager2.getProductModel() == null ? "" : systemInfoManager2.getProductModel().trim();
                    hv = systemInfoManager2.getHWVersion() == null ? "" : systemInfoManager2.getHWVersion().trim();
                    sv = systemInfoManager2.getFirmwareVersion() == null ? "" : systemInfoManager2.getFirmwareVersion().trim();
                    wlanMac = systemInfoManager2.getWMacInfo() == null ? "" : systemInfoManager2.getWMacInfo().trim();
                    mac = systemInfoManager2.getMacInfo() == null ? "" : systemInfoManager2.getMacInfo().trim();
                    sn = systemInfoManager2.getSnInfo() == null ? "" : systemInfoManager2.getSnInfo().trim();
                    model = systemInfoManager2.getProductModel() == null ? "" : systemInfoManager2.getProductModel().trim();
                    softwareVersion= com.hiveview.manager2.SystemInfoManager.getSystemInfoManager().getSofewareInfo();
                    Log.i(TAG, "HiveCore2:" + toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            SystemInfoManager sm = SystemInfoManager.getSystemInfoManager();
            if (null == sm) {
                return;
            }
            try {
                model = sm.getProductModel() == null ? "" : sm.getProductModel().trim();
                hv = sm.getHWVersion() == null ? "" : sm.getHWVersion().trim();
                sv = sm.getFirmwareVersion() == null ? "" : sm.getFirmwareVersion().trim();
                wlanMac = sm.getWMacInfo() == null ? "" : sm.getWMacInfo().trim();
                mac = sm.getMacInfo() == null ? "" : sm.getMacInfo().trim();
                sn = sm.getSnInfo() == null ? "" : sm.getSnInfo().trim();
                model = sm.getProductModel() == null ? "" : sm.getProductModel().trim();
                softwareVersion = SystemInfoManager.getSystemInfoManager().getSofewareInfo();
                Log.i(TAG, "HiveCore1:" + toString());
            }catch (RemoteException e) {
                e.printStackTrace();
                softwareVersion=0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 锁定Home键, true为锁定，false 为释放
     */
    public void lockHomeKey(boolean lock) {
        if (isSupportCore2) {
            SpFunManager.getSpFunManager().deviceProvisioned(lock ? 1 : 0); //HiveCore2 1为锁定，0为释放
        } else {
            ScreenManager.getScreenManager().setHomeShield(lock ? 0 : 1); //HiveCore 0为锁定，1为释放
        }
    }

    /**
     * 检测是否支持HiveCore2
     */
    public void checkSupportCore2() {
        isSupportCore2 = com.hiveview.manager2.SystemInfoManager.getSystemInfoManager().getHiveviewCoreFlag() == 0 ? false : true;
        Log.i(TAG, "flag:" + com.hiveview.manager2.SystemInfoManager.getSystemInfoManager().getHiveviewCoreFlag());
        Log.i(TAG, "isSupportCore2:" + isSupportCore2);
    }

    /**
     * @param context
     * @return 返回HiveCore工具类实例
     */
    public static HiveCoreUtils getInstance(Context context) {
        if (null == instance) {
            instance = new HiveCoreUtils(context);
        }
        return instance;
    }

    public String getMac() {
        return mac;
    }

    public String getSn() {
        return sn;
    }

    public String getHv() {
        return hv;
    }

    public String getSv() { return sv; }

    public int getSoftwareVersion() { return softwareVersion; }

    public String getModel() {
        return model;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public String getWlanMac() {
        return wlanMac;
    }

    @Override
    public String toString() {
        return "HiveCoreUtils{" +
                "mac='" + mac + '\'' +
                ", sn='" + sn + '\'' +
                ", hv='" + hv + '\'' +
                ", sv='" + sv + '\'' +
                ", model='" + model + '\'' +
                ", deviceCode='" + deviceCode + '\'' +
                ", wlanMac='" + wlanMac + '\'' +
                '}';
    }
}
