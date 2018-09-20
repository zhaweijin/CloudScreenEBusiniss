package com.hiveview.dianshang.showcommodity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.adapter.PromotionAdapter;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.commodity.promotion.ProData;
import com.hiveview.dianshang.entity.commodity.promotion.PromotionListData;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class PromotionInformation extends BaseActivity{

    public String tag = "PromotionInformation";
    private Context mContext = PromotionInformation.this;
    private static int data;
    //存储后台解释权文本
    private String expressNote;
    //发送给后台的商品数据
    private static String  mskuSn="";

    //最终解释权
    @BindView(R.id.express)
    TextView express;

    @BindView(R.id.homeList)
    ListView homeList;

    private PromotionAdapter promotionAdapter;

    private final static int DATA_NULL = 0;
    private final static int SERVER_ERROR = 1;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DATA_NULL:
                    Utils.print(tag,"handler here &&&&");
                    String data_null = mContext.getResources().getString(R.string.promotion_information_null);
                    ToastUtils.showToast(mContext,data_null);
                    express.setText("");
                    finish();
                    break;
                case SERVER_ERROR:
                    String server_error = mContext.getResources().getString(R.string.error_service_exception);
                    ToastUtils.showToast(mContext,server_error);
                    finish();
                    break;
            }
        }
    };

    /**
     * 商品详情进入促销信息界面
     * @param activity
     * @param skusn
     */
    public static void launch(Activity activity, String skusn) {
        Intent intent = new Intent(activity, PromotionInformation.class);
        intent.putExtra("skusn",skusn);
        mskuSn=skusn;
        activity.startActivity(intent);
    }

    /**
     * 商品规格页进入促销详情界面
     * @param activity
     * @param skusn
     * @param index
     */
    public static void launch(Activity activity, String skusn,int index) {
        Intent intent = new Intent(activity, PromotionInformation.class);
        intent.putExtra("skusn",skusn);
        mskuSn=skusn;
        data = index;
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        Utils.print(tag,"index is "+data);
        startProgressDialog();
        if (data==0) {
            // 获取spu　商品促销信息
            getSpuPromotionInfo();
        }else {
            // 获取sku　具体商品规格促销信息
            getSkuPromotionInfo();
        }
    }

    /**
     * 获取sku　具体商品规格促销信息
     */
    public void getSkuPromotionInfo() {
        Utils.print(tag, "getSkuPromotionInfo");
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("goodsSkuSn", mskuSn);//"427984796640088064_1"
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
                        express.setText("");
                        stopProgressDialog();
                        Utils.print(tag, "error=" + e.getMessage());
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        mHandler.sendEmptyMessage(SERVER_ERROR);
                    }

                    @Override
                    public void onNext(ProData data) {
                        Utils.print(tag, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>获取sku　具体商品规格促销信息>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        Utils.print(tag, "sku>>status=============" + data.getErrorMessage() + ",    sku>>value=" + data.getReturnValue());
                        Utils.print(tag, "sku>>data is null==========" + (data.getData() == null) + "   -----sku>>getPromotionExplain is =" + data.getData().getPromotionExplain());
                        Utils.print(tag, "sku>>value=" + data.getReturnValue());
                        stopProgressDialog();
                        if (data.getReturnValue() == -1) {
                            mHandler.sendEmptyMessage(DATA_NULL);
                            return;
                        }

                        //初始化策略界面数据
                        PromotionListData skuPromotionData = data.getData();
                        if (skuPromotionData != null) {
                            expressNote = skuPromotionData.getPromotionExplain();//获取最终解释权信息
                            express.setText(expressNote);

                            promotionAdapter = new PromotionAdapter(mContext,skuPromotionData);
                            homeList.setAdapter(promotionAdapter);
                            promotionAdapter.notifyDataSetChanged();
                        }
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 获取spu　商品促销信息
     */
    public void getSpuPromotionInfo() {
        Utils.print(tag, "getSpuPromotionInfo");
        Utils.print(tag,"mskuSn <<<<<<<>>>>>>>>>> is "+mskuSn);
        String input = "";
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("goodsSn", mskuSn);//"427984796640088064"
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
                        express.setText("");
                        stopProgressDialog();
                        String error_tips = "";
                        if(Utils.isConnected(mContext)){
                            error_tips = mContext.getResources().getString(R.string.error_service_exception);
                        }else{
                            error_tips = mContext.getResources().getString(R.string.error_network_exception);
                        }
                        Utils.print(tag, "error=" + e.getMessage());
                        mHandler.sendEmptyMessage(SERVER_ERROR);
                    }

                    @Override
                    public void onNext(ProData data) {
                        Utils.print(tag, "data is null==========" + (data.getData().toString()) + "   -----getPromotionExplain is =" + data.getData().getPromotionExplain());
                        Utils.print(tag, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 获取spu　商品促销信息>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                        Utils.print(tag, "status=============" + data.getErrorMessage() + ",     value=" + data.getReturnValue());
                        Utils.print(tag, "data is null==========" + (data.getData() == null) + "   -----getPromotionExplain is =" + data.getData().getPromotionExplain());
                        Utils.print(tag, "value=" + data.getData().getFullGiftList().size());
                        stopProgressDialog();
                        if (data.getReturnValue() == -1) {
                            Utils.print(tag,"data is null");
                            mHandler.sendEmptyMessage(DATA_NULL);
                            return;
                        }
                        PromotionListData promotionData = data.getData();
                        if (promotionData != null) {
                            expressNote = promotionData.getPromotionExplain();//获取最终解释权信息
                            express.setText(expressNote);

                            promotionAdapter = new PromotionAdapter(mContext,promotionData);
                            homeList.setAdapter(promotionAdapter);
                            promotionAdapter.notifyDataSetChanged();

                        }
                    }
                });
        addSubscription(subscription);
    }

}
