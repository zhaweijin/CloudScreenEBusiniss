package com.hiveview.dianshang.auction;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.AcutionListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.utils.Utils;

import java.util.Date;

/**
 * Created by zwj on 2/26/18.
 */

public class AcutionProgress {

    private String tag ="AcutionProgress";

    private int times=3; //总轮次
    private long durationTime=10; //每轮的时间
    private long consumeTime = 0; //活动消耗时间，活动开始后的时间

    /**
     * 定时器使用
     */
    private int time;

    /**
     * 服务器当前时间
     */
    private long timeStamp;


    private AcutionListener acutionListener;


    private CommodityCountDownTimer timer;
    private long TIME = 25 * 1000L;


    private ProgressBar progressBar;
    private Context mContext;

    /**
     * 发出倒计时警告
     */
    private boolean waring = false;

    /**
     * 与服务器时间差
     */
    private long divTime=0;

    public void setWaring(boolean waring) {
        this.waring = waring;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setDivTime(long divTime) {
        this.divTime = divTime;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public AcutionProgress(Context context){
        Utils.print(tag,"init");
        mContext = context;
    }



    public class CommodityCountDownTimer extends CountDownTimer {
        public CommodityCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        private long mDurationTime;

        public void setDurationTime(long durationTime) {
            this.mDurationTime = durationTime;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            float time = millisUntilFinished / 1000f;
            time = time*10;
            if(time<(1/10f)*mDurationTime && !waring){
                Utils.print(tag,"warin....");
                waring = true;
                acutionListener.onWaring();
            }

            if(time%mDurationTime==0){
                //执行完一个轮次了
                progressBar.setProgress((int)mDurationTime);
            }else {
                progressBar.setProgress((int)(time%mDurationTime));
            }
            updateProgressBarColor(progressBar);
            Utils.print(tag,"time="+time+",durationTime="+mDurationTime+",progressvalue="+progressBar.getProgress());
        }

        @Override
        public void onFinish() {
            acutionListener.OnItemAcutionListener(true);
        }
    }


    /**
     * 更新进度条颜色　
     */
    private void updateProgressBarColor(ProgressBar progressbar){
        //Utils.print(tag,"pro=="+progressbar.getProgress());
        if(progressbar.getProgress()>0.3*progressbar.getMax()){
            //Utils.print(tag,">30");
            progressbar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.acution_info_top_bar_green_color));
        }else if(progressbar.getProgress()<=0.3*progressbar.getMax() && progressbar.getProgress()>=0.1*progressbar.getMax()){
            //Utils.print(tag,">=10 <=30" );
            progressbar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.acution_info_top_bar_yellow_color));
        }else if(progressbar.getProgress()<0.1*progressbar.getMax()){
            //Utils.print(tag,"<10");
            progressbar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.acution_info_top_bar_red_color));
        }
    }

    /**
     * 开始倒计时
     */
    public void startTimer() {
        Date date = new Date(System.currentTimeMillis());
        Utils.print(tag,"local time="+date.getTime());
        Utils.print(tag,"divTime="+divTime+",timeStamp="+timeStamp);
        consumeTime = date.getTime()-timeStamp+divTime;
        long totalTime = durationTime*times*1000-consumeTime;
        TIME = totalTime;
        Utils.print(tag,"TIME="+totalTime);
        Utils.print(tag,"consumeTime="+consumeTime);
        Utils.print(tag,"totaltime="+durationTime*times*1000);
        if(durationTime*times*1000<consumeTime){
            Utils.print(tag,"server time error");
            return;
        }

        cancelTimer();
        long progressDurationTime = durationTime*10;
        progressBar.setMax((int)progressDurationTime);
        progressBar.setVisibility(View.VISIBLE);

        if (timer == null) {
            timer = new CommodityCountDownTimer(TIME, ConStant.ACUTION_PROGRESS_TIME_INTERVER);
            timer.setDurationTime(progressDurationTime);
        }
        timer.start();
    }

    /**
     * 取消倒计时
     */
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    public void release(){
        Utils.print(tag,"release");
        cancelTimer();
    }



    public void setAcutionListener(AcutionListener acutionListener) {
        this.acutionListener = acutionListener;
    }

}
