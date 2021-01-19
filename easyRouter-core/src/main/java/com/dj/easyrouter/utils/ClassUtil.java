package com.dj.easyrouter.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.DexFile;

public class ClassUtil {
    /**
     * 获得程序所有的apk(instant run会产生很多split apk)
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    private static List<String> getSourcePaths(Context context) throws PackageManager.NameNotFoundException{
        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),0);
        List<String> sourcePaths = new ArrayList<>();
        //当前应用apk的文件
        sourcePaths.add(applicationInfo.sourceDir);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(null != applicationInfo.splitSourceDirs){
                sourcePaths.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
            }
        }
        return sourcePaths;
    }

    /**
     * 通过包名获取包名下所有的文件名称（class类名称）
     * @param context
     * @param packageName
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static Set<String> getFileNameByPackageName(Application context,final String packageName) throws PackageManager.NameNotFoundException{
        //拿到apk当中的dex地址
        final Set<String> classNames = new HashSet<>();
        List<String> paths = getSourcePaths(context);

        for(final String path:paths){
            DexFile dexFile = null;
            try{
                //加载apk中的dex，并遍历获得所有packageName的类
                dexFile = new DexFile(path);
                Enumeration<String> dexEntries = dexFile.entries();
                while (dexEntries.hasMoreElements()){
                    String className = dexEntries.nextElement();
                    if(className.startsWith(packageName)){
                        classNames.add(className);
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(null != dexFile){
                    try{
                        dexFile.getClass();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return classNames;
    }
}
