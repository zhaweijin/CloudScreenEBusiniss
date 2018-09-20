package com.hiveview.dianshang.utils;

import android.content.Context;
import android.util.Log;

import com.hiveview.manager.SystemInfoManager;

/**
 * Created by carter on 4/18/17.
 */

public class DeviceInfo {

    private static final String TAG = "DeviceInfo";
    private String mac;
    private String sn;
    private String hv;
    private String sv;
    private String model;
    private String deviceCode;
    private String wlanMac;
    private boolean isSupportCore2 = false;

    public static DeviceInfo instance;
    private Context mContext;

    public static DeviceInfo getInstance(Context context) {
        if (null == instance) {
            instance = new DeviceInfo(context);
        }
        return instance;
    }

    DeviceInfo(Context context) {
        checkSupportCore2();
        getDeviceInfoFromCore(context);
    }

    /**
     * ºÏ≤‚ «∑Ò÷ß≥÷HiveCore2
     */
    public void checkSupportCore2() {
        isSupportCore2 = com.hiveview.manager2.SystemInfoManager.getSystemInfoManager().getHiveviewCoreFlag() == 0 ? false : true;
        Log.i(TAG, "flag:" + com.hiveview.manager2.SystemInfoManager.getSystemInfoManager().getHiveviewCoreFlag());
        Log.i(TAG, "isSupportCore2:" + isSupportCore2);
    }


    private void getDeviceInfoFromCore(Context context) {

        if (isSupportCore2) {
            com.hiveview.manager2.SystemInfoManager systemInfoManager2 = com.hiveview.manager2.SystemInfoManager.getSystemInfoManager();
            if (null == systemInfoManager2) {
                return;
            }
            try {
                model = systemInfoManager2.getProductModel() == null ? "" : systemInfoManager2.getProductModel().trim();
                hv = systemInfoManager2.getHWVersion() == null ? "" : systemInfoManager2.getHWVersion().trim();
                sv = systemInfoManager2.getFirmwareVersion() == null ? "" : systemInfoManager2.getFirmwareVersion().trim();
                wlanMac = systemInfoManager2.getWMacInfo() == null ? "" : systemInfoManager2.getWMacInfo().trim();
                mac = systemInfoManager2.getMacInfo() == null ? "" : systemInfoManager2.getMacInfo().trim();
                sn = systemInfoManager2.getSnInfo() == null ? "" : systemInfoManager2.getSnInfo().trim();
                model = systemInfoManager2.getProductModel() == null ? "" : systemInfoManager2.getProductModel().trim();
                Log.i(TAG, "HiveCore2:" + toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
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
                Log.i(TAG, "HiveCore1:" + toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


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

    public String getSv() {
        return sv;
    }

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
