package com.spider.dubbo.vo;

/**
 * 返回信息
 */
public class Response implements java.io.Serializable {
    private Code code;
    private String message;

    public enum Code {
        SUCCESS, FAIL, TIMEOUT
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
