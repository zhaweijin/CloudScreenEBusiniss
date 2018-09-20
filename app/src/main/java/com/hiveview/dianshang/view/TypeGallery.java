package com.hiveview.dianshang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.Gallery;

/**
 * Created by carter on 9/4/17.
 */

public class TypeGallery extends Gallery{


    private int itemSize;

    public TypeGallery(Context context) {
        super(context);
    }

    public TypeGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT && getSelectedItemId()==0){
                return true;
            }

            if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT && getSelectedItemId()==itemSize){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 设置当前item的大小
     * @param itemSize
     */
    public void setItemSize(int itemSize) {
        this.itemSize = itemSize;
    }
}
