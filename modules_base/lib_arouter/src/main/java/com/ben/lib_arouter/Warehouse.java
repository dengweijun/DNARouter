package com.ben.lib_arouter;

import com.ben.lib_arouter.template.IRouterGroup;
import com.ben.lib_arouter.template.IService;
import com.ben.router_annotation.model.RouterMeta;

import java.util.HashMap;
import java.util.Map;


/**
 * 存放数据的地方
 */
public class Warehouse {

    // root 映射表 保存分组信息
    static Map<String, Class<? extends IRouterGroup>> groupsIndex = new HashMap<>();

    // group 映射表 保存组中的所有数据
    static Map<String, RouterMeta> routers = new HashMap<>();

    // group 映射表 保存组中的所有数据
    static Map<Class, IService> services = new HashMap<>();

}