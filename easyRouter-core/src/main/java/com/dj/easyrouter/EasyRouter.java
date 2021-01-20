package com.dj.easyrouter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.dj.easyrouter.callback.InterceptorCallback;
import com.dj.easyrouter.callback.NavigationCallback;
import com.dj.easyrouter.data.DataWarehouse;
import com.dj.easyrouter.exception.NoRouteFoundException;
import com.dj.easyrouter.impl.InterceptorImpl;
import com.dj.easyrouter.model.EasyRouteMeta;
import com.dj.easyrouter.model.RouterForward;
import com.dj.easyrouter.inter.IRouteGroup;
import com.dj.easyrouter.inter.IRouteRoot;
import com.dj.easyrouter.inter.IService;
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
    private static Context mContext;

    private Handler mHandler;

    private EasyRouter(){
        mHandler = new Handler(Looper.getMainLooper());
    }
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
            mContext = application.getApplicationContext();
            loadInfo(application);
            InterceptorImpl.init(mContext);
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
                ((IRouteRoot) Class.forName(className).getConstructor().newInstance()).loadInfo(DataWarehouse.groupsIndex);
            }
//            else if (className.startsWith(ROUTE_ROOT_PAKCAGE + "." + SDK_NAME + SEPARATOR + SUFFIX_INTERCEPTOR)) {
//
//                ((IInterceptorGroup) Class.forName(className).getConstructor().newInstance()).loadInto(Warehouse.interceptorsIndex);
//            }
        }
    }


    public RouterForward build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return build(path, extractGroup(path));
        }
    }

    public RouterForward build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new RuntimeException("路由地址无效!");
        } else {
            return new RouterForward(path, group);
        }
    }

    /**
     * 获得组别
     *
     * @param path
     * @return
     */
    private String extractGroup(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new RuntimeException(path + " : 不能提取group.");
        }
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new RuntimeException(path + " : 不能提取group.");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用于停止service
     * @param context
     * @param routerForward
     * @param callback
     * @return
     */
    public Object stopNavigation(final Context context, final RouterForward routerForward, final NavigationCallback callback) {
        if (callback != null) {
            InterceptorImpl.onInterceptions(routerForward, new InterceptorCallback() {
                @Override
                public void onNext(RouterForward routerForward) {
                    _stopNavigation(context, routerForward, callback);
                }

                @Override
                public void onInterrupt(String interruptMsg) {

                    callback.onInterrupt(new Throwable(interruptMsg));
                }
            });
        }else{

            return _stopNavigation(context, routerForward, callback);
        }
        return null;
    }



    public Object navigation(final Context context, final RouterForward routerForward, final int requestCode, final NavigationCallback callback) {
        if (callback != null) {
            InterceptorImpl.onInterceptions(routerForward, new InterceptorCallback() {
                @Override
                public void onNext(RouterForward routerForward) {
                    _navigation(context, routerForward, requestCode, callback);
                }

                @Override
                public void onInterrupt(String interruptMsg) {

                    callback.onInterrupt(new Throwable(interruptMsg));
                }
            });
        }else{

            return _navigation(context, routerForward, requestCode, callback);
        }
        return null;
    }

    /**
     * 停止service
     * @param context
     * @param routerForward
     * @param callback
     * @return
     */
    protected Object _stopNavigation(final Context context, final RouterForward routerForward, final NavigationCallback callback) {
        try {
            prepareCard(routerForward);
        } catch (NoRouteFoundException e) {
            e.printStackTrace();
            //没找到
            if (null != callback) {
                callback.onLost(routerForward);
            }
            return null;
        }
        if (null != callback) {
            callback.onFound(routerForward);
        }

        switch (routerForward.getType()) {
            case SERVICE:{
                Context currentContext = null == context ? mContext : context;
                Intent intent = new Intent(currentContext, routerForward.getDestination());
                intent.putExtras(routerForward.getExtras());
                int flags = routerForward.getFlag();
                if (-1 != flags) {
                    intent.setFlags(flags);
                }
                //主线程中进行跳转操作
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        currentContext.stopService(intent);
                        //跳转完成
                        if (null != callback) {
                            callback.onArrival(routerForward);
                        }
                    }
                });
                break;
            }
            default:
                break;
        }
        return null;
    }

    /**
     * 跳转执行
     * @param context
     * @param routerForward
     * @param requestCode
     * @param callback
     * @return
     */
    protected Object _navigation(final Context context, final RouterForward routerForward, final int requestCode, final NavigationCallback callback) {
        try {
            prepareCard(routerForward);
        } catch (NoRouteFoundException e) {
            e.printStackTrace();
            //没找到
            if (null != callback) {
                callback.onLost(routerForward);
            }
            return null;
        }
        if (null != callback) {
            callback.onFound(routerForward);
        }

        switch (routerForward.getType()) {
            case ACTIVITY: {
                Context currentContext = null == context ? mContext : context;
                Intent intent = new Intent(currentContext, routerForward.getDestination());
                intent.putExtras(routerForward.getExtras());
                int flags = routerForward.getFlag();
                if (-1 != flags) {
                    intent.setFlags(flags);
                } else if (!(currentContext instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                //主线程中进行跳转操作
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //可能需要返回码
                        if (requestCode > 0) {
                            ActivityCompat.startActivityForResult((Activity) currentContext, intent, requestCode, routerForward.getOptionsBundle());
                        } else {
                            ActivityCompat.startActivity(currentContext, intent, routerForward.getOptionsBundle());
                        }

                        if ((0 != routerForward.getEnterAnim() || 0 != routerForward.getExitAnim()) &&
                                currentContext instanceof Activity) {
                            //老版本
                            ((Activity) currentContext).overridePendingTransition(routerForward.getEnterAnim()
                                    , routerForward.getExitAnim());
                        }
                        //跳转完成
                        if (null != callback) {
                            callback.onArrival(routerForward);
                        }
                    }
                });
                break;
            }
            case SERVICE:{
                Context currentContext = null == context ? mContext : context;
                Intent intent = new Intent(currentContext, routerForward.getDestination());
                intent.putExtras(routerForward.getExtras());
                int flags = routerForward.getFlag();
                if (-1 != flags) {
                    intent.setFlags(flags);
                }else if (!(currentContext instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                //主线程中进行跳转操作
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        currentContext.startService(intent);
                        //跳转完成
                        if (null != callback) {
                            callback.onArrival(routerForward);
                        }
                    }
                });
//                return routerForward.getService();
                break;
            }
            default:
                break;
        }
        return null;
    }

    /**
     * 准备跳转数据
     * @param routerForward
     */
    private void prepareCard(RouterForward routerForward) {
        EasyRouteMeta routeMeta = DataWarehouse.routes.get(routerForward.getPath());
        if (null == routeMeta) {
            Class<? extends IRouteGroup> groupMeta = DataWarehouse.groupsIndex.get(routerForward.getGroup());
            if (null == groupMeta) {
                throw new NoRouteFoundException("没找到对应路由：分组=" + routerForward.getGroup() + "   路径=" + routerForward.getPath());
            }
            IRouteGroup iGroupInstance;
            try {
                iGroupInstance = groupMeta.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("路由分组映射表记录失败.", e);
            }
            iGroupInstance.loadInfo(DataWarehouse.routes);
            //已经准备过了就可以移除了 (不会一直存在内存中)
            DataWarehouse.groupsIndex.remove(routerForward.getGroup());
            //再次进入 else
            prepareCard(routerForward);
        } else {
            //类 要跳转的activity 或IService实现类
            routerForward.setDestination(routeMeta.getDestination());
            routerForward.setType(routeMeta.getType());
//            switch (routeMeta.getType()) {
//                case SERVICE:
//                    Class<?> destination = routeMeta.getDestination();
//                    IService service = DataWarehouse.services.get(destination);
//                    if (null == service) {
//                        try {
//                            service = (IService) destination.getConstructor().newInstance();
//                            DataWarehouse.services.put(destination, service);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    routerForward.setService(service);
//                    break;
//                default:
//                    break;
//            }
        }
    }
}
