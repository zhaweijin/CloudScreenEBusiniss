package com.hiveview.dianshang.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.InputSignBean;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by carter on 4/10/17.
 */

public class Utils {

    private final static String tag ="Utils";
    private static SimpleDateFormat sf = null;

    /**
     * 打印测试
     * @param tag
     * @param value
     */
    public static void print(String tag,String value){
//        Log.v(tag,value);
    }

    /**
     * 检查输入是否为空
     *
     * @param values String[]
     * @return Returns true if the values of this string[] is empty ro where are empty
     */
    public static int checkStringIsEmpty(String... values) {
        int location = -1;
        if (values.length == 1) {
            return values[0].isEmpty() ? 0 : -1;
        }
        for (int i = 0, size = values.length; i < size; i++) {
            if (values[i].isEmpty()) {
                return i;
            }
        }
        return location;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * 获取屏幕高
     * @param context
     * @return
     */
    public static int getScrrenH(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return  wm.getDefaultDisplay().getHeight();
    }

    /**
     * 获取屏幕宽
     * @param context
     * @return
     */
    public static int getScreenW(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return  wm.getDefaultDisplay().getWidth();
    }


    public static int conver(Context context,int n){
        if(getScreenW(context)==1920){
            return n;
        }else if(getScreenW(context)==1280){
            return (int) Math.ceil((double)n/1.5);
        }
        return n;
    }


    /**
     * 价格格式化,小数点后两位
	 * 将显示的价格数据在小数部分不为0的情况下保留小数部分，为0时去除
     * @param price
     * @return
     */
    public static String getPrice(double price){
		DecimalFormat df = new DecimalFormat("#####0.00");
		//取个位数加小数部分赋值给s
        String s = df.format(price);
        //Log.e("Utils>>>s",s);
        //第一次出现的"."在此字符串中的索引
        int begin = s.indexOf(".");
        //Log.e("Utils>>>",String.valueOf(begin));

        //截取该字符串中小数点后的字符串，并赋值给s1
        String s1 = s.substring(begin+1,begin+3);
        //Log.e("Utils>>>s1",s1);

        //如果截取字符串为00，即小数部分为0，则舍去小数部分，只留下整数部分
        if (s1.equals("00")){
            int index = (int) price;
            String s2=String.valueOf(index);
            return s2;
        }else if(s1.substring(1,2).equals("0")){//如果小数部分仅百分位为0，则去除百分位，保留十分位输出数据
            String s3 = String.valueOf(price);
            return s3.substring(0,s3.length());
        }else  {//否则返回整个数
            return df.format(price);
        }

        //DecimalFormat df = new DecimalFormat("######0.00");
        //return df.format(price)+"";

		/*
		//任何情况下，保留两位小数
		DecimalFormat decimalFormat=new DecimalFormat("#,##0.00");
		return decimalFormat.format(price)+"";
        */

		//return subZeroAndDot(price+"");
    }


    /**
     * 使用java正则表达式去掉多余的.与0
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    public static Map<String,Object> buildMap(org.json.JSONObject json){
        Map<String,Object> map = new HashMap<>();
        try{
            Iterator<String> keys = json.keys();
            while(keys.hasNext()){
                String key = keys.next() ;
                Object value = json.get(key);
                //Utils.print(tag,"key="+key);
                //Utils.print(tag,"value="+value);
                map.put(key,value+"");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 数组字典排序 Map<String, Object>
     * @param map
     * @return
     */
    public static String buildObjectQuery(Map<String, Object> map) {
        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);
        StringBuffer query = new StringBuffer();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = map.get(key);
            if (value == null) {
                continue;
            }
            query.append("&").append(key);
            query.append("=").append(value);
        }
        if (query.length() > 0) {
            return query.substring(1);
        }
        return "";
    }


    /**
     * 数组字典排序 Map<String, String>
     * @param map
     * @return
     */
    public static String buildStringQuery(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys);
        StringBuffer query = new StringBuffer();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key);
            if (value == null || value.length() == 0) {
                continue;
            }
            query.append("&").append(key);
            query.append("=").append(value);
        }
        if (query.length() > 0) {
            return query.substring(1);
        }
        return "";
    }


    /**
     * 获取apk的版本号 appVersionName
     *
     * @param ctx
     * @return
     */
    public static String getAPPVersionCode(Context ctx) {
        String appVersionName = "";
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            appVersionName = info.versionName; // 版本名
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionName;
    }


    public static String getCurrentDate(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String date  = sDateFormat.format(new java.util.Date());
        return date;
    }


    /**
     * 获得MD5加密字符串
     * @param source 源字符串
     * @return 加密后的字符串
     */
    public static String getMD5(String source) {
        String mdString = null;
        if (source != null) {
            try {
                //关键是这句
                mdString = getMD5(source.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return mdString;
    }


    /**
     * 获得MD5加密字符串
     * @param source 源字节数组
     * @return 加密后的字符串
     */
    public static String getMD5(byte[] source) {
        String s = null;
        char [] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        final int temp = 0xf;
        final int arraySize = 32;
        final int strLen = 16;
        final int offset = 4;
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte [] tmp = md.digest();
            char [] str = new char[arraySize];
            int k = 0;
            for (int i = 0; i < strLen; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> offset & temp];
                str[k++] = hexDigits[byte0 & temp];
            }
            s = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }




    /**
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
     *
     * @param amount
     * @return
     */
    public static String changeY2F(String amount){
        String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if(index == -1){
            amLong = Long.valueOf(currency+"00");
        }else if(length - index >= 3){
            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));
        }else if(length - index == 2){
            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);
        }else{
            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");
        }
        return amLong.toString();
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
     * 获取字符长度,中文占2个，字母占2个
     * @param str
     */
    public static int getStringLength(String str) {
        Utils.print(tag,"length="+str.length());
        int length = 0;
        for (int i = 0; i < str.length(); i++) {
            String bb = str.substring(i, i + 1);
            // 生成一个Pattern,同时编译一个正则表达式,其中的u4E00("一"的unicode编码)-\u9FA5("龥"的unicode编码)
            boolean cc = java.util.regex.Pattern.matches("[\u4E00-\u9FA5]", bb);
            if (!cc) {
                Log.v(tag,"en"); //字母
                length = length + 1;
            }else{
                Log.v(tag,"zh");//中文
                length = length + 2;
            }
        }
        return length;

    }


    /**
     * 获取有效key显示有效str
     * @param str
     * @return
     */
    public static String getShowKeyString(String str,int MAX_LENGTH){
        String re = "";
        int length = 0;
        int pos = 0;
        Utils.print(tag,"MAX="+MAX_LENGTH);

        for (int i = 0; i < str.length(); i++) {
            String bb = str.substring(i, i + 1);
            // 生成一个Pattern,同时编译一个正则表达式,其中的u4E00("一"的unicode编码)-\u9FA5("龥"的unicode编码)
            boolean cc = java.util.regex.Pattern.matches("[\u4E00-\u9FA5]", bb);
            if (!cc) {
                Utils.print(tag,"en"); //字母
                length = length + 1;
            }else{
                Utils.print(tag,"zh");//中文
                length = length + 2;
            }
            if(length>MAX_LENGTH){
                pos = i;
                break;
            }
        }
        Utils.print(tag,"pos="+pos);
        re = str.substring(0,pos)+"...";
        return re;
    }


    /**
     * 检测当前网络是否正常
     * @param context
     * @return
     */
    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static String formatInvalidString(String str){
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    public static boolean StringFilter(String str) throws PatternSyntaxException {
        String regEx ="^[a-zABCDEFGHJKLMNPQRTUWXY0-9]+$";  //只允许字母和数字

        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }


    /**
     * 判断url是否合法
     * @param url
     * @return
     */
    public static boolean isValidUrl(String url){
        if (Patterns.WEB_URL.matcher(url).matches()) {
            return true;
        } else{
            return false;
        }
    }

    /**
     * 检测服务app是否启动
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            //Utils.print(tag,"name="+runningService.get(i).service.getClassName().toString());
            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测当前view是否可见
     * @param target
     * @return
     */
    public static boolean isVisibleLocal(View target){
        Rect rect =new Rect();
        target.getLocalVisibleRect(rect);
        return rect.top==0;
    }


    /**
     * 根据app包名和类名启动
     * @param context
     * @param packageName
     * @param activityName
     */
    public static void startAppFromName(Context context,String packageName,String activityName){
        Intent mIntent = new Intent( );
        ComponentName comp = new ComponentName(packageName,activityName);
        mIntent.setComponent(comp);
        context.startService(mIntent);
    }


    /**
     * 根据服务器返回的促销类型，返回客户端展示的响应字符
     * @param promotinType
     * @return
     */
    public static String promotionTypeTranform(Context context,String promotinType){
        if(promotinType.equals(ConStant.FULL_CUT)){
            return context.getResources().getString(R.string.full_cut_title);
        }else if(promotinType.equals(ConStant.FULL_GIFTS)){
            return context.getResources().getString(R.string.full_gift_title);
        }else if(promotinType.equals(ConStant.LIMIT_BUY)){
            return context.getResources().getString(R.string.limit_buy_title);
        }else if(promotinType.equals(ConStant.BUY_GIFTS)){
            return context.getResources().getString(R.string.buy_gift_title);
        }
        return "";
    }

    /**
     * 时间戳转换成字符串
     */
    public static String getDateToString(long time) {
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }


    /**
     * 获取未支付订单的输入参数
     * @return
     */
    public static InputSignBean getAcutionUnpayInput(Context mContext){

        InputSignBean bean = new InputSignBean();
        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            //Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Utils.print(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            //Utils.print(tag,"sign="+sign);
            ///*********

            bean.setInput(input);
            bean.setSign(sign);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bean;
    }




    /**
     * 将毫秒数换算成 00:00 形式
     */
    public static String getMinuteSecond(long time) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = time / dd;
        long hour = (time - day * dd) / hh;
        long minute = (time - day * dd - hour * hh) / mi;
        long second = (time - day * dd - hour * hh - minute * mi) / ss;

        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        return strMinute + ":" + strSecond;
    }
}
