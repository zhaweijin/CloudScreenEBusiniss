package com.hiveview.dianshang.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.OnItemKeyListener;
import com.hiveview.dianshang.base.OnItemViewSelectedListener;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.utils.Utils;


public class FocusRelativeLayout extends RelativeLayout {

    private Animation scaleSmallAnimation;
    private Animation scaleBigAnimation;

    private int position;
    private OnItemViewSelectedListener onItemViewSelectedListener;
    private OnItemKeyListener onItemKeyListener;


    private boolean isValid=true;


    /**
     * 区分模块
     */
    private int mType = -1;


    /**
     * 是否编辑模式
     */
    private boolean isEditMode = false;

    private final static String tag = "FocusRelativeLayout";

    public FocusRelativeLayout(Context context) {
        super(context);
        this.setOnKeyListener(onKeyListener);
    }

    public FocusRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnKeyListener(onKeyListener);
    }

    public FocusRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnKeyListener(onKeyListener);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        Utils.print(tag, "onFocusChanged: "+gainFocus);
        try {
             if (gainFocus) {
                 Utils.print(tag,"pos>>>"+position);
                 if(onItemViewSelectedListener!=null){
                     onItemViewSelectedListener.OnItemViewSelectedListener(position);
                 }
                 setTextLayoutSelected();
                 zoomOut();
             } else {
                 setTextLayoutUnSelect();
                 zoomIn();
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void zoomIn() {
        if(mType==ConStant.ACUTION_TO_INFO){
            return;
        }
        if (scaleSmallAnimation == null) {
            scaleSmallAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale_small);
        }
        startAnimation(scaleSmallAnimation);
    }

    private void zoomOut() {
        if(mType==ConStant.ACUTION_TO_INFO){
            return;
        }
        if (scaleBigAnimation == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                scaleBigAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale_big);
            }else{
                scaleBigAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.low_version_anim_scale_big);
            }
        }
        startAnimation(scaleBigAnimation);
    }

    /**
     * 当前位置
     * @param postion
     */
    public void setPostion(int postion){
        this.position = postion;
    }



    public void setTextLayoutSelected(){

        if(mType==ConStant.CATEGORY_TO_INFO){
            return;
        }
        //拍卖单独处理
        if(mType==ConStant.ACUTION_TO_INFO){
            if(isValid){
                findViewById(R.id.layout_add_price).setVisibility(VISIBLE);
            }else{
                findViewById(R.id.layout_add_price).setVisibility(INVISIBLE);
            }
        }else{
            findViewById(R.id.layout_commodity_item).setBackgroundResource(R.color.text_red_color);
            findViewById(R.id.layout_live).setBackgroundResource(R.color.text_red_color);
            findViewById(R.id.promotion_top1).setBackgroundResource(R.drawable.promotion_top_selected_bg);
            findViewById(R.id.promotion_top2).setBackgroundResource(R.drawable.promotion_top_selected_bg);

            if(mType== ConStant.OP_COLLECTION && isEditMode){
                findViewById(R.id.delete_status).setVisibility(INVISIBLE);
                findViewById(R.id.delete_icon).setVisibility(VISIBLE);
            }
        }
    }

    public void setTextLayoutUnSelect(){
        if(mType==ConStant.CATEGORY_TO_INFO){
            return;
        }
        if(mType==ConStant.ACUTION_TO_INFO){
            findViewById(R.id.layout_add_price).setVisibility(INVISIBLE);
        }else{
            findViewById(R.id.layout_commodity_item).setBackgroundResource(R.color.text_gray_color);
            findViewById(R.id.promotion_top1).setBackgroundResource(R.drawable.promotion_top_unselect_bg);
            findViewById(R.id.promotion_top2).setBackgroundResource(R.drawable.promotion_top_unselect_bg);
            findViewById(R.id.layout_live).setBackgroundResource(R.color.text_gray_color);

            if(mType== ConStant.OP_COLLECTION && isEditMode){
                findViewById(R.id.delete_status).setVisibility(VISIBLE);
                findViewById(R.id.delete_icon).setVisibility(INVISIBLE);
            }
        }
    }


    public void setOnItemSelectedListener(OnItemViewSelectedListener itemSelectedListener){
        this.onItemViewSelectedListener = itemSelectedListener;
    }



    public void setOnItemkeyListener(OnItemKeyListener onItemkeyListener){
        this.onItemKeyListener = onItemkeyListener;
    }


    /**
     * 设置模块调用的item,分类或收藏或
     * @param type
     */
    public void setType(int type){
        this.mType = type;
    }

    public void setEditMode(boolean editMode){
        this.isEditMode = editMode;
    }

    View.OnKeyListener onKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(onItemKeyListener==null)
                return false;
            return onItemKeyListener.onKey(v,keyCode,event);
        }
    };


    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isValid() {
        return isValid;
    }
}
