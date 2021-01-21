package com.dj.easyrouter.businessmodule2;

import android.content.Context;
import android.util.Log;

import com.dj.easyrouter.annotation.EasyRoute;
import com.dj.easyrouter.export.HelloService;

@EasyRoute(path = "/module2/helloService")
public class HelloServiceIml implements HelloService {
    @Override
    public void sayHello(String name) {
        Log.e("HelloService", "Hello " + name);
    }

    @Override
    public void init(Context context) {
        Log.e("HelloService", HelloService.class.getName() + " has init.");
    }
}
