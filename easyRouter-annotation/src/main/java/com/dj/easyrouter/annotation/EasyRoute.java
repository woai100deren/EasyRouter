package com.dj.easyrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//这个注解可以用到什么地方
@Target(ElementType.TYPE)
//注解保留到
//这里有三种类型，source给APT用、class给字节码插装技术用（编译后处理筛选）、RUNTIME给反射用
@Retention(RetentionPolicy.SOURCE)
public @interface EasyRoute {
    /**
     * 路由的路径
     * @return
     */
    String path();

    /**
     * 将路由节点进行分组，可以实现动态加载
     * @return
     */
    String group() default "";
}
