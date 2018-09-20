package com.hiveview.dianshang.entity.commodity;


import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.acution.addprice.AddPriceData;
import com.hiveview.dianshang.entity.acution.cancelorder.CancelOrderData;
import com.hiveview.dianshang.entity.acution.detail.DetailData;
import com.hiveview.dianshang.entity.acution.info.AcutionInfo;
import com.hiveview.dianshang.entity.acution.listdata.AcutionCommodityData;
import com.hiveview.dianshang.entity.acution.listdata.AcutionCommoditySuperData;
import com.hiveview.dianshang.entity.acution.time.ServerTimeData;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderData;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderUpdateData;
import com.hiveview.dianshang.entity.address.qrdata.QrData;
import com.hiveview.dianshang.entity.advertisement.AdvertisementData;
import com.hiveview.dianshang.entity.afterservice.AfterServiceData;
import com.hiveview.dianshang.entity.category.commodity.LevelCategoryData;
import com.hiveview.dianshang.entity.category.commodity.OneLevelCategoryData;
import com.hiveview.dianshang.entity.collection.CollectionData;
import com.hiveview.dianshang.entity.commodity.afterservice.phone.CommodityPhoneData;
import com.hiveview.dianshang.entity.commodity.detail.CommodityDetailData;
import com.hiveview.dianshang.entity.commodity.mergeData.MergeCommodityData;
import com.hiveview.dianshang.entity.commodity.promotion.ProData;
import com.hiveview.dianshang.entity.commodity.type.CommodityTypeData;
import com.hiveview.dianshang.entity.commodity.typenum.CommodityTypeNumData;
import com.hiveview.dianshang.entity.createorder.CommitOrderStatusData;
import com.hiveview.dianshang.entity.longconnect.LongConnectData;
import com.hiveview.dianshang.entity.message.MessageData;
import com.hiveview.dianshang.entity.notify.NotifyData;
import com.hiveview.dianshang.entity.order.OrderData;
import com.hiveview.dianshang.entity.order.item.OrderItemData;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfoData;
import com.hiveview.dianshang.entity.order.limit.OrderLimitData;
import com.hiveview.dianshang.entity.recommend.RecommendData;
import com.hiveview.dianshang.entity.recommend.special.RecommendSpecialData;
import com.hiveview.dianshang.entity.search.key.SearchKeyData;
import com.hiveview.dianshang.entity.shopping.cart.price.ShoppingCartPriceData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartData;
import com.hiveview.dianshang.entity.shoppingcart.discount.DiscountData;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfoData;
import com.hiveview.dianshang.entity.special.SpecialData;
import com.hiveview.dianshang.entity.token.TokenData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


public interface CommodityAPI {


    /**
     * 获取商品一级分类信息
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/category/getRootCategory?params={%22pageIndex%22:1,%22pageSize%22:10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/category/getRootCategory?")
    Observable<OneLevelCategoryData> httpGetOneLevelData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取商品一级以上的分类信息
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/category/getCategory?params={"pageIndex":1,"pageSize":10,"parentSn":"10002"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/category/getCategory?")
    Observable<LevelCategoryData> httpGetLevelData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取商品信息
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/goods/getGoodsData?params={"categorySn":"864054676134412288","pageIndex":1,"pageSize":10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/goods/getGoodsData?")
    Observable<CommodityData> httpGetCommodityData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取商品详情
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/goods/getGoodsDetail?params={"userid":4055212,"goodsSn":"861441359066746880"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/goods/getGoodsDetail?")
    Observable<CommodityDetailData> httpGetCommodityDetailData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取商品类型
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/goods/getGoodsSkuData?params={"goodsSn":"861441359066746880"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/goods/getGoodsSkuData?")
    Observable<CommodityTypeData> httpGetCommoditySkuInfoData(@Query(value = "params", encoded = true) String params, @Query("token") String token);

    /**
     * 获取首页广告信息
     *
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/message/getGoodsMessage?&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/message/getGoodsMessage?")
    Observable<AdvertisementData> httpGetAdvertisement(@Query("token") String token);


    /**
     * 获取售后成功提示信息
     *
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/hint/getHintMessage?token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/hint/getHintMessage?")
    Observable<MessageData> httpGetHintMessage(@Query("token") String token);


    /**
     * 获取首页专题推荐
     *
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/matrix/getRecomMatrix?token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/matrix/getRecomMatrix?")
    Observable<RecommendData> httpGetRecommendData(@Query("token") String token);


    /**
     * 获取搜索历史关键词
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/search/getSearchHistory?params={"userid":4055212}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/search/getSearchHistory?")
    Observable<SearchKeyData> httpGetSearchKeyData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取搜索商品结果
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/search/findSearchResult?params={"userid":4055212,"keywords":"添加商品","pageIndex":1,"pageSize":10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/search/getSearchResult?")
    Observable<CommodityData> httpGetSearchResultData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取搜索推荐商品
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/search/getSearchRecom?params={"userid":4055212,"pageIndex":1,"pageSize":10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/search/getSearchRecom?")
    Observable<CommodityData> httpGetSearchRecommendData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 添加收藏商品
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080//api/tv/favorite/addFavorite?params={"userid":4055212,"goodsSn":"862119236351320064"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/favorite/addFavorite?")
    Observable<StatusData> httpaddFavoriteCommodity(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 删除已删除的商品
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080//api/tv/favorite/deleteFavorite?params={"userid":4055212,"goodsSn":"862119236351320064"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/favorite/deleteFavorite?")
    Observable<StatusData> httpdeleteFavoriteCommodity(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取所有收藏商品
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080//api/tv/favorite/listFavorite?params={"userid":4055212,"pageIndex":1,"pageSize":10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/favorite/listFavorite?")
    Observable<CollectionData> httpGetAllFavoriteData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 添加商品到购物车
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/cart/addCart?params={"userid":4055212,"goodsSn":"861441359066746880","goodsSkuSn":"861441359066746880_4","quantity":1}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/{version}/cart/addCart")
    Observable<StatusData> httpAddShoppingcartData(@Path("version") String version, @Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 更新购物车数据
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/cart/updateCart?params={"userid":4055212,"cartid":2,"goodsSkuSn":"861441359066746880_4","quantity":2}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/{version}/cart/updateCart")
    Observable<StatusData> httpUpdateShoppingcartData(@Path("version") String version, @Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取购物车所有商品列表
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/cart/listCart?params={"userid":4055212,"pageIndex":1,"pageSize":10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/{version}/cart/listCart?")
    Observable<ShoppingCartData> httpGetShoppingCartData(@Path("version") String version, @Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取购物车商品的优惠价格以及满增商品策略
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.169:8081/api/tv/v1.2/cart/refreshCart?&params=%7B%22itemIdList%22:%5B5155,5157,5163,5164,5166,5167,5168,5169,5171,5172,5173%5D,%22userid%22:%223533877%22%7D&token=781494d5ba2c35a8ae660597f7ea67ef_3533877
    @GET("tv/{version}/cart/refreshCart?")
    Observable<DiscountData> httpGetShoppingCartDiscount(@Path("version") String version,@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 清空购物车商品
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080/api/tv/cart/clearCart?params={"userid":4055212}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/cart/clearCart")
    Observable<StatusData> httpClearShoppingcartData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取直接支付商品,总价,快递数据等信息
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/cart/statGoodsSku?params={"userid":4055212,"goodsSkuSn":"861441359066746880_1","quantity":"1","receiveId":12}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/{version}/cart/statGoodsSku?")
    Observable<ShoppingCartInfoData> httpGetShoppingItemInfoData(@Path("version") String version,@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取购物车总价等详细信息
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/cart/statCart?params={"userid":4055212,"receiveId":12}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/{version}/cart/statCart?")
    Observable<ShoppingCartInfoData> httpGetShoppingcartInfoData(@Path("version") String version,@Query(value = "params", encoded = true) String params, @Query("token") String token);



    /**
     * 删除购物车某个商品
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/cart/deleteCart?params={"userid":4055212,"cartid":2}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/cart/deleteCart")
    Observable<StatusData> httpDeleteShoppingcartData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 首页专题之下的商品推荐
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/goods/getAllGoods?params={"pageIndex":1,"pageSize":10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/goods/getAllGoods?")
    Observable<CommodityData> httpGetRecommendData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取订单列表
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/getOrderList?params={"userid":4055212,"pageIndex":1,"pageSize":10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/{version}/order/getOrderList?")
    Observable<OrderData> httpGetOrderListData(@Path("version") String version,@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 查看订单详情数据
     *
     * @param params
     * @param token
     * @return
     */
    //http://60.206.137.159:8082/api/open/tv/order/viewOrderData?params=%7B"userid":5328718,"orderSn":"11170024459166942209511424","pageIndex":1,"pageSize":10%7D&token=94aeef76ded038ceb1ed13e29b259966_5328718
    @GET("tv/{version}/order/viewOrderData?")
    Observable<OrderItemData> httpGetOrderItemData(@Path("version") String version,@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 查看订单详情地址,总价,发票等
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/viewOrderInfo?params={"userid":4055212,"orderSn":"866857048342577152"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/{version}/order/viewOrderInfo?")
    Observable<OrderStatusInfoData> httpGetOrderStatusInfoData(@Path("version") String version,@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 提交直接支付订单
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/goodsSkuCreateOrder?params={"userid":4055212,"quantity":2,"goodsSkuSn":"861441359066746880_7","receiveId":7,"isInvoice":true,"invoiceType":"单位","invoiceTitle":"深圳水公司","invoiceContent":"本发票用于购物","invoiceId":"111112222233333"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/order/goodsSkuCreateOrder")
    Observable<CommitOrderStatusData> httpCommitItemOrderData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 提交直接支付订单,加密接口
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/skuTakeOrder?params={"userid":4055212,"quantity":2,"goodsSkuSn":"861441359066746880_7","receiveId":7,"isInvoice":true,"invoiceType":"单位","invoiceTitle":"深圳水公司","invoiceContent":"本发票用于购物"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/{version}/order/skuTakeOrder")
    Observable<CommitOrderStatusData> httpCommitEncryptItemOrderData(@Path("version") String version,@Query(value = "params", encoded = true) String params, @Query("sign") String sign,@Query("token") String token);


    /**
     * 提交购物车支付订单,加密接口
     * @param params
     * @param sign
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/cartTakeOrder?params={"userid":4055212,"quantity":2,"goodsSkuSn":"861441359066746880_7","receiveId":7,"isInvoice":true,"invoiceType":"单位","invoiceTitle":"深圳水公司","invoiceContent":"本发票用于购物"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/{version}/order/cartTakeOrder")
    Observable<CommitOrderStatusData> httpCommitEncryptShoppingcartOrderData(@Path("version") String version,@Query(value = "params", encoded = true) String params, @Query("sign") String sign, @Query("token") String token);



    /**
     * 提交购物车订单
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/cartCreateOrder?params={"userid":4055212,"cartSn":"866479373920858112","receiveId":7,"isInvoice":true,"invoiceType":"单位","invoiceTitle":"深圳水公司","invoiceContent":"本发票用于购物","invoiceId":"222223333344444"}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("tv/order/cartCreateOrder")
    Observable<CommitOrderStatusData> httpCommitShoppingCartOrderData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取售后列表
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/getServiceList?params={"userid":4055212,"pageIndex":1,"pageSize":10}&token=7f0dfac38a2e3674bd7295e1ec184049_4055212
    @GET("tv/order/getServiceList?")
    Observable<AfterServiceData> httpGetAfterServiceData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 取消售后订单
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/cancelSelledService?params={"userid":4055212,"serviceType":2,"orderSn":"866857436105981952","status":3,"serviceSn":"866988852714147840"}&token=31a29091862a36d187dcd77ba37f53f1_4055212
    @POST("tv/order/cancelSelledService")
    Observable<StatusData> httpCancelAfterService(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 提交退款申请,订单退款(只存在整单退款的情况)
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/submitOrderService?params={"userid":4055212,"orderSn":"868297925204156416","status":3,"totalQuantity":2,"reason":"买错了","serviceType":1,"refundType":2}&token=0e8ac6090ad1382dbac9d3cbeac35fdb_4055212
    @POST("tv/{version}/order/submitOrderService")
    Observable<StatusData> httpSubmitOrderService(@Path("version") String version,@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 提交售后申请退款,可以单件退款
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/order/submitGoodsSkuService?params={"userid":4055212,"orderSn":"866856787729498112","status":3,"quantity":2,"reason":"发票信息有误","serviceType":2,"refundType":1,"goodsSkuSn":"861441359066746880_4"}&token=0e8ac6090ad1382dbac9d3cbeac35fdb_4055212
    @POST("tv/order/submitGoodsSkuService")
    Observable<StatusData> httpSubmitGoodsSkuService(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取token
     */
    //http://172.16.1.18:8080/api/user/getAuthToken?params={mac":"143DF24A133D","sn":"DMD3310E160803702"}
    @POST("user/getAuthToken?")
    Observable<TokenData> httpGetToken(@Query(value = "params", encoded = true) String params);


    /**
     * 获取支付回调URL
     *
     * @param token
     * @return
     */
    //http://172.16.1.18:8080/api/tv/payment/getNotifyUrl?token=0e8ac6090ad1382dbac9d3cbeac35fdb_4055212
    @GET("tv/{version}/payment/getNotifyUrl?")
    Observable<NotifyData> httpGetNotifyUrl(@Path("version") String version,@Query("token") String token);


    /**
     * 获取订单支付结果状态
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.18:8081/api/tv/payment/getPaymentStatus?params={%22userid%22:4053896,%22orderSn%22:%221221343140420875594875859378176%22}&token=1067b0b62ed232b69e7f2f29db83e175_4053896
    @GET("tv/payment/getPaymentStatus?")
    Observable<StatusData> httpGetPaymentStatus(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取专题列表
     */
    //http://60.206.137.159:8081/api/tv/matrix/getSpecData?params={"specSn":"399362214055645184","pageIndex":1,"pageSize":10}&token=94c94680df63359b8478802edbd4bba1_3971301
    @GET("tv/matrix/getSpecData?")
    Observable<SpecialData> httpGetSpecialData(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 获取商品售后电话
     * @param params
     * @param token
     * @return
     */
    @GET("tv/operation/getServicePhone?")
    Observable<CommodityPhoneData> httpGetAfterServicePhone(@Query("token") String token);


    /**
     * 获取用户中心二维码地址
     * @param params
     * @param token
     * @return
     */
    @GET("tv/qr/getReceiverQrData?")
    Observable<QrData> httpGetReceiverQr(@Query(value = "params", encoded = true) String params, @Query("token") String token);



    /**
     * 获取sku 商品具体规格的促销信息
     * @param params
     * @param token
     * @return
     */
    //http://172.16.1.111:8082/api/open/tv/goods/getSkuPromotion?params={"goodsSkuSn":"427984796640088064_1"}&token=72e6d4c68a6f32ed89f938659aeef91c_4055212
    @GET("tv/goods/getSkuPromotion?")
    Observable<ProData> httpGetSkuPromotionData(@Query(value = "params", encoded = true) String params, @Query("token") String token);



    /**
     * 获取spu 商品的促销信息
     * @param params
     * @param token
     * @return
     */
    //http://60.206.137.159:8082/api/open/tv/goods/getGoodsPromotion?params=%7B"goodsSn":"427984796640088064"%7D&token=72e6d4c68a6f32ed89f938659aeef91c_4055212
    @GET("tv/goods/getGoodsPromotion?")
    Observable<ProData> httpGetSpuPromotionData(@Query(value = "params", encoded = true) String params, @Query("token") String token);



    /**
     * 获取凑单商品信息
     * @param params
     * @param token
     * @return
     */
    //http://60.206.137.159:8082/api/open/tv/cart/mergeOrder?params=%7B"promotionSn":"460245798966267904","pageIndex":1,"pageSize":10%7D&token=72e6d4c68a6f32ed89f938659aeef91c_4055212
    @GET("tv/cart/mergeOrder?")
    Observable<MergeCommodityData> httpGetMergeCommodityData(@Query(value = "params", encoded = true) String params, @Query("token") String token);





    /**
     * 清空收藏夹
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080//api/open/tv/favorite/deleteAllFavorite?params={"userid":4055212}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/favorite/deleteAllFavorite?")
    Observable<StatusData> httpDeleteAllFavorite(@Query(value = "params", encoded = true) String params, @Query("token") String token);



    /**
     * 清空购物车失效的商品
     *
     * @param params
     * @param token
     * @return
     */
    //http://172.16.0.89:8080//api/open/tv/favorite/deleteAllFavorite?params={"userid":4055212}&token=2f1e344843b733af97e641e9a517347c_4055212
    @GET("tv/cart/deleteFailureCart?")
    Observable<StatusData> httpDeleteAllInvalidCart(@Query(value = "params", encoded = true) String params, @Query("token") String token);



    /**
     * 获取搜索二维码地址
     * @param params
     * @param token
     * @return
     */
    @GET("tv/qr/getSearchQrData?")
    //http://60.206.137.159:8082/api/open/tv/qr/getSearchQrData?params=%7B"userid":4055212%7D&token=72e6d4c68a6f32ed89f938659aeef91c_4055212
    Observable<QrData> httpGetSearchQr(@Query(value = "params", encoded = true) String params, @Query("token") String token);



    /**
     * 获取发票二维码地址
     * @param params
     * @param token
     * @return
     */
    @GET("tv/qr/getInvoiceQrData?")
    //http://60.206.137.159:8082/api/open/tv/qr/getInvoiceQrData?params=%7B"userid":4055212%7D&token=72e6d4c68a6f32ed89f938659aeef91c_4055212
    Observable<QrData> httpGetInvoiceQr(@Query(value = "params", encoded = true) String params, @Query("token") String token);


    /**
     * 查询订单是否限购
     * @param version
     * @param params
     * @param token
     * @return
     */
    //http://60.206.137.159:8081/api/tv/v1.2/order/getOrderLimitBuy?params=%7B"orderSn":"866857048342577152","userid":4055212%7D&token=e090105921123a64b916c36757d9e419_4055212
    @GET("tv/{version}/order/getOrderCanPay?")
    Observable<OrderLimitData> httpOrderLimitBuy(@Path("version") String version, @Query(value = "params", encoded = true) String params, @Query("token") String token);



   /***  拍卖  *********/


    /**
     * 获取未支付的拍卖订单
     */
    //http://60.206.137.159:8082/api/open/tv/auction/getUnpayAuctionOrder?params=%7B"userid":"4055212"%7D&sign=CCA21F9548A579CDB879D022D7DB9719
    @GET("tv/auction/getUnpayAuctionOrder?")
    Observable<UnpayOrderData> httpGetAcutionUnpayData(@Query(value = "params", encoded = true) String params, @Query("sign") String sign);


    /**
     * 拍卖商品竞拍出价
     */
    //http://172.16.0.121:8090/api/open/tv/auction/bidAuctionPrice/?params=%7B"auctionSn":"19495679594645426176","currentPrice":"","userid":"12456789"%7D&sign=AB9008F9D8CFEA4E8936D97C6AFF6108
    @GET("tv/auction/bidAuctionPrice?")
    Observable<AddPriceData> httpGetAcutionAddPriceData(@Query(value = "params", encoded = true) String params, @Query("sign") String sign);


    /**
     * 更新拍卖订单发票信息
     */
    //http://60.206.137.159:8082/api/open/tv/auction/updateOrderInvoice?params=%7B"orderSn":"488878363671535616","isInvoice":true,"invoiceType":"单位","invoiceTitle":"深圳水公司","invoiceContent":"本发票用于购物","invoiceId":"1212122"%7D&sign=34B8D57723CA2A54583C6AC974B1C06F
    @POST("tv/auction/updateOrderInvoice?")
    Observable<UnpayOrderUpdateData> httpUpdateAcutionInvoiceData(@Query(value = "params", encoded = true) String params, @Query("sign") String sign);


    /**
     * 更新拍卖订单收件人信息
     */
    //http://60.206.137.159:8082/api/open/tv/auction/updateOrderReceiver?params=%7B"orderSn":"488878363671535616","receiveId":26441%7D&sign=CC70C2F329FB7EB032EC88BA91656BC9
    @POST("tv/auction/updateOrderReceiver?")
    Observable<UnpayOrderUpdateData> httpUpdateAcutionReceiveData(@Query(value = "params", encoded = true) String params, @Query("sign") String sign);


    /**
     * 取消拍卖订单
     * @param params
     * @param sign
     * @return
     */
    //http://60.206.137.159:8082/api/open/tv/auction/cancelAuctionOrder?params=%7B"orderSn":"491770794486022144"%7D&sign=3E70CA0E3D13BE76714996E1BDD2B7E8
    @POST("tv/auction/cancelAuctionOrder?")
    Observable<CancelOrderData> httpCancelAcutionOrder(@Query(value = "params", encoded = true) String params, @Query("sign") String sign);


    /**
     * 获取拍卖服务器的时间，目的是与本地时间比较，同步
     * @param params
     * @param sign
     * @return
     */
    //http://60.206.137.159:8082/api/open/tv/auction/getAuctionTime?params=%7B"userid":4055212%7D&sign=CCA21F9548A579CDB879D022D7DB9719
    @GET("tv/auction/getAuctionTime?")
    Observable<ServerTimeData> httpGetAcutionServerTime(@Query(value = "params", encoded = true) String params, @Query("sign") String sign);


    //http://60.206.137.159:8082/api/open/tv/auction/getAuctionDetail?params=%7B%22goodsSn":%22861441359066746880"%7D&sign=e090105921123a64b916c36757d9e419_4055212
    @GET("tv/auction/getAuctionDetail?")
    Observable<DetailData> httpGetAcutionDetail(@Query(value = "params", encoded = true) String params, @Query("sign") String sign);

}
