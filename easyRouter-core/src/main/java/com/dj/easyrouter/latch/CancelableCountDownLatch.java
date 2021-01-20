package com.dj.easyrouter.latch;

import java.util.concurrent.CountDownLatch;

/**
 * 计数器
 * CountDownLatch是一种java.util.concurrent包下一个同步工具类，它允许一个或多个线程等待直到在其他线程中一组操作执行完成。
 */
public class CancelableCountDownLatch extends CountDownLatch {

    private String msg = "";

    public CancelableCountDownLatch(int count) {
        super(count);
    }

    /**
     * 当遇到特殊情况时，需要将计数器清0
     */
    public void cancel(String msg) {
        this.msg = msg;
        while (getCount() > 0) {
            countDown();
        }
    }

    public String getMsg(){
        return msg;
    }
}
