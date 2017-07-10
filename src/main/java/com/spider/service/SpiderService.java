package com.spider.service;

import com.spider.browser.AriticleBrowser;

/**
 * 爬虫服务
 *
 */
public interface SpiderService extends Spider {

    /**
     * 获取浏览器
     */
    AriticleBrowser getBrowser();


    /**
     * 保存内容
     *
     */
    void save(String content);

    /**
     * 保存地址
     *
     */
    void saveUrl(String url, boolean manual);

    void setBrowser(AriticleBrowser browser);

    /**
     * 获取连接
     *
     */
    void links(String s);

    /**
     * 结束
     */
    void finish(boolean success);

    /**
     * 加载延迟
     */
    long timeout();

}
