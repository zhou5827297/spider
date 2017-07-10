package com.spider.config;


import com.spider.util.NetUtil;
import com.spider.util.PropertiesUtils;

import java.util.Properties;

/**
 * 服务配置信息
 */
public class ServerConstant {
    private final static Properties PROPERTIES = PropertiesUtils.loadProperties("conf/server.properties");
    /**
     * 是否开启zookeeper
     */
    public final static int ZOOKEEPER_SWITCH = Integer.parseInt(PROPERTIES.getProperty("zookeeper.switch"));
    /**
     * zookeeper注册中心
     */
    public final static String ZOOKEEPER_SERVER = PROPERTIES.getProperty("zookeeper.server");
    /**
     * spider所在组名
     */
    public final static String ZOOKEEPER_GROUP_NODE = PROPERTIES.getProperty("zookeeper.group_node");
    /**
     * 本机ip地址，优先取配置文件，之后取网卡地址
     */
    public final static String CLIENT_HOST = PROPERTIES.getProperty("client.host") == null ? NetUtil.getIp() : PROPERTIES.getProperty("client.host");

    /**
     * 工作ID (0~31)
     */
    public final static long CLIENT_WORKERID = Long.parseLong(PROPERTIES.getProperty("client.workerId"));

    /**
     * 数据中心(0~31)
     */
    public final static long LIENT_DATACENTERID = Long.parseLong(PROPERTIES.getProperty("client.datacenterId"));

}
