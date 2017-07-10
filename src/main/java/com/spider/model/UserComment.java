package com.spider.model;

/**
 * 用户评论实体
 */
public class UserComment {
    private Long articleId;
    private String userId;
    private String content;
    private String userImg;
    private Long clickLikes;
    private String nickName;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public Long getClickLikes() {
        return clickLikes;
    }

    public void setClickLikes(Long clickLikes) {
        this.clickLikes = clickLikes;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
