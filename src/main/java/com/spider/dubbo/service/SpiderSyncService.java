package com.spider.dubbo.service;


import com.spider.dubbo.vo.Article;
import com.spider.dubbo.vo.Response;

import java.util.List;

/**
 * 爬虫文章等入库
 */
public interface SpiderSyncService {

    /**
     * 文章入库
     */
    Response addArticle(Article article);

    /**
     * 最近的24小时的抓取url
     */
    List<String> listUrl(String orderBy, long p, long l);
}
