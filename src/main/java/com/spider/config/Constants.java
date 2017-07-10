package com.spider.config;

/**
 * 静态变量
 */
public class Constants {

    //===================================================================
    //##############################proxy################################
    //===================================================================
    /**
     * 是否使用代理
     */
    public static String PROXY_SWITCH = "PROXY_SWITCH";
    /**
     * 是否展示浏览器
     */
    public static String SHOW_BROWER = "SHOW_BROWER";
    /**
     * 读取超时时间
     */
    public static String TIMEOUT_READ = "TIMEOUT_READ";
    /**
     * JS执行超时时间
     */
    public static String JS_EXECUTE_TIMEOUT_READ = "JS_EXECUTE_TIMEOUT_READ";
    /**
     * 连接超时时间
     */
    public static String TIMEOUT_CONNECT = "TIMEOUT_CONNECT";
    /**
     * 代理服务器
     */
    public static String PROXY_URL = "PROXY_URL";

    /**
     * 获取ip的接口
     */
    public static String PROXY_GET = "PROXY_GET";
    /**
     * 通知成功接口
     */
    public static String PROXY_NOTICE_SUCCESS = "PROXY_NOTICE_SUCCESS";

    /**
     * 通知失败接口
     */
    public static String PROXY_NOTICE_FAILED = "PROXY_NOTICE_FAILED";

    /**
     * 代理ip重试最大次数
     */
    public static String PROXY_MAX_RETRY = "PROXY_MAX_RETRY";
    /**
     * 任务重试最大次数
     */
//    public static String TASK_MAX_RETRY = "TASK_MAX_RETRY";

    /**
     * 浏览器池中借出的对象的最大数目
     */
    public static String POOL_MAXACTIVE = "POOL_MAXACTIVE";
    /**
     * 包执行超时时间(分钟)
     */
    public static String PACKAGE_EXECUTE_TIMEOUT = "PACKAGE_EXECUTE_TIMEOUT";

    /**
     * 池获取等待毫秒
     */
    public static String POOL_MAXWAIT = "POOL_MAXWAIT";
    /**
     * 执行最大线程数
     */
    public static String MAX_THREAD_COUNT = "MAX_THREAD_COUNT";
    /**
     * 配置文件目录
     */
    public static String CONFIG_ROOT = "CONFIG_ROOT";
    /**
     * 执行文件目录
     */
    public static String DATA_ROOT = "DATA_ROOT";
    /**
     * 抓取当前N小时以内的新闻
     */
    public static String FETCH_BEFORE_HOUR = "FETCH_BEFORE_HOUR";
    /**
     * 是否保存网页快照文件
     */
    public static String SNAPSHOT_WHETHER_SAVE = "SNAPSHOT_WHETHER_SAVE";
    /**
     * 快照文件路径
     */
    public static String SNAPSHOT_DATA_ROOT = "SNAPSHOT_DATA_ROOT";


    //===================================================================
    //##############################remote###############################
    //===================================================================


    /**
     * 单任务推送重试次数
     */
    public final static String TASK_MAX_RETRY = "TASK_MAX_RETRY";
    /**
     * 离线单任务推送重试轮次数
     */
    public final static String TASK_OFFLINE_ROUND_MAX_RETRY = "TASK_OFFLINE_ROUND_MAX_RETRY";
    /**
     * 离线单次最大推送数目
     */
    public final static String TASK_ONCE_MAX = "TASK_ONCE_MAX";
    /**
     * 订阅接口地址
     */
    public final static String SUBSCRIBE_URL = "SUBSCRIBE_URL";
    /**
     * 获取文章列表接口
     */
    public final static String ARTICLE_LIST = "ARTICLE_LIST";
    /**
     * 文章入库接口
     */
    public final static String ARTICLE_ADD = "ARTICLE_ADD";
    /**
     * 推送方式
     */
    public final static String PUSH_METHOD = "PUSH_METHOD";

    /**
     * 监控开关打开
     */
    public final static String DUBBO_MONITOR_SWITCH = "DUBBO_MONITOR_SWITCH";


    //===================================================================
    //##############################server###############################
    //===================================================================

    /**
     * 是否开启zookeeper
     */
    public final static String ZOOKEEPER_SWITCH = "ZOOKEEPER_SWITCH";
    /**
     * zookeeper注册中心
     */
    public final static String ZOOKEEPER_SERVER = "ZOOKEEPER_SERVER";
    /**
     * spider所在组名
     */
    public final static String ZOOKEEPER_GROUP_NODE = "ZOOKEEPER_GROUP_NODE";
    /**
     * 本机ip地址，优先取配置文件，之后取网卡地址
     */
    public final static String CLIENT_HOST = "CLIENT_HOST";
}
