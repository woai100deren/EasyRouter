package com.dj.easyrouter.model;

import com.dj.easyrouter.annotation.EasyRoute;

import javax.lang.model.element.Element;

/**
 * 路由相关信息
 */
public class EasyRouteMeta {
    public enum Type {
        ACTIVITY, SERVICE
    }

    private Type type;

    /**
     * 节点（Activity）
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

    public static EasyRouteMeta build(Type type, Class<?> destination, String path, String
            group) {
        return new EasyRouteMeta(type, null, destination, path, group);
    }

    public EasyRouteMeta() {
    }

    public EasyRouteMeta(Type type, EasyRoute route, Element element) {
        this(type, element, null, route.path(), route.group());
    }

    public EasyRouteMeta(Type type, Element element, Class<?> destination, String path, String
            group) {
        this.type = type;
        this.destination = destination;
        this.element = element;
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
}
