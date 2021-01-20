package com.dj.easyrouter.inter;

import android.content.Context;

import com.dj.easyrouter.callback.InterceptorCallback;
import com.dj.easyrouter.model.RouterForward;

/**
 * 拦截器接口
 */
public interface IInterceptor {
    
    /**
     * 拦截器流程
     */
    void process(RouterForward routerForward, InterceptorCallback callback);

    /**
     * 在调用EasyRouter.init()初始化时，会调用到此方法
     */
    void init(Context context);
}
