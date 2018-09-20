package com.hiveview.dianshang.base;

import android.view.KeyEvent;
import android.view.View;

/**
 * Created by carter on 5/4/17.
 */

public interface OnItemKeyListener {
    public boolean onKey(View v, int keyCode, KeyEvent event);
}