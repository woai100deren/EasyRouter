package com.dj.easyrouter.template;

import com.dj.easyrouter.model.EasyRouteMeta;
import java.util.Map;

/**
 * 分组
 */

public interface IRouteGroup {
    void loadInfo(Map<String, EasyRouteMeta> atlas);
}
