/**
 * @Title NetworkUtil.java
 * @Package com.hiveview.cloudscreen.video.utils
 * @author haozening
 * @date 2014年9月20日 上午10:06:16
 * @Description
 * @version V1.0
 */
package com.hiveview.dianshang.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 网络连接状态工具类
 */
public class NetworkUtil {

	public static final int WIFI_MAX_LEVEL = 5;
	public static final int CHECK_NETWORK_STATE_DELAY = 5000;
	private ConnectivityManager connectivityManager;
	private WifiManager wifiManager;
	private NetworkInfo networkInfo;
	private WifiInfo wifiInfo;

	public NetworkUtil(Context context) {
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		networkInfo = connectivityManager.getActiveNetworkInfo();
		wifiInfo = wifiManager.getConnectionInfo();
	}

	/**
	 * 判断网络是否连接
	 *
	 */
	public boolean isConnected() {
		if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是本地连接
	 *
	 */
	@SuppressLint("InlinedApi")
	public boolean isEthernet() {
		if (null == networkInfo) {
			return false;
		}
		return ConnectivityManager.TYPE_ETHERNET == networkInfo.getType();
	}

	/**
	 * 判断是否是WIFI连接
	 *
	 */
	public boolean isWifi() {
		if (null == networkInfo) {
			return false;
		}
		return ConnectivityManager.TYPE_WIFI == networkInfo.getType();
	}

	/**
	 * 获取WIFI的当前强度
	 *
	 */
	public int getWifiLevel() {
		if (null == wifiInfo) {
			return 0;
		} else {
			return WifiManager.calculateSignalLevel(wifiInfo.getRssi(), WIFI_MAX_LEVEL);
		}
	}
	public static boolean hasNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}


	/**
	 * 获取网络状态
	 **/
	public static boolean checkNetConnect(Context context) {
		ConnectivityManager mConnectivityManager;
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isAvailable()) {
			if (((netInfo.getType() == ConnectivityManager.TYPE_WIFI
					|| netInfo.getType() == ConnectivityManager.TYPE_ETHERNET))) {//网络连接成功
				return true;
			}
		}
		return false;
	}

}
