package com.dj.easyrouter.inter;

import java.util.Map;

public interface IRouteRoot {
    void loadInfo(Map<String, Class<? extends IRouteGroup>> routes);
}
