package com.spider.util;

import java.net.InetAddress;

/**
 * 网卡工具
 */
public class NetUtil {
    /**
     * 简单获取ip地址
     */
    public static String getIp() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ip;
        }
    }
}
