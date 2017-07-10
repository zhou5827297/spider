package com.spider.model;

import java.util.List;
import java.util.Map;

/**
 * 站点
 *
 */
public class Site {
    Long id;
    /**
     * 最近时间
     */
    Long maxTime;
    /**
     * 域名
     */
    String domain;
    /**
     * 上下文
     */
    String context;
    /**
     * 首页
     */
    String index;
    /**
     * 浏览器头部信息
     */
    Map<String, String> header;
    /**
     * 配置
     */
    List<Config> configs;

    /**
     * 替换参数，${date}
     */
    String replaceArgs;

    public Long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public final String getContext() {
        return context;
    }

    public final void setContext(String context) {
        this.context = context;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Config> configs) {
        this.configs = configs;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getReplaceArgs() {
        return replaceArgs;
    }

    public void setReplaceArgs(String replaceArgs) {
        this.replaceArgs = replaceArgs;
    }
}
