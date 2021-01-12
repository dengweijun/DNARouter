package com.ben.lib_arouter.template;

import com.ben.router_annotation.model.RouterMeta;

import java.util.Map;

/**
 * @author: BD
 */
public interface IRouterGroup {
    void loadInto(Map<String, RouterMeta> atlas);
}
