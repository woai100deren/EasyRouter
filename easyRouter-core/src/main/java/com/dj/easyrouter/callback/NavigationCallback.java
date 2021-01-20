package com.dj.easyrouter.callback;

import com.dj.easyrouter.model.RouterForward;

/**
 * 页面跳转状态回调接口
 */
public interface NavigationCallback {

    /**
     * 找到跳转页面
     * @param postcard
     */
    void onFound(RouterForward postcard);

    /**
     * 未找到
     * @param postcard
     */
    void onLost(RouterForward postcard);

    /**
     * 成功跳转
     * @param postcard
     */
    void onArrival(RouterForward postcard);

    /**
     * 中断了路由跳转
     */
    void onInterrupt(Throwable throwable);
}
