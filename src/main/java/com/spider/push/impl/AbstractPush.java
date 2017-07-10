package com.spider.push.impl;

import com.spider.dubbo.vo.Article;
import com.spider.model.Comment;
import com.spider.push.DataPush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象推送功能
 */
public abstract class AbstractPush implements DataPush {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    /**
     * 推送次数
     */
    private int pushCount;

    /**
     * 推送失败次数
     */
    private int pushErrorCount;

    /**
     * 文章对象
     */
    private Article article;
    /**
     * 评论对象
     */
    private Comment comment;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public int getPushCount() {
        return pushCount;
    }

    public void setPushCount(int pushCount) {
        this.pushCount = pushCount;
    }

    public int getPushErrorCount() {
        return pushErrorCount;
    }

    public void setPushErrorCount(int pushErrorCount) {
        this.pushErrorCount = pushErrorCount;
    }

    public synchronized void incrementPushCount() {
        pushCount++;
    }

    public synchronized void incrementPushErrorCount() {
        pushErrorCount++;
    }

    @Override
    public String toString() {
        return "AbstractPush{" +
                "pushCount=" + pushCount +
                ", pushErrorCount=" + pushErrorCount +
                ", article=" + article +
                ", comment=" + comment +
                '}';
    }
}
