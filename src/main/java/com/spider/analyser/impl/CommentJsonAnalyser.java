package com.spider.analyser.impl;

import com.spider.analyser.CommentAnalyser;
import com.spider.model.Comment;
import com.spider.model.UserComment;
import com.spider.util.MapUtil;
import com.spider.util.MatcherUtils;
import common.base.util.JsonUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * json解析器
 */
public class CommentJsonAnalyser implements CommentAnalyser {

    @Override
    public List<UserComment> analyser(String response, Comment comment) {
        List<UserComment> userComments = new ArrayList<UserComment>();
        Map<String, Object> configMap = (Map<String, Object>) MapUtil.getValue(comment.getResponse(), "json");
        if (configMap == null) {
            return userComments;
        }

        Map<String, Object> jsonMap = (Map<String, Object>) JsonUtil.readJsonMap(response);
        if (jsonMap == null) {
            return userComments;
        }
        Object listObj = MapUtil.getValue(jsonMap, configMap.get("list").toString());
        if (listObj instanceof String && StringUtils.isEmpty(listObj)) {
            return userComments;
        }
        List<Map<String, Object>> lists = (List<Map<String, Object>>) MapUtil.getValue(jsonMap, configMap.get("list").toString());
        for (Map<String, Object> listMap : lists) {
            UserComment userComment = new UserComment();
            String userId = getValue(listMap, comment, "json.userId");
            userComment.setUserId(userId);
            String content = getValue(listMap, comment, "json.content");
            userComment.setContent(content);
            String userImg = getValue(listMap, comment, "json.userImg");
            userComment.setUserImg(userImg);
            String clickLikes = getValue(listMap, comment, "json.clickLikes");
            //公共代码json工具类未处理格式化null的问题，此处暂时处理掉
            userComment.setClickLikes(StringUtils.hasText(clickLikes) && !clickLikes.equals("null") ? Long.parseLong(clickLikes) : 0L);
            String nickName = getValue(listMap, comment, "json.nickName");
            userComment.setNickName(nickName);
            userComments.add(userComment);
        }
        return userComments;
    }

    /**
     * 从json获取对应的值
     */
    private String getValue(Map<String, Object> jsonMap, Comment comment, String pathKey) {
        Map<String, String> userConfigMap = (Map<String, String>) MapUtil.getValue(comment.getResponse(), pathKey);
        for (Map.Entry<String, String> entry : userConfigMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String jsonValue = String.valueOf(jsonMap.get(key));
            jsonValue = MatcherUtils.matcher(value, jsonValue);
            if (StringUtils.hasText(jsonValue) && !jsonValue.equals("null")) {
                return jsonValue;
            }
        }
        return null;
    }
}
