package com.hiveview.inputmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent in =  getIntent();
        if(in==null){
            return;
        }

        Log.v("input","onCreate");
        Intent intent = new Intent(this,InputManagerService.class);
        String className=in.getStringExtra(Utils.ACTION_INPUT_METHOD_CLASS_NAME);
        String packageName = className.substring(0,className.indexOf("/"));
        Log.v("input","packagename="+packageName);
        intent.putExtra(Utils.ACTION_INPUT_METHOD_PACKAGE_NAME,packageName);
        intent.putExtra(Utils.ACTION_INPUT_METHOD_CLASS_NAME,className);
        startService(intent);


        new Thread(new Runnable() {
            @Override
            public void run() {
                 try {
                     Thread.sleep(100);
                     finish();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
            }
        }).start();

    }


}
