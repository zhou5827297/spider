package com.spider.config;

import java.util.Map;

/**
 * 配置字段
 */
public interface Constant {

    void loadConstant();

    String getValue(String key);

    <T> T getValue(String key, Class<T> cls);

    void setValue(String key, String value);

    void clear();
}
