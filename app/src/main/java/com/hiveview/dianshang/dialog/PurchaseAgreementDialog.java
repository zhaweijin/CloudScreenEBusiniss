package com.hiveview.dianshang.dialog;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BDialog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

/**
 * Created by ThinkPad on 2017/7/12.
 */

public class PurchaseAgreementDialog extends BDialog {

    private LayoutInflater mFactory = null;
    private Context context;
    private View mView;
    private TextView content;
    private String tag = "PurchaseAgreementDialog";

    public PurchaseAgreementDialog(Context context) {
        super(context, R.style.Dialog_Fullscreen);
        this.context = context;

        mFactory = LayoutInflater.from(context);
        mView = mFactory.inflate(R.layout.layout_purchaseagreement, null);

        setContentView(mView);
        initView();
    }

    public void initView() {
        content = (TextView) mView.findViewById(R.id.content);
        String s=context.getResources().getString(R.string.agreecontent);
        Log.i("=======", "==String==" + s);
        content.setText(s);

    }

    public String getFromAssets(String fileName){
        String Result="";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName),"UTF-8");
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            while((line = bufReader.readLine()) != null)
                Result += line;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result;
    }

}
