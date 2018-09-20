package com.hiveview.dianshang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.hiveview.dianshang.utils.Utils;

/**
 * Created by carter on 9/6/17.
 */

public class ObservableScrollView extends ScrollView {

    private String tag = "ObservableScrollView";

    private OnScrollListener onScrollListener;

    private RelativeLayout top_menu;

    private boolean hasMoveDown = false;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Utils.print(tag,"dispatchKeyEvent-"+getScrollY());
        /*if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_UP
                && getScrollY()==0 && !Utils.isVisibleLocal(top_menu)){
            return true;
        }*/

        /*if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN){
            hasMoveDown=true;
        }*/

        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(onScrollListener!=null)
           onScrollListener.onScroll(getScrollY());
    }


    public void setOnScrollListener(OnScrollListener listener){
        this.onScrollListener = listener;
    }


    /**
     *
     * 滚动的回调接口
     *
     * @author xiaanming
     *
     */
    public interface OnScrollListener{
        /**
         * 回调方法， 返回MyScrollView滑动的Y方向距离
         * @param scrollY
         *              、
         */
        public void onScroll(int scrollY);
    }


    /**
     *
     * @param status
     */
    public void initHasMoveStatus(boolean status){
        this.hasMoveDown = status;
    }



    public void setTop_menu(RelativeLayout top_menu){
        this.top_menu = top_menu;
    }

}