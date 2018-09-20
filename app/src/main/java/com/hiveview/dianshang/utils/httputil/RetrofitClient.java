package com.hiveview.dianshang.utils.httputil;

import com.google.gson.Gson;
import com.hiveview.dianshang.entity.acution.AcutionLuaAPI;
import com.hiveview.dianshang.entity.address.AddressAPI;
import com.hiveview.dianshang.entity.commodity.CommodityAPI;
import com.hiveview.dianshang.utils.httputil.Converter.AvatarConverter;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import static com.hiveview.dianshang.utils.httputil.OkHttpHelper.addLogClient;
import static com.hiveview.dianshang.utils.httputil.OkHttpHelper.addLuaLogClient;


/**
 * Created by LiCola on  2016/04/16  0:08
 */
public class RetrofitClient {

    //所有的联网地址 统一成https
    public final static String mBaseUrl = "http://shoptvapi.domybox.com/api/open/";
    public final static String mBaseLuaUrl = "http://shopluatv.domybox.com/api/open/";
    //public final static String mBaseUrl = "http://shoptvapi.domybox.com/api/open/"; //运营上线地址
    //http://60.206.137.159:8082/api/open/   正式发布平台
    //http://172.16.1.18:8081/api/　　　　个人环境平台
    //http://60.206.137.209:8081/api/　　　测试平台
    //运维平台　http://60.206.137.209:8080  或者　http://60.206.137.159:8080
    //徐本地　http://172.16.0.169:8081/api/
    public static Gson gson = new Gson();

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();




    private static CommodityAPI commodityAPI;
    private static AddressAPI addressAPI;
    private static AcutionLuaAPI acutionLuaAPI;


    public static CommodityAPI getCommodityAPI(){
        if(commodityAPI==null){
            Retrofit retrofit = builder
                    .client(addLogClient(httpClient).build())
                    .addConverterFactory(AvatarConverter.create(gson))
                    .build();
            commodityAPI = retrofit.create(CommodityAPI.class);
        }
        return commodityAPI;
    }


    public static AddressAPI getAddressAPI(){
        if(addressAPI==null){
            Retrofit retrofit = builder
                    .client(addLogClient(httpClient).build())
                    .addConverterFactory(AvatarConverter.create(gson))
                    .build();
            addressAPI = retrofit.create(AddressAPI.class);
        }
        return addressAPI;
    }

    public static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(mBaseUrl)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create());



    public static Retrofit.Builder luaBuilder = new Retrofit.Builder()
            .baseUrl(mBaseLuaUrl)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create());


    public static AcutionLuaAPI getAcutionLauAPI(){
        if(acutionLuaAPI==null){
            Retrofit retrofit = luaBuilder
                    .client(addLuaLogClient(httpClient).build())
                    .addConverterFactory(AvatarConverter.create(gson))
                    .build();
            acutionLuaAPI = retrofit.create(AcutionLuaAPI.class);
        }
        return acutionLuaAPI;
    }

}
