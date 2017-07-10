package com.spider.model;

import com.spider.proxy.ProxyBean;

import java.util.Map;

/**
 * 请求数据包
 */
public class DataInfo {
    /**
     * 访问url
     */
    private String url;

    /**
     * 访问代理
     */
    private ProxyBean proxyBean;

    /**
     * 请求头参数
     */
    private Map<String, String> header;

    /**
     * 启动js引擎
     */
    private boolean javaScriptEnabled;

    /**
     * js执行超时时间
     */
    private Long timeout;

    public DataInfo() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ProxyBean getProxyBean() {
        return proxyBean;
    }

    public void setProxyBean(ProxyBean proxyBean) {
        this.proxyBean = proxyBean;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public boolean getJavaScriptEnabled() {
        return javaScriptEnabled;
    }

    public void setJavaScriptEnabled(boolean javaScriptEnabled) {
        this.javaScriptEnabled = javaScriptEnabled;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
