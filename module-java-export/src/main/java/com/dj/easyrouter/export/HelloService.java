package com.dj.easyrouter.export;

import com.dj.easyrouter.inter.IProvider;

public interface HelloService extends IProvider {
    void sayHello(String name);
}