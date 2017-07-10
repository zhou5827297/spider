package com.spider.analyser;

import com.spider.dubbo.vo.Article;
import com.spider.model.Site;
import com.spider.model.Config;

import java.util.List;

/**
 * 特殊文章解析器
 */
public interface ArticleAnalyser {

    List<Article> analyserArticle(String content, Site site, Config config);

    List<String> analyserList(String content, Site site, Config config);
}
