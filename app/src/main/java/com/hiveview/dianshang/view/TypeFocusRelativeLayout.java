package com.hiveview.dianshang.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.OnItemTypeSelectedListener;
import com.hiveview.dianshang.utils.Utils;


public class TypeFocusRelativeLayout extends RelativeLayout {


    /**
     *当前那个gallery
     */
    private int item;
    /**
     * 当前gallery item位置
     */
    private int itemPosition;

    /**
     * 标志是不同类型还是颜色类型
     */
    private int mType;

    private OnItemTypeSelectedListener onItemTypeSelectedListener;

    private Context context;

    public static final int GENERAL_TYPE = 1;
    public static final int COLOR_TYPE = 2 ;


    private final static String tag = "TypeFocusRelativeLayout";

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(onItemTypeSelectedListener!=null)
                onItemTypeSelectedListener.OnItemTypeSelectedListener(item,itemPosition,mType);
        }
    };

    public TypeFocusRelativeLayout(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public TypeFocusRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public TypeFocusRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView(){
        Utils.print(tag,"mType="+mType);
        if(mType==GENERAL_TYPE){
            Utils.print(tag,"initview");
            ((Button)findViewById(R.id.item_type_textview)).setOnFocusChangeListener(onFocusChangeListener);
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Log.d(tag, "onFocusChanged: "+gainFocus);
        if (gainFocus) {
            Log.v(tag,"pos>>>"+itemPosition+",item="+item);
            setTextLayoutSelected();

            mHandler.removeMessages(0);
            mHandler.sendEmptyMessage(0);
        } else {
            setTextLayoutUnSelect();
        }
    }


    /**
     *
     * @param type  置模块,确定是不同类型，还是颜色类型
     * @param item  当前那行item
     * @param itemPosition item具体位置
     */
    public void setData(int type,int item,int itemPosition){
        this.itemPosition = itemPosition;
        this.mType = type;
        this.item = item;
    }



    View.OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.d(tag, "button onFocusChanged: "+hasFocus);
            if (hasFocus) {
                Log.v(tag,"button pos>>>"+itemPosition+",item="+item);

                setTextLayoutSelected();
                mHandler.removeMessages(0);
                mHandler.sendEmptyMessage(0);
            } else {
                setTextLayoutUnSelect();
            }
        }
    };


    public void setTextLayoutSelected(){
        ((TextView)findViewById(R.id.item_type_textview)).setTextColor(context.getResources().getColor(R.color.gallery_selected_commodity_item_color));
        if(mType==GENERAL_TYPE){
            Utils.print(tag,"initview2");
            ((Button)findViewById(R.id.item_type_textview)).setOnFocusChangeListener(onFocusChangeListener);
        }
    }

    public void setTextLayoutUnSelect(){
        ((TextView)findViewById(R.id.item_type_textview)).setTextColor(context.getResources().getColor(R.color.gallery_commodity_item_color));
    }


    public void setOnItemTypeSelectedListener(OnItemTypeSelectedListener onItemTypeSelectedListener){
        this.onItemTypeSelectedListener = onItemTypeSelectedListener;
    }


}
