package com.dj.easyrouter.simple;

import android.app.Application;

import com.dj.easyrouter.EasyRouter;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyRouter.init(this);
    }
}
