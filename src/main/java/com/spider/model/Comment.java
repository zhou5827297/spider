package com.spider.model;

import java.util.Map;

/**
 * 评论
 */
public class Comment {
    /**
     * 源id
     */
    private String sourceId;
    /**
     * 请求报文
     */
    private Request request;
    /**
     * 请求报文
     */
    private Map<String, Object> response;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }


    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Map<String, Object> getResponse() {
        return response;
    }

    public void setResponse(Map<String, Object> response) {
        this.response = response;
    }

    /**
     * 请求报文
     */
    public class Request {
        /**
         * 文章id
         */
        private String articleId;
        /**
         * 地址
         */
        private String url;

        /**
         * 请求头
         */
        private Map<String, String> header;

        public String getArticleId() {
            return articleId;
        }

        public void setArticleId(String articleId) {
            this.articleId = articleId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getHeader() {
            return header;
        }

        public void setHeader(Map<String, String> header) {
            this.header = header;
        }
    }

    /**
     * 响应报文
     */
    public class Response {
        /**
         * 响应类型(html,json,jsonp)
         */
        private Map<String, Object> type;
        /**
         * html格式
         */
        private Map<String, Object> html;
        /**
         * json格式
         */
        private Map<String, Object> json;
        /**
         * jsonp格式
         */
        private Map<String, Object> jsonp;

        /**
         * 请求头
         */
        private Map<String, Object> header;

        public Map<String, Object> getType() {
            return type;
        }

        public void setType(Map<String, Object> type) {
            this.type = type;
        }

        public Map<String, Object> getHtml() {
            return html;
        }

        public void setHtml(Map<String, Object> html) {
            this.html = html;
        }

        public Map<String, Object> getJson() {
            return json;
        }

        public void setJson(Map<String, Object> json) {
            this.json = json;
        }

        public Map<String, Object> getJsonp() {
            return jsonp;
        }

        public void setJsonp(Map<String, Object> jsonp) {
            this.jsonp = jsonp;
        }

        public Map<String, Object> getHeader() {
            return header;
        }

        public void setHeader(Map<String, Object> header) {
            this.header = header;
        }
    }

}
