package com.hiveview.dianshang.base;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.home.YunPushService;
import com.hiveview.dianshang.utils.ActivityManagerApplication;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.CustomProgressDialog;
import java.lang.ref.WeakReference;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by carter on 4/5/17.
 */

public class BaseActivity extends FragmentActivity{

    protected Context mContext;
    private String tag = "BaseActivity";
    public ActivityManagerApplication activityManagerApplication;

    private CompositeSubscription mCompositeSubscription;
    public CustomProgressDialog mProgressDialog = null;

    public CompositeSubscription getCompositeSubscription() {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        return this.mCompositeSubscription;
    }


    public void addSubscription(Subscription s) {
        if (s == null) {
            return;
        }

        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(s);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == activityManagerApplication) {
            activityManagerApplication = ActivityManagerApplication.getInstance();
        }
        activityManagerApplication.addActivity(this);
        mContext = this;


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConStant.REMOTE_ACTION);
        registerReceiver(receiver, intentFilter);



        //启动安装服务
        Intent intent = new Intent(mContext,YunPushService.class);
        startService(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EBusinessApplication.setmContext(null);
        if (this.mCompositeSubscription != null) {

            this.mCompositeSubscription.unsubscribe();
        }

        stopProgressDialog();
        unregisterReceiver(receiver);
        if (null == activityManagerApplication) {
            activityManagerApplication = ActivityManagerApplication.getInstance();
        }
        activityManagerApplication.removeActivity(this);
    }


    public void startProgressDialog() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new CustomProgressDialog(this);
            }
            Utils.print(tag, "startProgressDialog");
            //mProgressDialog.setMessage(msg);
//            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            mProgressDialog.startLoading();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public void stopProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                Utils.print(tag, "stopProgressDialog");
                mProgressDialog.stopLoading();
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }



    private BroadcastReceiver receiver = new FinishBroadcastReceiver(this);

    private static class FinishBroadcastReceiver extends BroadcastReceiver {
        private WeakReference<BaseActivity> ref;

        public FinishBroadcastReceiver(BaseActivity activity) {
            ref = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Utils.print("aa","bb");
            if (action.equals(ConStant.REMOTE_ACTION)) {
                BaseActivity activity = ref.get();
                if (null != activity) {
                    activity.finish();
                }
            }
        }
    }
}
