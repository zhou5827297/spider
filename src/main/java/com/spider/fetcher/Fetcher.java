package com.spider.fetcher;

/**
 * 抓取器
 */
public interface Fetcher {
    /**
     * 抓取逻辑
     */
    void execute(String rootPath);
}
