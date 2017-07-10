package com.spider.push.article;

/**
 * 推送工厂
 */
public class PushFactory {

    /**
     * 创建推送实体对象
     */
    public static AbstractPush makeDataPush(String type) {
        AbstractPush push;
        if ("dubbo".equalsIgnoreCase(type)) {
            push = new ArticleDubboPush();
        } else {
            push = new ArticleHttpPush();
        }
        return push;
    }
}
