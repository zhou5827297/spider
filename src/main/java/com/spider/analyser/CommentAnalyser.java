package com.spider.analyser;

import com.spider.model.Comment;
import com.spider.model.UserComment;

import java.util.List;

/**
 * 评论的解析器
 */
public interface CommentAnalyser {

    /**
     * 解析成评论对象
     */
    List<UserComment> analyser(String response, Comment comment);


}
