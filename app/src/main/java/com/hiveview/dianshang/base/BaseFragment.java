package com.hiveview.dianshang.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.utils.RxBus;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.view.CustomProgressDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;



public abstract class BaseFragment extends Fragment {


    private String tag = "BaseFragment";

    protected View mRootView;

    protected Unbinder unbinder;

    protected Context mContext;

    public CustomProgressDialog mProgressDialog = null;

    private CompositeSubscription mCompositeSubscription;



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


    protected abstract int getLayoutId();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " @" + Integer.toHexString(hashCode());
    }


    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (null == mRootView) {
            mRootView = inflater.inflate(getLayoutId(), null);
        }
        unbinder= ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }






    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (this.mCompositeSubscription != null) {

            this.mCompositeSubscription.unsubscribe();
        }

        stopProgressDialog();

    }


    public void startProgressDialog() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = new CustomProgressDialog(mContext);
            }
            Utils.print(tag, "startProgressDialog");
            //mProgressDialog.setMessage(msg);
            mProgressDialog.setCancelable(true);
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


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return false;
    }


}
