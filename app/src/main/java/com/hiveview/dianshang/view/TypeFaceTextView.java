/**
 * @Title TypeFaceTextView.java
 * @Package com.hiveview.domyphonemate.view
 * @author haozening
 * @date 2014年6月6日 下午7:49:06
 * @Description
 * @version V1.0
 */
package com.hiveview.dianshang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.hiveview.dianshang.base.EBusinessApplication;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.utils.TypefaceUtil;


/**
 * 可以指定字体的TextView 需要设置属性usertypeface[enum]</BR>
 * 调用TypeFaceTextView的xml中需要指定命名空间 xmlns:domy="http://schemas.android.com/apk/res/com.hiveview.cloudscreen.video"</BR>
 * 设置属性domy:usertypeface="[enum]"</BR>
 * 具体[enum]值参见attr文件的attr_domy_text节点的配置</BR>
 *
 * @author haozening
 * @ClassName TypeFaceTextView
 * @Description
 * @date 2014年6月6日 下午7:49:06
 */
public class TypeFaceTextView extends TextView {

    private static final String TAG = TypeFaceTextView.class.getSimpleName();

    public TypeFaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public TypeFaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TypeFaceTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        init(context, null);
    }

    /**
     * 初始化View的操作
     *
     * @param context
     * @param attrs
     * @Title init
     * @author haozening
     * @Description
     */
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null && !isInEditMode()) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.attr_domy_text);
            for (int i = 0; i < typedArray.getIndexCount(); i++) {
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.attr_domy_text_usertypeface) {
                    try {
                        int typeface = typedArray.getInt(attr, -1);
                        TypefaceUtil.TypefaceFile file = TypefaceUtil.getTypefaceNameFromAttrValue(typeface);
                        selectTypeface(file);
                    } catch (Exception e) {

                        //Log.v(TAG, "系统缺少字体 e=" + e.toString());
                    }
                }
            }
            typedArray.recycle();
        }
    }

    /**
     * 选择字体
     *
     * @Title selectTypeface
     * @author haozening
     * @Description
     */
    private void selectTypeface(TypefaceUtil.TypefaceFile file) {
        try {
            setTypeface(TypefaceUtil.getTypeface(EBusinessApplication.getInstance().getApplicationContext(), file));
        } catch (Exception e) {
            Log.v(TAG, "e=" + e.toString());
        }

    }

    public void setTypeface(TypefaceUtil.TypefaceFile typefaceFile) {
        try {
            selectTypeface(typefaceFile);
        } catch (Exception e) {
            Log.v(TAG, "e=" + e.toString());
        }

    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) super.onWindowFocusChanged(hasWindowFocus);
    }

    private int focusColor;
    private int lostFocusColor;

    public void setFocusColor(int color) {
        focusColor = color;
    }

    public void setLostFocusColor(int color) {
        lostFocusColor = color;
    }

    public void setFocusFlag(boolean focus) {
        if (focus) {
            setTextColor(focusColor);
        } else {
            setTextColor(lostFocusColor);
        }
    }

    //设置文字行间距
    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
    }
}
