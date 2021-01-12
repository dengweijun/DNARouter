package com.ben.lib_arouter.template;

import java.util.Map;

/**
 * @author: BD
 */
public interface IRouterRoot {
    void loadInto(Map<String, Class<? extends IRouterGroup>> routes);
}
