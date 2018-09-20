package com.hiveview.dianshang.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

	private static Toast mToast = null;
	private static final int time = 1500;

	public static void showToast(Context context, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, time);
		} else {
			mToast.setText(text);
			mToast.setDuration(time);
		}
		mToast.show();
	}

}
