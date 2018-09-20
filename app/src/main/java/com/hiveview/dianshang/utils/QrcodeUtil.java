package com.hiveview.dianshang.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hiveview.dianshang.R;

import java.util.Hashtable;

/**
 * Created by carter on 6/13/15.
 */
public class QrcodeUtil {

    private Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;

                if (matrix.get(i, j)) {
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public Drawable generateQRCode(Context mContext,String content) {
        try {

            QRCodeWriter writer = new QRCodeWriter();
            // MultiFormatWriter writer = new MultiFormatWriter();

            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, mContext.getResources().getDimensionPixelSize(R.dimen.length_400),
                    mContext.getResources().getDimensionPixelSize(R.dimen.length_400));
            Bitmap bitmap = bitMatrix2Bitmap(matrix);
//            Drawable drawable =new BitmapDrawable(new QrcodeUtil().generateQRCode("carter"));
            return  new BitmapDrawable(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Drawable generateQRCodeFirst(Context mContext,String content) {
        try {

            //配置参数
            Hashtable hints = new Hashtable();
            //设置编码格式，防止汉字乱码
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //设置容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度，默认为4
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter writer = new QRCodeWriter();
            // MultiFormatWriter writer = new MultiFormatWriter();

            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, mContext.getResources().getDimensionPixelSize(R.dimen.length_400),
                    mContext.getResources().getDimensionPixelSize(R.dimen.length_400),hints);
            Bitmap bitmap = bitMatrix2Bitmap(matrix);
            return  new BitmapDrawable(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }



}
