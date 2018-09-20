/**
 * @Title LoadingImageView.java
 * @Package com.hiveview.cloudscreen.player.view
 * @author haozening
 * @date 2014年11月3日 下午7:21:04
 * @Description 
 * @version V1.0
 */
package com.hiveview.dianshang.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hiveview.dianshang.R;


/**
 * @ClassName LoadingImageView
 * @Description
 * @author haozening
 * @date 2014年11月3日 下午7:21:04
 * 
 */
@SuppressLint("NewApi")
public class LoadingView extends LinearLayout {

	private TextView firstLineView;
	private TextView twoLineView;

	private static final int DURATION = 600;

	private View leftItemView;
	private View centerItemView;
	private View rightItemView;

	private LoadViewWrapper leftWrapper;
	private LoadViewWrapper centerWrapper;
	private LoadViewWrapper rightWrapper;

	private ValueAnimator leftAni;
	private ValueAnimator centerAni;
	private ValueAnimator rightAni;

	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LoadingView(Context context) {
		super(context);
		init(context);
	}

	public void init(Context context) {
		setOrientation(LinearLayout.VERTICAL);
		addAllView(context);
		Log.d("init", "init");
	}

	private void addAllView(Context context) {

		firstLineView = new TextView(context);
		firstLineView.setTextColor(Color.parseColor("#FFFFFF"));
		firstLineView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.sp_40));
		LayoutParams firstParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		firstLineView.setLayoutParams(firstParams);
		firstLineView.setGravity(Gravity.CENTER_HORIZONTAL);

		twoLineView = new TextView(context);
		twoLineView.setTextColor(Color.parseColor("#FFFFFF"));
		twoLineView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.sp_40));
		LayoutParams twoParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		twoLineView.setLayoutParams(twoParams);
		twoLineView.setGravity(Gravity.CENTER_HORIZONTAL);

		addView(createLoadingLayout(context));
		addView(firstLineView);
		addView(twoLineView);

		createLoadingAnimator();
	}

	private View createLoadingLayout(Context context) {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(HORIZONTAL);
		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		leftItemView = LayoutInflater.from(context).inflate(
				R.layout.view_item_loading_zb, null);
		centerItemView = LayoutInflater.from(context).inflate(
				R.layout.view_item_loading_zb, null);
		rightItemView = LayoutInflater.from(context).inflate(
				R.layout.view_item_loading_zb, null);

		int width = context.getResources().getDimensionPixelSize(
				R.dimen.loadingview_width_57);
		int height = context.getResources().getDimensionPixelSize(
				R.dimen.loadingview_height_145);
		leftItemView.setLayoutParams(new LayoutParams(width, height));
		centerItemView.setLayoutParams(new LayoutParams(width, height));
		rightItemView.setLayoutParams(new LayoutParams(width, height));

		leftWrapper = new LoadViewWrapper(leftItemView);
		centerWrapper = new LoadViewWrapper(centerItemView);
		rightWrapper = new LoadViewWrapper(rightItemView);

		layout.addView(leftItemView);
		layout.addView(centerItemView);
		layout.addView(rightItemView);

		return layout;
	}

	private void createLoadingAnimator() {
		// alpha变化
		PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat(
				"alpha", 0.3f, 1f);
		PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(
				"scaleY", 1f, 4f);

		leftAni = ObjectAnimator.ofPropertyValuesHolder(leftWrapper,
				scaleYHolder, alphaHolder);
		centerAni = ObjectAnimator.ofPropertyValuesHolder(centerWrapper,
				scaleYHolder, alphaHolder);
		rightAni = ObjectAnimator.ofPropertyValuesHolder(rightWrapper,
				scaleYHolder, alphaHolder);
		leftAni.setDuration(DURATION).reverse();
		centerAni.setDuration(DURATION).reverse();
		rightAni.setDuration(DURATION).reverse();
		leftAni.setRepeatCount(ObjectAnimator.INFINITE);
		leftAni.setRepeatMode(ObjectAnimator.REVERSE);
		centerAni.setRepeatCount(ObjectAnimator.INFINITE);
		centerAni.setRepeatMode(ObjectAnimator.REVERSE);
		rightAni.setRepeatCount(ObjectAnimator.INFINITE);
		rightAni.setRepeatMode(ObjectAnimator.REVERSE);

		leftAni.setInterpolator(new LinearInterpolator());
		centerAni.setInterpolator(new LinearInterpolator());
		rightAni.setInterpolator(new LinearInterpolator());
		
		if (Build.VERSION.SDK_INT >= 19) {
			leftAni.start();
			 centerAni.start();
			 rightAni.start();
			 leftAni.setCurrentPlayTime(360);
			 centerAni.setCurrentPlayTime(180);
			 rightAni.setCurrentPlayTime(0);
		}
		 

	}

	public void setFirstLineText(String text) {
		firstLineView.setText(text);
	}

	public void setTwoLineText(String text) {
		twoLineView.setText(text);
	}

	public void setTextVisibility(int visibility) {
		firstLineView.setVisibility(visibility);
		twoLineView.setVisibility(visibility);
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.INVISIBLE || visibility == View.GONE) {
			stop();
		} else {
			start();
		}
	}

	private void stop() {
		if (Build.VERSION.SDK_INT >= 19) {
			leftAni.pause();
			centerAni.pause();
			rightAni.pause();
		} else {
			leftAni.cancel();
			centerAni.cancel();
			rightAni.cancel();
		}

	}

	private void start() {
		if (Build.VERSION.SDK_INT >= 19) {
			leftAni.resume();
			centerAni.resume();
			rightAni.resume();
		} else {
			leftAni.start();
			centerAni.start();
			rightAni.start();
			leftAni.setCurrentPlayTime(360);
			centerAni.setCurrentPlayTime(180);
			rightAni.setCurrentPlayTime(0);
		}

	}

	public void recycle() {
		if (null != leftAni) {
			leftAni.cancel();
		}
		if (null != centerAni) {
			centerAni.cancel();
		}
		if (null != rightAni) {
			rightAni.cancel();
		}
		leftWrapper.recycle();
		centerWrapper.recycle();
		rightWrapper.recycle();
	}

	public static class LoadViewWrapper {
		private ViewGroup.LayoutParams params;
		private View child;
		private View top;
		private View bottom;
		private View parent;
		private float scaleY = 1f;

		public LoadViewWrapper(View view) {
			parent = view;
			top = view.findViewById(R.id.iv_top);
			child = view.findViewById(R.id.iv_center);
			bottom = view.findViewById(R.id.iv_bottom);

			params = (ViewGroup.LayoutParams) child.getLayoutParams();
			if (null == params) {
				params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				child.setLayoutParams(params);
			}

		}

		public void setScaleY(float scaleY) {
			if (null != top && null != child && null != bottom) {
				top.setTranslationY(-(child.getMeasuredHeight() - 1)
						* (scaleY - 1) / 2);
				child.setScaleY(scaleY);
				bottom.setTranslationY((child.getMeasuredHeight() - 1)
						* (scaleY - 1) / 2);
			}
			this.scaleY = scaleY;

		}

		public float getScaleY() {
			return scaleY;
		}

		public void setAlpha(float alpha) {
			if (parent != null) {
				parent.setAlpha(alpha);
			}
		}

		public void recycle() {
			params = null;
			child = null;
			top = null;
			bottom = null;
			parent = null;
		}

		public float getAlpha() {
			if (parent != null) {
				return parent.getAlpha();
			} else {
				return -1f;
			}
		}
	}
}
