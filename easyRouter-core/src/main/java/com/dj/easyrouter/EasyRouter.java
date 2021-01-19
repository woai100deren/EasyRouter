package com.dj.easyrouter;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.dj.easyrouter.inter.IRouterLoad;
import com.dj.easyrouter.utils.ClassUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 路由主操作类
 */
public class EasyRouter {
    private EasyRouter(){}
    private volatile static EasyRouter instance;
    /**路由表*/
    private static Map<String,Class<? extends Activity>> routers = new HashMap<>();
    private static boolean registerByPlugin;

    /**
     * 获取单例
     * @return
     */
    public static EasyRouter getInstance() {
        if (instance == null) {
            synchronized (EasyRouter.class) {
                if (instance == null) {
                    instance = new EasyRouter();
                }
            }
        }
        return instance;
    }

    private static void loadRouterMap(){
        registerByPlugin = false;
    }

    /**
     * 初始化路由器
     * @param application
     */
    public static void init(Application application){
//        loadRouterMap();
//        if(registerByPlugin){
//            return;
//        }
        try {
            //注解处理器会把生成的代码，放到com.dj.easyrouter.routers包下
            Set<String> classNames = ClassUtil.getFileNameByPackageName(application,"com.dj.easyrouter.routers");
            for(String className:classNames){
                Class<?> cls = Class.forName(className);
                //判断cls类是否实现了IRouterLoad接口
                if(IRouterLoad.class.isAssignableFrom(cls)){
                    IRouterLoad iRouterLoad = (IRouterLoad)cls.newInstance();
                    iRouterLoad.loadInfo(routers);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册路由
     * @param path 路由路径
     * @param cls 目标Activity类
     */
    public void register(String path,Class<? extends Activity> cls){
        routers.put(path,cls);
    }

    /**
     * 启动（跳转）页面
     * @param activity 当前Activity上下文
     * @param path 路由路径（目标页面）
     */
    public void startActivity(Activity activity,String path){
        Class<? extends Activity> cls = routers.get(path);
        if(cls != null){
            Intent intent = new Intent(activity, cls);
            activity.startActivity(intent);
        }
    }
}
