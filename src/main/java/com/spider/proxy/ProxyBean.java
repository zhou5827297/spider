package com.spider.proxy;

import java.io.Serializable;

/*
 *代理bean
 */
public class ProxyBean implements Serializable {

    private static final long serialVersionUID = 9214313860895272893L;

    /**
     * ip地址
     */
    private String ip;
    /**
     * 端口
     */
    private int port;
    /**
     * 成功标志
     */
    private boolean successFlag = false;


    public ProxyBean(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }


    @Override
    public int hashCode() {
        return ip.hashCode() + new Integer(port).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        ProxyBean other = (ProxyBean) obj;
        return other != null && ip.equals(other.ip) && port == other.port;
    }


    @Override
    public String toString() {
        return ip + ":" + port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(boolean successFlag) {
        this.successFlag = successFlag;
    }
}