package com.spider.config.impl;

import com.spider.config.Constant;
import com.spider.util.PropertiesUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 本机配置文件
 */
public class LocalConstant extends AbstractConstant implements Constant {

    public LocalConstant(String path) {
        this.path = path;
    }

    @Override
    public void loadConstant() {
        Properties properties = PropertiesUtils.loadProperties(getPath());
        Iterator iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = entry.getKey().toString();
            String value = entry.getValue() != null ? String.valueOf(entry.getValue()) : null;
            setValue(key, value);
        }
    }
}
