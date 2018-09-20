package com.hiveview.dianshang.auction;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hiveview.dianshang.auction.database.AcutionBean;
import com.hiveview.dianshang.auction.database.AcutionDao;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.acution.common.AcutionItemData;
import com.hiveview.dianshang.entity.acution.listdata.AcutionCommodityData;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.hiveview.dianshang.home.InstallService.ENTER;
import static com.hiveview.dianshang.home.InstallService.EXIT;


/**
 * Created by zwj on 3/28/18.
 */

public class AcutionService extends Service{


    private final String tag = "AcutionService";

    private int pageIndex=1;
    private int maxPage=1;
    private int PAGE_SIZE = 200;

    private Subscription s;

    private List<String> acutionSns = new ArrayList<>();

    private Context mContext;

    private int requestCount = 0;
    private boolean isHandle = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mContext = this;
        if(intent==null)
            return;

        if(intent.getIntExtra("status",ENTER)==ENTER){
            Utils.print(tag,"enter");
            getAcutionListData(true);
        }else if(intent.getIntExtra("status",ENTER)==EXIT){
            Utils.print(tag,"exit");
            stopSelf();
        }
    }


    /**
     * 获取拍卖商品列表
     */
    public void getAcutionListData(boolean init){
        Utils.print(tag,"getAcutionListData");
        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", pageIndex);
            json.put("pageSize", PAGE_SIZE);
            input = json.toString();
            input = input.replace("{", "%7B").replace("}", "%7D");
            Utils.print(tag, "input=" + input);

            ///*********
            String domyshop_order_key = ConStant.domyshop_order_key;
            String domyshop_value = "";
            domyshop_value = Utils.buildObjectQuery(Utils.buildMap(json));
            domyshop_value = domyshop_value + "&key="+domyshop_order_key;
            domyshop_value = domyshop_value.replace(" ","");
            Utils.print(tag,""+domyshop_value);
            sign = Utils.getMD5(domyshop_value);
            Utils.print(tag,"sign="+sign);
            ///*********
        }catch (Exception e){
            e.printStackTrace();
        }

        s = RetrofitClient.getAcutionLauAPI()
                .httpGetAcutionListData(input,sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AcutionCommodityData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"1error="+e.getMessage());
                    }

                    @Override
                    public void onNext(AcutionCommodityData commodityData) {
                        Utils.print(tag,"status=="+commodityData.getErrorMessage()+",value="+commodityData.getReturnValue());

                        if(commodityData.getReturnValue()==-1){
                            return;
                        }

                        //计算最大页数
                        if(init && commodityData.getData().getTotalCount()>0){
                            int totalCount = commodityData.getData().getTotalCount();
                            maxPage = totalCount / PAGE_SIZE;
                            Utils.print(tag,"maxpage=="+maxPage);
                            if(totalCount%PAGE_SIZE!=0){
                                maxPage++;
                            }
                            Utils.print(tag,"totalcount="+totalCount+",maxpage="+maxPage);
                        }
                        List<AcutionItemData> recored = commodityData.getData().getRecords();
                        if(recored==null){
                            return;
                        }

                        for (int i = 0; i <recored.size() ; i++) {
                            acutionSns.add(recored.get(i).getAuctionVo().getAuctionSn());
                        }

                        if(init && maxPage>1){
                            for (int i = 1; i < maxPage; i++) {
                                pageIndex++;
                                Utils.print(tag,"current page=="+pageIndex);
                                getAcutionListData(false);

                            }
                        }

                        //请求服务器,返回
                        requestCount++;

                        Utils.print(tag,"requestCount="+requestCount);
                        if(requestCount==pageIndex && !isHandle){
                            isHandle=true;
                            handDatabase();
                        }
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(s!=null){
            s.unsubscribe();
        }
        acutionSns.clear();
    }


    private void handDatabase(){
        Utils.print(tag,"handDatabase");
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                List<AcutionBean> beans = AcutionDao.getInstance(mContext).queryAll();
                if(beans!=null){
                    Utils.print(tag,"table size="+beans.size());
                     try {
                         for (int i = 0; i < beans.size(); i++) {
                             String sn = beans.get(i).getSn();
                             Utils.print(tag,"compare sn=="+sn);
                             if(!querySn(sn)){
                                 Utils.print(tag,"delete sn=="+sn);
                                 AcutionDao.getInstance(mContext).delete(sn);
                             }
                         }
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                    stopSelf();
                }

            }
        });

        s = observable.subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                    }
                });
    }



    private boolean querySn(String acutionSn){
        boolean result = false;
        for (int i = 0; i < acutionSns.size(); i++) {
            if(acutionSn.equals(acutionSns.get(i))){
                result = true;
                break;
            }
        }
        return result;
    }



}
