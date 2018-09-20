package com.hiveview.dianshang.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.entity.commodity.CommodityRecored;
import com.hiveview.dianshang.entity.recommend.RecommendItemData;
import com.hiveview.dianshang.entity.special.SpecialRecored;
import com.hiveview.dianshang.utils.FlowRateTester;
import com.hiveview.dianshang.utils.LoadingSpaceStack;
import com.hiveview.dianshang.utils.NetworkUtil;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.HiveviewVideoView;
import com.hiveview.dianshang.view.LoadingView;
import com.hiveview.dianshang.view.NetworkErrorWindow;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 播放器
 * Created by carter on 4/17/17.
 */

public class LivePlay extends BaseActivity{

    private String tag = "LivePlay";

    /**
     * 播放器
     */
    @BindView(R.id.video)
    HiveviewVideoView videoView;

    /**
     * 无法播放提示图
     */
    @BindView(R.id.video_play_bg)
    ImageView video_play_bg;

    /**
     * 播放图标
     */
    @BindView(R.id.video_play_icon)
    ImageView video_play_icon;

    private String urlString="";
    private String sn = "";
    private String name="";

    private CommodityRecored commodityRecored;
    private RecommendItemData recommendItemData;
    private SpecialRecored specialRecored;
    private int mFromType;
    private HiveViewVideoState hiveViewVideoState;

    private BroadcastReceiver mNetworkChangeListener = null;

    /**
     * 商品进入直播
     * @param activity
     * @param commodityRecored
     */
    public static void launch(Activity activity,CommodityRecored commodityRecored,int mFromType) {
        Intent intent = new Intent(activity, LivePlay.class);
        intent.putExtra("commodityrecored",commodityRecored);
        intent.putExtra("mFromType",mFromType);
        activity.startActivity(intent);
    }


    /**
     * 推荐为进入直播
     * @param activity
     * @param recommendItemData
     * @param mFromType
     */
    public static void launch(Activity activity,RecommendItemData recommendItemData,int mFromType) {
        Intent intent = new Intent(activity, LivePlay.class);
        intent.putExtra("recommenditemdata",recommendItemData);
        intent.putExtra("mFromType",mFromType);
        activity.startActivity(intent);
    }


    /**
     * 专题进入直播流
     * @param activity
     * @param specialRecored
     * @param mFromType
     */
    public static void launch(Activity activity,SpecialRecored specialRecored,int mFromType) {
        Intent intent = new Intent(activity, LivePlay.class);
        intent.putExtra("specialRecored",specialRecored);
        intent.putExtra("mFromType",mFromType);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.play_live);

        commodityRecored = getIntent().getParcelableExtra("commodityrecored");
        recommendItemData = getIntent().getParcelableExtra("recommenditemdata");
        specialRecored = getIntent().getParcelableExtra("specialRecored");
        if (commodityRecored != null) {
            urlString = commodityRecored.getProductVideos();
        }else if(recommendItemData!=null){
            urlString = recommendItemData.getProductVideos();
        }else if(specialRecored!=null){
            urlString = specialRecored.getProductVideos();
        } else {
            return;
        }

        //urlString="http://zhibo.video.pthv.gitv.tv/live/gamefy/live.m3u8";

        this.mFromType = getIntent().getIntExtra("mFromType",-1);
        initNetErrorWindow();
        flowRateTester = new FlowRateTester(getApplicationContext());
        Utils.print(tag,"url="+urlString);


        startPlayM3U8(urlString);

        registerNetworkListener();
    }


    private void initPlay(){
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        videoView.setLayoutParams(layoutParams);

        videoView.setHiveViewVideoStateListener(hiveViewVideoState);
        videoView.setVideoURI(Uri.parse(urlString));
        videoView.setOnInfoListener(mOnInfoListener);
        videoView.openVideo();
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Utils.print(tag,"what=="+what);
                //video_play_bg.setVisibility(View.VISIBLE);
                //video_play_icon.setVisibility(View.VISIBLE);
                //取消无法播放对话框提示
                //ToastUtils.showToast(mContext,mContext.getResources().getString(R.string.play_error));
                //finish();
                return true;
            }
        });


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Utils.print(tag,"prepare==");
                video_play_bg.setVisibility(View.INVISIBLE);
                video_play_icon.setVisibility(View.INVISIBLE);
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Utils.print(tag,"play finish");
                //finish();
            }
        });
        bufferStart(true,false);
        //发送埋点数据
        sendInfoStatistics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(videoView!=null && !videoView.isPlaying()){
            Utils.print(tag,"start");
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(videoView!=null && videoView.isPlaying()){
            videoView.pause();
        }
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_MENU){
            Utils.print(tag,"menu");
            videoView.pause();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/


    /**
     * 商品流浏览量埋点发送
     */
    private void sendInfoStatistics(){

        HashMap<String,String> simpleMap=new HashMap<String,String>();
        //商品详情页浏览量埋点
        simpleMap.clear();
        String info = "";
        simpleMap.put("tabNo","");
        simpleMap.put("actionType","Ec4001");
        String[] categorysns=null;
        String[] categoryNames=null;

        if(commodityRecored!=null){
            //商品ID
            info = info + "goodsId="+commodityRecored.getGoodsSn()+"&";
            //商品SN
            info = info + "goodsName="+commodityRecored.getName();
            categorysns = commodityRecored.getCategoryTreePath().split(",");
            categoryNames = commodityRecored.getCategoryTreeName().split(",");

        }else if(recommendItemData!=null){
            //商品ID
            info = info + "goodsId="+recommendItemData.getGoodsSn()+"&";
            //商品SN
            info = info + "goodsName="+recommendItemData.getGoodsName();
            categorysns = recommendItemData.getCategoryTreePath().split(",");
            categoryNames = recommendItemData.getCategoryTreeName().split(",");

        }else if(specialRecored!=null){
            //商品ID
            info = info + "goodsId="+specialRecored.getGoodsSn()+"&";
            //商品SN
            info = info + "goodsName="+specialRecored.getGoodsName();
            categorysns = specialRecored.getCategoryTreePath().split(",");
            categoryNames = specialRecored.getCategoryTreeName().split(",");
        }

        if(categoryNames!=null && categorysns!=null){
            //一级
            if(categoryNames.length>0 && categorysns.length>0){
                info = info + "firstTabId="+categorysns[0] +"&";
                info = info + "firstTabName="+categoryNames[0] +"&";
            }

            //二级
            if(categoryNames.length>1 && categorysns.length>1){
                info = info + "secondTabId="+categorysns[1] +"&";
                info = info + "secondTabName="+categoryNames[1] +"&";
            }

            //三级
            if(categoryNames.length>2 && categorysns.length>2){
                info = info + "thirdTabId="+categorysns[2] +"&";
                info = info + "thirdTabName="+categoryNames[2] +"&";
            }
        }
        //直播流
        info = info + "goodsType=2"+"&";
        //来源
        info = info + "source="+mFromType;

        simpleMap.put("actionInfo",info);
        EBusinessApplication.getHSApi().addAction(simpleMap);
    }
    private class HiveViewVideoState implements HiveviewVideoView.HiveViewVideoStateListener {

        @Override
        public void onPrepared() {
            Utils.print(tag,"onPrepared");
        }

        @Override
        public void onMovieStart() {
            Utils.print(tag,"onMovieStart");
        }

        @Override
        public void onMoviePause() {
            Utils.print(tag,"onMoviePause");
        }

        @Override
        public void onMovieComplete() {
            Utils.print(tag,"onMovieComplete");
        }

        @Override
        public void onMovieStop() {
            Utils.print(tag,"onMovieStop");
        }

        @Override
        public boolean onError() {
            Utils.print(tag,"onError");
            return false;
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            Utils.print(tag,"onBufferingUpdate");
        }

        @Override
        public void onVideoSizeChange(int width, int height) {
            Utils.print(tag,"onVideoSizeChange");
        }

        @Override
        public void onSeekComplete() {
            Utils.print(tag,"onSeekComplete");
        }

        @Override
        public void onBufferStart() {
            Utils.print(tag,"onBufferStart");
        }

        @Override
        public void onBufferEnd() {
            Utils.print(tag,"onBufferEnd");
        }
    }

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.i(tag, "mOnInfoListener   what=" + what + "   extra=" + extra);
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    bufferStart(false,false);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    bufferEnd();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    /**
     * 网络异常弹框
     */
    protected NetworkErrorWindow noNetworkWindow;

    /**
     * msg.what
     */
    public static final int BUFFER_OVERTIME = 0x120002;

    /**
     * 网络异常弹窗出现延迟时间，delay time before nerwork error dialog show
     */
    public static final int BUFFER_OVERTIME_DELAY = 10000;// 10秒

    /**
     * 网络异常Handler，network error Handler
     */
    Handler bufferOvertimeHandler = new BufferOvertimeHandler(this);
    private boolean isOnBuffer;

    protected static class BufferOvertimeHandler extends Handler {
        private WeakReference<LivePlay> reference;

        public BufferOvertimeHandler(LivePlay activity) {
            reference = new WeakReference<>(activity);
        }

        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LivePlay activity = reference.get();
            if (null != activity && !activity.isFinishing() && !activity.isDestroyed()) {
                switch (msg.what) {
                    case BUFFER_OVERTIME:
                        removeMessages(BUFFER_OVERTIME);
                        activity.noNetworkWindow.showWindow();
                        break;
                    default:
                        break;
                }

            }
        }
    }


    /**
     * 初始化网络异常窗口
     */
    protected void initNetErrorWindow() {
        noNetworkWindow = new NetworkErrorWindow(this);
        noNetworkWindow.setOnPressClickListener(new NetworkErrorWindow.OnPressClickListener() {

            @Override
            public void onPressClick() {
                openNetWorkSetting();
            }

            @Override
            public void onCancelClick() {
                bufferOvertimeHandler.sendEmptyMessageDelayed(BUFFER_OVERTIME, BUFFER_OVERTIME_DELAY);
            }

        });
    }

    /**
     * @Title SingleChannelActivity
     * @author guosongsheng
     * @date 2016/10/31 14:21
     * @Description 打开系统设置
     */
    protected void openNetWorkSetting() {
        Intent intent = new Intent();
        ComponentName com = new ComponentName("com.hiveview.cloudtv.settings", "com.hiveview.cloudtv.settings.NetworkSettingsActivity");
        intent.setComponent(com);
        this.startActivity(intent);

    }
    /**
     * 缓冲动画View
     */
    @BindView(R.id.loadingImage)
    protected LoadingView loadingView;

    /**
     * @Title: MainActivity
     * @author:yupengtong
     * @Description: 缓冲开始显示加载动画方法，show loading anim when buffering
     */
    private void bufferStart(boolean isFirstStart, boolean isLauncherStart) {
        Log.d(tag, "bufferStart: -----> ");
        isOnBuffer = true;
        if (isFirstStart) {
            loadingView.setVisibility(View.VISIBLE);
            loadingView.setFirstLineText(getString(R.string.load_view_netlinking_zb));
            bufferOvertimeHandler.sendEmptyMessageDelayed(BUFFER_OVERTIME, BUFFER_OVERTIME_DELAY);
        } else {
            Log.d(tag, "bufferStart: ---noNetworkWindow--> " + noNetworkWindow.isShowing());
            if (!noNetworkWindow.isShowing()) {
                loadingView.setVisibility(View.VISIBLE);
                // loadingView.setTwoLineText("加载进度0%");
                bufferOvertimeHandler.sendEmptyMessageDelayed(BUFFER_OVERTIME, BUFFER_OVERTIME_DELAY);
               deleteToastDelayMsg();
            // loadingHandler.sendEmptyMessageDelayed(LOADING_OVER_TIME_NEED_TOAST, 10000);
            }
            loadingHandler.sendEmptyMessageDelayed(LOADING_MSG_FRESH, BUFFER_MSG_FRESH_RATE);
            flowRateTester.startTest();
        }

    }

    /**
     * @Title: MainActivity
     * @author:yupengtong
     * @Description: 缓冲结束后去除加载动画方法，hide loading anim when buffer finished
     */
    private void bufferEnd() {
        Log.d(tag, "bufferEnd: -----> ");
        isOnBuffer = false;
        loadingView.setVisibility(View.GONE);
        if (null != bufferOvertimeHandler) {
            deleteToastDelayMsg();
            bufferOvertimeHandler.removeMessages(BUFFER_OVERTIME);
        }
        if (null != noNetworkWindow && noNetworkWindow.isShowing()) {
            noNetworkWindow.dismiss();
        }
        if (null != loadingHandler) {
            loadingHandler.removeMessages(LOADING_MSG_FRESH);
        }

    }

    private void deleteToastDelayMsg() {
        if (loadingHandler.hasMessages(LOADING_OVER_TIME_NEED_TOAST)) {
            loadingHandler.removeMessages(LOADING_OVER_TIME_NEED_TOAST);
        }
    }


    private Handler loadingHandler = new LoadingHandler(this);
    protected boolean isNewIntent;
    protected static final int BUFFER_MSG_FRESH_RATE = 500;
    protected static final int WAITING_NETWORK_CONNECT = 0x0002;
    /**
     * 需要显示toast
     */
    protected static final int LOADING_OVER_TIME_NEED_TOAST = 0X11011;

    protected  static  final int AD_OVERTIME_TOAST = 0X0003;
    /**
     * msg.what
     */
    protected static final int LOADING_MSG_FRESH = 0x0001;
    /**
     * 网络智判
     */
    protected FlowRateTester flowRateTester;

    private static class LoadingHandler extends Handler {
        private WeakReference<LivePlay> ref;

        public LoadingHandler(LivePlay activity) {
            ref = new WeakReference<LivePlay>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LivePlay activity = ref.get();
            switch (msg.what) {
                case LOADING_MSG_FRESH:
                    if (null != activity) {
                        removeMessages(LOADING_MSG_FRESH);
                        activity.loadingView.setFirstLineText(activity.getApplicationContext().getString(R.string.load_view_netrate_zb)
                                + activity.flowRateTester.getFlowRate(BUFFER_MSG_FRESH_RATE));
                        activity.loadingHandler.sendEmptyMessageDelayed(LOADING_MSG_FRESH, BUFFER_MSG_FRESH_RATE);
                    }
                    break;
                case WAITING_NETWORK_CONNECT:
                    if (null != activity) {
                        activity.checkNetwork();
                    }
                    break;
                case LOADING_OVER_TIME_NEED_TOAST:
                    long currentRate = activity.flowRateTester.getFlowRateLong(10000);
                    LoadingSpaceStack.getInstance().definitionAdapting(currentRate);
                    break;
                default:
                    break;
            }
        }
    }
    // 检查网络的次数
    private int count;
    public void checkNetwork() {
        Log.d(tag, "checkNetwork()  count---> " + count);
        // ToastUtil.showToast(this, " count = " + count, Toast.LENGTH_SHORT);
        NetworkUtil networkUtil = new NetworkUtil(this);
        if (networkUtil.isConnected()) {

        } else {
            if (count < 8) {
                loadingHandler.sendEmptyMessageDelayed(WAITING_NETWORK_CONNECT, 1000);
                count++;
            } else {
                // 超过检测次数后弹出网络异常的框；
                if (!noNetworkWindow.isShowing()) {
                    noNetworkWindow.showWindow();
                }
            }

        }
    }



    private void startPlayM3U8(String path) {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String url = httpPlayerUrl(path);
                subscriber.onNext(url);
                subscriber.onCompleted();
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.print(tag,"error......");
                    }

                    @Override
                    public void onNext(Object o) {
                        urlString = (String)o;
                        initPlay();
                    }
                });
    }







    public String httpPlayerUrl(String url) {

        String playerUrl = "";
        try {
            Log.v(tag, "start get");
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);
            DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
            HttpResponse response;

            HttpGet httpget = new HttpGet(url);
            httpget.getParams().setBooleanParameter(
                    CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            response = httpclient.execute(httpget);


            HttpEntity entity = response.getEntity();
            Utils.print(tag,"length---"+entity.getContentLength());
            if(entity.getContentLength()>65534){
                Log.v(tag, "rev=== null" );
                return "";
            }
            String retSrc = EntityUtils.toString(entity);
            Log.v(tag, "rev===" + retSrc);
            JSONObject result = new JSONObject(retSrc);
            String return_url = result.getString("u");
            Utils.print(tag,"return_url="+return_url);
            playerUrl = return_url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerUrl;
    }



    private void registerNetworkListener() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (mNetworkChangeListener == null) {
            mNetworkChangeListener = new BroadcastReceiver() {

                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    Utils.print(tag,"change");
                    if (networkInfo != null && networkInfo.isConnected()
                            && networkInfo.isAvailable()) {
                        Utils.print(tag,"change1");
                        bufferOvertimeHandler.sendEmptyMessageDelayed(BUFFER_OVERTIME, BUFFER_OVERTIME_DELAY);
                    }
                }
            };
        }
        registerReceiver(mNetworkChangeListener, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetworkChangeListener != null) {
            unregisterReceiver(mNetworkChangeListener);
            mNetworkChangeListener = null;
        }
    }
}
