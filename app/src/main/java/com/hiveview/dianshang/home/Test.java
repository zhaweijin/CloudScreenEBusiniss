package com.hiveview.dianshang.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.hiveview.dianshang.auction.database.AcutionDao;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.StatusData;
import com.hiveview.dianshang.entity.acution.common.AcutionItemData;
import com.hiveview.dianshang.entity.acution.listdata.AcutionCommodityData;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hiveview.dianshang.utils.httputil.RetrofitClient.getAcutionLauAPI;

/**
 * Created by carter on 5/8/17.
 */

public class Test extends BaseActivity {


    private String tag = "test";



    public static void launch(Activity activity,String aa) {
        Intent intent = new Intent(activity, Test.class);
        activity.startActivity(intent);
    }


    private RecyclerView recycler;

    private boolean initRequest = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_sample);


        /*ImageView imageView = (ImageView)findViewById(R.id.image);
        Glide.with(this).load("http://60.206.137.209/group2/M00/00/03/PM6J0Vmx-bOACri8AAAfeMnJnGw244.jpg").into(imageView);
*/
        /*recycler = (RecyclerView)findViewById(R.id.tv_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recycler.setLayoutManager(layoutManager);

        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < 220; i++) {
            items.add("item" + i);
        }



        recyclerHeader = (RecyclerViewHeader) findViewById(R.id.top_menu);
        recyclerHeader.attachTo(recycler);


        recycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(initRequest){
                    initRequest = false;
                    recyclerHeader.requestFocus();
                }
            }
        });*/



        getMoreCommodity();



        List<String> snS = new ArrayList<>();
        snS.add("428256796155580416");
        snS.add("429807765725122560");
        snS.add("429370315726917632");

        snS.add("429794271944511488");
        snS.add("420366279094833152");
        snS.add("429857417157677056");

        snS.add("429794901295632384");
        snS.add("429356029738749952");
        snS.add("431257615431897088");

        snS.add("465665407118741504");
        snS.add("465665407118741505");
        snS.add("465663917889163265");

        snS.add("431237500896284672");
        snS.add("431234170765709312");
        snS.add("431232361552678912");

        snS.add("431228980457115648");
        snS.add("429787356514488320");
        snS.add("429788337029517312");

        snS.add("429777416076922880");
        snS.add("429363623610028032");
        snS.add("465654895593820160");

        snS.add("465585820024180736");
        snS.add("401893446484234240");
        snS.add("401892943411023872");

        snS.add("401892499406196736");
        snS.add("400081485958254592");
        snS.add("401892053820116992");

        snS.add("401891420337606656");
        snS.add("401890306364018688");
        snS.add("401889913554866176");

        snS.add("401885324675911680");
        snS.add("401882136174399488");


        for (int i = 0; i < snS.size(); i++) {
            addFavoriteCommodity(snS.get(i));
        }
    }



    /**
     * 添加收藏的商品
     */
    public void addFavoriteCommodity(String goodsSn){
        Utils.print(tag,"addFavoriteCommodity");

        String input="";
        try{
            Map<String,String> json = new HashMap<>();
            json.put("userid",ConStant.getInstance(mContext).userID);
            json.put("goodsSn",goodsSn);
            input = new Gson().toJson(json);
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpaddFavoriteCommodity(input,ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(StatusData statusData) {
                        Utils.print(tag,"status=="+statusData.getErrorMessage()+",value="+statusData.getReturnValue());

                    }
                });
        addSubscription(s);
    }


    public void getMoreCommodity(){
        Utils.print(tag,"getMoreCommodity");

        String input="";
        String sign="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("pageIndex", 1);
            json.put("pageSize", ConStant.ACUTION_PAGE_SIZE);
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

        Subscription s = getAcutionLauAPI()
                .httpGetAcutionListData(input,sign)
                .map(new Func1<AcutionCommodityData, Integer>() {
                    @Override
                    public Integer call(AcutionCommodityData commodityData) {
                        Log.v(tag,"get more status=="+commodityData.getReturnValue()+",message="+commodityData.getErrorMessage());
                        if (commodityData.getReturnValue() != -1 && commodityData.getData().getTotalCount()>0) {
                            List<AcutionItemData> itemDatas = commodityData.getData().getRecords();
                            for (int i = 0; i < itemDatas.size(); i++) {
                                String sn = itemDatas.get(i).getAuctionStatusVo().getAuctionSn();
                                if (AcutionDao.getInstance(mContext).querySn(sn).size() > 0) {
                                    //已经参拍
                                    itemDatas.get(i).setJoinAcution(true);
                                }
                                Log.v(tag, " data name=" + itemDatas.get(i).getAuctionVo().getGoodsName()+",price==="+itemDatas.get(i).getAuctionStatusVo().getCurrentPrice());
                            }
                        }
                        return 0;
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        Utils.print(tag,"error="+e.getMessage());
                    }

                    @Override
                    public void onNext(Integer status) {
                        getMoreCommodity();
                    }
                });
        addSubscription(s);
    }
}

