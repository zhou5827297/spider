package com.spider.analyser.impl;

import com.spider.model.Site;
import com.spider.analyser.ArticleAnalyser;
import com.spider.dubbo.vo.Article;
import com.spider.model.Config;
import com.spider.model.ScriptEnum;
import com.spider.util.DateUtil;
import com.spider.util.JsoupUtils;
import com.spider.util.SiteUtils;
import common.base.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * 特殊文章解析器
 */
public class SpecialAnalyser implements ArticleAnalyser {

    @Override
    public List<Article> analyserArticle(String content, Site site, Config config) {
        List<Article> articles = new ArrayList<Article>();
        Element root = Jsoup.parse(content);
        root.setBaseUri(site.getContext() + site.getIndex());
        for (String link : config.getJs().get("links")) {
            String[] strs = link.split("\\|");
            for (Element articleEle : root.select(strs[0])) {
                Article article = new Article();
                article.setSourceId(site.getId() + "");

                Iterator<Map.Entry<String, List<String>>> js = config.getJs().entrySet().iterator();
                while (js.hasNext()) {
                    Map.Entry<String, List<String>> entry = js.next();
                    String key = entry.getKey();
                    ScriptEnum type = ScriptEnum.parseCode(key);
                    List<String> scripts = entry.getValue();
                    for (String scriptJs : scripts) {
                        scriptJs = SiteUtils.replaceText(site.getReplaceArgs(), scriptJs);
                        if (type == ScriptEnum.TITLE) { //TODO 分页单独处理
                            String title = JsoupUtils.getValueBySelector(articleEle, scriptJs);
                            article.setTitle(title);
                        } else if (type == ScriptEnum.HEAD) {
                            String head = JsoupUtils.getValueBySelector(articleEle, scriptJs);
                            article.setHead(head);
                        } else if (type == ScriptEnum.BODY) {
                            String body = JsoupUtils.getValueBySelector(articleEle, scriptJs);
                            article.setBody(body);
                        } else if (type == ScriptEnum.AUTHOR) {
                            String author = JsoupUtils.getValueBySelector(articleEle, scriptJs);
                            article.setAuthor(author);
                        } else if (type == ScriptEnum.PTIME) {
                            String publishTime = JsoupUtils.getValueBySelector(articleEle, scriptJs);
                            if (StringUtil.isEmpty(publishTime)) {
                                //替换所有的替换符号
                                publishTime = DateUtil.parseDateTimeToSec(DateUtil.getDate(scriptJs));
                            }
                            article.setPublishTime(publishTime);
                        }
                    }
                }
//                String subfix = System.nanoTime() + new Random().nextLong() + "";
                article.setUrl(site.getContext() + "?title=" + article.getTitle()); // 因无唯一标志，为重复抓取，采用当前标题唯一
                articles.add(article);
            }
        }
        return articles;
    }

    @Override
    public List<String> analyserList(String content, Site site, Config config) {
        List<String> urls = new ArrayList<String>();
        Element root = Jsoup.parse(content);
        root.setBaseUri(site.getContext() + site.getIndex());
        for (String link : config.getJs().get("links")) {
            String[] strs = link.split("\\|");
            for (Element pageEle : root.select(strs[0])) {
                String url = JsoupUtils.getFirstValueBySelector(pageEle, strs[1]);
                urls.add(url);
            }
        }

        return urls;
    }
}
