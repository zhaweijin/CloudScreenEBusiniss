package com.hiveview.dianshang.home;


import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.auction.AcutionService;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.advertisement.Advertisement;
import com.hiveview.dianshang.entity.advertisement.AdvertisementData;
import com.hiveview.dianshang.entity.token.TokenData;
import com.hiveview.dianshang.utils.DeviceInfo;
import com.hiveview.dianshang.utils.SPUtils;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.hiveview.dianshang.view.RecommentVideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carter on 4/23/17.
 */

public class Welcome extends BaseActivity{

    private String tag = "welcome";

    @BindView(R.id.video_view)
    RecommentVideoView video_view;

    //广告信息
    private String advertisement_message="";
    private boolean playComplete = false;
    private boolean getAdFinished = false;

    /**
     * 测试还是发布模式
     */
    private int APP_MODE = ConStant.RELEASE_MODE;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        //http://211.103.138.120/data_collect/showLog.jsp?mac=63:E3
        sendStatistics();
        Utils.print(tag,">>"+Utils.isConnected(this));

        //检测是否启动安装服务app
        if(!Utils.isServiceWorked(this, ConStant.Install_ServiceName)){
            //启动安装服务
            Intent intent = new Intent(mContext,InstallService.class);
            intent.putExtra("status",InstallService.ENTER);
            startService(intent);
        }

        //清理拍卖冗余数据
        Intent acuintent = new Intent(mContext,AcutionService.class);
        acuintent.putExtra("status",InstallService.ENTER);
        startService(acuintent);

        if(APP_MODE==ConStant.RELEASE_MODE) {
            //检测token是否失效
            String user = (String) SPUtils.get(mContext, ConStant.USERID, "");
            String token = (String) SPUtils.get(mContext, ConStant.USER_TOKEN, "");

            if (!user.equals("") && !token.equals("")) {
                Utils.print(tag, "set token");
                ConStant.userID = user;
                ConStant.Token = token;
                //token已经存在
                loadAdvertisement();
                loadPlay();
            } else {
                getTokenData();
                loadPlay();
            }
        }else {
            //debug!!!!!!!!!
            String user = (String) SPUtils.get(mContext,ConStant.USERID,"");
            String token = (String) SPUtils.get(mContext,ConStant.USER_TOKEN,"");
            ConStant.userID = user;
            ConStant.Token = token;
            MainActivity.launch(mContext,"admessage test");
            //Test.launch((Welcome)mContext,"");
            //APITestActivity.launch(this);
            //PromotionCommodity.launch(this);
            finish();
        }

    }


    /**
     * 进入主页
     */
    private void goMainActivity(){
        if(playComplete && getAdFinished){
            Utils.print(tag,"userid="+ConStant.getInstance(mContext).userID);
            Utils.print(tag,"token="+ConStant.getInstance(mContext).Token);

            MainActivity.launch((Welcome)mContext,advertisement_message);
            finish();
        }
    }


    /**
     * 初始化动画播放
     * @param filepath
     */
    private void initPlay(String filepath){

        Utils.print(tag,"url="+filepath);
        video_view.setVideoURI(Uri.parse(filepath));
        video_view.start();

        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playComplete = true;
                goMainActivity();
            }
        });

        video_view.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Utils.print("tt", "error what==" + what);
                //取消无法播放对话框提示
                return true;
            }
        });
    }


    /**
     * 拷贝asset 文件
     * @return
     */
    private String copyVideoAssets() {
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        String filepath="";
        try {

            //delete all old version video
            String prefix = "^[start_home].*$";
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            File fileList[] = file.listFiles();
            for (File f : fileList) {
                if (f.isFile()) {
                    if (f.getName().matches(prefix)){
                        f.delete();
                    }
                }
            }

            String filename = "start_home_1.2.mp4";
            filepath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/" +filename;
            if(new File(filepath).exists()){
                Utils.print(tag,"file exist");
                return filepath;
            }

            Utils.print(tag,"1111");
            new File("/mnt/sdcard/bb.txt").createNewFile();
            Utils.print(tag,"222");

            in = assetManager.open(filename);
            File outFile = new File(Environment.getExternalStorageDirectory(), filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.print(tag,"copy finish=="+filepath);
        return filepath;
    }


    /**
     * 拷贝文件
     * @param in
     * @param out
     * @throws IOException
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }



    /**
     * 获取token
     */
    public void getTokenData(){
        Utils.print(tag,"getTokenData");
        String input="";
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("mac", DeviceInfo.getInstance(mContext).getMac());
            json.put("sn",DeviceInfo.getInstance(mContext).getSn());
            input = json.toString();
            input = input.replace("{","%7B").replace("}","%7D");
            Utils.print(tag,"input="+input);
        }catch (Exception e){
            e.printStackTrace();
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetToken(input)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TokenData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        try{
                            String error_tips = "";
                            if(Utils.isConnected(mContext)){
                                error_tips = mContext.getResources().getString(R.string.error_service_exception);
                            }else{
                                error_tips = mContext.getResources().getString(R.string.error_network_exception);
                            }
                            ToastUtils.showToast(mContext,error_tips);
                            finish();
                            Utils.print(tag,"error="+e.getMessage());
                        }catch (Exception ee){
                            ee.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(TokenData tokenData) {
                        Utils.print(tag,"token status=="+tokenData.getErrorMessage());
                        if(tokenData.getReturnValue()==-1){
                            ToastUtils.showToast(mContext,tokenData.getErrorMessage());
                            finish();
                            return;
                        }


                        String user = tokenData.getData().getUserid();
                        String token = tokenData.getData().getToken();
                        SPUtils.putApply(mContext, ConStant.USERID,tokenData.getData().getUserid());
                        SPUtils.putApply(mContext,ConStant.USER_TOKEN,tokenData.getData().getToken());


                        ConStant.userID = user;
                        ConStant.Token = token;

                        loadAdvertisement();

                    }
                });
        addSubscription(s);
    }


    /**
     * 获取首页广告信息
     */
    public void loadAdvertisement(){
        Utils.print(tag,"getAdvertisement");
        if(!Utils.isConnected(mContext)){
            String error_tips = mContext.getResources().getString(R.string.error_network_exception);
            ToastUtils.showToast(mContext,error_tips);
            finish();
            return;
        }

        Subscription s = RetrofitClient.getCommodityAPI()
                .httpGetAdvertisement(ConStant.getInstance(mContext).Token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AdvertisementData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        try{
                            Utils.print(tag,"error="+e.getMessage());
                            String message = "";
                            advertisement_message = message;
                            getAdFinished = true;
                            goMainActivity();
                        }catch (Exception ee){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(AdvertisementData advertisementData) {
                        Utils.print(tag,"ad status=="+advertisementData.getErrorMessage()+",value="+advertisementData.getReturnValue());

                        if(advertisementData.getReturnValue()==-2){   //token已经失效,再次请求
                            getTokenData();
                            return;
                        }

                        String message = "";
                        if(advertisementData.getReturnValue()==-1){
                            /*ToastUtils.showToast(mContext,advertisementData.getErrorMessage());
                            finish();*/
                        }else{
                            List<Advertisement> advertisements = advertisementData.getData();
                            for(int i=0;i<advertisements.size();i++){
                                Utils.print(tag,"s=="+advertisements.get(i).getDescription());
                                if(i==advertisements.size()-1){
                                    message = message+advertisements.get(i).getDescription();
                                }else{
                                    message = message+advertisements.get(i).getDescription()+"      ";
                                }
                            }
                        }
                        advertisement_message = message;
                        getAdFinished = true;
                        goMainActivity();
                    }
                });
        addSubscription(s);
    }


    /**
     * 拷贝视频动画
     */
    private void loadPlay(){
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String filepath = copyVideoAssets();
                subscriber.onNext(filepath);
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Utils.print(tag,">>>>>"+s);
                        initPlay(s);
                    }
                });
    }


    /**
     * 发送电商启动量埋点
     */
    private void sendStatistics(){
        HashMap<String,String> simpleMap=new HashMap<String,String>();

        simpleMap.put("tabNo","");
        simpleMap.put("actionType","Ec0000");
        simpleMap.put("actionInfo","");
        EBusinessApplication.getHSApi().addAction(simpleMap);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Utils.print(tag,"onPause");
        if(video_view!=null && video_view.isPlaying()){
            video_view.pause();
            playComplete = true;
            goMainActivity();
        }

        if(!Utils.isServiceWorked(this, ConStant.Install_ServiceName)){
            Intent intent = new Intent(this,InstallService.class);
            intent.putExtra("status",InstallService.EXIT);
            startService(intent);
        }
    }
}
