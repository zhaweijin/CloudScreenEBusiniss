package com.hiveview.dianshang.base;

/**
 * Created by zwj on 2/26/18.
 */

public interface AcutionListener {
    /**
     *
     * @param status　　商品是否竞拍结束
     */
    void OnItemAcutionListener(boolean status);

    /**
     * 小于10%警报
     */
    void onWaring();
}
