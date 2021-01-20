package com.dj.easyrouter.businessmodule1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute(path = "/module1/myService")
public class MyService extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("111","业务1的MyService服务启动");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("111","业务1的MyService服务停止");
        super.onDestroy();
    }
}
