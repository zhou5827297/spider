package com.spider.config;

import com.spider.util.PropertiesUtils;

import java.util.Properties;

/**
 * 外部接口地址
 */
public class RemoteConstant {
    private final static Properties PROPERTIES = PropertiesUtils.loadProperties("conf/remote.properties");

    /**
     * 单任务推送重试次数
     */
    public final static int TASK_MAX_RETRY = Integer.parseInt(PROPERTIES.getProperty("TASK_MAX_RETRY"));
    /**
     * 离线单任务推送重试轮次数
     */
    public final static int TASK_OFFLINE_ROUND_MAX_RETRY = Integer.parseInt(PROPERTIES.getProperty("TASK_OFFLINE_ROUND_MAX_RETRY"));
    /**
     * 离线单次最大推送数目
     */
    public final static int TASK_ONCE_MAX = Integer.parseInt(PROPERTIES.getProperty("TASK_ONCE_MAX"));
    /**
     * 订阅接口地址
     */
    public final static String SUBSCRIBE_URL = PROPERTIES.getProperty("SUBSCRIBE_URL");
    /**
     * 获取文章列表接口
     */
    public final static String ARTICLE_LIST = SUBSCRIBE_URL + PROPERTIES.getProperty("ARTICLE_LIST");
    /**
     * 文章入库接口
     */
    public final static String ARTICLE_ADD = SUBSCRIBE_URL + PROPERTIES.getProperty("ARTICLE_ADD");
    /**
     * 推送方式
     */
    public final static String PUSH_METHOD = PROPERTIES.getProperty("PUSH_METHOD");

    /**
     * 监控开关打开
     */
    public final static int DUBBO_MONITOR_SWITCH = Integer.parseInt(PROPERTIES.getProperty("DUBBO.MONITOR.SWITCH"));
    /**
     * 监控开关打开
     */
    public final static int SPIDER_REMOTE_URL_SYNC = Integer.parseInt(PROPERTIES.getProperty("SPIDER_REMOTE_URL_SYNC"));
}
