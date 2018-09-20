package com.hiveview.dianshang.entity.address;



import com.hiveview.dianshang.entity.StatusData;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;



public interface AddressAPI {


    /**
     * 获取省列表
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/city/getCityData?&params={%22userid%22:%224055212%22,%22parentCode%22:%22%22}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/city/getCityData?")
    Observable<ProviceData> httpGetProvice(@Query(value="params", encoded=true) String params,@Query("token") String token);



    /**
     * 获取市,区,街道列表
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/city/getCityData?&params={%22userid%22:%224055212%22,%22parentCode%22:%22110000%22}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/city/getCityData?")
    Observable<AreaData> httpGetAreaData(@Query(value="params", encoded=true) String params, @Query("token") String token);



    /**
     * 获取所有用户通信地址
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/receiver/listReceiver?params={%22userid%22:4055212,%22pageIndex%22:1,%22pageSize%22:10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/receiver/listReceiver?")
    Observable<UserAddress> httpGetUserAddressListData(@Query(value="params", encoded=true) String params,@Query("token") String token);


    /**
     * 添加用户通信地址
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/receiver/addReceiver?params={"userid":4055212,"isDefault":true,"addressProvince":"广东省深圳市南山区","addressTown":"南山街道","addressDetail":"科技园科技南四王路131号","phone":"1867558525","consignee":"李李李","treePath":"440000,440300,440305,440305002"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/receiver/addReceiver?")
    Observable<StatusData> httpAddUserAddressData(@Query(value="params", encoded=true) String params,@Query("token") String token);

    /**
     * 更新用户地址
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/receiver/updateReceiver?params={"receiveId":6,"userid":4055212,"isDefault":true,"address":"广东省深圳市南山区南山街道科技>园科技南三路113号","phone":"18678831995","consignee":"李子树","treePath":"440000,440300,440305,440305002"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/receiver/updateReceiver?")
    Observable<StatusData> httpUpdateUserAddressData(@Query(value="params", encoded=true) String params,@Query("token") String token);


    /**
     * 获取快递信息
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.223:8080/api/tv/tracker/getTrackerData?params={"userid":4055212,"orderSn":"113343","deliverySn":"438256314882","deliveryCorpName":"中通快递"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/tracker/getTrackerData")
    Observable<ExpressData> httpGetExpressInfo(@Query(value="params", encoded=true) String params,@Query("token") String token);


    /**
     * 删除用户地址
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/receiver/deleteReceiver?params={"receiveId":6,"isDefault":true,"userid":4055212}&token=e090105921123a64b916c36757d9e419_4055212
    @POST("tv/receiver/deleteReceiver?")
    Observable<StatusData> httpdeleteUserAddressData(@Query(value="params", encoded=true) String params,@Query("token") String token);




}
