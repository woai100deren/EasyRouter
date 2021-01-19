package com.dj.easyrouter.compiler.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * 日志工具类
 */
public class LogUtils {
    private Messager messager;

    private LogUtils(Messager messager) {
        this.messager = messager;
    }

    public static LogUtils newLog(Messager messager) {
        return new LogUtils(messager);
    }

    public void i(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
