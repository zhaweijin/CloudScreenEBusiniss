package com.hiveview.dianshang.entity.acution;


import com.hiveview.dianshang.entity.acution.info.AcutionInfo;
import com.hiveview.dianshang.entity.acution.listdata.AcutionCommodityData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


public interface AcutionLuaAPI {


    //#################拍卖##############################

    /**
     * 获取拍卖商品列表
     * @param params
     * @param sign
     * @return
     */
    //http://60.206.137.159:8082/api/open/tv/auction/getAuctionList?params=%7B"pageIndex":1,"pageSize":10%7D&sign=7B8D90A85AD92DA6AC3A0AA8EE7B45E8
    @GET("tv/auction/getAuctionList?")
    Observable<AcutionCommodityData> httpGetAcutionListData(@Query(value = "params", encoded = true) String params, @Query("sign") String sign);



    /**
     * 获取拍卖详情信息
     * @param params
     * @param sign
     * @return
     */
    //http://60.206.137.159:8082/api/open/tv/auction/getAuctionData?params=%7B"sn":"488878363671535616"%7D&sign=AB9008F9D8CFEA4E8936D97C6AFF6108
    @GET("tv/auction/getAuctionData?")
    Observable<AcutionInfo> httpGetAcutionInfoData(@Query(value = "params", encoded = true) String params, @Query("sign") String sign);




}
