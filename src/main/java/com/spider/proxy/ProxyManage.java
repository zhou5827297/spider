package com.spider.proxy;

/**
 * 代理管理
 */
public interface ProxyManage {

    /**
     * 通知代理ip，可使用
     */
    void noticeSuccess();

    /**
     * 通知代理ip，不可使用
     */

    void noticeFail(String errorMessage);

    /**
     * 简单获取一条代理ip
     */
    ProxyBean getSimpleProxyBean();

    /**
     * 获取一条可靠的代理ip，会多次重试
     */
    ProxyBean getProxyBean();

}
