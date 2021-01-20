package com.dj.easyrouter.impl;

import android.content.Context;

import com.dj.easyrouter.data.DataWarehouse;
import com.dj.easyrouter.callback.InterceptorCallback;
import com.dj.easyrouter.inter.IInterceptor;
import com.dj.easyrouter.latch.CancelableCountDownLatch;
import com.dj.easyrouter.model.RouterForward;
import com.dj.easyrouter.thread.DefaultPoolExecutor;
import com.dj.easyrouter.utils.Utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 拦截器实现，在初始化路由，以及调用路由时，都需要调用到此类
 */
public class InterceptorImpl {

    /**
     * 初始化路由时，需要轮询每个拦截器中的init()方法
     */
    public static void init(final Context context) {
        DefaultPoolExecutor.newDefaultPoolExecutor(6).execute(new Runnable() {
            @Override
            public void run() {
                if (!Utils.isEmpty(DataWarehouse.interceptorsIndex)) {
                    for (Map.Entry<Integer, Class<? extends IInterceptor>> entry : DataWarehouse.interceptorsIndex.entrySet()) {
                        Class<? extends IInterceptor> interceptorClass = entry.getValue();
                        try {
                            IInterceptor iInterceptor = interceptorClass.getConstructor().newInstance();
                            iInterceptor.init(context);
                            DataWarehouse.interceptors.add(iInterceptor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    /**
     * 执行拦截逻辑
     */
    public static void onInterceptions(final RouterForward routerForward, final InterceptorCallback callback) {
        if (DataWarehouse.interceptors.size() > 0) {
            DefaultPoolExecutor.executor.execute(new Runnable() {
                @Override
                public void run() {
                    CancelableCountDownLatch countDownLatch = new CancelableCountDownLatch(DataWarehouse.interceptors.size());
                    execute(0, countDownLatch, routerForward);
                    try {
                        countDownLatch.await(300, TimeUnit.SECONDS);
                        if (countDownLatch.getCount() > 0){
                            callback.onInterrupt("拦截器处理超时");
                        }else if(!Utils.isEmpty(countDownLatch.getMsg())){
                            callback.onInterrupt(countDownLatch.getMsg());
                        }else {
                            callback.onNext(routerForward);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            callback.onNext(routerForward);
        }
    }


    /**
     * 以递归的方式走完所有拦截器的process()方法
     */
    private static void execute(final int index, final CancelableCountDownLatch countDownLatch, final RouterForward routerForward) {
        if (index < DataWarehouse.interceptors.size()){

            IInterceptor iInterceptor = DataWarehouse.interceptors.get(index);
            iInterceptor.process(routerForward, new InterceptorCallback() {
                @Override
                public void onNext(RouterForward routerForward) {
                    countDownLatch.countDown();
                    execute(index + 1, countDownLatch, routerForward);
                }

                @Override
                public void onInterrupt(String msg) {
                    countDownLatch.cancel(msg);
                }
            });
        }
    }
}
































