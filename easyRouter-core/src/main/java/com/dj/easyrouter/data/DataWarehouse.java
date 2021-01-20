package com.dj.easyrouter.data;


import com.dj.easyrouter.inter.IInterceptor;
import com.dj.easyrouter.model.EasyRouteMeta;
import com.dj.easyrouter.inter.IRouteGroup;
import com.dj.easyrouter.inter.IService;
import com.dj.easyrouter.utils.UniqueKeyTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 数据仓库
 */
public class DataWarehouse {

    /**
     * root 映射表 保存分组信息
     */
    public static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();

    /**
     * group 映射表 保存组中的所有数据
     */
    public static Map<String, EasyRouteMeta> routes = new HashMap<>();

    /**
     * group 映射表 保存组中的所有数据
     */
    public static Map<Class<?>, IService> services = new HashMap<>();

    /**
     * 以键值对优先级的方式保存拦截器对象
     */
    public static Map<Integer, Class<? extends IInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>();
    /**
     * 以集合的方式保存所有拦截器对象
     */
    public static List<IInterceptor> interceptors = new ArrayList<>();
}
