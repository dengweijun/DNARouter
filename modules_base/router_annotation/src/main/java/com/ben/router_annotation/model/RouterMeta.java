package com.ben.router_annotation.model;

import com.ben.router_annotation.Router;

import javax.lang.model.element.Element;

/**
 * 路由数据封装
 *
 * @author: BD
 */
public class RouterMeta {

    /**
     * 路由枚举类型：
     * Activity或Activity的子类；
     * 实现了IService接口的类；
     */
    public enum Type {
        ACTIVITY,
        ISERVICE
    }

    private Type type;

    /**
     * 节点（Activity，IService）
     */
    private Element element;

    /**
     * 注解使用的类对象
     */
    private Class<?> destination;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 路由组
     */
    private String group;

    /**
     * 提供给代码生成器调用
     *
     * @param type
     * @param destination
     * @param path
     * @param group
     * @return
     */
    public static RouterMeta build(Type type, Class<?> destination, String path, String
            group) {
        return new RouterMeta(type, null, destination, path, group);
    }


    public RouterMeta() {
    }

    /**
     * Type
     *
     * @param router  router
     * @param element element
     */
    public RouterMeta(Type type, Router router, Element element) {
        this(type, element, null, router.path(), router.group());
    }

    public RouterMeta(Type type, Element element, Class<?> destination, String path, String group) {
        this.type = type;
        this.element = element;
        this.destination = destination;
        this.path = path;
        this.group = group;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "RouterMeta{" +
                "type=" + type +
                ", element=" + element +
                ", destination=" + destination +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
