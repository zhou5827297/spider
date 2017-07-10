package com.spider.proxy.impl;

import com.spider.config.ProxyConstant;
import com.spider.engine.httpclient.HttpPoolManage;
import com.spider.proxy.ProxyBean;
import com.spider.proxy.ProxyManage;
import common.base.util.Base64;
import common.base.util.JsonUtil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 代理远程调用
 */
public class ProxyRemoteManage implements ProxyManage {
    private final ThreadLocal<ProxyBean> local = new ThreadLocal<ProxyBean>();

    @Override
    public void noticeSuccess() {
        ProxyBean proxyBean = local.get();
        if (proxyBean != null) {
            proxyBean.setSuccessFlag(true);
            HttpPoolManage.sendGet(ProxyConstant.PROXY_NOTICE_SUCCESS + "?ip=" + proxyBean.getIp() + "&port=" + proxyBean.getPort());
        }
    }

    @Override
    public void noticeFail(String errorMessage) {
        ProxyBean proxyBean = local.get();
        errorMessage = errorMessage == null ? "" : Base64.encode(errorMessage.getBytes());
        if (proxyBean != null) {
            proxyBean.setSuccessFlag(false);
            HttpPoolManage.sendGet(ProxyConstant.PROXY_NOTICE_FAILED + "?ip=" + proxyBean.getIp() + "&port=" + proxyBean.getPort() + "&msg=" + errorMessage);
        }
    }

    @Override
    public ProxyBean getProxyBean() {
        ProxyBean proxyBean = null;
        for (int i = 0; i < ProxyConstant.PROXY_MAX_RETRY; i++) {
            proxyBean = this.getSimpleProxyBean();
            if (proxyBean == null) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception ex) {
                    // ingore
                }
            } else {
                break;
            }
        }
        return proxyBean;
    }

    @Override
    public ProxyBean getSimpleProxyBean() {
        //成功过的ip认为，对此网站连通率最好，继续使用
        ProxyBean proxyBean = local.get();
        if (proxyBean != null && proxyBean.isSuccessFlag()) {
            return proxyBean;
        }
        //-----------------------------------------------------------
        String proxy = HttpPoolManage.sendGet(ProxyConstant.PROXY_GET);
        if (proxy == null) {
            local.set(null);
            return null;
        }
        Map<String, Object> proxyMap = (Map<String, Object>) JsonUtil.readJsonMap(proxy);
        String ip = (String) proxyMap.get("ip");
        int port = (int) proxyMap.get("port");
        proxyBean = new ProxyBean(ip, port);
        local.set(proxyBean);
        return proxyBean;
    }


}
