package com.spider.analyser.impl;

import com.spider.model.UserComment;
import com.spider.analyser.CommentAnalyser;
import com.spider.model.Comment;
import com.spider.util.JsoupUtils;
import com.spider.util.MapUtil;
import com.spider.util.MatcherUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * html解析器
 */

public class CommentHtmlAnalyser implements CommentAnalyser {

    @Override
    public List<UserComment> analyser(String response, Comment comment) {
        List<UserComment> userComments = new ArrayList<UserComment>();
        Element root = Jsoup.parse(response);
        String listKey = (String) MapUtil.getValue(comment.getResponse(), "html.list");
        if (listKey == null) {
            return userComments;
        }
        Elements items = root.select(listKey);
        for (Element item : items) {
            UserComment userComment = new UserComment();
            String userId = getValue(item, comment, "html.userId");
            userComment.setUserId(userId);
            String content = getValue(item, comment, "html.content");
            userComment.setContent(content);
            String userImg = getValue(item, comment, "html.userImg");
            userComment.setUserImg(userImg);
            String clickLikes = getValue(item, comment, "html.clickLikes");
            userComment.setClickLikes(StringUtils.hasText(clickLikes) ? Long.parseLong(clickLikes) : 0L);
            String nickName = getValue(item, comment, "html.nickName");
            userComment.setNickName(nickName);
            userComments.add(userComment);
        }

        return userComments;
    }

    /**
     * 从html获取对应的值
     */
    private String getValue(Element item, Comment comment, String pathKey) {
        Map<String, String> userConfigMap = (Map<String, String>) MapUtil.getValue(comment.getResponse(), pathKey);
        for (Map.Entry<String, String> entry : userConfigMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String htmlValue = JsoupUtils.getFirstValueBySelector(item, key);
            htmlValue = MatcherUtils.matcher(value, htmlValue);
            if (StringUtils.hasText(htmlValue)) {
                return htmlValue;
            }
        }
        return null;
    }
}
