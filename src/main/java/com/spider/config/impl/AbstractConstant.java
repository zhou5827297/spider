package com.spider.config.impl;

import com.spider.config.Constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象静态字段类
 */
public abstract class AbstractConstant implements Constant {
    protected final static Map<String, String> PROPERTIES = new ConcurrentHashMap<String, String>();

    protected String path;

    @Override
    public <T> T getValue(String key, Class<T> cls) {
        String value = getValue(key);
        T realValue = null;
        if (cls.isAssignableFrom(Integer.class)) {
            realValue = cls.cast(value == null ? 0 : Integer.parseInt(value));
        } else if (cls.isAssignableFrom(String.class)) {
            realValue = cls.cast(value);
        } else if (cls.isAssignableFrom(Double.class)) {
            realValue = cls.cast(value == null ? 0.00 : Double.parseDouble(value));
        } else if (cls.isAssignableFrom(Float.class)) {
            realValue = cls.cast(value == null ? 0.0f : Float.parseFloat(value));
        } else if (cls.isAssignableFrom(Long.class)) {
            realValue = cls.cast(value == null ? 0L : Long.parseLong(value));
        } else if (cls.isAssignableFrom(Boolean.class)) {
            realValue = cls.cast(value == null ? false : Boolean.parseBoolean(value));
        }
        return realValue;
    }

    @Override
    public void setValue(String key, String value) {
        PROPERTIES.put(key, value);
    }

    @Override
    public String getValue(String key) {
        return PROPERTIES.get(key);
    }

    @Override
    public void clear() {
        PROPERTIES.clear();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
