package com.dj.easyrouter.callback;

import com.dj.easyrouter.model.RouterForward;

/**
 * 拦截器回调
 */
public interface InterceptorCallback {

    /**
     * 未拦截，走正常流程
     */
    void onNext(RouterForward routerForward);

    /**
     * 拦截器拦截成功，中断流程
     */
    void onInterrupt(String interruptMsg);
}
