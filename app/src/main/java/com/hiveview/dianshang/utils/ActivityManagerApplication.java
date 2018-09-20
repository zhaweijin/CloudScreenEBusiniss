/**
 * @Title ActivityManagerApplication.java
 * @Package com.hiveview.pay.net
 * @author 郭松胜
 * @date 2014-6-25 下午2:17:24
 * @Description TODO
 * @version V1.0
 */
package com.hiveview.dianshang.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;

/**
 * @ClassName: ActivityManagerApplication
 * @Description: TODO
 * @author:
 * @date
 * 
 */
public class ActivityManagerApplication {
	/**
	 * activity栈用来存储activity
	 */
	private static Stack<Activity> activityStack;
	/**
	 * Application实例
	 */
	private static ActivityManagerApplication singleton;


	public static ActivityManagerApplication getInstance() {
		if (null == singleton) {
			singleton = new ActivityManagerApplication();
		}
		return singleton;
	}

	/**
	 * add Activity 添加Activity到栈
	 * @Description: TODO
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * get current Activity 获取当前Activity（栈中最后一个压入的）
	 * 
	 *
	 * @Description: TODO
	 * @return
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（栈中最后一个压入的）
	 * 
	 *
	 * @Description: TODO
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 * 
	 * @Title: ActivityManagerApplication
	 *
	 * @param activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}
	
	/**
	 * 从栈中移除activity
	 * @Title: ActivityManagerApplication
	 *
	 * @param activity
	 */
	public void removeActivity(Activity activity){
		if (activity != null) {
			activityStack.remove(activity);
		}
	}

	/**
	 * 结束指定类名的Activity
	 * 
	 * @Title: ActivityManagerApplication
	 *
	 * @param cls
	 */
	public void finishActivity(Class<?> cls) {
		for(int i=0;i<activityStack.size();i++){
			if (activityStack.get(i).getClass().equals(cls)) {
				finishActivity(activityStack.get(i));
			}

		}
		/*List<Activity> delList = new ArrayList<Activity>();//用来装需要删除的元素
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				//finishActivity(activity);
				delList.add(activity);
			}
		}
		for(int i=0;i<delList.size();i++){
			finishActivity(delList.get(i));
		}*/

	}

	/**
	 * 结束所有Activity
	 * 
	 * @Title: ActivityManagerApplication
	 *
	 * @Description: TODO
	 */
	public void finishAllActivity() {
		int size = activityStack.size();
		for (int i = 0; i < size; i++) {
			if (i>-1&&null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 操作完毕后退出应用程序
	 * 
	 * @Title: ActivityManagerApplication
	 *
	 * @Description: TODO
	 */
	public void appExit() {
		try {
			finishAllActivity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取集合的大小
	 * @Title: ActivityManagerApplication
	 *
	 * @return
	 */
	public int activityStackSize() {
		if (null != activityStack && activityStack.size() > 0) {
			return activityStack.size();
		}

		return 0;
	}

	/**
	 * 关闭其他activity
	 * 
	 * @Title: ActivityManagerApplication
	 *
	 * @Description: TODO
	 */
	public void closeOthers() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
	}

}
