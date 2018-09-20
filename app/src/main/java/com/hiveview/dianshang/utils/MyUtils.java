package com.hiveview.dianshang.utils;

import com.hiveview.dianshang.base.EBusinessApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;


/**
 * Created by carter on 3/6/17.
 */
public class MyUtils {

    public static OkHttpClient getOkHttpClient(){

        //设置缓存路径和大小
        Cache cache = new Cache(EBusinessApplication.getContext().getCacheDir(),10*1024*1024);
        //初始化OkHttpClient
        OkHttpClient client = new OkHttpClient();
        client.newBuilder().connectTimeout(5, TimeUnit.SECONDS);
        client.newBuilder().writeTimeout(5, TimeUnit.SECONDS);
        client.newBuilder().readTimeout(5, TimeUnit.SECONDS);
        return client;
    }
}
