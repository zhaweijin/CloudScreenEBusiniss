/**
 * @Title MarqueeTextView.java
 * @Package com.hiveview.cloudscreen.video.view
 * @author haozening
 * @date 2014年9月3日 上午10:30:46
 * @Description 
 * @version V1.0
 */
package com.hiveview.dianshang.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;

/**
 * @ClassName MarqueeTextView
 * @Description 
 * @author haozening
 * @date 2014年9月3日 上午10:30:46
 * 
 */
public class MarqueeTextView extends TypeFaceTextView {
	
	private static final String TAG = "MarqueeTextView";
	private boolean isInFocusView = false;

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context) {
		super(context);
	}

	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return isInFocusView;
	}
	
	public void setIsInFocusView(boolean isInFocusView) {
		this.isInFocusView = isInFocusView;
	}
	
}
