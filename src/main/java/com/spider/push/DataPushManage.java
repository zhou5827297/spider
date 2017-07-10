package com.spider.push;

/**
 * 推送管理器
 */
public interface DataPushManage<T> {

    /**
     * 开启推送
     */
    void start();


    /**
     * 关闭推送
     */
    void shutdown();

    /**
     * 进行推送
     */
    void pushAll();

    /**
     * 推送成功
     */
    void pushSuccess(T push);

    /**
     * 推送失败
     */
    void pushFail(T push);

    /**
     * 加入推送队列
     */
    boolean addPush(T push);
}
