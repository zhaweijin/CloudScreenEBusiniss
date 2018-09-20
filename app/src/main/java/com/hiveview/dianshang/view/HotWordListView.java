package com.hiveview.dianshang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;

/**
 * @Title CloudScreenVIPVideo3.1
 * @Auther Spr_ypt
 * @Date 2016/5/12
 * @Description
 */
public class HotWordListView extends ListView {


    private static final String TAG = HotWordListView.class.getSimpleName();
    private boolean loseFocusFromTop;


    public HotWordListView(Context context) {
        super(context);
    }

    public HotWordListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HotWordListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isLoseFocusFromTop() {
        return loseFocusFromTop;
    }

    public void resetLoseFocusFromTop() {
        loseFocusFromTop = false;
    }


    @Override
    public void setSelection(int position) {
        setSelectionFromTop(position, getMeasuredHeight() / 2);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("FilmListActivity","event :" + event.getAction());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                Log.d("FilmListActivity","getSelectedItemPosition :" + getSelectedItemPosition());
                if (getSelectedItemPosition() > 3) {
                    smoothScrollByOffset(1);
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (getSelectedItemPosition() == 0) {
                    loseFocusFromTop = true;
                }
                if (getSelectedItemPosition() <= 3) {
                    setScrollY(0);
                } else if (getSelectedItemPosition() <= getAdapter().getCount() - 4) {
                    smoothScrollByOffset(-1);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
