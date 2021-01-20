package com.dj.easyrouter.simple;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute(path = "/app/myService")
public class MyService extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("111","MyService服务启动");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("111","MyService服务停止");
        super.onDestroy();
    }
}
