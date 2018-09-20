package com.hiveview.dianshang.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.hiveview.dianshang.R;

/**
 * Created by carter on 4/11/17.
 */

public class BaseDialog extends Dialog{

    private Context mContext;
    private LayoutInflater mFactory = null;
    private View mView = null;

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        Log.v("test","set dialog content view");
    }

    public BaseDialog(Context context, int layout_id) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mFactory = LayoutInflater.from(mContext);
        mView = mFactory.inflate(layout_id, null);

        final WindowManager.LayoutParams WMLP = this.getWindow().getAttributes();
        WMLP.gravity = Gravity.CENTER;
        this.getWindow().setAttributes(WMLP);

        this.setContentView(mView);
    }

    public BaseDialog(@NonNull Context context) {
        super(context);
    }
}
