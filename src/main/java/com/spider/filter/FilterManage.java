package com.spider.filter;

import java.util.Collection;

/**
 * 过滤器
 */
public interface FilterManage {


    /**
     * 是否已经存在
     */
    boolean isExists(String url);

    /**
     * 添加url
     */
    void add(String url);

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 刷新缓存
     */
    void refresh(Collection<String> urls);

}
