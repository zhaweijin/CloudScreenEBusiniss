package com.hiveview.dianshang.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.media.ThumbnailUtils;
import android.view.View;

/***
 * 
 * @ClassName: BitmapUtils
 * @Description: TODO 防止报oom错误的图片解析工具类
 * @author: maliang
 * @date 2014-8-15 下午12:03:48
 * 
 */
public class BitmapUtils {

	private static final String TAG = "BitmapUtils";

	private static Matrix matrix;

    /* 屏幕宽 */
	private static int screenWidth=0;
	/* 屏幕高*/
	private static int screenHeight=0;
	
	public static Bitmap readBitMap(Context context, int resId, int inSampleSize) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		if (inSampleSize > 0) {
			opt.inSampleSize = 2;
		}
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		// opt.outHeight=250;
		// opt.outWidth = 250;
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 
	 * @Title: BitmapUtils
	 * @author:maliang
	 * @Description: TODO 获取图片的采样系数
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			int heightRatio = Math.round((float) height / (float) reqHeight);
			int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/***
	 * 
	 * @Title: BitmapUtils
	 * @author:maliang
	 * @Description: TODO 拉伸图片(主要在主界面的文件列表中对apk的文件的icon进行拉伸)
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createBitmapLarge(Bitmap bitmap) {
		if (matrix == null) {
			matrix = new Matrix();
			matrix.postScale(6, 6);
		}
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	private static PackageManager pm;



	/***
	 * 
	 * @Title: BitmapUtils
	 * @author:maliang
	 * @Description: TODO 获取视频文件的缩略图
	 * @param videoPath
	 * @param width
	 * @param height
	 * @param kind
	 * @return
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		try {
			bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	
	public static Bitmap createReflectedImage(Bitmap originalImage) {

        final int reflectionGap = 0;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                height / 2, width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 4), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);

        canvas.drawBitmap(originalImage, 0, 0, null);

        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
                        + reflectionGap, 0x70ffffff, 0x00ffffff,
                TileMode.MIRROR);

        paint.setShader(shader);

        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }
	

    public static void saveBitmapFile(Bitmap bitmap){
		if(bitmap==null){
			Utils.print("main","bitmap null");
			return;
		}
		File file= new File("/mnt/sdcard/0001.jpg");//将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static Bitmap getViewBitmap(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		return bitmap;
	}
}
