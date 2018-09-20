package com.hiveview.dianshang.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.entity.payment.GoodsItem;
import com.hiveview.dianshang.entity.payment.Payment;
import com.hiveview.dianshang.entity.payment.PaymentData;
import com.hiveview.dianshang.utils.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;



/**
 * 测试接口
 */
public class APITestActivity extends BaseActivity  {


    private String tag = "main";

    @BindView(R.id.image)
    ImageView image;

    APIServer apiServer;


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, APITestActivity.class);
        activity.startActivity(intent);
    }

    public static void launch(Activity activity,String tt) {
        Intent intent = new Intent(activity, APITestActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);




        Utils.print(tag,"222");
        //TestActivity.launch(this);
        //CommodityInfomation.launch(this);
        //SubmitOrder.launch(this);
        //ShowCommodity.launch(this);
        //AfterSaleServiceMain.launch(this);
        //EditAddress.launch(this);
        //SearchMain.launch(this);

//        activity.filmListFromHotAdapter.setOnActionListener(activity);


        //new DialogUtil().showAddShoppingCart(this,R.layout.add_shopping_cart_sucess);
        //new DialogUtil().showCancelOrderDialog(this,R.layout.cancel_order_reason);
        //new DialogUtil().showHandleOrderSaleService(this,R.layout.handle_sucess_order_sale_service);
        //new DialogUtil().showHandleDeleteDialog(this,R.layout.handle_delete,"ssssss");
        //new DialogUtil().showTimeoutPayDialog(this);
        //new DialogUtil().showPaySucessDialog(this);
        //Utils.print("test",DeviceInfo.getInstance(mContext).getMac());


        Utils.print(tag, "start.....");

        /*Subscription subscription = RetrofitClient.createService(MplayerAPI.class)
                //.httpsGetUser("dataprovider","1.0")
                .httpsGetUser2("ip","isGroupUser.json","dataprovider","1.0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<IspData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(IspData ispData) {
                        Utils.print("test","bbbb="+ispData.getMessage());
                    }
                });*/


        apiServer = new APIServer(this);
        //apiServer.getProviceList();
        //apiServer.getAreaList("110000");
        //apiServer.getUserAddressList();

        //apiServer.addUserAddress();
        //apiServer.getExpressInfo();
        //apiServer.updateUserAddress(new UserData());


        //apiServer.getOneLevelCategory();
        //apiServer.getLevelCategory();
        //apiServer.getCommodity();
        //apiServer.getCommodityDetail();
        //apiServer.getCommoditySkuInfo();


        //apiServer.getAdvertisement();
        //apiServer.getHintMessage();
        //apiServer.getAllCommodity();
        //apiServer.getSearchKeyData();
        //apiServer.getSearchResultData(getResources().getString(R.string.test_add_commodity));
        //apiServer.getSearchRecommendData();
        //apiServer.addFavoriteCommodity();
        //apiServer.deleteFavoriteCommodity();

        //apiServer.getAllFavoriteData();

        //apiServer.addShoppingCartCommodity();
        //apiServer.getShoppingCartCommodity();

        List<Long> list = new ArrayList<>();
        list.add(5155l);
        list.add(5157l);
        list.add(5163l);
        list.add(5164l);
        list.add(5166l);
        list.add(5167l);
        list.add(5168l);
        list.add(5169l);
        list.add(5171l);
        list.add(5172l);
        list.add(5173l);
        //apiServer.getShoppingCartDiscount(list);
        //apiServer.deleteShoppingCartData();
        //apiServer.clearShoppingCartCommodity();
        //apiServer.updateShoppingCartCommodity();

        //apiServer.deleteUserAddress(19);适配器
        //apiServer.getShoppingCartInfoData();
        //apiServer.getShoppingItemInfoData();

        //获取订单列表数据
        //apiServer.getOrderListData();
        //查看订单详情
        //apiServer.getOrderItemInfoData();//有一个在适配器中
        //获取订单详情右边的信息
        //apiServer.getOrderStatusInfoData();

        //提交订单
        //apiServer.commitItemOrderData();
        //购物车提交订单
        //apiServer.commitShoppingCartOrderData();





        //获取售后列表数据
        //apiServer.getAfterServiceListData();
        //取消售后申请
        //apiServer.cancelAfterService();

        //订单申请退款
        //apiServer.orderRefundService();
        //售后申请退款
        //apiServer.afterServiceRefund();
        //apiServer.getTokenData();


        //apiServer.getNotifyUrl();
        //apiServer.getPaymentStatus();


        //获取专题列表
        //apiServer.getSpecialList();

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                apiServer.paymentConfirm("11165404402270452862423040");
            }
        }).start();*/

        //apiServer.getShoppingCartPrice();

        //apiServer.getPaymentLongConnect();

        //获取售后电话
        //apiServer.getAfterServicePhone();
        //apiServer.getRecommendSpecialList();
        //apiServer.getReceiveQr();


        //商品的规格促销信息
        //apiServer.getSkuPromotionInfo();

        //商品的促销信息
        //apiServer.getSpuPromotionInfo();


        //apiServer.getMergeCommodityInfo();

        //apiServer.deleteAllFavoriteCommodity();

        //删除购物车无效商品
        //apiServer.deleteAllInvalidCart();

        //apiServer.getSearchQr();

        //apiServer.getInvoiceQr();

        //判断是否能继续购买,用于已经生产订单然后去支付
        //apiServer.getOrderLimitBuy();


        //apiServer.getAcutionListData();
        //apiServer.getAcutionInfoData();
        //apiServer.getAcutionUnpayData();
        //apiServer.getUpdateAcutionInvoiceData("488878363671535616");
        //apiServer.getUpdateAcutionReceiveData("488878363671535616");
        //apiServer.getAcutionAddPriceData();
        //apiServer.getCancelAcutionOrder();

//        apiServer.getAcutionServerTime();
        apiServer.getAcutionDetail();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Utils.print(tag,"back");

        }else if(keyCode==KeyEvent.KEYCODE_MENU){
            MainActivity.launch((APITestActivity)mContext,"ssss");
        }
        return super.onKeyDown(keyCode, event);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void testGoodDetail(){
        Payment payment = new Payment();
        List<PaymentData> paymentDatas = new ArrayList<>();
        for(int i=0;i<2;i++){
            PaymentData paymentData = new PaymentData();
            List<GoodsItem> goodsItems = new ArrayList<>();
            for(int j=0;j<2;j++){
                GoodsItem goodsItem = new GoodsItem();
                goodsItem.setBody("test"+j);
                goodsItem.setPrice(33);
                goodsItem.setProduct_id("衣服");
                goodsItem.setQuailty(3);
                goodsItems.add(goodsItem);
            }
            paymentData.setGood_items(goodsItems);
            paymentData.setDomypay_id("编号"+i);
            paymentDatas.add(paymentData);
        }
        payment.setGood_groups(paymentDatas);

        Gson gson = new Gson();
        Utils.print(tag,gson.toJson(payment));
    }
}
