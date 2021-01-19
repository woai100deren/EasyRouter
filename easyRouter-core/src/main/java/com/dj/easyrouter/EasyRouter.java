package com.dj.easyrouter;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.dj.easyrouter.template.IRouteRoot;
import com.dj.easyrouter.utils.ClassUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 路由主操作类
 */
public class EasyRouter {
    private static final String ROUTE_ROOT_PAKCAGE = "com.dj.easyrouter.routers";
    private static final String SDK_NAME = "EasyRouter";
    private static final String SEPARATOR = "_";
    private static final String SUFFIX_ROOT = "Root";
    private static final String SUFFIX_INTERCEPTOR = "Interceptor";

    private EasyRouter(){}
    private volatile static EasyRouter instance;
    /**路由表*/
    private static Map<String,Class<? extends Activity>> routers = new HashMap<>();

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
    /**
     * 初始化路由器
     * @param application
     */
    public static void init(Application application){
        try {
            loadInfo(application);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


    /**
     * 分组表制作
     */
    private static void loadInfo(Application application) throws PackageManager.NameNotFoundException, InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //获得所有 apt生成的路由类的全类名 (路由表)
        Set<String> routerMap = ClassUtil.getFileNameByPackageName(application, ROUTE_ROOT_PAKCAGE);
        for (String className : routerMap) {
            if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                //root中注册的是分组信息 将分组信息加入仓库中
                ((IRouteRoot) Class.forName(className).getConstructor().newInstance()).loadInfo(Warehouse.groupsIndex);
            }
//            else if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR + SUFFIX_INTERCEPTOR)) {
//
//                ((IInterceptorGroup) Class.forName(className).getConstructor().newInstance()).loadInto(Warehouse.interceptorsIndex);
//            }
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
