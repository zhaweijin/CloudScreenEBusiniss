/**
 * @Title TypefaceUtil.java
 * @Package com.hiveview.domyphonemate.utils
 * @author haozening
 * @date 2014年6月5日 下午12:44:24
 * @Description
 * @version V1.0
 */
package com.hiveview.dianshang.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取字体的工具类
 *
 * @author haozening
 * @ClassName TypefaceUtil
 * @Description
 * @date 2014年6月5日 下午12:44:24
 */
public class TypefaceUtil {

    /**
     * 字体文件和配置文件属性值的映射
     *
     * @author haozening
     * @ClassName TypefaceFile
     * @Description
     * @date 2014年8月30日 上午11:25:25
     */
    public static enum TypefaceFile {
        YAHEI("arial.ttf", 0),
        SIMHEI("SIMHEI.TTF", 1);

        private String fileName;
        private int attrValue;

        private TypefaceFile(String fileName, int attrValue) {
            this.fileName = fileName;
            this.attrValue = attrValue;
        }

        @Override
        public String toString() {
            return fileName;
        }

        /**
         * 获取文件名称
         *
         * @param attrValue
         * @return
         * @Title getFileName
         * @author haozening
         * @Description
         */
        public TypefaceFile getFileName(int attrValue) {
            return TYPEFACES_ATTR_MAP.get(attrValue);
        }

        public int getAttrValue() {
            return attrValue;
        }
    }

    private static final String FONTS_PATH = Environment.getRootDirectory().getPath() + File.separator + "fonts";

    /**
     * 字体文件和属性配置文件中的对应关系
     */
    private static final SparseArray<TypefaceFile> TYPEFACES_ATTR_MAP = new SparseArray<TypefaceFile>();
    /**
     * 字体文件和字体的对应关系
     */
    private static final Map<TypefaceFile, Typeface> TYPEFACES = new HashMap<TypefaceFile, Typeface>();

    private static final String TAG = "TypefaceUtil";

    /**
     * 获取字体
     *
     * @param context
     * @param file
     * @return
     * @Title getTypeface
     * @author haozening
     * @Description
     */
    public static Typeface getTypeface(Context context, TypefaceFile file) {
        Typeface typeface = TYPEFACES.get(file);
        if (null == typeface) {
            typeface = getTypefaceFromFile(context, file.toString());
            TYPEFACES.put(file, typeface);
        }
        return typeface;
    }

    /**
     * 通过属性获取字体文件名称
     *
     * @param attrValue
     * @return
     * @Title getTypefaceNameFromAttrValue
     * @author haozening
     * @Description
     */
    public static TypefaceFile getTypefaceNameFromAttrValue(int attrValue) {
        if (TYPEFACES_ATTR_MAP.size() <= 0) {
            TypefaceFile[] files = TypefaceFile.values();
            for (int i = 0; i < files.length; i++) {
                TypefaceFile typefaceFile = files[i];
                Log.d(TAG, "typefaceFile attrValue : " + typefaceFile.getAttrValue() + "  fileName : " + typefaceFile.toString());
                TYPEFACES_ATTR_MAP.put(typefaceFile.getAttrValue(), typefaceFile);
            }
        }
        return TYPEFACES_ATTR_MAP.get(attrValue);
    }

    /**
     * 从文件中获取字体
     *
     * @param context
     * @param typefaceFileName
     * @return
     * @Title getTypefaceFromFile
     * @author haozening
     * @Description
     */
    private static Typeface getTypefaceFromFile(Context context, String typefaceFileName) {
        Typeface tf = Typeface.createFromFile(getTypefacePath(typefaceFileName));
        return tf;
    }

    /**
     * 获取字体文件路径
     *
     * @param typefaceFileName
     * @return
     * @Title getTypefacePath
     * @author haozening
     * @Description
     */
    private static String getTypefacePath(String typefaceFileName) {
        return FONTS_PATH + File.separator + typefaceFileName;
    }
}
