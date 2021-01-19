package com.dj.easyrouter;


import com.dj.easyrouter.model.EasyRouteMeta;
import com.dj.easyrouter.template.IRouteGroup;
import com.dj.easyrouter.template.IService;
import java.util.HashMap;
import java.util.Map;
/**
 * 仓库
 */

public class Warehouse {

    // root 映射表 保存分组信息
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();

    // group 映射表 保存组中的所有数据
    static Map<String, EasyRouteMeta> routes = new HashMap<>();

    // group 映射表 保存组中的所有数据
    static Map<Class, IService> services = new HashMap<>();
    // TestServiceImpl.class , TestServiceImpl 没有再反射

//    /**
//     * 以键值对优先级的方式保存拦截器对象
//     */
//    public static Map<Integer, Class<? extends IInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>();
//    /**
//     * 以集合的方式保存所有拦截器对象
//     */
//    public static List<IInterceptor> interceptors = new ArrayList<>();
}
