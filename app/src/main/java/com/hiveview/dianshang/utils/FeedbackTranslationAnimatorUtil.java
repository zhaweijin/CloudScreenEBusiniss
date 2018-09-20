/**
 * @Title FeedbackAnimationUtils.java
 * @Package com.hiveview.cloudscreen.video.utils
 * @author haozening
 * @date 2014年9月1日 下午4:09:21
 * @Description
 * @version V1.0
 */
package com.hiveview.dianshang.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;

/**
 * 到View底部按键反馈动画
 * @ClassName FeedbackTranslationAnimator
 * @Description
 * @author haozening
 * @date 2014年9月1日 下午4:09:21
 *
 */
@SuppressLint("NewApi")
public class FeedbackTranslationAnimatorUtil {

    private AnimatorSet animator;
    private ObjectAnimator horizontalPositiveAnimator;
    private ObjectAnimator horizontalNegativeAnimator;
    private ObjectAnimator verticalPositiveAnimator;
    private ObjectAnimator verticalNegativeAnimator;

    private FeedbackTranslationAnimatorUtil() {

    }

    private static class FeedbackTranslationAnimatorUtilHolder {
        static FeedbackTranslationAnimatorUtil util = new FeedbackTranslationAnimatorUtil();
    }

    public static FeedbackTranslationAnimatorUtil getInstance() {
        return FeedbackTranslationAnimatorUtilHolder.util;
    }

    public AnimatorSet getAnimationSet(Object obj, Orientation orientation, float distance, int duration) {
        animator = new AnimatorSet();
        switch (orientation) {
            case HORIZONTAL:
                horizontalPositiveAnimator = ObjectAnimator.ofFloat(obj, "translationX", 0, distance);
                horizontalNegativeAnimator = ObjectAnimator.ofFloat(obj, "translationX", distance, 0);
                animator.playSequentially(horizontalPositiveAnimator.setDuration(duration),
                        horizontalNegativeAnimator.setDuration(3 * duration));
                break;
            case VERTICAL:
                verticalPositiveAnimator = ObjectAnimator.ofFloat(obj, "translationY", 0, distance);
                verticalNegativeAnimator = ObjectAnimator.ofFloat(obj, "translationY", distance, 0);
                animator.playSequentially(verticalPositiveAnimator.setDuration(duration),
                        verticalNegativeAnimator.setDuration(3 * duration));
                break;
        }
        return animator;
    }

    public AnimatorSet getAnimationSet(Object obj, Orientation orientation, float distance) {
        return getAnimationSet(obj, orientation, distance, 200);
    }

    /**
     * 回收动画，解除动画和View之间的引用关系
     * @Title recycle
     * @author haozening
     * @Description
     */
    public void recycle() {
        if (null != animator) {
            animator.removeAllListeners();
            animator.cancel();
        }
        if (null != horizontalPositiveAnimator) {
            horizontalPositiveAnimator.removeAllListeners();
            horizontalPositiveAnimator.cancel();
        }
        if (null != horizontalNegativeAnimator) {
            horizontalNegativeAnimator.removeAllListeners();
            horizontalNegativeAnimator.cancel();
        }
        if (null != verticalPositiveAnimator) {
            verticalPositiveAnimator.removeAllListeners();
            verticalPositiveAnimator.cancel();
        }
        if (null != verticalNegativeAnimator) {
            verticalNegativeAnimator.removeAllListeners();
            verticalNegativeAnimator.cancel();
        }
        if (null != animator) {
            animator.setTarget(null);
        }
        if (null != horizontalPositiveAnimator) {
            horizontalPositiveAnimator.setTarget(null);
        }
        if (null != horizontalNegativeAnimator) {
            horizontalNegativeAnimator.setTarget(null);
        }
        if (null != verticalPositiveAnimator) {
            verticalPositiveAnimator.setTarget(null);
        }
        if (null != verticalNegativeAnimator) {
            verticalNegativeAnimator.setTarget(null);
        }
        animator = null;
    }

    /**
     * 反馈的方向
     * @ClassName Orientation
     * @Description
     * @author haozening
     * @date 2014年9月1日 下午5:15:16
     *
     */
    public static enum Orientation {
        /**
         * 横向的反馈
         */
        HORIZONTAL,
        /**
         * 纵向的反馈
         */
        VERTICAL
    }

}
