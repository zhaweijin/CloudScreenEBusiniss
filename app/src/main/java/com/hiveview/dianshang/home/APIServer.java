package com.hiveview.dianshang.home;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.acution.addprice.AddPriceData;
import com.hiveview.dianshang.entity.acution.cancelorder.CancelOrderData;
import com.hiveview.dianshang.entity.acution.common.AcutionItemData;
import com.hiveview.dianshang.entity.acution.detail.DetailData;
import com.hiveview.dianshang.entity.acution.info.AcutionInfo;
import com.hiveview.dianshang.entity.acution.listdata.AcutionCommodityData;
import com.hiveview.dianshang.entity.acution.time.ServerTimeData;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderData;
import com.hiveview.dianshang.entity.acution.unpay.order.UnpayOrderUpdateData;
import com.hiveview.dianshang.entity.address.AreaData;
import com.hiveview.dianshang.entity.address.ChildData;
import com.hiveview.dianshang.entity.address.ExpressData;
import com.hiveview.dianshang.entity.address.ProviceData;
import com.hiveview.dianshang.entity.address.UserAddress;
import com.hiveview.dianshang.entity.address.UserData;
import com.hiveview.dianshang.entity.address.qrdata.QrData;
import com.hiveview.dianshang.entity.advertisement.Advertisement;
import com.hiveview.dianshang.entity.advertisement.AdvertisementData;
import com.hiveview.dianshang.entity.afterservice.AfterServiceData;
import com.hiveview.dianshang.entity.afterservice.AfterServiceRecord;
import com.hiveview.dianshang.entity.afterservice.exchange.Exchange;
import com.hiveview.dianshang.entity.afterservice.exchange.ExchangeItem;
import com.hiveview.dianshang.entity.afterservice.refund.Refund;
import com.hiveview.dianshang.entity.afterservice.refund.RefundItem;
import com.hiveview.dianshang.entity.category.commodity.LevelCategoryData;
import com.hiveview.dianshang.entity.category.commodity.LevelCategoryRecored;
import com.hiveview.dianshang.entity.category.commodity.OneLevelCategoryData;
import com.hiveview.dianshang.entity.category.commodity.OneLevelCategoryRecored;
import com.hiveview.dianshang.entity.collection.CollectionData;
import com.hiveview.dianshang.entity.collection.CollectionRecord;
import com.hiveview.dianshang.entity.commodity.CommodityData;
import com.hiveview.dianshang.entity.commodity.CommodityRecored;
import com.hiveview.dianshang.entity.commodity.afterservice.phone.CommodityPhoneData;
import com.hiveview.dianshang.entity.commodity.detail.CommodityDetailData;
import com.hiveview.dianshang.entity.commodity.mergeData.MergeCommodity;
import com.hiveview.dianshang.entity.commodity.mergeData.MergeCommodityData;
import com.hiveview.dianshang.entity.commodity.promotion.ProData;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionGift;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionListData;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionRecord;
import com.hiveview.dianshang.entity.commodity.type.CommodityTypeData;
import com.hiveview.dianshang.entity.commodity.type.SkuList;
import com.hiveview.dianshang.entity.commodity.type.SpecItemList;
import com.hiveview.dianshang.entity.commodity.type.SpecList;
import com.hiveview.dianshang.entity.createorder.CommitOrderStatusData;
import com.hiveview.dianshang.entity.message.MessageData;
import com.hiveview.dianshang.entity.notify.NotifyData;
import com.hiveview.dianshang.entity.order.OrderData;
import com.hiveview.dianshang.entity.order.OrderRecord;
import com.hiveview.dianshang.entity.order.item.OrderGiftData;
import com.hiveview.dianshang.entity.order.item.OrderItemData;
import com.hiveview.dianshang.entity.order.item.OrderItemInfo;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfo;
import com.hiveview.dianshang.entity.order.item.OrderStatusInfoData;
import com.hiveview.dianshang.entity.order.limit.OrderLimitData;
import com.hiveview.dianshang.entity.recommend.RecommendData;
import com.hiveview.dianshang.entity.search.key.SearchKeyData;
import com.hiveview.dianshang.entity.shoppingcart.GroupData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartData;
import com.hiveview.dianshang.entity.shoppingcart.ShoppingCartRecord;
import com.hiveview.dianshang.entity.shoppingcart.discount.DiscountData;
import com.hiveview.dianshang.entity.shoppingcart.discount.DiscountType;
import com.hiveview.dianshang.entity.shoppingcart.discount.FullCutSkuData;
import com.hiveview.dianshang.entity.shoppingcart.discount.PostCartItemInfo;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfo;
import com.hiveview.dianshang.entity.shoppingcart.info.ShoppingCartInfoData;
import com.hiveview.dianshang.entity.shoppingcart.post.ShoppingPostOrderInfo;
import com.hiveview.dianshang.entity.special.SpecialData;
import com.hiveview.dianshang.entity.token.TokenData;
import com.hiveview.dianshang.saleservice.AfterServiceStatus;
import com.hiveview.dianshang.utils.DeviceInfo;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carter on 5/16/17.
 */

//!!!!测试数据请勿删除

public class APIServer {

    private String tag = "apitest";

    private Context mContext;

    public APIServer(Context context) {
        this.mContext = context;
    }


    /**
     * 获取用户地址列表
     */
    public void getUserAddressList() {
        Utils.print(tag, "getUserAddressList");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("pageIndex", "1");
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetUserAddressListData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserAddress>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error>>>>");
                    }

                    @Override
                    public void onNext(UserAddress userAddress) {
                        Utils.print(tag, "get data==" + userAddress.getErrorMessage() + ",value=" + userAddress.getReturnValue());
                        if (userAddress.getReturnValue() == -1)
                            return;
                        Utils.print(tag, "total count=" + userAddress.getData().getTotalCount());
                        List<UserData> userDatas = userAddress.getData().getRecords();
                        for (int i = 0; i < userDatas.size(); i++) {
                            Utils.print(tag, "name=" + userDatas.get(i).getAddressDetail());
                            Utils.print(tag, "id=" + userDatas.get(i).getId());
                        }
                    }
                });
    }


    /**
     * 获取市,区,街道列表
     * eg:北京110000
     *
     * @param code
     */
    public void getAreaList(String code) {
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("parentCode", code);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetAreaData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AreaData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AreaData areaData) {
                        Utils.print(tag, "size=" + areaData.getData().size());
                        Utils.print(tag, "returnValue=" + areaData.getErrorMessage());

                        List<ChildData> childDatas = areaData.getData();
                        Utils.print(tag, "child size = " + childDatas.size());
                    }
                });

    }


    /**
     * 获取省份列表
     */
    public void getProviceList() {

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("parentCode", "");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetProvice(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ProviceData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ProviceData proviceData) {
                        Utils.print(tag, "size=" + proviceData.getData().size());
                    }
                });
    }


    /**
     * 添加用户地址
     */
    public void addUserAddress() {

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("addressDetail", "广东南昌随碟附送是非得失");
            json.put("consignee", "test测试");
            json.put("isDefault", false);
            json.put("addressProvince", "广东深圳南昌");
            json.put("addressTown", "");
            json.put("phone", "1322222");
            json.put("treePath", "");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpAddUserAddressData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status=" + statusData.getErrorMessage());
                    }
                });
    }


    /**
     * 更新用户地址
     */
    public void updateUserAddress(UserData userData) {
        Utils.print(tag, "updateUserAddress");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("receiveId", "9");
            json.put("address", "北京上海11111");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpUpdateUserAddressData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status=" + statusData.getErrorMessage());
                    }
                });
    }


    /**
     * 删除用户地址
     */
    public void deleteUserAddress(int receiveId) {
        Utils.print(tag, "deleteUserAddress");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("receiveId", receiveId);
            json.put("isDefault", true);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpdeleteUserAddressData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status=" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                    }
                });
    }


    /**
     * 获取快递信息
     */
    public void getExpressInfo() {
        Utils.print(tag, "getExpressInfo");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("orderSn", "11151719398259839853137920");
            json.put("deliverySn", "70094351403772");
            json.put("deliveryCorpName", "HTKY");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getAddressAPI()
                .httpGetExpressInfo(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExpressData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(ExpressData expressData) {
                        // Utils.print(tag,"code="+expressData.getData().getLogisticCode());
                    }
                });
    }


    /**
     * 获取商品一级分类信息
     */
    public void getOneLevelCategory() {
        Utils.print(tag, "getOneLevelCategory");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", "1");
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetOneLevelData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OneLevelCategoryData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(OneLevelCategoryData oneLevelCategoryData) {
                        Utils.print(tag, "status==" + oneLevelCategoryData.getErrorMessage());
                        List<OneLevelCategoryRecored> recored = oneLevelCategoryData.getData().getRecords();
                        for (int i = 0; i < recored.size(); i++) {
                            Utils.print(tag, "imgurl=" + recored.get(i).getImgUrl());
                        }

                    }
                });
    }


    /**
     * 获取商品一级以上分类信息
     */
    public void getLevelCategory() {
        Utils.print(tag, "getLevelCategory");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", "1");
            json.put("pageSize", ConStant.PAGESIZE);
            json.put("parentSn", "10002");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetLevelData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LevelCategoryData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(LevelCategoryData levelCategoryData) {
                        Utils.print(tag, "status==" + levelCategoryData.getErrorMessage());
                        List<LevelCategoryRecored> recored = levelCategoryData.getData().getRecords();
                        for (int i = 0; i < recored.size(); i++) {
                            Utils.print(tag, "imgurl=" + recored.get(i).getImgUrl());
                        }

                    }
                });
    }


    /**
     * 获取商品信息
     */
    public void getCommodity() {
        Utils.print(tag, "getCommodity");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", "1");
            json.put("pageSize", ConStant.PAGESIZE);
            json.put("categorySn", "864054676134412288");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetCommodityData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag, "status==" + commodityData.getErrorMessage());
                        List<CommodityRecored> recored = commodityData.getData().getRecords();
                        for (int i = 0; i < recored.size(); i++) {
                            Utils.print(tag, "name=" + recored.get(i).getName());
                        }

                    }
                });
    }


    /**
     * 获取商品详细信息
     */
    public void getCommodityDetail() {
        Utils.print(tag, "getCommodityDetail");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("goodsSn", "861441359066746880");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetCommodityDetailData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityDetailData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityDetailData commodityDetailData) {
                        Utils.print(tag, "status==" + commodityDetailData.getErrorMessage());
                        List<String> imageurls = commodityDetailData.getData().getDetailUrl();
                        for (int i = 0; i < imageurls.size(); i++) {
                            Utils.print(tag, "name=" + imageurls.get(i));
                        }

                    }
                });
    }


    /**
     * 获取商品类型信息
     */
    public void getCommoditySkuInfo() {
        Utils.print(tag, "getCommoditySkuInfo");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("goodsSn", "861441359066746880");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetCommoditySkuInfoData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityTypeData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityTypeData commodityTypeData) {
                        Utils.print(tag, "status==" + commodityTypeData.getErrorMessage());
                        List<SkuList> skuLists = commodityTypeData.getData().getSkuList();
                        List<SpecList> specLists = commodityTypeData.getData().getSpecList();
                        for (int i = 0; i < specLists.size(); i++) {
                            List<SpecItemList> specItemLists = specLists.get(i).getSpecItemList();
                            for (int j = 0; j < specItemLists.size(); j++) {
                                Utils.print(tag, "color==" + specItemLists.get(j).getColor());
                                Utils.print(tag, "name==" + specItemLists.get(j).getName());
                            }
                        }
                    }
                });
    }



    /**
     * 获取首页商品广告信息
     */
    public void getAdvertisement() {
        Utils.print(tag, "getAdvertisement");

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetAdvertisement(ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AdvertisementData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(AdvertisementData advertisementData) {
                        Utils.print(tag, "status==" + advertisementData.getErrorMessage());
                        List<Advertisement> advertisements = advertisementData.getData();
                        for (int i = 0; i < advertisements.size(); i++) {
                            Utils.print(tag, "s==" + advertisements.get(i).getDescription());
                        }
                    }
                });
    }


    /**
     * 获取售后提示信息
     */
    public void getHintMessage() {
        Utils.print(tag, "getHintMessage");

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetHintMessage(ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MessageData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(MessageData messageData) {
                        Utils.print(tag, "status==" + messageData.getErrorMessage());
                        Utils.print(tag, "s==" + messageData.getData().getDescription());

                    }
                });
    }


    /**
     * 获取首页推荐专题2页数据
     */
    public void getHomeRecommendData() {
        Utils.print(tag, "getRecommendData");

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetRecommendData(ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RecommendData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(RecommendData recommendData) {
                        Utils.print(tag, "status==" + recommendData.getErrorMessage());

                        for (int i = 0; i < recommendData.getRecommend().getOperationMatrixItemList().size(); i++) {
                            Utils.print(tag, "s==" + recommendData.getRecommend().getOperationMatrixItemList().get(i).getGoodsName());
                        }


                    }
                });
    }


    /**
     * 获取搜索历史关键词
     */
    public void getSearchKeyData() {
        Utils.print(tag, "getSearchKeyData");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetSearchKeyData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SearchKeyData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(SearchKeyData searchKeyData) {
                        Utils.print(tag, "status==" + searchKeyData.getErrorMessage());
                        if (searchKeyData.getReturnValue() == -1)
                            return;
                        for (int i = 0; i < searchKeyData.getData().size(); i++) {
                            Utils.print(tag, "s==" + searchKeyData.getData().get(i));
                        }
                    }
                });
    }


    /**
     * 获取搜索商品结果
     *
     * @param key
     */
    public void getSearchResultData(String key) {
        Utils.print(tag, "getSearchResultData");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("keywords", key);
            json.put("pageIndex", "1");
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetSearchResultData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag, "status==" + commodityData.getErrorMessage());
                        if (commodityData.getReturnValue() == -1)
                            return;
                        for (int i = 0; i < commodityData.getData().getRecords().size(); i++) {
                            Utils.print(tag, "s==" + commodityData.getData().getRecords().get(i).getName());
                        }
                    }
                });
    }


    /**
     * 获取搜索推荐
     *
     * @param
     */
    public void getSearchRecommendData() {
        Utils.print(tag, "getSearchRecommendData");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("pageIndex", "1");
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetSearchRecommendData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag, "status==" + commodityData.getErrorMessage());
                        if (commodityData.getReturnValue() == -1)
                            return;
                        for (int i = 0; i < commodityData.getData().getRecords().size(); i++) {
                            Utils.print(tag, "s==" + commodityData.getData().getRecords().get(i).getName());
                        }
                    }
                });
    }


    /**
     * 添加收藏的商品
     */
    public void addFavoriteCommodity() {
        Utils.print(tag, "addFavoriteCommodity");

        String input = "";
        try {
            Map<String, String> json = new HashMap<>();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("goodsSn", "862119236351320064");
            Gson gson = new Gson();
            input = gson.toJson(json);
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpaddFavoriteCommodity(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;

                        if (statusData.getReturnValue() == 1 &&
                                mContext.getResources().getString(R.string.sucess_status).equals(statusData.getErrorMessage())) {
                            Utils.print(tag, "添加成功");
                        } else {
                            Utils.print(tag, "添加失败");
                        }
                    }
                });
    }


    /**
     * 删除收藏的商品
     */
    public void deleteFavoriteCommodity() {
        Utils.print(tag, "deleteFavoriteCommodity");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("goodsSn", "862119236351320064");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpdeleteFavoriteCommodity(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 获取所有收藏商品
     */
    public void getAllFavoriteData() {
        Utils.print(tag, "getAllFavoriteData");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetAllFavoriteData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CollectionData collectionData) {
                        Utils.print(tag, "status==" + collectionData.getErrorMessage());
                        if (collectionData.getReturnValue() == -1)
                            return;
                        List<CollectionRecord> collectionRecord = collectionData.getData().getRecords();
                        for (int i = 0; i < collectionRecord.size(); i++) {
                            Utils.print(tag, "s==" + collectionRecord.get(i).getGoodsName());
                        }
                    }
                });
    }


    /**
     * 添加购物车商品
     */
    public void addShoppingCartCommodity() {
        Utils.print(tag, "addShoppingCartCommodity");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("goodsSn", "861441359066746880");
            json.put("goodsSkuSn", "861441359066746880_3");
            json.put("quantity", 18);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpAddShoppingcartData(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 删除收藏的所有商品
     */
    public void deleteAllFavoriteCommodity() {
        Utils.print(tag, "deleteAllFavoriteCommodity");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpDeleteAllFavorite(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 清空购物车商品
     */
    public void clearShoppingCartCommodity() {
        Utils.print(tag, "clearShoppingCartCommodity");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpClearShoppingcartData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 获取购物车商品列表
     */
    public void getShoppingCartCommodity() {
        Utils.print(tag, "getShoppingCartCommodity");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetShoppingCartData(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "2error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartData shoppingCartData) {
                        Utils.print(tag, "status==" + shoppingCartData.getErrorMessage() + ",value=" + shoppingCartData.getReturnValue());
                        if (shoppingCartData.getReturnValue() == -1)
                            return;

                        List<GroupData> groupDatas = shoppingCartData.getData();
                        for (int i = 0; i < groupDatas.size(); i++) {
                            Utils.print(tag, "type==" + groupDatas.get(i).getPromotionType());
                            Utils.print(tag, "sn==" + groupDatas.get(i).getPromotionSn());
                            List<ShoppingCartRecord> records = groupDatas.get(i).getSkuList();
                            for (int j = 0; j < records.size(); j++) {
                                Utils.print(tag, "id==" + records.get(i).getCartId());
                                Utils.print(tag, "name==" + records.get(i).getGoodsName());
                            }
                        }
                    }
                });
    }




    /**
     * 获取购物车选择商品的满增商品已经优惠价格
     */
    public void getShoppingCartDiscount(List<Long> cartItemIds) {
        Utils.print(tag, "getShoppingCartDiscount");

        String input = "";
        try {
            Gson json = new Gson();
            PostCartItemInfo postCartInfo = new PostCartItemInfo();
            postCartInfo.setUserid("3533877");
            postCartInfo.setItemIdList(cartItemIds);
            input = json.toJson(postCartInfo);
            input = input.replace("{","%7B").replace("}","%7D");
            input = input.replace("[","%5B").replace("]","%5D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetShoppingCartDiscount(ConStant.APP_VERSION,input, "781494d5ba2c35a8ae660597f7ea67ef_3533877")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DiscountData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(DiscountData data) {
                        Utils.print(tag, "status==" + data.getErrorMessage() + ",value=" + data.getReturnValue());
                        if(data.getReturnValue()==-1)
                            return;

                        Utils.print(tag,"list size="+data.getData().size());
                        for (int i = 0; i < data.getData().size(); i++) {
                            DiscountType type = data.getData().get(i);
                            Utils.print(tag,"type="+type.getPromotionType());
                            Utils.print(tag,"reachCondition="+type.isReachCondition());
                            List<FullCutSkuData> skuDatas = type.getSkuList();
                            if(skuDatas==null)
                                return;
                            for (int j = 0; j < skuDatas.size(); j++) {
                                Utils.print(tag,"sumRatePrice="+ skuDatas.get(j).getSumRatePrice());
                            }
                        }

                    }
                });
    }


    /**
     * 更新购物车商品
     */
    public void updateShoppingCartCommodity() {
        Utils.print(tag, "updateShoppingCartCommodity");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("cartItemId", 8);
            json.put("goodsSkuSn", "861441359066746880_4");
            json.put("quantity", 10);
            json.put("goodsSn","12222222");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpUpdateShoppingcartData(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 获取首页商品推荐信息
     */
    public void getAllCommodity() {
        Utils.print(tag, "getAllCommodity");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", "1");
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetRecommendData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityData commodityData) {
                        Utils.print(tag, "status==" + commodityData.getErrorMessage());
                        if (commodityData.getReturnValue() == -1)
                            return;
                        List<CommodityRecored> recored = commodityData.getData().getRecords();
                        for (int i = 0; i < recored.size(); i++) {
                            Utils.print(tag, "name=" + recored.get(i).getName());
                        }

                    }
                });
    }


    /**
     * 删除购物车的商品
     */
    public void deleteShoppingCartData() {
        Utils.print(tag, "deleteShoppingCartData");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("cartItemId", "10");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpDeleteShoppingcartData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 获取购物车总价等详情信息
     */
    public void getShoppingCartInfoData() {
        Utils.print(tag, "getShoppingCartInfoData");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("receiveId", 12);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetShoppingcartInfoData(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartInfoData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartInfoData shoppingCartInfoData) {
                        Utils.print(tag, "status==" + shoppingCartInfoData.getErrorMessage());
                        if (shoppingCartInfoData.getReturnValue() == -1)
                            return;
                        ShoppingCartInfo info = shoppingCartInfoData.getData();

                        Utils.print(tag, "total price=" + info.getTotalOrderPrice());

                    }
                });
    }


    /**
     * 获取直接支付总价等详情信息
     */
    public void getShoppingItemInfoData() {
        Utils.print(tag, "getShoppingItemInfoData");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("goodsSkuSn", "861441359066746880_1");
            json.put("quantity", 1);
            json.put("receiveId", 12);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetShoppingItemInfoData(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShoppingCartInfoData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(ShoppingCartInfoData shoppingCartInfoData) {
                        Utils.print(tag, "status==" + shoppingCartInfoData.getErrorMessage());
                        if (shoppingCartInfoData.getReturnValue() == -1)
                            return;
                        ShoppingCartInfo info = shoppingCartInfoData.getData();

                        Utils.print(tag, "total number=" + info.getTotalNumber());

                    }
                });
    }


    /**
     * 获取订单列表数据
     */
    public void getOrderListData() {
        Utils.print(tag, "getOrderData");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("pageIndex", "1");
            json.put("orderStatus",ConStant.ORDER_STATUS_UNPAY); //不传递表示查询所有的
            json.put("orderType",ConStant.ORDER_TYPE_COMMOM);
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetOrderListData(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(OrderData orderData) {
                        Utils.print(tag, "status==" + orderData.getErrorMessage());
                        if (orderData.getReturnValue() == -1)
                            return;
                        List<OrderRecord> orderRecords = orderData.getData().getRecords();

                        for (int i = 0; i < orderRecords.size(); i++) {
                            Utils.print(tag, "time=" + orderRecords.get(i).getCreateTime() + ",orderSn=" + orderRecords.get(i).getOrderSn());
                        }

                    }
                });
    }


    /**
     * 查看订单详情
     */
    public void getOrderItemInfoData() {
        Utils.print(tag, "getOrderItemData");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", "5328718");//ConStant.getInstance(mContext).userID
            json.put("orderSn", "11170024459166942209511424");
            json.put("orderStatus",ConStant.ORDER_STATUS_UNPAY);
            json.put("pageIndex", "1");
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetOrderItemData(ConStant.APP_VERSION,input,"94aeef76ded038ceb1ed13e29b259966_5328718")   //ConStant.getInstance(mContext).Token
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderItemData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(OrderItemData orderItemData) {
                        Utils.print(tag, "status==" + orderItemData.getErrorMessage());
                        if (orderItemData.getReturnValue() == -1)
                            return;

                        List<OrderGiftData> fullGiftList = orderItemData.getData().getFullGiftList();
                        for (int i = 0; i < fullGiftList.size(); i++) {
                            Utils.print(tag, "name=" + fullGiftList.get(i).getGiftName());
                        }

                        List<OrderItemInfo> orderRecords = orderItemData.getData().getOrderContentDataVoPage().getRecords();
                        for (int i = 0; i < orderRecords.size(); i++) {
                            Utils.print(tag, "time=" + orderRecords.get(i).getGoodsName() + ",orderSn=" + orderRecords.get(i).getGoodsTitle());

                            List<OrderGiftData> buyGiftList = orderRecords.get(i).getBuyGiftList();
                            for (int j = 0; j < buyGiftList.size(); j++) {
                                Utils.print(tag,"buy gift name="+buyGiftList.get(j).getGiftName());
                            }
                        }

                    }
                });
    }


    /**
     * 查看订单详情,总价,状态,快递信息
     */
    public void getOrderStatusInfoData() {
        Utils.print(tag, "getOrderStatusInfoData");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("orderStatus",ConStant.ORDER_STATUS_UNPAY);
            json.put("orderSn", "866857048342577152");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetOrderStatusInfoData(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderStatusInfoData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(OrderStatusInfoData orderStatusInfoData) {
                        Utils.print(tag, "status==" + orderStatusInfoData.getErrorMessage());
                        if (orderStatusInfoData.getReturnValue() == -1)
                            return;
                        OrderStatusInfo info = orderStatusInfoData.getData();


                        Utils.print(tag, "time=" + info.getDeliveryCorpCode() + ",xxx=" + info.getStatus());


                    }
                });
    }


    /**
     * 商品直接进入支付订单
     */
    public void commitItemOrderData() {
        Utils.print(tag, "commitItemOrderData");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("quantity", 2);  //商品购买数量
            json.put("goodsSkuSn", "861441359066746880_7"); //商品选择类型SN
            json.put("receiveId", 7); //邮寄地址ID
            json.put("isInvoice", true); //是否需要发票
            json.put("invoiceType", "单位"); //发票抬头
            json.put("invoiceTitle", "深圳水公司"); //发票标题
            json.put("invoiceContent", "本发票用于购物"); //发票用途
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpCommitItemOrderData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommitOrderStatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CommitOrderStatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());

                        if (statusData.getReturnValue() == -1)
                            return;

                        Utils.print(tag, "ordersn=" + statusData.getData().getOrderSn());
                        Utils.print(tag, "price=" + statusData.getData().getAmount());
                    }
                });
    }


    /**
     * 购物车直接进入支付订单
     */
    public void commitShoppingCartOrderData() {
        Utils.print(tag, "commitShoppingCartOrderData");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("cartSn", "866479373920858112"); //购物车sn
            json.put("receiveId", 7);   //地址ID
            json.put("isInvoice", true);
            json.put("invoiceType", "单位");
            json.put("invoiceTitle", "深圳水公司");
            json.put("invoiceContent", "本发票用于购物");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpCommitShoppingCartOrderData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommitOrderStatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CommitOrderStatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;

                        Utils.print(tag, "ordersn=" + statusData.getData().getOrderSn());
                        Utils.print(tag, "price=" + statusData.getData().getAmount());
                    }
                });
    }


    /**
     * 获取售后列表数据
     */
    public void getAfterServiceListData() {
        Utils.print(tag, "getAfterServiceListData");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("pageIndex", 1);
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetAfterServiceData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AfterServiceData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(AfterServiceData afterServiceData) {
                        Utils.print(tag, "status==" + afterServiceData.getErrorMessage());
                        if (afterServiceData.getReturnValue() == -1)
                            return;

                        List<AfterServiceRecord> records = afterServiceData.getData().getRecords();
                        for (int i = 0; i < records.size(); i++) {
                            Utils.print(tag, "type=" + records.get(i).getServiceType());
                            if (records.get(i).getServiceType() == AfterServiceStatus.exchange.ordinal()) {
                                Exchange exchange = records.get(i).getExchangeVo();
                                Utils.print(tag, "reason=" + exchange.getReason());
                                List<ExchangeItem> exchangeItems = exchange.getExchangeItemList();
                                Utils.print(tag, "item size=" + exchangeItems.size());
                            } else if (records.get(i).getServiceType() == AfterServiceStatus.refund.ordinal()) {
                                Refund refund = records.get(i).getRefundVo();
                                Utils.print(tag, "reason=" + refund.getReason());
                                List<RefundItem> refundItems = refund.getRefundItemList();
                                Utils.print(tag, "item size=" + refundItems.size());
                            }
                        }
                    }
                });
    }


    /**
     * 取消售后订单
     */
    public void cancelAfterService() {
        Utils.print(tag, "cancelAfterService");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("serviceType", "2"); //售后方式
            json.put("status", 3); //订单状态
            json.put("orderSn", "866857436105981952");//订单号
            json.put("serviceSn", "867000768740200448"); //售后订单号
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpCancelAfterService(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 申请订单退款
     */
    public void orderRefundService() {
        Utils.print(tag, "orderRefundService");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("orderSn", "870560123397918720"); //订单号
            json.put("status", 3);//订单状态
            json.put("reason", "by error"); //原因
            json.put("totalQuantity", "2"); //数量
            json.put("serviceType", "1"); //退款类型: 2换货 1退款
            json.put("refundType", "2"); //1单件退款 ,2整单退款
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpSubmitOrderService(ConStant.APP_VERSION, input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 获取token
     */
    public void getTokenData() {
        Utils.print(tag, "getTokenData");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();

            json.put("mac", DeviceInfo.getInstance(mContext).getMac());
            json.put("sn", DeviceInfo.getInstance(mContext).getSn());

            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetToken(input)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TokenData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(TokenData tokenData) {
                        Utils.print(tag, "status==" + tokenData.getErrorMessage());
                        if (tokenData.getReturnValue() == -1)
                            return;

                        Utils.print(tag, "userid=" + tokenData.getData().getUserid());
                        Utils.print(tag, "token" + tokenData.getData().getToken());
                    }
                });
    }


    /**
     * 申请售后退款
     */
    public void afterServiceRefund() {
        Utils.print(tag, "afterServiceRefund");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("orderSn", "868297925204156416"); //订单号
            json.put("reason", "by error"); //原因
            json.put("totalQuantity", "2"); //数量
            json.put("status", 3);//订单状态
            json.put("serviceType", "1"); //退款类型: 2换货 1退款
            json.put("refundType", "1"); //1单件退款 ,2整单退款
            json.put("amount", "460"); //总价
            json.put("goodsSkuSn", "861441359066746880_4");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpSubmitGoodsSkuService(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * @param paymentType  支付类型
     * @param amount       支付总价
     * @param paymentName  支付商品名称
     * @param paymentOrder 支付订单号
     *                     获取支付二维码地址
     * @return
     */
    public String httpGetPaymentUrl(String paymentType, String amount,
                                    String paymentName, String paymentOrder, String notify_url) {

        String paymentUrl = "";
        try {
            Log.v(tag, "start get");
            String uri = "http://pay.pthv.gitv.tv/trade/unified/payment/5.json";
            //http://pay.pthv.gitv.tv/trade/unified/payment/5.json  host:60.206.137.177 正式环境
            //http://test.pay.pthv.gitv.tv/trade/unified/payment/5.json 测试环境

            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);
            DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
            HttpResponse response;

            HttpPost httpost = new HttpPost(uri);
            httpost.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            HashMap<String, String> map = new HashMap<>();
            map.put("biz_content", "{\"a\":\"2001633999614633\"}");
            map.put("client_ip", "");  //本地ip 可以为空
            map.put("device_code", DeviceInfo.getInstance(mContext).getModel()); //设置model

            map.put("bundle_id", mContext.getPackageName()); //调用apk包名
            map.put("coupon_amount", "0");  //代金券商场不需求,默认写0
            map.put("device_mac", DeviceInfo.getInstance(mContext).getMac());//设备mac

            map.put("user_uuid", ConStant.getInstance(mContext).userID); //用户中心ID
            map.put("product_icon", "http://pay.pthv.gitv.tv");
            map.put("product_id", "1"); //商品唯一标识，1为默认值

            map.put("product_type", ConStant.product_type);
            map.put("timestamp", Utils.getCurrentDate()); //当前时间
            map.put("device_sn", DeviceInfo.getInstance(mContext).getSn());//设备sn

            map.put("notify_url", notify_url);  //电商服务器回调
            map.put("partner_id", ConStant.partner_id);
            map.put("payment_channel", paymentType); //支付方式

            map.put("product_duration", ConStant.PAYMENT_TIMEOUT); //支付超时时间,默认2小时
            map.put("version", "V-5.0-" + Utils.getAPPVersionCode(mContext)); //当前app 版本号,!!!必须加前缀V-5.0因为后台需要


            map.put("product_name", paymentName); //商品名称
            map.put("total_amount", amount); //总价
            map.put("out_trade_no", paymentOrder);  //订单号

            String domyshop_key = ConStant.domyshop_key;

            String domyshop_value = "";

            domyshop_value = Utils.buildStringQuery(map);
            domyshop_value = domyshop_value + "&key=" + domyshop_key;
            Log.v(tag, "" + domyshop_value);

            String sign = Utils.getMD5(domyshop_value);
            Log.v(tag, "sigh=" + sign);
            map.put("sign", sign);


            List<String> keys = new ArrayList<String>(map.keySet());
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = map.get(key);
                nvps.add(new BasicNameValuePair(key, value));
            }


            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            response = httpclient.execute(httpost);

            HttpEntity entity = response.getEntity();
            String retSrc = EntityUtils.toString(entity);

            Log.v(tag, "rev===" + retSrc);
            JSONObject result = new JSONObject(retSrc);
            String return_code = result.getString("return_code");
            if (return_code.equals("000")) {
                paymentUrl = result.getString("qr_code");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return paymentUrl;
    }


    public String paymentConfirm(String paymentOrder) {

        String paymentUrl = "";
        try {
            Log.v(tag, "start paymentConfirm");
            String uri = "http://pay.pthv.gitv.tv/trade/unified/confirm.json";
            ///trade/unified/confirm.json

            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);
            DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
            HttpResponse response;

            HttpPost httpost = new HttpPost(uri);
            httpost.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            HashMap<String, String> map = new HashMap<>();
            map.put("trace_type", "1");//确认来源：1-退出支付，2-问题帮助
            map.put("device_code", DeviceInfo.getInstance(mContext).getModel()); //设置model
            map.put("device_mac", DeviceInfo.getInstance(mContext).getMac());//设备mac
            map.put("timestamp", Utils.getCurrentDate()); //当前时间
            map.put("device_sn", DeviceInfo.getInstance(mContext).getSn());//设备sn
            map.put("version", "V-5.0-" + Utils.getAPPVersionCode(mContext)); //当前app 版本号,!!!必须加前缀V-5.0因为后台需要
            map.put("trade_no", paymentOrder);  //订单号

            String domyshop_key = ConStant.confirm_payment_key;

            String domyshop_value = "";
            domyshop_value = Utils.buildStringQuery(map);
            domyshop_value = domyshop_value + "&key=" + domyshop_key;
            Log.v(tag, ">>" + domyshop_value);

            String sign = Utils.getMD5(domyshop_value);
            Log.v(tag, "sign=" + sign);
            map.put("sign", sign);


            List<String> keys = new ArrayList<String>(map.keySet());
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = map.get(key);
                nvps.add(new BasicNameValuePair(key, value));
            }


            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            response = httpclient.execute(httpost);

            HttpEntity entity = response.getEntity();
            String retSrc = EntityUtils.toString(entity);

            Log.v(tag, "rev===" + retSrc);
            /*JSONObject result = new JSONObject(retSrc);
            String return_code = result.getString("return_code");
            if(return_code.equals("000")){
                paymentUrl = result.getString("qr_code");
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return paymentUrl;
    }


    /**
     * 获取支付notifyURL
     */
    public void getNotifyUrl() {
        Utils.print(tag, "getNotifyUrl");

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetNotifyUrl(ConStant.APP_VERSION,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NotifyData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(NotifyData notifyData) {
                        Utils.print(tag, "status==" + notifyData.getErrorMessage());
                        if (notifyData.getReturnValue() == -1)
                            return;

                        Utils.print(tag, "url=" + notifyData.getData().getNotifyUrl());

                    }
                });
    }


    /**
     * 获取支付结果状态
     */
    public void getPaymentStatus() {
        Utils.print(tag, "getPaymentStatus");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("orderSn", "868297925204156416"); //订单号
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetPaymentStatus(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 获取专题列表
     */
    public void getSpecialList() {
        Utils.print(tag, "getSpecialList");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("specSn", "399362214055645184"); //专题号
            json.put("pageIndex", 1);
            json.put("pageSize", 10);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetSpecialData(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SpecialData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(SpecialData specialData) {
                        Utils.print(tag, "status==" + specialData.getErrorMessage() + ",value=" + specialData.getReturnValue());
                        if (specialData.getReturnValue() == -1)
                            return;
                    }
                });
    }


    /**
     * 获取售后列表头部的电话
     */
    public void getAfterServicePhone() {
        Utils.print(tag, "getAfterServicePhone");

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetAfterServicePhone(ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommodityPhoneData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(CommodityPhoneData commodityPhoneData) {
                        Utils.print(tag, "status==" + commodityPhoneData.getErrorMessage() + ",value=" + commodityPhoneData.getReturnValue());
                        if (commodityPhoneData.getReturnValue() == -1)
                            return;

                        Utils.print(tag, "phone=" + commodityPhoneData.getData().get(0).getPhone());
                    }
                });
    }


    /**
     * 获取用户中心二维码地址
     */
    public void getReceiveQr() {
        Utils.print(tag, "getReceiveQr");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            //type=1 新增收件人地址,无须receiverId字段
            //type=2 修改收件人地址,需要传入receiverId字段
            json.put("type", 1);
            json.put("receiverId", 12);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "2input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetReceiverQr(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<QrData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(QrData qrData) {
                        Utils.print(tag, "status==" + qrData.getErrorMessage() + ",value=" + qrData.getReturnValue());
                        if (qrData.getReturnValue() == -1)
                            return;
                        Utils.print(tag, "qr==" + qrData.getData().getQrData());
                        Utils.print(tag, "qr==" + qrData.getData().getTimeStamp());
                    }
                });
    }




    /**
     * 获取sku　具体商品规格促销信息
     */
    public void getSkuPromotionInfo() {
        Utils.print(tag, "getSkuPromotionInfo");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("goodsSkuSn", "427984796640088064_1");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "2input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetSkuPromotionData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ProData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(ProData data) {
                        Utils.print(tag, "status==" + data.getErrorMessage() + ",value=" + data.getReturnValue());
                        if (data.getReturnValue() == -1)
                            return;

                        PromotionListData promotionData = data.getData();
                        List<PromotionRecord> buygiftlist = promotionData.getBuyGiftList();
                        for (int i = 0; i < buygiftlist.size(); i++) {
                            Utils.print(tag,"title="+buygiftlist.get(i).getPromotionTitle()+",type="+buygiftlist.get(i).getPromotionType());
                            Utils.print(tag,"remark="+buygiftlist.get(i).getPromotionNote());

                            List<PromotionGift> gifts = buygiftlist.get(i).getGiftList();
                            for (int j = 0; j < gifts.size(); j++) {
                                Utils.print(tag,"name="+gifts.get(j).getGiftName());
                            }
                        }


                        List<PromotionRecord> fullgiftlist = promotionData.getFullGiftList();
                        for (int i = 0; i < fullgiftlist.size(); i++) {
                            Utils.print(tag,"title="+fullgiftlist.get(i).getPromotionTitle()+",type="+fullgiftlist.get(i).getPromotionType());
                            Utils.print(tag,"remark="+fullgiftlist.get(i).getPromotionNote());

                            List<PromotionGift> gifts = fullgiftlist.get(i).getGiftList();
                            for (int j = 0; j < gifts.size(); j++) {
                                Utils.print(tag,"name="+gifts.get(j).getGiftName());
                            }
                        }
                    }
                });
    }





    /**
     * 获取spu　商品促销信息
     */
    public void getSpuPromotionInfo() {
        Utils.print(tag, "getSpuPromotionInfo");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("goodsSn", "427984796640088064");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "2input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetSpuPromotionData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ProData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(ProData data) {
                        Utils.print(tag, "status==" + data.getErrorMessage() + ",value=" + data.getReturnValue());
                        if (data.getReturnValue() == -1)
                            return;

                        PromotionListData promotionData = data.getData();
                        List<PromotionRecord> buygiftlist = promotionData.getBuyGiftList();
                        Utils.print(tag,"1=="+buygiftlist.size());
                        for (int i = 0; i < buygiftlist.size(); i++) {
                            Utils.print(tag,"title="+buygiftlist.get(i).getPromotionTitle()+",type="+buygiftlist.get(i).getPromotionType());
                            Utils.print(tag,"remark="+buygiftlist.get(i).getPromotionNote());

                            List<PromotionGift> gifts = buygiftlist.get(i).getGiftList();
                            Utils.print(tag,"1=="+gifts.size());
                            for (int j = 0; j < gifts.size(); j++) {
                                Utils.print(tag,"name="+gifts.get(j).getGiftName());
                            }
                        }


                        /*List<PromotionRecord> fullgiftlist = promotionData.getFullGiftList();
                        for (int i = 0; i < fullgiftlist.size(); i++) {
                            Utils.print(tag,"title="+fullgiftlist.get(i).getPromotionTitle()+",type="+fullgiftlist.get(i).getPromotionType());
                            Utils.print(tag,"remark="+fullgiftlist.get(i).getPromotionNote());

                            List<PromotionGift> gifts = fullgiftlist.get(i).getFullGiftList();
                            for (int j = 0; j < gifts.size(); j++) {
                                Utils.print(tag,"name="+gifts.get(j).getGiftName());
                            }
                        }*/

                    }
                });
    }





    /**
     * 获取凑单信息
     */
    public void getMergeCommodityInfo() {
        Utils.print(tag, "getMergeCommodityInfo");

        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("promotionSn", "460245798966267904");
            json.put("pageIndex", 1);
            json.put("pageSize", 10);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "2input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetMergeCommodityData(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MergeCommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(MergeCommodityData data) {
                        Utils.print(tag, "status==" + data.getErrorMessage() + ",value=" + data.getReturnValue());
                        if (data.getReturnValue() == -1)
                            return;

                        MergeCommodity mergeCommodity = data.getData();
                        Utils.print(tag,"title=="+mergeCommodity.getPromotionTitle());
                        List<CommodityRecored> records = data.getData().getMegerGoodsVoPage().getRecords();
                        Utils.print(tag,"size="+records.size());
                        for (int i = 0; i < records.size(); i++) {
                            Utils.print(tag,"name="+records.get(i).getName());
                            Utils.print(tag,"type="+records.get(i).getPromotionType());
                        }

                    }
                });
    }


    /**
     * 删除所有失效的购物车商品
     */
    public void deleteAllInvalidCart() {
        Utils.print(tag, "deleteAllInvalidCart");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("cartSn", "426460797535784960"); //购物车id
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpDeleteAllInvalidCart(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag, "status==" + statusData.getErrorMessage() + ",value=" + statusData.getReturnValue());
                        if (statusData.getReturnValue() == -1)
                            return;
                    }
                });
    }



    /**
     * 获取搜索二维码地址
     */
    public void getSearchQr() {
        Utils.print(tag, "getSearchQr");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetSearchQr(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<QrData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(QrData qrData) {
                        Utils.print(tag, "status==" + qrData.getErrorMessage() + ",value=" + qrData.getReturnValue());
                        if (qrData.getReturnValue() == -1)
                            return;
                        Utils.print(tag, "qr==" + qrData.getData().getQrData());
                        Utils.print(tag, "qr==" + qrData.getData().getTimeStamp());
                    }
                });
    }



    /**
     * 获取发票二维码地址
     */
    public void getInvoiceQr() {
        Utils.print(tag, "getInvoiceQr");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpGetInvoiceQr(input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<QrData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(QrData qrData) {
                        Utils.print(tag, "status==" + qrData.getErrorMessage() + ",value=" + qrData.getReturnValue());
                        if (qrData.getReturnValue() == -1)
                            return;
                        Utils.print(tag, "qr==" + qrData.getData().getQrData());
                        Utils.print(tag, "qr==" + qrData.getData().getTimeStamp());
                    }
                });
    }




    /**
     * 判断是否能够购买,是否已经超过限制购买
     */
    public void getOrderLimitBuy() {
        Utils.print(tag, "getOrderLimitBuy");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", ConStant.getInstance(mContext).userID);
            json.put("orderSn", "11105845429360123501088768"); //购物车id
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subscription subscription = RetrofitClient.getCommodityAPI()
                .httpOrderLimitBuy(ConStant.APP_VERSION,input, ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderLimitData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag, "error=" + e.getMessage());
                    }

                    @Override
                    public void onNext(OrderLimitData data) {
                        Utils.print(tag, "status==" + data.getErrorMessage() + ",value=" + data.getReturnValue());
                        if (data.getReturnValue() == -1)
                            return;
                        Utils.print(tag, "canpuy==" + data.getData().isCanPay());

                    }
                });
    }






    /**
     * 获取拍卖商品列表
     */
    public void getAcutionListData(){
        Utils.print(tag,"getAcutionListData");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", 1);
            json.put("pageSize", 10);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getAcutionLauAPI()
                .httpGetAcutionListData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AcutionCommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(AcutionCommodityData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData()!=null){
                            List<AcutionItemData> recores = statusData.getData().getRecords();
                            for (int i = 0; i < recores.size() ; i++) {
                                Utils.print(tag,recores.get(i).getAuctionVo().getGoodsName());
                            }
                        }

                    }
                });

    }





    /**
     * 获取拍卖商品列表
     */
    public void getAcutionInfoData(){
        Utils.print(tag,"getAcutionInfoData");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("sn", "488878363671535616");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getAcutionLauAPI()
                .httpGetAcutionInfoData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AcutionInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(AcutionInfo statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData()!=null){
                            AcutionItemData itemData = statusData.getData();
                            Utils.print(tag,"name=="+itemData.getAuctionVo().getGoodsName());
                        }

                    }
                });

    }




    /**
     * 获取拍卖未支付的订单信息
     */
    public void getAcutionUnpayData(){
        Utils.print(tag,"getAcutionUnpayData");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid", "12456789"/*ConStant.getInstance(mContext).userID*/);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetAcutionUnpayData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UnpayOrderData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData()!=null){

                            Utils.print(tag,"isHaveUnpayOrder=="+statusData.getData().isHaveUnpayOrder());
                            Utils.print(tag,"url=="+statusData.getData().getThumbnail());
                        }

                    }
                });

    }



    /**
     * 更新拍卖未支付的订单的发票信息
     */
    public void getUpdateAcutionInvoiceData(String orderSn){
        Utils.print(tag,"getUpdateAcutionInvoiceData");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("orderSn",orderSn); //订单号
            json.put("invoiceId", 7); //邮寄地址ID
            json.put("isInvoice", true); //是否需要发票
            json.put("invoiceType", "单位"); //发票抬头
            json.put("invoiceTitle", "深圳水公司"); //发票标题
            json.put("invoiceContent", "本发票用于购物"); //发票用途
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpUpdateAcutionInvoiceData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UnpayOrderUpdateData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderUpdateData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData()!=null){

                            Utils.print(tag,"orderSn=="+statusData.getData().getOrderSn());
                        }

                    }
                });

    }




    /**
     * 更新拍卖未支付的订单的地址信息
     */
    public void getUpdateAcutionReceiveData(String orderSn){
        Utils.print(tag,"getUpdateAcutionReceiveData");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("orderSn",orderSn); //订单号
            json.put("receiveId", 26441); //邮寄地址ID
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpUpdateAcutionReceiveData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UnpayOrderUpdateData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(UnpayOrderUpdateData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        if(statusData.getData()!=null){

                            Utils.print(tag,"orderSn=="+statusData.getData().getOrderSn());
                        }

                    }
                });

    }



    /**
     * 拍卖的商品参加竞拍
     */
    public void getAcutionAddPriceData(){
        Utils.print(tag,"getAcutionAddPriceData");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("auctionSn","19495679594645426176"); //订单号
            json.put("currentPrice", 3); //当前价格
            json.put("userid",ConStant.getInstance(mContext).userID);// 用户ID
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetAcutionAddPriceData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AddPriceData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(AddPriceData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                    }
                });

    }





    /**
     * 拍卖的商品参加竞拍
     */
    public void getCancelAcutionOrder(){
        Utils.print(tag,"getCancelAcutionOrder");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("orderSn","11144313496095979414491136"); //订单号
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }


        Subscription s = RetrofitClient.getCommodityAPI()
                .httpCancelAcutionOrder(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CancelOrderData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CancelOrderData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }
                        Utils.print(tag,"sn=="+statusData.getData().getOrderSn());

                    }
                });
    }



    /**
     * 拍卖的商品参加竞拍
     */
    public void getAcutionServerTime(){
        Utils.print(tag,"getAcutionServerTime");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID); //订单号
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }


        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetAcutionServerTime(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ServerTimeData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(ServerTimeData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        Utils.print(tag,"servertime=="+statusData.getData().getTimeStamp());
                        Date date =  new Date(System.currentTimeMillis());
                        Utils.print(tag,"div=="+Math.abs(date.getTime()-statusData.getData().getTimeStamp()));
                    }
                });
    }



    /**
     * 商品直接进入支付订单
     */
    public void commitEncryptItemOrderData(){
        Utils.print(tag,"commitEncryptItemOrderData");

        String input="";
        String sign="";
        /*try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("quantity",info.getTotalNumber());  //商品购买数量
            json.put("goodsSkuSn",directPaymentData.getGoodskuSn()); //商品选择类型SN
            json.put("receiveId",receiveId); //邮寄地址ID
            if(invoiceType==ConStant.INVOICE_NULL){
                json.put("isInvoice",false);
            }else if(invoiceType==ConStant.INVOICE_PERSON){
                json.put("isInvoice",true);
                json.put("invoiceType",invoice_info_value[0]); //发票抬头
            }else if(invoiceType==ConStant.INVOICE_COMPANY){
                json.put("isInvoice",true); //是否需要发票
                json.put("invoiceType", invoice_info_value[0]); //发票抬头
                json.put("invoiceTitle",Utils.formatInvalidString(invoice_info_value[1])); //发票标题
                json.put("invoiceContent",invoice_info_value[3]); //发票用途
                json.put("invoiceId",invoice_info_value[2]); //纳税人编号
            }

            //*//*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sigh="+sign);
            //*//*********
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpCommitEncryptItemOrderData(ConStant.APP_VERSION,input,sign,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommitOrderStatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CommitOrderStatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                    }
                });
    }



    /**
     * 购物车直接进入支付订单(加密接口)
     */
    public void commitEncryptShoppingCartOrderData(){
        Utils.print(tag,"commitEncryptShoppingCartOrderData");
        String input="";
        String sign="";
        try{
            Gson gson = new Gson();
            //用户提交服务器实体类
            ShoppingPostOrderInfo postOrderInfo = new ShoppingPostOrderInfo();
            //map 用户计算md5
            org.json.JSONObject json = new org.json.JSONObject();

            //userid
            postOrderInfo.setUserid(ConStant.getInstance(mContext).userID);
            json.put("userid",ConStant.getInstance(mContext).userID);

            //cardSn
            //postOrderInfo.setCartSn(shoppingCartRecords.get(0).getCartSn());//购物车sn
            //json.put("cartSn",shoppingCartRecords.get(0).getCartSn()); //购物车sn

            //receiveId
            //postOrderInfo.setReceiveId(receiveId);
            //json.put("receiveId",receiveId);

            /*if(invoiceType==ConStant.INVOICE_NULL){
                postOrderInfo.setInvoice(false);
                json.put("isInvoice",false);
            }else if(invoiceType==ConStant.INVOICE_PERSON){
                postOrderInfo.setInvoice(true);
                postOrderInfo.setInvoiceType(invoice_info_value[0]);//发票抬头

                json.put("isInvoice",true);
                json.put("invoiceType",invoice_info_value[0]); //发票抬头
            }else if(invoiceType==ConStant.INVOICE_COMPANY){
                postOrderInfo.setInvoice(true);//是否需要发票
                postOrderInfo.setInvoiceType(invoice_info_value[0]);//发票抬头
                postOrderInfo.setInvoiceTitle(Utils.formatInvalidString(invoice_info_value[1])); //发票标题
                postOrderInfo.setInvoiceContent(invoice_info_value[3]); //发票用途
                postOrderInfo.setInvoiceId(invoice_info_value[2]); //纳税人编号

                json.put("isInvoice",true); //是否需要发票
                json.put("invoiceType", invoice_info_value[0]); //发票抬头
                json.put("invoiceTitle", Utils.formatInvalidString(invoice_info_value[1])); //发票标题
                json.put("invoiceContent",invoice_info_value[3]); //发票用途
                json.put("invoiceId",invoice_info_value[2]); //纳税人编号
            }
            postOrderInfo.setItemIdList(itemIdList);
            if(itemIdList.size()>0){
                json.put("itemIdList",itemIdList);
            }*/

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sigh="+sign);
            ///*********

            input = gson.toJson(postOrderInfo);
            input = input.replace("{","%7B").replace("}","%7D");
            input = input.replace("[","%5B").replace("]","%5D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpCommitEncryptShoppingcartOrderData(ConStant.APP_VERSION,input,sign,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommitOrderStatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(CommitOrderStatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());
                    }
                });
    }




    /**
     * 获取拍卖详情大图
     */
    public void getAcutionDetail(){
        Utils.print(tag,"getAcutionDetail");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("goodsSn","505171336982036480");
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Log.v(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Log.v(tag,"sign="+sign);
            ///*********

        }catch (Exception e){
            e.printStackTrace();
        }


        Subscription s= RetrofitClient.getCommodityAPI()
                .httpGetAcutionDetail(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DetailData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(DetailData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                        if(statusData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,statusData.getErrorMessage());
                            return;
                        }

                        for (int i = 0; i < statusData.getData().getDetailUrl().size(); i++) {
                            Utils.print(tag,"url="+statusData.getData().getDetailUrl().get(i));
                        }

                    }
                });


                 
    }
}
