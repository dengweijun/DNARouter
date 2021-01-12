package com.ben.lib_arouter.callback;

import com.ben.lib_arouter.Postcard;

/**
 * @author: BD
 */
public interface NavigationCallback {

    /**
     * 找到跳转的页面
     *
     * @param postcard
     */
    void onFound(Postcard postcard);

    /**
     * 未找到
     *
     * @param postcard
     */
    void onLost(Postcard postcard);

    /**
     * 成功跳转
     *
     * @param postcard
     */
    void onArrival(Postcard postcard);

}
