package com.dj.easyrouter.exception;

/**
 * 自定义异常类
 */
public class NoRouteFoundException extends RuntimeException {

    public NoRouteFoundException(String detailMessage) {
        super(detailMessage);
    }
}
