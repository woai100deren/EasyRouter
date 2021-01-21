package com.dj.easyrouter.inter;

import android.content.Context;

/**
 * 交互接口
 */
public interface IProvider {
    /**
     * 初始化操作，会在注解处理器时调用
     * @param context
     */
    void init(Context context);
}
