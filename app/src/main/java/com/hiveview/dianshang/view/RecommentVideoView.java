package com.hiveview.dianshang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.utils.Utils;

/**
 * Created by carter on 7/19/17.
 */
public class RecommentVideoView extends VideoView {

    public Context mContext;

    public RecommentVideoView(Context context) {
        super(context);
        this.mContext = context;
    }

    public RecommentVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public RecommentVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(getWidth(), widthMeasureSpec);
        int height = getDefaultSize(getHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public int getCurrTime(){
        Utils.print("tt","start>>>>>+"+getDuration());
        Utils.print("tt","start>>>>>+"+getCurrentPosition());
        return getDuration();
    }

}