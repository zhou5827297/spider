package com.spider.config;

import com.spider.util.PropertiesUtils;

import java.io.File;
import java.util.Properties;

/**
 * 代理相关的配置
 */
public class ProxyConstant {
    private final static Properties PROPERTIES = PropertiesUtils.loadProperties("conf/proxy.properties");

    /**
     * 是否使用代理
     */
    public final static int PROXY_SWITCH = Integer.parseInt(PROPERTIES.getProperty("PROXY_SWITCH"));
    /**
     * 是否展示浏览器
     */
    public final static int SHOW_BROWER = Integer.parseInt(PROPERTIES.getProperty("SHOW_BROWER"));
    /**
     * 读取超时时间
     */
    public final static int TIMEOUT_READ = Integer.parseInt(PROPERTIES.getProperty("TIMEOUT_READ"));
    /**
     * JS执行超时时间
     */
    public final static int JS_EXECUTE_TIMEOUT_READ = Integer.parseInt(PROPERTIES.getProperty("JS_EXECUTE_TIMEOUT_READ"));
    /**
     * 连接超时时间
     */
    public final static int TIMEOUT_CONNECT = Integer.parseInt(PROPERTIES.getProperty("TIMEOUT_CONNECT"));
    /**
     * 代理服务器
     */
    public final static String PROXY_URL = PROPERTIES.getProperty("PROXY_URL");

    /**
     * 获取ip的接口
     */
    public final static String PROXY_GET = PROXY_URL + PROPERTIES.getProperty("PROXY_GET");
    /**
     * 通知成功接口
     */
    public final static String PROXY_NOTICE_SUCCESS = PROXY_URL + PROPERTIES.getProperty("PROXY_NOTICE_SUCCESS");

    /**
     * 通知失败接口
     */
    public final static String PROXY_NOTICE_FAILED = PROXY_URL + PROPERTIES.getProperty("PROXY_NOTICE_FAILED");

    /**
     * 代理ip重试最大次数
     */
    public final static int PROXY_MAX_RETRY = Integer.parseInt(PROPERTIES.getProperty("PROXY_MAX_RETRY"));
    /**
     * 任务重试最大次数
     */
    public final static int TASK_MAX_RETRY = Integer.parseInt(PROPERTIES.getProperty("TASK_MAX_RETRY"));

    /**
     * 浏览器池中借出的对象的最大数目
     */
    public final static int POOL_MAXACTIVE = Integer.parseInt(PROPERTIES.getProperty("POOL_MAXACTIVE"));
    /**
     * 包执行超时时间(分钟)
     */
    public final static int PACKAGE_EXECUTE_TIMEOUT = Integer.parseInt(PROPERTIES.getProperty("PACKAGE_EXECUTE_TIMEOUT"));

    /**
     * 池获取等待毫秒
     */
    public final static int POOL_MAXWAIT = Integer.parseInt(PROPERTIES.getProperty("POOL_MAXWAIT"));
    /**
     * 执行最大线程数
     */
    public final static int MAX_THREAD_COUNT = Integer.parseInt(PROPERTIES.getProperty("MAX_THREAD_COUNT"));
    /**
     * 配置文件目录
     */
    public final static String CONFIG_ROOT = PROPERTIES.getProperty("CONFIG_ROOT");
    /**
     * 执行文件目录
     */
    public final static String DATA_ROOT = PROPERTIES.getProperty("DATA_ROOT");
    /**
     * 抓取当前N小时以内的新闻
     */
    public final static int FETCH_BEFORE_HOUR = Integer.parseInt(PROPERTIES.getProperty("FETCH_BEFORE_HOUR"));
    /**
     * 是否保存网页快照文件
     */
    public final static int SNAPSHOT_WHETHER_SAVE = Integer.parseInt(PROPERTIES.getProperty("SNAPSHOT_WHETHER_SAVE"));
    /**
     * 快照文件路径
     */
    public final static String SNAPSHOT_DATA_ROOT = PROPERTIES.getProperty("SNAPSHOT_DATA_ROOT");
    /**
     * json文件临时目录路径
     */
    public final static String SPIDER_JSON_TMP_DIR = DATA_ROOT + File.separator + "spider-json-tmp";


}
